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
package org.anvilpowered.anvil.spigot

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl
import org.anvilpowered.anvil.api.registry.ConfigurationService
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode
import org.anvilpowered.anvil.common.module.CommonModule
import org.anvilpowered.anvil.spigot.listener.SpigotPlayerListener
import org.anvilpowered.anvil.spigot.module.ApiSpigotModule
import org.anvilpowered.anvil.spigot.module.SpigotFallbackModule
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.InvocationTargetException

class AnvilSpigot : JavaPlugin() {
  private val inner: Inner

  init {
    val module: AbstractModule = object : AbstractModule() {
      override fun configure() {
        bind(JavaPlugin::class.java).toInstance(this@AnvilSpigot)
        bind(AnvilSpigot::class.java).toInstance(this@AnvilSpigot)
      }
    }
    val injector = Guice.createInjector(module)
    inner = Inner(injector)
  }

  private inner class Inner(injector: Injector) :
    AnvilImpl(injector, object : CommonModule<CommandSender>("plugins") {}) {
    override fun applyToBuilder(builder: Environment.Builder) {
      super.applyToBuilder(builder)
      builder.addEarlyServices(SpigotPlayerListener::class.java) {
        Bukkit.getPluginManager().registerEvents(it, this@AnvilSpigot)
      }
      builder.addEarlyServices(object :
        TypeLiteral<CommonAnvilCommandNode<Player, Player, CommandSender>>() {
      })
    }

    override fun whenLoaded(environment: Environment) {
      // Attempt to autodetect proxy mode (contributed by mikroskeem)
      var serverProxyMode = false
      // Check for Paper-specific Velocity mode first
      try {
        val getPaperConfigMethod = Server.Spigot::class.java.getMethod("getPaperConfig")
        val paperConfig = getPaperConfigMethod.invoke(Bukkit.spigot()) as YamlConfiguration
        serverProxyMode = paperConfig.getBoolean("settings.velocity-support.enabled", false)
      } catch (e: Exception) {
        when (e) {
          is IllegalAccessException,
          is InvocationTargetException,
          -> throw IllegalStateException("Unable to get Paper configuration", e)
          // else ignored (e.g. NoSuchMethodException)
        }
      }
      // Check BungeeCord mode if Velocity setting was not enabled or server implementation is not Paper
      if (!serverProxyMode) {
        serverProxyMode = Bukkit.spigot().config.getBoolean("settings.bungeecord", false)
      }
      val configurationService = environment.injector.getInstance(ConfigurationService::class.java)
      val anvilProxyMode = configurationService.getOrDefault(Keys.PROXY_MODE)
      if (serverProxyMode && !anvilProxyMode) {
        getLogger().error(
          """
          It looks like you are running this server behind a proxy.
          If this is the case, set server.proxyMode=true in the anvil config.
          """.trimIndent()
        )
      } else if (anvilProxyMode && !serverProxyMode) {
        getLogger().error(
          """
          It looks like you are not running this server behind a proxy.
          If this is the case, set server.proxyMode=false in the anvil config
          """.trimIndent()
        )
      }
    }
  }

  override fun onEnable() {
    Bukkit.getPluginManager().registerEvents(object : Listener {
      @EventHandler
      fun onLoad(event: ServerLoadEvent) {
        EnvironmentBuilderImpl.completeInitialization(ApiSpigotModule(), SpigotFallbackModule())
      }
    }, this)
  }

  override fun toString(): String = inner.toString()
}
