package org.anvilpowered.anvil.nukkit.adventure

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.AudienceProvider

interface NukkitAudiences : AudienceProvider {

  fun sender(sender: CommandSender): Audience

  fun player(player: Player): Audience
}
