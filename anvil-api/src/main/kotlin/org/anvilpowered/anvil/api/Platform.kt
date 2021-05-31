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

import org.anvilpowered.anvil.api.misc.Named

interface Platform: Named {

  /**
   * Returns a lowercase identifier for the current platform
   *
   * Examples: "bungee", "spigot", "sponge", "velocity"
   *
   * @return A lowercase identifier for the current platform
   */
  override val name: String

  /**
   * @return The platform's version as a string. E.g. "7.3.0"
   */
  fun getVersionString(): String?

  /**
   * @return Whether the current platform is a proxy like bungee or velocity
   */
  fun isProxy(): Boolean
}
