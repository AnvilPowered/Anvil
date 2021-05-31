/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.common.command.regedit

import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.registry.ConfigurationService
import org.anvilpowered.anvil.api.registry.Key
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService

class Stage<TCommandSource>(
  val envName: String,
  val registries: MutableMap<String, Registry>,
  val pluginInfo: PluginInfo,
  val textService: TextService<TCommandSource>,
) {

  lateinit var registry: Pair<String, Registry>

  private val changes: MutableList<Change<*, TCommandSource>> = ArrayList()
  private val availableRegistries: Component

  private val border = textService.builder()
    .dark_gray().appendCount(15, '=')
    .append(" [ ").gold().append("Anvil RegEdit")
    .dark_gray().append(" ] ").appendCount(15, '=')
    .build()

  private val view = textService.builder()
    .aqua().append("[ Key ]")
    .onHoverShowText(textService.builder()
      .aqua().append("Key\n")
      .gray().append("/$anvilAlias regedit key <key> [info|set|unset|unstage]")
    ).onClickSuggestCommand("/$anvilAlias regedit key ")
    .build()

  private val cancel = textService.builder()
    .red().append("[ Cancel ]")
    .onHoverShowText(textService.builder()
      .red().append("Cancel\n")
      .gray().append("/$anvilAlias regedit cancel")
    ).onClickRunCommand("/$anvilAlias regedit cancel")
    .build()

  private val commit = textService.builder()
    .gold().append("[ Commit ]")
    .onHoverShowText(textService.builder()
      .gold().append("Commit\n")
      .gray().append("/$anvilAlias regedit commit")
    ).onClickRunCommand("/$anvilAlias regedit commit")
    .build()

  private val noSuchChange = textService.builder()
    .append(pluginInfo.prefix)
    .red().append("Could not find change")
    .build()

  private val selectRegistry = textService.builder()
    .append(pluginInfo.prefix)
    .red().append("You must select a registry")
    .build()

  private val sensitive = textService.builder()
    .append(pluginInfo.prefix)
    .red().append("This key is sensitive and may only be viewed or edited if ")
    .gold().append("server.regeditAllowSensitive ")
    .red().append("is enabled in the config")
    .build()

  private val userImmutable = textService.builder()
    .append(pluginInfo.prefix)
    .red().append("This key is user immutable and may not be edited with the regedit command")
    .build()

  fun setRegistry(name: String?): Component {
    val newReg = when (name) {
      null, "internal", "r", "registry" -> Pair("main", registries["main"])
      "c" -> Pair("config", registries["config"])
      else -> Pair(name, registries[name])
    }
    return if (newReg.second == null) {
      textService.builder()
        .appendPrefix()
        .red()
        .append("Could not find registry $name")
        .build()
    } else {
      registry = Pair(newReg.first, newReg.second!!)
      print()
    }
  }

  init {
    val builder = textService.builder().aqua()
    // reference equality, we want to check specifically for the same instance
    // if these are the same, don't show [ Internal ] and [ Config ] separately
    if (registries["main"] !== registries["config"]) {
      builder.append(textService.builder()
        .aqua().append("[ Internal ]")
        .onHoverShowText(textService.builder()
          .aqua().append("The main registry\n")
          .gray().append("/$anvilAlias regedit select internal")
        ).onClickRunCommand("/$anvilAlias regedit select internal")
      ).append(" ")
    }
    builder.append(textService.builder()
      .aqua().append("[ Config ]")
      .onHoverShowText(textService.builder()
        .aqua().append("The configuration\n")
        .gray().append("/$anvilAlias regedit select config")
      ).onClickRunCommand("/$anvilAlias regedit select config")
    )
    for ((name, _) in registries) {
      if (name == "main" || name == "config") {
        continue
      }
      val cmd = "/$anvilAlias regedit select $name"
      builder.append(" ", textService.builder()
        .aqua().append("[ ", name, " ]")
        .onHoverShowText(textService.builder()
          .aqua().append(name)
          .gray().append(cmd)
        ).onClickRunCommand(cmd)
      )
    }
    availableRegistries = builder.build()
  }

  fun info(key: Key<*>): Component {
    return when {
      !::registry.isInitialized -> selectRegistry
      else -> when (key.isSensitive(registry.second)) {
        true -> sensitive
        false -> textService.info(key, registry.second)
      }
    }
  }

  fun commit(source: TCommandSource): Boolean {
    if (!::registry.isInitialized) {
      textService.send(selectRegistry, source)
      return false
    }
    val reg = registry.second
    if (changes.isEmpty()) {
      textService.builder()
        .append(pluginInfo.prefix)
        .red().append("You have no changes!")
        .sendTo(source)
      return false
    }
    changes.forEach { it.apply(reg) }
    if (reg is ConfigurationService) {
      if (reg.save()) {
        reg.load()
        textService.builder()
          .append(pluginInfo.prefix)
          .green().append("Successfully committed and saved ")
          .gold().append(changes.size)
          .green().append(" changes!")
          .sendTo(source)
      } else {
        textService.builder()
          .append(pluginInfo.prefix)
          .red().append("There was an error saving the config!")
          .sendTo(source)
        return false
      }
    } else {
      reg.load()
      textService.builder()
        .append(pluginInfo.prefix)
        .green().append("Successfully committed ")
        .gold().append(changes.size)
        .green().append(" changes!")
        .sendTo(source)
    }
    return true
  }

  fun <T> addChange(key: Key<T>, newValue: T? = null): Component {
    if (!::registry.isInitialized) {
      return selectRegistry
    }
    if (key.isUserImmutable) {
      return userImmutable
    }
    if (key.isSensitive(registry.second)) {
      return sensitive
    }
    val existing = changes.stream()
      .filter { it.key == key }
      .findAny().orElse(null) as? Change<T, TCommandSource>
    val action: String
    if (existing == null) {
      changes += Change(this, key, newValue)
      action = "added"
    } else {
      existing.newValue = newValue
      action = "edited"
    }
    return print(textService.builder()
      .green().append("Successfully $action change for ")
      .gold().append(key, " ", textService.undo("/$anvilAlias regedit key $key unstage"))
      .build())
  }

  fun <T> unstageChange(key: Key<T>): Component {
    if (!::registry.isInitialized) {
      return selectRegistry
    }
    val index = changes.indexOfFirst { it.key == key }
    if (index == -1) {
      return noSuchChange
    }
    val removed = changes.removeAt(index) as Change<T, TCommandSource>
    return print(textService.builder()
      .green().append("Successfully unstaged change for ")
      .gold().append(key, " ", textService.undo("/$anvilAlias regedit key $key set ${key.toString(removed.newValue)}"))
      .build())
  }

  fun print(message: Component? = null): Component {
    if (!::registry.isInitialized) {
      return textService.builder()
        .append(border, "\n\n")
        .green().append("Started a regedit session for ")
        .gold().append(pluginInfo.name, "\n\n")
        .gray().append("Please select one of the following registries:\n\n", availableRegistries)
        .gray().append("\nor ", cancel, "\n\n", border)
        .build()
    }
    val builder = textService.builder()
      .append(border, "\n\n")
      .appendIf(message != null, message!!, "\n\n")
      .aqua().append("[ ", registry.first, " ]")
      .gray().append(" Queued changes:")
    if (changes.isEmpty()) {
      builder.red().append(" (none)")
    } else {
      builder.append("\n")
      for (change in changes) {
        builder.append("\n", change.print())
      }
    }
    return builder.gray().append("\n\nPlease select an action:\n\n")
      .append(view, " ", commit, " ", cancel, "\n\n", border)
      .build()
  }
}
