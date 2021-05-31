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
package org.anvilpowered.anvil.velocity.listener

import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.PlayerChatEvent
import net.kyori.adventure.identity.Identity
import org.anvilpowered.anvil.api.coremember.CoreMemberManager
import org.anvilpowered.anvil.api.model.coremember.CoreMember
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.AudienceService
import org.anvilpowered.anvil.common.plugin.AnvilPluginMessages

class VelocityPlayerListener @Inject constructor(
  private val audienceService: AudienceService<CommandSource>,
  private val coreMemberManager: CoreMemberManager,
  private val pluginMessages: AnvilPluginMessages,
  private val registry: Registry
) {

  @Subscribe
  fun onPlayerJoin(event: LoginEvent) {
    if (registry.getOrDefault(Keys.PROXY_MODE)) {
      return
    }
    val player = event.player
    coreMemberManager.primaryComponent
      .getOneOrGenerateForUser(
        player.uniqueId,
        player.username,
        player.remoteAddress.hostString
      ).thenAcceptAsync { optionalMember ->
        if (!optionalMember.isPresent) {
          return@thenAcceptAsync
        }
        val member: CoreMember<*> = optionalMember.get()
        if (coreMemberManager.primaryComponent.checkBanned(member)) {
          player.disconnect(
            pluginMessages.getBanMessage(member.banReason, member.banEndUtc)
          )
        }
        audienceService.addToPossible(player)
      }.join()
  }

  @Subscribe
  fun onPlayerChat(event: PlayerChatEvent) {
    if (registry.getOrDefault(Keys.PROXY_MODE)) {
      return
    }
    val player = event.player
    coreMemberManager.primaryComponent
      .getOneForUser(
        player.uniqueId
      ).thenAcceptAsync { optionalMember ->
        if (!optionalMember.isPresent) {
          return@thenAcceptAsync
        }
        val member: CoreMember<*> = optionalMember.get()
        if (coreMemberManager.primaryComponent.checkMuted(member)) {
          event.result = PlayerChatEvent.ChatResult.denied()
          player.sendMessage(
            Identity.nil(),
            pluginMessages.getMuteMessage(member.muteReason, member.muteEndUtc)
          )
        }
      }.join()
  }
}
