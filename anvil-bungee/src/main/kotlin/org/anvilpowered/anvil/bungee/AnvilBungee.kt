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
package org.anvilpowered.anvil.bungee

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.TypeLiteral
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Plugin
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl
import org.anvilpowered.anvil.bungee.listener.BungeePlayerListener
import org.anvilpowered.anvil.bungee.module.ApiBungeeModule
import org.anvilpowered.anvil.bungee.module.BungeeFallbackModule
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode
import org.anvilpowered.anvil.common.module.CommonModule

class AnvilBungee : Plugin() {
  private val inner: Inner

  init {
    val module: AbstractModule = object : AbstractModule() {
      override fun configure() {
        bind(Plugin::class.java).toInstance(this@AnvilBungee)
        bind(AnvilBungee::class.java).toInstance(this@AnvilBungee)
      }
    }
    inner = Inner(Guice.createInjector(module))
  }

  private inner class Inner @Inject constructor(injector: Injector) :
    AnvilImpl(injector, object : CommonModule<CommandSender>("plugins") {}) {
    override fun applyToBuilder(builder: Environment.Builder) {
      super.applyToBuilder(builder)
      builder.addEarlyServices(BungeePlayerListener::class.java) {
        proxy.pluginManager.registerListener(this@AnvilBungee, it)
      }
      builder.addEarlyServices(object :
        TypeLiteral<CommonAnvilCommandNode<ProxiedPlayer, ProxiedPlayer, CommandSender>>() {
      })
    }
  }

  override fun onEnable() = EnvironmentBuilderImpl.completeInitialization(ApiBungeeModule(), BungeeFallbackModule())
  override fun toString(): String = inner.toString()
}
