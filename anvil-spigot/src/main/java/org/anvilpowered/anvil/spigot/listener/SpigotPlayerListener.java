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

package org.anvilpowered.anvil.spigot.listener;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.entity.RestrictionService;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.AudienceService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpigotPlayerListener implements Listener {

    @Inject
    private AudienceService<CommandSender> audienceService;

    @Inject
    private CoreMemberManager coreMemberManager;

    @Inject
    private Registry registry;

    @Inject
    private RestrictionService restrictionService;

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
                player.getAddress().getHostString()
            );
        audienceService.addToPossible(player);
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent event) {
        if (restrictionService.get(event.getPlayer().getUniqueId()).movement()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteraction(EntityInteractEvent event) {
        if (restrictionService.get(event.getEntity().getUniqueId()).interaction()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventory(InventoryInteractEvent event) {
        if (restrictionService.get(event.getWhoClicked().getUniqueId()).inventory()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommands(PlayerCommandPreprocessEvent event) {
        if (restrictionService.get(event.getPlayer().getUniqueId()).commands()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onReceiveDamage(EntityDamageEvent event) {
        if (restrictionService.get(event.getEntity().getUniqueId()).damage()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDealDamage(EntityDamageByEntityEvent event) {
        if (restrictionService.get(event.getDamager().getUniqueId()).damage()) {
            event.setCancelled(true);
        }
    }
}
