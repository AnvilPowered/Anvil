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
package org.anvilpowered.anvil.sponge7.util

import com.google.inject.Inject
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.platform.spongeapi.SpongeAudiences
import org.anvilpowered.anvil.api.Audiences
import org.anvilpowered.anvil.api.util.AudienceService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import java.lang.IllegalStateException

class Sponge7AudienceService @Inject constructor(
  private val spongeAudience: SpongeAudiences
) : AudienceService<CommandSource>{

  override fun create(key: Key, permission: String, subjects: Array<out CommandSource>) {
    val audience = mutableListOf<Audience>()
    for (subject in subjects) {
      audience.add(subject.asAudience)
    }
    Audiences.create(key, permission, Audience.audience(audience))
  }

  override fun add(key: Key, subjects: List<CommandSource>) {
    for (subject in subjects) {
      Audiences.add(key, subject.asAudience)
    }
  }

  override fun add(key: Key, permission: String, subjects: List<CommandSource>) {
    for (subject in subjects) {
      if (subject.hasPermission(permission)) {
        Audiences.add(key, subject.asAudience)
      }
    }
  }

  override fun addToPossible(subject: CommandSource) {
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
    for (subject in Sponge.getServer().onlinePlayers) {
      addToPossible(subject)
    }
  }

  override fun updateAudience(key: Key) {
    val permission =
      Audiences.permissionMap[key] ?: throw IllegalStateException("Permission not defined for ${key.value()}")
    for (subject in Sponge.getServer().onlinePlayers) {
      if (subject.hasPermission(permission)) {
        Audiences.add(key, subject.asAudience)
      }
    }
  }

  private val CommandSource.asAudience get() = spongeAudience.receiver(this)
}
