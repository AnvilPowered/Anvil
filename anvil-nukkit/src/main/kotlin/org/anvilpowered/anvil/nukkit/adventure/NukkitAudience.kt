package org.anvilpowered.anvil.nukkit.adventure

import cn.nukkit.command.CommandSender
import net.kyori.adventure.platform.facet.Facet
import net.kyori.adventure.platform.facet.FacetAudience
import java.util.function.Supplier

class NukkitAudience constructor(viewers: Collection<CommandSender>) :
  FacetAudience<CommandSender>(viewers, null, Facet.of(
    Supplier {
      NukkitFacet.ChatConsole()
      NukkitFacet.ChatPlayer()
    }
  ), null, null, null, null, null, null) {

}

