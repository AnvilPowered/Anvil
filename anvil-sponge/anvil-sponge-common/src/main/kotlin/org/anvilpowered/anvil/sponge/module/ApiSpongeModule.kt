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

package org.anvilpowered.anvil.sponge.module

import org.anvilpowered.anvil.api.Platform
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.common.entity.EntityUtils
import org.anvilpowered.anvil.common.module.PlatformModule
import org.anvilpowered.anvil.sponge.entity.SpongeEntityUtils
import org.anvilpowered.anvil.sponge.util.SpongePermissionService

open class ApiSpongeModule(platform: Platform) : PlatformModule(platform) {
  override fun configure() {
    super.configure()
    bind(EntityUtils::class.java).to(SpongeEntityUtils::class.java)
    bind(PermissionService::class.java).to(SpongePermissionService::class.java)
  }
}
