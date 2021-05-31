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
package org.anvilpowered.anvil.velocity.command

import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.common.command.CommonSimpleCommandService
import com.velocitypowered.api.command.SimpleCommand as VelocitySimpleCommand

class VelocitySimpleCommandService : CommonSimpleCommandService<CommandSource>() {

  @Inject
  private lateinit var proxyServer: ProxyServer

  private inner class PlatformCommand(
    private val delegate: SimpleCommand<CommandSource>,
  ) : VelocitySimpleCommand {

    override fun execute(invocation: VelocitySimpleCommand.Invocation) {
      delegate.execute(invocation.source(), invocation.arguments())
    }
  }

  override fun register(mapping: CommandMapping<out SimpleCommand<CommandSource>>) {
    proxyServer.commandManager.register(mapping.name, PlatformCommand(mapping.command), *mapping.otherAliases.toTypedArray())
  }
}
