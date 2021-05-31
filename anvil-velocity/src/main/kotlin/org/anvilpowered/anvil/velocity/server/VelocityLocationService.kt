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

package org.anvilpowered.anvil.velocity.server

import com.google.inject.Inject
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.server.CommonLocationService
import java.util.Optional
import java.util.UUID
import kotlin.streams.toList

class VelocityLocationService @Inject constructor(
    private val proxyServer: ProxyServer,
    private val userService: UserService<Player, Player>
) : CommonLocationService() {

    private fun Optional<Player>.getServer(): Optional<VelocityBackendServer> {
        return flatMap { it.currentServer }.map { VelocityBackendServer(it.server, userService) }
    }

    override fun getServer(userUUID: UUID): VelocityBackendServer? = userService.getPlayer(userUUID).getServer().orElse(null)
    override fun getServer(userName: String): VelocityBackendServer? = userService.getPlayer(userName).getServer().orElse(null)
    override fun getServerForName(serverName: String): VelocityBackendServer? {
        return proxyServer.getServer(serverName).map { VelocityBackendServer(it, userService) }.orElse(null)
    }

    override fun getServers(): List<VelocityBackendServer> {
        return proxyServer.allServers.stream().map { VelocityBackendServer(it, userService) }.toList()
    }
}
