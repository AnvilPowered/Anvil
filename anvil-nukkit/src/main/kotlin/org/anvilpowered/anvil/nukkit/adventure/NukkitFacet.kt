package org.anvilpowered.anvil.nukkit.adventure

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import com.google.inject.Inject
import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.platform.facet.Facet
import net.kyori.adventure.platform.facet.FacetBase
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.util.TextService

sealed class NukkitFacet<T: CommandSender> constructor(viewerClass: Class<T>)
  : FacetBase<T>(viewerClass) {

  @Inject
  lateinit var textService: TextService<CommandSender>

  class ChatConsole : NukkitFacet<CommandSender>(CommandSender::class.java), Facet.Chat<CommandSender, String> {

    override fun isApplicable(viewer: CommandSender): Boolean {
      return super<NukkitFacet>.isApplicable(viewer)
    }

    override fun createMessage(viewer: CommandSender, message: Component): String {
      return textService.serializeAmpersand(message)
    }

    override fun sendMessage(viewer: CommandSender, source: Identity, message: String, type: MessageType) {
      viewer.sendMessage(message)
    }
  }

  abstract class Message : NukkitFacet<Player>(Player::class.java), Facet.Message<Player, String> {
    override fun createMessage(viewer: Player, message: Component): String {
      return textService.serializeAmpersand(message)
    }
  }

  class ChatPlayer : Message(), Facet.Chat<Player, String> {
    override fun sendMessage(viewer: Player, source: Identity, message: String, type: MessageType) {
      viewer.sendMessage(message)
    }
  }
}
