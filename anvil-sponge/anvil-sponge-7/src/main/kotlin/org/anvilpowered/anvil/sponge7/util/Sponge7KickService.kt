/*
 * Anvil - AnvilPowered
 *   Copyright (C) 2020
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package org.anvilpowered.anvil.sponge7.util

import com.google.inject.Inject
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.UserService
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.text.Text
import java.util.Optional
import java.util.UUID

class Sponge7KickService : KickService {

  @Inject
  private lateinit var userService: UserService<User, Player>

  private fun Optional<User>.kick(reason: Any? = null) {
    orElse(null)?.apply {
      reason?.let { kick((reason as? Text ?: Text.of(reason.toString()))) }
        ?: kick(Text.of("You have been kicked"))
    }
  }

  override fun kick(userUUID: UUID, reason: Any) = userService[userUUID].kick(reason)
  override fun kick(userName: String, reason: Any) = userService[userName].kick(reason)
  override fun kick(userUUID: UUID) = userService[userUUID].kick()
  override fun kick(userName: String) = userService[userName].kick()
}
