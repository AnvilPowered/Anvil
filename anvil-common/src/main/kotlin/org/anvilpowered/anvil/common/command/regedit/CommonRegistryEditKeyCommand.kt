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

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.registry.Key
import org.anvilpowered.anvil.api.registry.Keys
import kotlin.streams.toList

class CommonRegistryEditKeyCommand<TUser, TPlayer, TCommandSource>
  : CommonRegistryEditBaseCommand<TUser, TPlayer, TCommandSource>() {

  @Inject
  private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TCommandSource>

  private val keyActions = listOf("info", "set", "unset", "unstage")

  private val usage: Component by lazy {
    textService.builder()
      .red().append("Usage: /$anvilAlias regedit key <key> [info|set|unset|unstage] [<value>]")
      .build()
  }

  private val setUsage: Component by lazy {
    textService.builder()
      .append(pluginInfo.prefix)
      .red().append("Value required for set subcommand. Usage: /$anvilAlias regedit key <key> set <value>")
      .build()
  }

  private fun unknownKey(keyName: String) = textService.builder()
    .append(pluginInfo.prefix)
    .red().append("Unknown key: ")
    .gold().append(keyName)
    .build()

  private fun String.resolveKey(envName: String): Key<Any>? {
    return Keys.resolveLocalAndGlobal<Any>(this, envName).orElse(null)
  }

  override fun execute(source: TCommandSource, context: Array<String>) {
    val stage = registryEditRootCommand.stages[userService.getUUIDSafe(source)]
    if (stage == null) {
      textService.send(registryEditRootCommand.notInStage, source)
      return
    }
    textService.send(when (context.size) {
      0, 1 -> usage
      2 -> when (val key = context[0].resolveKey(stage.envName)) {
        null -> unknownKey(context[0])
        else -> when (context[1]) {
          "info" -> stage.info(key)
          "set" -> setUsage
          "unset" -> stage.addChange(key)
          "unstage" -> stage.unstageChange(key)
          else -> usage
        }
      }
      3 -> when (val key = context[0].resolveKey(stage.envName)) {
        null -> unknownKey(context[0])
        else -> when (context[1]) {
          "set" -> {
            try {
              stage.addChange(key, key.parse(context[2]))
            } catch (e: Exception) {
              textService.builder()
                .append(pluginInfo.prefix)
                .red().append("Error: ", e.message ?: "")
                .build()
            }
          }
          "info", "unset", "unstage" -> textService.builder()
            .append(pluginInfo.prefix)
            .red().append("Too many args for ${context[1]}! ", usage)
            .build()
          else -> usage
        }
      }
      else -> usage
    }, source)
  }

  override fun suggest(source: TCommandSource, context: Array<String>): List<String> {
    val stage = registryEditRootCommand.stages[userService.getUUIDSafe(source)] ?: return listOf()
    return when (context.size) {
      1 -> Keys.getAll(stage.envName).keys.stream().filter { it.startsWith(context[0]) }.toList()
      2 -> keyActions.stream().filter { it.startsWith(context[1]) }.toList()
      else -> listOf()
    }
  }
}
