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
package org.anvilpowered.anvil.spigot.server

import com.google.inject.Inject
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.server.CommonLocationService
import org.bukkit.entity.Player
import org.spongepowered.math.vector.Vector3d
import java.util.UUID
import java.util.concurrent.CompletableFuture

class SpigotLocationService : CommonLocationService() {

    @Inject
    private lateinit var userService: UserService<Player, Player>

    override fun teleport(teleportingUserUUID: UUID, targetUserUUID: UUID): CompletableFuture<Boolean> {
        val teleporter = userService[teleportingUserUUID]
        val target = userService[targetUserUUID]
        return CompletableFuture.completedFuture(
            if (teleporter.isPresent && target.isPresent) {
                teleporter.get().teleport(target.get().location)
            } else false
        )
    }

    override fun getWorldName(userUUID: UUID): String? {
        return userService[userUUID].map { it.world }.map { it.name }.orElse(null)
    }

    override fun getWorldName(userName: String): String? {
        return userService[userName].map { it.world }.map { it.name }.orElse(null)
    }

    private fun extractCoords(player: Player): Vector3d {
        val pos = player.location
        return Vector3d(pos.x, pos.y, pos.z)
    }

    override fun getPosition(userUUID: UUID): Vector3d? {
        return userService[userUUID].map(::extractCoords).orElse(null)
    }

    override fun getPosition(userName: String): Vector3d? {
        return userService[userName].map(::extractCoords).orElse(null)
    }
}
