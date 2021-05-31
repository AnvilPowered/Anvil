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
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.registry.Registry

class CommonRegistryEditCommandNode<TUser, TPlayer, TCommandSource> @Inject constructor(
  private val commandService: SimpleCommandService<TCommandSource>,
  registry: Registry,
  private val registryEditCancelCommand: CommonRegistryEditCancelCommand<TUser, TPlayer, TCommandSource>,
  private val registryEditCommitCommand: CommonRegistryEditCommitCommand<TUser, TPlayer, TCommandSource>,
  private val registryEditKeyCommand: CommonRegistryEditKeyCommand<TUser, TPlayer, TCommandSource>,
  private val registryEditSelectCommand: CommonRegistryEditSelectCommand<TUser, TPlayer, TCommandSource>,
  private val registryEditStartCommand: CommonRegistryEditStartCommand<TUser, TPlayer, TCommandSource>,
) {

  companion object {
    val CANCEL_ALIAS = listOf("cancel")
    val COMMIT_ALIAS = listOf("commit")
    val KEY_ALIAS = listOf("key")
    val SELECT_ALIAS = listOf("select")
    val START_ALIAS = listOf("start")
    val HELP_ALIAS = listOf("help")

    val REGEDIT_ALIAS = listOf("regedit")

    val HELP_USAGE: String = "/$anvilAlias regedit help"
  }

  private var alreadyLoaded = false

  lateinit var regeditMapping: CommandMapping<SimpleCommand<TCommandSource>>
    private set

  init {
    registry.whenLoaded {
      if (alreadyLoaded) {
        return@whenLoaded
      }
      loadCommands()
      alreadyLoaded = true
    }.order(-10).register() // has to load before main node
  }

  private fun loadCommands() {
    val subCommands = listOf(
      commandService.mapTerminal(CANCEL_ALIAS, registryEditCancelCommand),
      commandService.mapTerminal(COMMIT_ALIAS, registryEditCommitCommand),
      commandService.mapTerminal(KEY_ALIAS, registryEditKeyCommand),
      commandService.mapTerminal(SELECT_ALIAS, registryEditSelectCommand),
      commandService.mapTerminal(START_ALIAS, registryEditStartCommand),
      commandService.mapTerminal(HELP_ALIAS, commandService.generateHelp { regeditMapping.subCommands }),
    )
    regeditMapping = commandService.mapRouting(
      REGEDIT_ALIAS,
      commandService.generateRoot(HELP_USAGE),
      subCommands,
      false
    )
  }
}
