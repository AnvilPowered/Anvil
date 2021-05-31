/*
 *   Anvil - AnvilPowered
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
package org.anvilpowered.anvil.nukkit

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.CommandSender
import cn.nukkit.plugin.Plugin
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Logger
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Provider
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode
import org.anvilpowered.anvil.common.module.CommonModule
import org.anvilpowered.anvil.nukkit.adventure.NukkitAudiencesImpl
import org.anvilpowered.anvil.nukkit.listener.NukkitPlayerListener
import org.anvilpowered.anvil.nukkit.module.ApiNukkitModule
import org.anvilpowered.anvil.nukkit.module.NukkitFallbackModule

class AnvilNukkit : PluginBase() {
  private val inner: Inner

  init {
    val module: AbstractModule = object : AbstractModule() {
      override fun configure() {
        bind(Plugin::class.java).toInstance(this@AnvilNukkit)
        bind(AnvilNukkit::class.java).toInstance(this@AnvilNukkit)
        bind(Logger::class.java).toProvider(Provider<Logger> { logger })
      }
    }
    val injector = Guice.createInjector(module)
    inner = Inner(injector)
  }

  private inner class Inner @Inject constructor(injector: Injector) :
    AnvilImpl(injector, object : CommonModule<CommandSender>("plugins") {}) {
    override fun applyToBuilder(builder: Environment.Builder) {
      super.applyToBuilder(builder)
      builder.addEarlyServices(object : TypeLiteral<CommonAnvilCommandNode<Player, Player, CommandSender>>() {})
    }
  }

  override fun onEnable() {
    EnvironmentBuilderImpl.completeInitialization(ApiNukkitModule(), NukkitFallbackModule())
    Server.getInstance().pluginManager.registerEvents(
      AnvilImpl.getEnvironment().injector.getInstance(NukkitPlayerListener::class.java),
      this
    )
    Server.getInstance().pluginManager.registerEvents(
      AnvilImpl.getEnvironment().injector.getInstance(NukkitAudiencesImpl::class.java),
      this
    )
  }

  override fun toString(): String = inner.toString()
}
