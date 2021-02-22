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
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.plugin.PluginMessages
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.jetbrains.annotations.Contract
import java.math.BigDecimal
import java.util.function.Function
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import java.util.stream.Collectors

class CommonAnvilReloadCommand<TString, TCommandSource> :
  SimpleCommand<TString, TCommandSource> {

  companion object {
    private val reloadEnvironment = Function { e: Environment ->
      e.reload()
      e.getPluginInfo<Any>().name
    }
  }

  @Inject
  private lateinit var permissionService: PermissionService

  @Inject
  private lateinit var pluginMessages: PluginMessages<TString>

  @Inject
  private lateinit var registry: Registry

  @Inject
  private lateinit var textService: TextService<TString, TCommandSource>

  override fun execute(source: TCommandSource, alias: String, context: Array<String>) {
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
    alias: String,
    context: Array<String>
  ): MutableList<String>? {
    val suggestions = Anvil.getEnvironmentManager()
      .environments.values.stream()
      .map { obj: Environment -> obj.name }
      .sorted().collect(Collectors.toList())
    suggestions.add("--all")
    suggestions.add("--regex")
    return suggestions
  }

  private fun doAll(): String {
    return Anvil.getEnvironmentManager()
      .environments.values.stream()
      .map(reloadEnvironment)
      .collect(Collectors.joining(", "))
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