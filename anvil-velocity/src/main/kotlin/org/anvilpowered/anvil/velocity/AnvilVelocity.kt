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
package org.anvilpowered.anvil.velocity

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.TypeLiteral
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode
import org.anvilpowered.anvil.common.module.CommonModule
import org.anvilpowered.anvil.common.plugin.AnvilPluginInfo
import org.anvilpowered.anvil.velocity.listener.VelocityPlayerListener
import org.anvilpowered.anvil.velocity.module.ApiVelocityModule
import org.anvilpowered.anvil.velocity.module.VelocityFallbackModule

@Plugin(
  id = AnvilPluginInfo.id,
  name = AnvilPluginInfo.name,
  version = AnvilPluginInfo.version,
  description = AnvilPluginInfo.description,
  url = AnvilPluginInfo.url,
  authors = [AnvilPluginInfo.organizationName]
)
class AnvilVelocity @Inject constructor(injector: Injector) :
  AnvilImpl(injector, object : CommonModule<CommandSource>("plugins") {}) {

  @Inject
  private lateinit var proxyServer: ProxyServer

  @Subscribe(order = PostOrder.EARLY)
  fun onInit(event: ProxyInitializeEvent) {
    EnvironmentBuilderImpl.completeInitialization(ApiVelocityModule(), VelocityFallbackModule())
    proxyServer.eventManager.register(
      this,
      Anvil.environment.injector.getInstance(VelocityPlayerListener::class.java)
    )
  }

  override fun applyToBuilder(builder: Environment.Builder) {
    super.applyToBuilder(builder)
    builder.addEarlyServices(object : TypeLiteral<CommonAnvilCommandNode<Player, Player, CommandSource>>() {})
  }
}
