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
import net.kyori.adventure.key.Key
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.command.regedit.CommonRegistryEditCommandNode

class CommonAnvilCommandNode<TUser, TPlayer, TCommandSource> @Inject constructor(
  private val commandService: SimpleCommandService<TCommandSource>,
  private val pluginsCommand: CommonAnvilPluginsCommand<TCommandSource>,
  private val reloadCommand: CommonAnvilReloadCommand<TCommandSource>,
  private val callbackCommand: CommonCallbackCommand<TCommandSource>,
  private val regeditNode: CommonRegistryEditCommandNode<TUser, TPlayer, TCommandSource>,
  registry: Registry,
  private val textService: TextService<TCommandSource>,
  private val userService: UserService<TUser, TPlayer>
) {

  companion object {
    val ALIAS: String by lazy {
      when (Anvil.getPlatform().name) {
        "bungee" -> "anvilb"
        "velocity" -> "anvilv"
        else -> "anvil"
      }
    }

    val CALLBACK_ALIAS = listOf("callback")
    val HELP_ALIAS = listOf("help")
    val PLUGINS_ALIAS = listOf("plugins")
    val RELOAD_ALIAS = listOf("reload")
    val VERSION_ALIAS = listOf("version")
    val ANVIL_ALIAS = listOf(ALIAS)
    val HELP_USAGE: String = "/$ALIAS help"
  }

  lateinit var anvilMapping: CommandMapping<SimpleCommand<TCommandSource>>
    private set
  lateinit var callbackMapping: CommandMapping<SimpleCommand<TCommandSource>>
    private set
  private var alreadyLoaded = false

  init {
    registry.whenLoaded {
      if (alreadyLoaded) {
        return@whenLoaded
      }
      loadCommands()
      alreadyLoaded = true
    }.register()
  }

  private fun loadCommands() {
    val subCommands = listOf(
      commandService.mapTerminal(HELP_ALIAS, commandService.generateHelp { anvilMapping.subCommands }),
      commandService.mapTerminal(PLUGINS_ALIAS, pluginsCommand),
      regeditNode.regeditMapping,
      commandService.mapTerminal(RELOAD_ALIAS, reloadCommand),
      commandService.mapTerminal(VERSION_ALIAS, commandService.generateVersion(HELP_USAGE)),
    )
    anvilMapping = commandService.mapRouting(
      ANVIL_ALIAS,
      commandService.generateRoot(HELP_USAGE),
      subCommands,
      false
    )
    commandService.register(anvilMapping)
    // we'll use sponge's callback command on sponge
    if (Anvil.getPlatform().name != "sponge") {
      callbackMapping = commandService.mapTerminal(CALLBACK_ALIAS, callbackCommand)
      commandService.register(callbackMapping)
    }
  }
}
