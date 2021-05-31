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

package org.anvilpowered.anvil.bungee.server

import com.google.inject.Inject
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.anvilpowered.anvil.api.server.BackendServer
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.server.CommonLocationService
import java.util.Optional
import java.util.UUID
import kotlin.streams.toList

class BungeeLocationService @Inject constructor(
    private val userService: UserService<ProxiedPlayer, ProxiedPlayer>
) : CommonLocationService() {

    private fun getServer(user: Optional<ProxiedPlayer>): BackendServer? {
        return user.map { it.server }.map { BungeeBackendServer(it.info, userService) }.orElse(null)
    }

    override fun getServer(userUUID: UUID): BackendServer? = getServer(userService.getPlayer(userUUID))
    override fun getServer(userName: String): BackendServer? = getServer(userService.getPlayer(userName))
    override fun getServerForName(serverName: String): BackendServer? {
        return BungeeBackendServer(ProxyServer.getInstance().getServerInfo(serverName) ?: return null, userService)
    }

    override fun getServers(): List<BungeeBackendServer> {
        return ProxyServer.getInstance().servers.values.stream().map { BungeeBackendServer(it, userService) }.toList()
    }
}
