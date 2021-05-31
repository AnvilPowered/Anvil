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
package org.anvilpowered.anvil.sponge8.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import org.anvilpowered.anvil.api.Audiences
import org.anvilpowered.anvil.api.util.AudienceService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandCause

class Sponge8AudienceService : AudienceService<CommandCause> {

  override fun create(key: Key, permission: String, subjects: Array<out CommandCause>) {
    val audience = mutableListOf<Audience>()
    for (subject in subjects) {
      audience.add(subject.audience())
    }
    Audiences.create(key, permission, Audience.audience(audience))
  }

  override fun add(key: Key, subjects: List<CommandCause>) {
    for (subject in subjects) {
      Audiences.add(key, subject.audience())
    }
  }

  override fun add(key: Key, permission: String, subjects: List<CommandCause>) {
    for (subject in subjects) {
      if (subject.hasPermission(permission)) {
        Audiences.add(key, subject.audience())
      }
    }
  }

  override fun addToPossible(subject: CommandCause) {
    for (audience in Audiences.permissionMap) {
      if (subject.hasPermission(audience.value)) {
        Audiences.add(audience.key, subject.audience())
      }
    }

    for (audience in Audiences.conditionalMap) {
      if (Audiences.test(audience.key, subject)) {
        Audiences.add(audience.key, subject.audience())
      }
    }
  }

  override fun updateAll() {
    for (subject in Sponge.server().onlinePlayers()) {
      // Boilerplate because the player is not a CommandCause (only on sponge v8)
      for (audience in Audiences.permissionMap) {
        if (subject.hasPermission(audience.value)) {
          Audiences.add(audience.key, subject)
        }
      }
    }
  }

  override fun updateAudience(key: Key) {
    val permission =
      Audiences.permissionMap[key] ?: throw IllegalStateException("Permission not defined for ${key.value()}")
    for (subject in Sponge.server().onlinePlayers()) {
      if (subject.hasPermission(permission)) {
        Audiences.add(key, subject)
      }
    }
  }
}
