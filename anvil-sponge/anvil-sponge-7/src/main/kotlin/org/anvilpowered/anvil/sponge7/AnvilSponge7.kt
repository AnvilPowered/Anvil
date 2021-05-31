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
package org.anvilpowered.anvil.sponge7

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode
import org.anvilpowered.anvil.common.module.CommonModule
import org.anvilpowered.anvil.common.plugin.AnvilPluginInfo
import org.anvilpowered.anvil.sponge7.listener.Sponge7PlayerListener
import org.anvilpowered.anvil.sponge7.module.ApiSponge7Module
import org.anvilpowered.anvil.sponge7.module.Sponge7FallbackModule
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin

@Plugin(
  id = AnvilPluginInfo.id,
  name = AnvilPluginInfo.name,
  version = AnvilPluginInfo.version,
  description = AnvilPluginInfo.description,
  url = AnvilPluginInfo.url,
  authors = [AnvilPluginInfo.organizationName]
)
class AnvilSponge7 @Inject constructor(
  injector: Injector,
) : AnvilImpl(injector, object : CommonModule<CommandSource>("config") {}) {
  @Listener(order = Order.EARLY)
  fun onInit(event: GameInitializationEvent) {
    EnvironmentBuilderImpl.completeInitialization(ApiSponge7Module(), Sponge7FallbackModule())
    Sponge.getEventManager().registerListeners(
      this,
      Anvil.environment.injector.getInstance(Sponge7PlayerListener::class.java)
    )
  }

  override fun applyToBuilder(builder: Environment.Builder) {
    super.applyToBuilder(builder)
    builder.addEarlyServices(object : TypeLiteral<CommonAnvilCommandNode<User, Player, CommandSource>>() {})
  }
}
