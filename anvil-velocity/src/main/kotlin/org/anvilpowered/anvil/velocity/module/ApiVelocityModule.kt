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
package org.anvilpowered.anvil.velocity.module

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
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
import org.anvilpowered.anvil.common.command.CommonCallbackCommand
import org.anvilpowered.anvil.common.module.PlatformModule
import org.anvilpowered.anvil.common.util.CommonTextService
import org.anvilpowered.anvil.common.util.SendTextService
import org.anvilpowered.anvil.velocity.command.VelocityCommandExecuteService
import org.anvilpowered.anvil.velocity.command.VelocitySimpleCommandService
import org.anvilpowered.anvil.velocity.server.VelocityLocationService
import org.anvilpowered.anvil.velocity.util.VelocityAudienceService
import org.anvilpowered.anvil.velocity.util.VelocityKickService
import org.anvilpowered.anvil.velocity.util.VelocityPermissionService
import org.anvilpowered.anvil.velocity.util.VelocitySendTextService
import org.anvilpowered.anvil.velocity.util.VelocityUserService

class ApiVelocityModule : PlatformModule(
  PlatformImpl(
    "velocity",
    true,
    { it.getInstance(ProxyServer::class.java).version.version }
  )
) {
  override fun configure() {
    super.configure()
    with(binder()) {
      bind<AudienceService<CommandSource>>().to<VelocityAudienceService>()
      bind<AudienceService<*>>().to<VelocityAudienceService>()
      bind<CommonCallbackCommand<CommandSource>>()
      bind<CommandExecuteService>().to<VelocityCommandExecuteService>()
      bind<KickService>().to<VelocityKickService>()
      bind<LocationService>().to<VelocityLocationService>()
      bind<PermissionService>().to<VelocityPermissionService>()
      bind<SimpleCommandService<CommandSource>>().to<VelocitySimpleCommandService>()
      bind<SendTextService<*>>().to<VelocitySendTextService>()
      bind<SendTextService<CommandSource>>().to<VelocitySendTextService>()
      bind<TextService<*>>().to<CommonTextService<CommandSource>>()
      bind<TextService<CommandSource>>().to<CommonTextService<CommandSource>>()
      bind<UserService<Player, Player>>().to<VelocityUserService>()
    }
  }
}
