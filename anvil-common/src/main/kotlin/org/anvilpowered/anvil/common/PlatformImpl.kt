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
package org.anvilpowered.anvil.common

import com.google.common.base.MoreObjects
import com.google.inject.Binder
import com.google.inject.Injector
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.Platform

class PlatformImpl(
  override val name: String,
  private val isProxy: Boolean,
  private val versionFetcher: (Injector) -> String,
  private val loggerBinder: ((Environment, Binder) -> Unit)? = null,
) : Platform {

  private val versionStringKt: String by lazy { versionFetcher(Anvil.getEnvironment().injector) }

  fun bindLoggerOptionally(environment: Environment, binder: Binder) = loggerBinder?.let { it(environment, binder) }

  override fun getVersionString(): String = versionStringKt
  override fun isProxy(): Boolean = isProxy

  private val stringRepresentation: String by lazy {
    MoreObjects.toStringHelper(this)
      .add("name", name)
      .add("version", getVersionString())
      .add("isProxy", isProxy)
      .toString()
  }

  override fun toString(): String = stringRepresentation
}
