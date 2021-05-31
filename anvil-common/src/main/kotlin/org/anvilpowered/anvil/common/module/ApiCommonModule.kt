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
package org.anvilpowered.anvil.common.module

import org.anvilpowered.anvil.api.messaging.RedisService
import org.anvilpowered.anvil.api.misc.bind
import org.anvilpowered.anvil.api.misc.to
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.anvilpowered.anvil.common.messaging.CommonRedisService
import org.anvilpowered.anvil.common.util.CommonTimeFormatService

open class ApiCommonModule : com.google.inject.AbstractModule() {
  override fun configure() {
    with(binder()) {
      bind<TimeFormatService>().to<CommonTimeFormatService>()
      bind<RedisService>().to<CommonRedisService>()
    }
  }
}
