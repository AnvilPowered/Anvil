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
package org.anvilpowered.anvil.spigot.module

import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.misc.bind
import org.anvilpowered.anvil.api.misc.to
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.api.util.AudienceService
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.entity.EntityUtils
import org.anvilpowered.anvil.common.module.JavaUtilLoggingAdapter
import org.anvilpowered.anvil.common.module.PlatformModule
import org.anvilpowered.anvil.common.util.CommonTextService
import org.anvilpowered.anvil.common.util.SendTextService
import org.anvilpowered.anvil.spigot.command.SpigotCommandExecuteService
import org.anvilpowered.anvil.spigot.command.SpigotSimpleCommandService
import org.anvilpowered.anvil.spigot.entity.SpigotEntityUtils
import org.anvilpowered.anvil.spigot.server.SpigotLocationService
import org.anvilpowered.anvil.spigot.util.SpigotAudienceService
import org.anvilpowered.anvil.spigot.util.SpigotKickService
import org.anvilpowered.anvil.spigot.util.SpigotPermissionService
import org.anvilpowered.anvil.spigot.util.SpigotSendTextService
import org.anvilpowered.anvil.spigot.util.SpigotUserService
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ApiSpigotModule : PlatformModule(
  PlatformImpl(
    "spigot",
    false,
    { Bukkit.getVersion() },
    JavaUtilLoggingAdapter::bindLogger,
  )
) {
  override fun configure() {
    super.configure()
    with(binder()) {
      bind<AudienceService<CommandSender>>().to<SpigotAudienceService>()
      bind<CommandExecuteService>().to<SpigotCommandExecuteService>()
      bind<EntityUtils>().to<SpigotEntityUtils>()
      bind<KickService>().to<SpigotKickService>()
      bind<LocationService>().to<SpigotLocationService>()
      bind<PermissionService>().to<SpigotPermissionService>()
      bind<SimpleCommandService<*>>().to<SpigotSimpleCommandService>()
      bind<SimpleCommandService<CommandSender>>().to<SpigotSimpleCommandService>()
      bind<SendTextService<*>>().to<SpigotSendTextService>()
      bind<SendTextService<CommandSender>>().to<SpigotSendTextService>()
      bind<TextService<*>>().to<CommonTextService<CommandSender>>()
      bind<TextService<CommandSender>>().to<CommonTextService<CommandSender>>()
      bind<UserService<Player, Player>>().to<SpigotUserService>()
    }
  }
}
