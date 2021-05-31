package org.anvilpowered.anvil.nukkit.util

import cn.nukkit.Server
import cn.nukkit.command.CommandSender
import com.google.inject.Inject
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import org.anvilpowered.anvil.api.Audiences
import org.anvilpowered.anvil.api.util.AudienceService
import org.anvilpowered.anvil.nukkit.adventure.NukkitAudiences
import java.lang.IllegalStateException

class NukkitAudienceService @Inject constructor(
  private val nukkitAudience: NukkitAudiences
) : AudienceService<CommandSender> {

  override fun create(key: Key, permission: String, subjects: Array<out CommandSender>) {
    val audience = mutableListOf<Audience>()
    for (subject in subjects) {
      audience.add(subject.asAudience)
    }
    Audiences.create(key, Audience.audience(audience))
  }

  override fun add(key: Key, subjects: List<CommandSender>) {
    for (subject in subjects) {
      Audiences.add(key, subject.asAudience)
    }
  }

  override fun add(key: Key, permission: String, subjects: List<CommandSender>) {
    for (subject in subjects) {
      if (subject.hasPermission(permission)) {
        Audiences.add(key, subject.asAudience)
      }
    }
  }

  override fun addToPossible(subject: CommandSender) {
    for (audience in Audiences.permissionMap) {
      if (subject.hasPermission(audience.value)) {
        Audiences.add(audience.key, subject.asAudience)
      }
    }

    for (audience in Audiences.conditionalMap) {
      if (Audiences.test(audience.key, subject)) {
        Audiences.add(audience.key, subject.asAudience)
      }
    }
  }

  override fun updateAll() {
    for (subject in Server.getInstance().onlinePlayers.values) {
      addToPossible(subject)
    }
  }

  override fun updateAudience(key: Key) {
    val permission =
      Audiences.permissionMap[key] ?: throw IllegalStateException("Permission not defined for ${key.value()}")
    for (subject in Server.getInstance().onlinePlayers.values) {
      if (subject.hasPermission(permission)) {
        Audiences.add(key, subject.asAudience)
      }
    }
  }

  private val CommandSender.asAudience get() = nukkitAudience.sender(this)
}
