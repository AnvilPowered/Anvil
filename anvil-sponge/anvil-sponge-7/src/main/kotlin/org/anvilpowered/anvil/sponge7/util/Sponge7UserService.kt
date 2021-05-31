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
import org.anvilpowered.anvil.common.util.CommonUserService
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.user.UserStorageService
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

class Sponge7UserService @Inject constructor() : CommonUserService<User, Player>(User::class.java) {

  override fun get(userName: String): Optional<User> =
    Sponge.getServiceManager().provide(UserStorageService::class.java).flatMap { u: UserStorageService -> u[userName] }

  override fun get(userUUID: UUID): Optional<User> =
    Sponge.getServiceManager().provide(UserStorageService::class.java).flatMap { u: UserStorageService -> u[userUUID] }

  override fun getPlayer(userName: String): Optional<Player> = get(userName).flatMap { obj: User -> obj.player }
  override fun getPlayer(userUUID: UUID): Optional<Player> = get(userUUID).flatMap { obj: User -> obj.player }
  override fun getOnlinePlayers(): Collection<Player> = Sponge.getServer().onlinePlayers
  override fun getPlayer(user: User): Optional<Player> = user.player
  override fun getUUID(user: User): UUID = user.uniqueId
  override fun getUserName(user: User): String = user.name

  override fun matchPlayerNames(lastKnownName: String): List<String> {
    return Sponge.getServiceManager().provideUnchecked(UserStorageService::class.java).match(lastKnownName).stream()
      .map { obj: GameProfile -> obj.name }
      .filter { obj: Optional<String> -> obj.isPresent }
      .map { obj: Optional<String> -> obj.get() }
      .collect(Collectors.toList())
  }

  override fun getUUID(userName: String): CompletableFuture<Optional<UUID>> {
    val userUUID = getPlayer(userName).map { obj: Player -> obj.uniqueId }
    return if (userUUID.isPresent) {
      CompletableFuture.completedFuture(userUUID)
    } else super.getUUID(userName)
  }

  override fun getUserName(userUUID: UUID): CompletableFuture<Optional<String>> {
    val userName = getPlayer(userUUID).map { obj: Player -> obj.name }
    return if (userName.isPresent) {
      CompletableFuture.completedFuture(userName)
    } else super.getUserName(userUUID)
  }
}
