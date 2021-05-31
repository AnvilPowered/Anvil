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
package org.anvilpowered.anvil.sponge7.listener

import com.google.inject.Inject
import org.anvilpowered.anvil.api.coremember.CoreMemberManager
import org.anvilpowered.anvil.api.entity.RestrictionService
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.action.InteractEvent
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.event.command.SendCommandEvent
import org.spongepowered.api.event.entity.DamageEntityEvent
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.filter.cause.Root
import org.spongepowered.api.event.item.inventory.TargetInventoryEvent
import org.spongepowered.api.event.network.ClientConnectionEvent

class Sponge7PlayerListener {

  @Inject
  private lateinit var coreMemberManager: CoreMemberManager

  @Inject
  private lateinit var registry: Registry

  @Inject
  private lateinit var restrictionService: RestrictionService

  @Listener
  fun onPlayerJoin(event: ClientConnectionEvent.Join) {
    if (registry.getOrDefault(Keys.PROXY_MODE)) {
      return
    }
    val player = event.targetEntity
    coreMemberManager.primaryComponent.getOneOrGenerateForUser(
      player.uniqueId,
      player.name,
      player.connection.address.hostString
    )
  }

  @Listener
  fun onMovement(event: MoveEntityEvent, @Getter("getTargetEntity") entity: Entity?) {
    if (restrictionService[entity].movement()) {
      event.isCancelled = true
    }
  }

  @Listener
  fun onInteraction(event: InteractEvent, @Root entity: Entity?) {
    if (restrictionService[entity].interaction()) {
      event.isCancelled = true
    }
  }

  @Listener
  fun onInventory(event: TargetInventoryEvent, @Root player: Player) {
    if (event is Cancellable && restrictionService[player.uniqueId].inventory()) {
      (event as Cancellable).isCancelled = true
    }
  }

  @Listener
  fun onCommands(event: SendCommandEvent, @Root player: Player) {
    if (restrictionService[player.uniqueId].commands()) {
      event.isCancelled = true
    }
  }

  @Listener
  fun onDamage(event: DamageEntityEvent, @Getter("getTargetEntity") target: Entity, @Root source: DamageSource) {
    if (restrictionService[target].damage()
      || (source is EntityDamageSource && restrictionService[source.source].damage())
    ) {
      event.isCancelled = true
    }
  }
}
