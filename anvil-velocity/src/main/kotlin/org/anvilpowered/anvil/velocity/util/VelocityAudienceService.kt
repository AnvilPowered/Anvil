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
package org.anvilpowered.anvil.velocity.util

import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.key.Key
import org.anvilpowered.anvil.api.Audiences
import org.anvilpowered.anvil.api.util.AudienceService

class VelocityAudienceService @Inject constructor(
  private val proxyServer: ProxyServer
) : AudienceService<CommandSource> {

  override fun create(key: Key, permission: String, subjects: Array<out CommandSource>) {
    Audiences.create(key, permission, *subjects)
  }

  override fun add(key: Key, subjects: List<CommandSource>) {
    Audiences.create(key, *subjects.toTypedArray())
  }

  override fun add(key: Key, permission: String, subjects: List<CommandSource>) {
    for (subject in subjects) {
      if (subject.hasPermission(permission)) {
        Audiences.add(key, subject)
      }
    }
  }

  override fun addToPossible(subject: CommandSource) {
    for (audience in Audiences.permissionMap) {
      if (subject.hasPermission(audience.value)) {
        Audiences.add(audience.key, subject)
      }
    }

    for (audience in Audiences.conditionalMap) {
      if (Audiences.test(audience.key, subject)) {
        Audiences.add(audience.key, subject)
      }
    }
  }

  override fun updateAll() {
    for (subject in proxyServer.allPlayers) {
      addToPossible(subject)
    }
  }

  override fun updateAudience(key: Key) {
    val permission =
      Audiences.permissionMap[key] ?: throw IllegalStateException("Permission not defined for ${key.value()}")
    for (subject in proxyServer.allPlayers) {
      if (subject.hasPermission(permission)) {
        Audiences.add(key, subject)
      }
    }
  }
}
