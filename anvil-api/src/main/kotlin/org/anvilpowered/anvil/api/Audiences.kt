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
package org.anvilpowered.anvil.api

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import java.util.concurrent.locks.Condition
import java.util.function.Predicate

object Audiences {

  private val audienceMap: MutableMap<Key, Audience> = mutableMapOf()
  val conditionalMap: MutableMap<Key, (Any) -> Boolean?> = mutableMapOf()
  val permissionMap: MutableMap<Key, String> = mutableMapOf()

  fun create(key: Key, vararg audience: Audience) {
    if (audienceMap.containsKey(key)) {
      val existing = mutableListOf(audienceMap[key] ?: Audience.empty())
      for (a in audience) {
        existing.add(a)
      }
      audienceMap[key] = Audience.audience(existing)
      return
    }
    audienceMap[key] = Audience.audience(*audience)
  }

  fun create(key: Key, condition: (Any) -> Boolean?) {
    conditionalMap[key] = condition
  }

  fun test(key: Key, target: Any): Boolean {
    return conditionalMap[key]?.invoke(target) ?: false
  }

  fun create(key: Key, permission: String, vararg audience: Audience?) {
    audienceMap[key] = Audience.audience(*audience )
    permissionMap[key] = permission
    if (!audience.isNullOrEmpty()) {
      create(key, Audience.audience(*audience))
    }
  }

  fun add(key: Key, vararg audience: Audience) {
    if (!audienceMap.containsKey(key)) {
      create(key, *audience)
    } else {
      audienceMap[key] = Audience.audience(audienceMap[key], *audience)
    }
  }
}
