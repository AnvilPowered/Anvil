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
package org.anvilpowered.anvil.spigot.command

import com.google.inject.Inject
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.common.command.CommonSimpleCommandService
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin

class SpigotSimpleCommandService : CommonSimpleCommandService<CommandSender>() {

  @Inject(optional = true)
  private lateinit var plugin: JavaPlugin

  private inner class PlatformExecutor(
    private val delegate: SimpleCommand<CommandSender>,
  ) : TabExecutor {
    override fun onCommand(source: CommandSender, command: Command, alias: String, context: Array<String>): Boolean {
      if (delegate.canExecute(source)) {
        delegate.execute(source, context)
      } else {
        source.sendNoPermission()
      }
      return true
    }

    override fun onTabComplete(source: CommandSender, command: Command, alias: String, context: Array<String>): List<String> {
      return delegate.suggest(source, context)
    }
  }

  override fun register(mapping: CommandMapping<out SimpleCommand<CommandSender>>) {
    check(this::plugin.isInitialized) { "org.bukkit.plugin.java.JavaPlugin not bound" }
    val pluginCommand = checkNotNull(plugin.getCommand(mapping.name)) { "Alias ${mapping.name} not defined in plugin.yml" }
    pluginCommand.setExecutor(PlatformExecutor(mapping.command))
  }
}
