package org.anvilpowered.anvil.nukkit.adventure

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.CommandSender
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerLoginEvent
import cn.nukkit.event.player.PlayerQuitEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.platform.facet.FacetAudienceProvider
import java.util.Collections
import java.util.Locale
import java.util.UUID

class NukkitAudiencesImpl : FacetAudienceProvider<CommandSender, NukkitAudience>(), NukkitAudiences, Listener {

  init {
    addViewer(Server.getInstance().consoleSender)
    changeViewer(Server.getInstance().consoleSender, Locale.getDefault())

    for (player in Server.getInstance().onlinePlayers.values) {
      this.addViewer(player)
    }
  }

  override fun sender(sender: CommandSender): Audience {
    if (sender is Player) {
      return this.player(sender.uniqueId)
    } else if (isConsole(sender)) {
      return console()
    }
    return createAudience(Collections.singletonList(sender))
  }

  override fun hasId(viewer: CommandSender): UUID? {
    if (viewer is Player) {
      return viewer.uniqueId
    }
    return null
  }
  override fun isConsole(viewer: CommandSender): Boolean {
    return Server.getInstance().consoleSender.equals(viewer)
  }

  override fun player(player: Player): Audience = player(player.uniqueId)
  override fun hasPermission(viewer: CommandSender, permission: String): Boolean = viewer.hasPermission(permission)
  override fun isInWorld(viewer: CommandSender, world: Key): Boolean = true
  override fun isOnServer(viewer: CommandSender, server: String): Boolean = true

  override fun createAudience(viewers: MutableCollection<CommandSender>): NukkitAudience {
    return NukkitAudience(viewers)
  }

  @EventHandler
  fun onLogin(event: PlayerLoginEvent) {
    this.addViewer(event.player)
  }

  @EventHandler
  fun onDisconnect(event: PlayerQuitEvent) {
    this.removeViewer(event.player)
  }
}
