/*
 *   MSRepository - MilSpecSG
 *   Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.mscore.plugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import rocks.milspecsg.mscore.listeners.VelocityPlayerListener;
import rocks.milspecsg.mscore.module.CommonModule;
import rocks.milspecsg.msrepository.api.MSRepository;
import rocks.milspecsg.msrepository.api.data.registry.Registry;

@Plugin(
    id = MSCorePluginInfo.id,
    name = MSCorePluginInfo.name,
    version = MSCorePluginInfo.version,
    description = MSCorePluginInfo.description,
    url = MSCorePluginInfo.url,
    authors = MSCorePluginInfo.authors
)
public class MSCoreVelocity extends MSCore {

    @Inject
    Injector velocityRootInjector;

    @Inject
    ProxyServer proxyServer;

    @Override
    public String toString() {
        return MSCorePluginInfo.id;
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        injector = velocityRootInjector.createChildInjector(new CommonModule());
        MSRepository.createEnvironment("mscore", injector, Key.get(Registry.class));
        proxyServer.getEventManager().register(this, injector.getInstance(VelocityPlayerListener.class));
        load();
    }
}