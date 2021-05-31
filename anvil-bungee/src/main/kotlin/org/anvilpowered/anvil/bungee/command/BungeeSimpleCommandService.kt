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

package org.anvilpowered.anvil.bungee.command

import com.google.inject.Inject
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.common.command.CommonSimpleCommandService

class BungeeSimpleCommandService : CommonSimpleCommandService<CommandSender>() {

  @Inject(optional = true)
  private lateinit var plugin: Plugin

  private inner class PlatformCommand(
    private val delegate: SimpleCommand<CommandSender>,
    primaryAlias: String,
    vararg otherAliases: String
  ) : Command(primaryAlias, null, *otherAliases) {
    override fun execute(source: CommandSender, context: Array<String>) {
      if (delegate.canExecute(source)) {
        delegate.execute(source, context)
      } else {
        source.sendNoPermission()
      }
    }

    override fun hasPermission(source: CommandSender): Boolean = delegate.canExecute(source)
  }

  override fun register(mapping: CommandMapping<out SimpleCommand<CommandSender>>) {
    check(this::plugin.isInitialized) { "net.md_5.bungee.api.plugin.Plugin not bound" }
    ProxyServer.getInstance().pluginManager.registerCommand(
      plugin,
      PlatformCommand(mapping.command, mapping.name, *mapping.otherAliases.toTypedArray())
    )
  }
}
