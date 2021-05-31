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

package org.anvilpowered.anvil.nukkit.command

import cn.nukkit.Server
import cn.nukkit.command.CommandSender
import cn.nukkit.command.PluginCommand
import cn.nukkit.plugin.Plugin
import com.google.inject.Inject
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.common.command.CommonSimpleCommandService

class NukkitSimpleCommandService : CommonSimpleCommandService<CommandSender>() {

  @Inject(optional = true)
  private lateinit var plugin: Plugin

  private inner class PlatformCommand(
    primaryAlias: String,
    plugin: Plugin,
    private val delegate: SimpleCommand<CommandSender>,
  ) : PluginCommand<Plugin>(primaryAlias, plugin) {
    override fun execute(source: CommandSender, alias: String, context: Array<String>): Boolean {
      if (delegate.canExecute(source)) {
        delegate.execute(source, context)
      } else {
        source.sendNoPermission()
      }
      return true
    }

    override fun testPermissionSilent(source: CommandSender): Boolean = delegate.canExecute(source)
  }

  override fun register(mapping: CommandMapping<out SimpleCommand<CommandSender>>) {
    check(this::plugin.isInitialized) { "cn.nukkit.plugin.PluginBase not bound" }
    Server.getInstance().commandMap.register(pluginInfo.id, PlatformCommand(mapping.name, plugin, mapping.command))
  }
}
