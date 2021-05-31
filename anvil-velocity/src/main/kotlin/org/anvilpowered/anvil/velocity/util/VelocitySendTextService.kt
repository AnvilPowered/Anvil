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

package org.anvilpowered.anvil.velocity.util

import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.common.util.SendTextService

class VelocitySendTextService @Inject constructor(
  proxyServer: ProxyServer,
) : SendTextService<CommandSource> {

  override fun send(text: Component, receiver: CommandSource) {
    receiver.sendMessage(Identity.nil(), text)
  }

  override val console: CommandSource = proxyServer.consoleCommandSource
}
