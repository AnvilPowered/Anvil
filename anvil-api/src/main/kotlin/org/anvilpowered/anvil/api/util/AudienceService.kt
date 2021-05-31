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
package org.anvilpowered.anvil.api.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import org.anvilpowered.anvil.api.Audiences

interface AudienceService<TCommandSource> {

  /**
   * Creates a new [Audience] with the given [permission]
   */
  fun create(key: Key, permission: String) {
    Audiences.create(key, permission)
  }

  /**
   * Creates a new [Audience] with the given [Key] and [condition]
   */
  fun create(key: Key, condition: (Any) -> Boolean?) {
    Audiences.create(key, condition)
  }

  fun create(key: Key, permission: String, subjects: Array<out TCommandSource>)

  /**
   * Adds the [subjects] to the [Audience] represented by the provided [Key]
   */
  fun add(key: Key, subjects: List<TCommandSource>)

  /**
   * Adds the [subjects] to the [Audience] represented by the provided [Key],
   * given they have the required [permission]
   */
  fun add(key: Key, permission: String, subjects: List<TCommandSource>)

  /**
   * Adds the provided [subject] to all possible [Audience]
   */
  fun addToPossible(subject: TCommandSource)

  /**
   * Ensures each [TCommandSource] present on the Server is present in
   * all possible [Audience]
   */
  fun updateAll()

  /**
   * Ensures each [TCommandSource] present on the server is present
   * in the [Audience] represented by the provided [Key]
   */
  fun updateAudience(key: Key)
}
