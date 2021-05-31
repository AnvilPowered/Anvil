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

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.identity.Identified
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.common.util.SendTextService
import org.spongepowered.api.Sponge
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.event.Cause
import org.spongepowered.api.event.EventContext
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.world.server.ServerLocation
import org.spongepowered.math.vector.Vector3d
import java.util.Optional

class Sponge8SendTextService : SendTextService<CommandCause> {

  override fun send(text: Component, receiver: CommandCause) {
    receiver.sendMessage(Identity.nil(), text)
  }

  override val console: CommandCause =
    // yes, this is a hack and fundamentally flawed
    // yes, there will be a permanent, correct (and API breaking) fix in the future
    object : CommandCause {
      override fun subject(): Subject = Sponge.systemSubject()
      override fun cause(): Cause = Cause.of(EventContext.empty(), subject())
      override fun audience(): Audience = Sponge.systemSubject()
      override fun location(): Optional<ServerLocation> = Optional.empty()
      override fun rotation(): Optional<Vector3d> = Optional.empty()
      override fun targetBlock(): Optional<BlockSnapshot> = Optional.empty()
      override fun sendMessage(source: Identified, message: Component) = audience().sendMessage(source, message)
      override fun sendMessage(source: Identity, message: Component) = audience().sendMessage(source, message)
  }
}
