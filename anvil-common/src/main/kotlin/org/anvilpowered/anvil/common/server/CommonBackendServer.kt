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

package org.anvilpowered.anvil.common.server

import org.anvilpowered.anvil.api.server.BackendServer
import org.anvilpowered.anvil.api.util.UserService
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture

abstract class CommonBackendServer<TUser : Any, TPlayer : Any>(
  val userService: UserService<TUser, TPlayer>,
) : BackendServer {
  abstract fun Optional<TPlayer>.connect(): CompletableFuture<Boolean>
  override fun connect(userUUID: UUID): CompletableFuture<Boolean> = userService.getPlayer(userUUID).connect()
  override fun connect(userName: String): CompletableFuture<Boolean> = userService.getPlayer(userName).connect()
}
