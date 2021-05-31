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

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.server.CommonBackendServer
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.streams.toList

class VelocityBackendServer(
    private val server: RegisteredServer,
    userService: UserService<Player, Player>,
) : CommonBackendServer<Player, Player>(userService) {
    override val name: String = server.serverInfo.name
    override fun getVersion(): CompletableFuture<VelocityVersion> {
        return server.ping().thenApply { VelocityVersion(it.version) }
    }
    override fun Optional<Player>.connect(): CompletableFuture<Boolean> {
        return map { it.createConnectionRequest(server).connect().thenApply { c -> c.isSuccessful } }
            .orElse(CompletableFuture.completedFuture(false))
    }

    override fun connect(player: Any?): CompletableFuture<Boolean> = Optional.ofNullable(player as? Player).connect()
    override fun getPlayerUUIDs(): List<UUID> = server.playersConnected.stream().map { it.uniqueId }.toList()
}
