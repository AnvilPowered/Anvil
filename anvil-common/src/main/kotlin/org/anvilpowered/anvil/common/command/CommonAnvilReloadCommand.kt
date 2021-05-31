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
package org.anvilpowered.anvil.common.command

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.jetbrains.annotations.Contract
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import java.util.stream.Collectors

class CommonAnvilReloadCommand<TCommandSource> : SimpleCommand<TCommandSource> {

  @Inject
  private lateinit var permissionService: PermissionService

  @Inject
  private lateinit var textService: TextService<TCommandSource>

  @Inject
  private lateinit var registry: Registry

  companion object {
    private val reloadEnvironment = { e: Environment ->
      e.reload()
      e.pluginInfo.name
    }
  }

  private val USAGE: Component = Component.text("[-a|--all|-r|--regex] [<plugin>]")

  private val DESCRIPTION: Component by lazy {
    textService.of("Anvil reload command")
  }

  override fun execute(source: TCommandSource, context: Array<String>) {
    val length = context.size
    if (length == 0) {
      checkPresent(source, false)
      return
    }
    val reloadedResult = arrayOf("")
    if ("-a" == context[0] || "--all" == context[0]) {
      reloadedResult[0] = doAll()
    } else if ("-r" == context[0] || "--regex" == context[0]) {
      if (!checkPresent(source, length > 1) || !doRegex(source, context[1], reloadedResult)) {
        return
      }
    } else {
      if (!doDirect(source, context[0], reloadedResult)) {
        return
      }
    }
    sendSuccess(source, reloadedResult)
  }

  override fun suggest(
    source: TCommandSource,
    context: Array<String>,
  ): List<String> {
    val suggestions = Anvil.getEnvironmentManager()
      .environments.values.stream()
      .map { obj: Environment -> obj.name }
      .sorted().collect(Collectors.toList())
    suggestions.add("--all")
    suggestions.add("--regex")
    return suggestions
  }

  override fun canExecute(source: TCommandSource): Boolean {
    return permissionService.hasPermission(source, registry.getOrDefault(Keys.RELOAD_PERMISSION))
  }

  override fun usage(source: TCommandSource): Component = USAGE
  override fun shortDescription(source: TCommandSource): Component = DESCRIPTION

  private fun doAll(): String {
    return Anvil.getEnvironmentManager()
      .environments.values.asSequence()
      .map(reloadEnvironment)
      .joinToString(", ")
  }

  @Contract("_, _ -> param2")
  private fun checkPresent(source: TCommandSource, present: Boolean): Boolean {
    if (present) {
      return true
    }
    textService.builder()
      .appendPrefix()
      .red().append("Plugin is required if '--all' is not set")
      .sendTo(source)
    return false
  }

  private fun doRegex(source: TCommandSource, regex: String, reloadedResult: Array<String>): Boolean {
    try {
      reloadedResult[0] = Anvil.getEnvironmentManager()
        .getEnvironmentsAsStream(Pattern.compile(regex))
        .map(reloadEnvironment)
        .collect(Collectors.joining(", "))
      if (reloadedResult[0].isEmpty()) {
        textService.builder()
          .appendPrefix()
          .red().append("Regex ")
          .gold().append(regex)
          .red().append(" did not match any plugins")
          .sendTo(source)
        return false
      }
    } catch (e: PatternSyntaxException) {
      textService.builder()
        .appendPrefix()
        .red().append("Failed to parse ")
        .gold().append(regex)
        .sendTo(source)
      return false
    }
    return true
  }

  private fun doDirect(source: TCommandSource, plugin: String, reloadedResult: Array<String>): Boolean {
    val optionalReloaded = Anvil.getEnvironmentManager()
      .getEnvironment(plugin)
      .map(reloadEnvironment)
    if (!optionalReloaded.isPresent) {
      textService.builder()
        .appendPrefix()
        .red().append("Could not find plugin ")
        .gold().append(plugin)
        .sendTo(source)
      return false
    }
    reloadedResult[0] = optionalReloaded.get()
    return true
  }

  private fun sendSuccess(source: TCommandSource, reloadedResult: Array<String>) {
    textService.builder()
      .appendPrefix()
      .green().append("Successfully reloaded ")
      .gold().append(reloadedResult[0])
      .sendTo(source)
  }
}
