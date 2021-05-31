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

import net.md_5.bungee.api.connection.ProxiedPlayer
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.server.CommonBackendServer
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.streams.toList
import net.md_5.bungee.api.config.ServerInfo as BServerInfo

class BungeeBackendServer(
    private val server: BServerInfo,
    userService: UserService<ProxiedPlayer, ProxiedPlayer>,
) : CommonBackendServer<ProxiedPlayer, ProxiedPlayer>(userService) {
    override val name: String = server.name
    override fun getVersion(): CompletableFuture<BungeeVersion> {
        val future = CompletableFuture<BungeeVersion>()
        server.ping { ping, _ -> future.complete(BungeeVersion(ping.version)) }
        return future
    }

    override fun Optional<ProxiedPlayer>.connect(): CompletableFuture<Boolean> {
        return if (isPresent) {
            val future = CompletableFuture<Boolean>()
            get().connect(server) { r, _ -> future.complete(r) }
            future
        } else {
            CompletableFuture.completedFuture(false)
        }
    }

    override fun connect(player: Any?): CompletableFuture<Boolean> = Optional.ofNullable(player as? ProxiedPlayer).connect()
    override fun getPlayerUUIDs(): List<UUID> = server.players.stream().map { it.uniqueId }.toList()
}
