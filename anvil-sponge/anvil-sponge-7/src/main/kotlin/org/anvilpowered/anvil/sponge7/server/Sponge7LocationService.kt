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
package org.anvilpowered.anvil.sponge7.server

import com.google.inject.Inject
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.server.CommonLocationService
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.math.vector.Vector3d
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture

class Sponge7LocationService : CommonLocationService() {

  @Inject
  private lateinit var userService: UserService<User, Player>

  override fun teleport(teleportingUserUUID: UUID, targetUserUUID: UUID): CompletableFuture<Boolean> {
    val teleporter = userService[teleportingUserUUID]
    val target = userService[targetUserUUID]
    return CompletableFuture.completedFuture(
      if (teleporter.isPresent && target.isPresent) {
        target.get().worldUniqueId
          .filter { teleporter.get().setLocation(target.get().position, it) }
          .isPresent
      } else false
    )
  }

  private fun Optional<User>.getWorldName(): Optional<String> =
    flatMap { it.worldUniqueId }
      .flatMap { Sponge.getServer().getWorld(it) }
      .map { it.name }

  override fun getWorldName(userUUID: UUID): String = userService[userUUID].getWorldName().orElse(null)
  override fun getWorldName(userName: String): String = userService[userName].getWorldName().orElse(null)

  private fun Optional<User>.getPosition(): Optional<Vector3d> =
    map { it.position }.map { Vector3d.from(it.x, it.y, it.z) }

  override fun getPosition(userUUID: UUID): Vector3d? = userService[userUUID].getPosition().orElse(null)
  override fun getPosition(userName: String): Vector3d? = userService[userName].getPosition().orElse(null)
}
