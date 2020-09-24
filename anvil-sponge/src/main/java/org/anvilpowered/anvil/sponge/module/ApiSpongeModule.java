/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.sponge.module;

import com.google.inject.TypeLiteral;
import org.anvilpowered.anvil.api.Platform;
import org.anvilpowered.anvil.api.PlatformImpl;
import org.anvilpowered.anvil.api.command.CommandExecuteService;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.util.CurrentServerService;
import org.anvilpowered.anvil.api.util.CurrentWorldService;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TaskService;
import org.anvilpowered.anvil.api.util.TeleportationService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.common.entity.EntityUtils;
import org.anvilpowered.anvil.common.module.ApiCommonModule;
import org.anvilpowered.anvil.sponge.command.SpongeCommandExecuteService;
import org.anvilpowered.anvil.sponge.command.SpongeCommandService;
import org.anvilpowered.anvil.sponge.entity.SpongeEntityUtils;
import org.anvilpowered.anvil.sponge.util.SpongeCurrentServerService;
import org.anvilpowered.anvil.sponge.util.SpongeCurrentWorldService;
import org.anvilpowered.anvil.sponge.util.SpongeKickService;
import org.anvilpowered.anvil.sponge.util.SpongePermissionService;
import org.anvilpowered.anvil.sponge.util.SpongeTaskService;
import org.anvilpowered.anvil.sponge.util.SpongeTeleportationService;
import org.anvilpowered.anvil.sponge.util.SpongeTextService;
import org.anvilpowered.anvil.sponge.util.SpongeUserService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class ApiSpongeModule extends ApiCommonModule {

    @Override
    protected void configure() {
        super.configure();
        bind(CommandExecuteService.class).to(SpongeCommandExecuteService.class);
        bind(new TypeLiteral<CommandService<CommandExecutor, CommandSource>>() {
        }).to(SpongeCommandService.class);
        bind(CurrentServerService.class).to(SpongeCurrentServerService.class);
        bind(CurrentWorldService.class).to(SpongeCurrentWorldService.class);
        bind(EntityUtils.class).to(SpongeEntityUtils.class);
        bind(KickService.class).to(SpongeKickService.class);
        bind(PermissionService.class).to(SpongePermissionService.class);
        bind(Platform.class).toInstance(new PlatformImpl(false, "sponge"));
        bind(TaskService.class).to(SpongeTaskService.class);
        bind(TeleportationService.class).to(SpongeTeleportationService.class);
        bind(new TypeLiteral<TextService<Text, CommandSource>>() {
        }).to(SpongeTextService.class);
        bind(new TypeLiteral<UserService<User, Player>>() {
        }).to(SpongeUserService.class);
    }
}
