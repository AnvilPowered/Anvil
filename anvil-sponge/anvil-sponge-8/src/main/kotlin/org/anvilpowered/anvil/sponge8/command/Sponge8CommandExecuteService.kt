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
package org.anvilpowered.anvil.sponge8.command

import com.google.inject.Inject
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.spongepowered.api.Sponge
import org.spongepowered.api.scheduler.Task
import org.spongepowered.plugin.PluginContainer

class Sponge8CommandExecuteService : CommandExecuteService {

  @Inject
  private lateinit var pluginContainer: PluginContainer

  private fun executeDirect(command: String) {
    Sponge.server().commandManager().process(Sponge.systemSubject(), command)
  }

  override fun execute(command: String) {
    if (Sponge.server().onMainThread()) {
      executeDirect(command)
    } else {
      Sponge.asyncScheduler().submit(Task.builder().execute { _ -> executeDirect(command) }.plugin(pluginContainer).build())
    }
  }
}
