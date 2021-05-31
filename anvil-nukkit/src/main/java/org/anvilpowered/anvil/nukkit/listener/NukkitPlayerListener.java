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

package org.anvilpowered.anvil.nukkit.listener;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import com.google.inject.Inject;
import org.anvilpowered.anvil.api.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.model.coremember.CoreMember;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.common.plugin.AnvilPluginMessages;

public class NukkitPlayerListener implements Listener {

    @Inject
    private CoreMemberManager coreMemberManager;

    @Inject
    private AnvilPluginMessages pluginMessages;

    @Inject
    private TextService<CommandSender> textService;

    @Inject
    private Registry registry;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (registry.getOrDefault(Keys.PROXY_MODE)) {
            return;
        }
        Player player = event.getPlayer();
        coreMemberManager.getPrimaryComponent()
            .getOneOrGenerateForUser(
                player.getUniqueId(),
                player.getName(),
                player.getAddress()
            ).thenAcceptAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return;
            }
            CoreMember<?> member = optionalMember.get();
            if (coreMemberManager.getPrimaryComponent().checkBanned(member)) {
                player.kick(textService.serializeAmpersand(
                    pluginMessages.getBanMessage(member.getBanReason(), member.getBanEndUtc())),
                    false
                );
            }
        }).join();
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (registry.getOrDefault(Keys.PROXY_MODE)) {
            return;
        }
        Player player = event.getPlayer();
        coreMemberManager.getPrimaryComponent()
            .getOneForUser(
                player.getUniqueId()
            ).thenAcceptAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return;
            }
            CoreMember<?> member = optionalMember.get();
            if (coreMemberManager.getPrimaryComponent().checkMuted(member)) {
                event.setCancelled();
                player.sendMessage(textService.serializeAmpersand(
                    pluginMessages.getMuteMessage(member.getMuteReason(), member.getMuteEndUtc()))
                );
            }
        }).join();
    }
}
