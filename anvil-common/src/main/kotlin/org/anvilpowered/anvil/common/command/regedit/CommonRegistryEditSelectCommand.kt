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
import kotlin.streams.toList

class CommonRegistryEditSelectCommand<TUser, TPlayer, TCommandSource>
  : CommonRegistryEditBaseCommand<TUser, TPlayer, TCommandSource>() {

  @Inject
  private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TCommandSource>

  private val usage: Component by lazy {
    textService.builder()
      .append(pluginInfo.prefix)
      .red().append("Please provide exactly one argument!\nUsage: /$anvilAlias regedit select <reg>")
      .build()
  }

  override fun execute(source: TCommandSource, context: Array<String>) {
    val uuid = userService.getUUIDSafe(source)
    val stage = registryEditRootCommand.stages[uuid]
    textService.send(when {
      stage == null -> registryEditRootCommand.notInStage
      context.size == 1 -> stage.setRegistry(context[0])
      else -> usage
    }, source)
  }

  override fun suggest(source: TCommandSource, context: Array<String>): List<String> {
    val stage = registryEditRootCommand.stages[userService.getUUIDSafe(source)] ?: return listOf()
    return when (context.size) {
      1 -> stage.registries.keys.stream().filter { it.startsWith(context[0]) }.toList()
      else -> listOf()
    }
  }
}
