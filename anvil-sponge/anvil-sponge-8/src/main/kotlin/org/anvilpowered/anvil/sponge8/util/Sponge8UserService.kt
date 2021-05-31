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
package org.anvilpowered.anvil.sponge8.util

import org.anvilpowered.anvil.common.util.CommonUserService
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import java.util.Optional
import java.util.UUID
import kotlin.streams.asSequence

class Sponge8UserService : CommonUserService<User, ServerPlayer>(User::class.java) {
  override fun get(userName: String): Optional<User> = Sponge.server().userManager().find(userName)
  override fun get(userUUID: UUID): Optional<User> = Sponge.server().userManager().find(userUUID)
  override fun getPlayer(userName: String): Optional<ServerPlayer> = Sponge.server().player(userName)
  override fun getPlayer(userUUID: UUID): Optional<ServerPlayer> = Sponge.server().player(userUUID)
  override fun getPlayer(user: User): Optional<ServerPlayer> = user.player()

  override fun matchPlayerNames(startsWith: String): List<String> {
    return Sponge.server().userManager().streamOfMatches(startsWith).asSequence()
      .map { it.name().orElse(null) }.filter { it != null }.toList()
  }

  override fun getOnlinePlayers(): Collection<ServerPlayer> = Sponge.server().onlinePlayers()
  override fun getUUID(user: User): UUID = user.uniqueId()
  override fun getUserName(user: User): String = user.name()
}
