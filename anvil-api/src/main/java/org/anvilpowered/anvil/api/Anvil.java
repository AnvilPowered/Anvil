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

package org.anvilpowered.anvil.api;

import com.google.common.base.Preconditions;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.anvilpowered.anvil.api.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.coremember.CoreMemberRepository;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.base.plugin.BasePlugin;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused"})
public class Anvil extends BasePlugin {

    static final Map<Long, Binding<?>> bindingsCache = new HashMap<>();
    protected static ServiceManager serviceManager;
    protected static Environment environment;
    private static final String NOT_LOADED = "Anvil has not been loaded yet!";

    Anvil(String name, Injector rootInjector, Module module) {
        super(name, rootInjector, module);
    }

    public static Environment.Builder getEnvironmentBuilder() {
        return getServiceManager().provide(Environment.Builder.class);
    }

    public static EnvironmentManager getEnvironmentManager() {
        return getServiceManager().provide(EnvironmentManager.class);
    }

    public static Environment getEnvironment() {
        return Preconditions.checkNotNull(environment, NOT_LOADED);
    }

    public static Platform getPlatform() {
        return getEnvironment().getInjector().getInstance(Platform.class);
    }

    public static Registry getRegistry() {
        return getEnvironment().getInjector().getInstance(Registry.class);
    }

    public static CoreMemberManager getCoreMemberManager() {
        return getEnvironment().getInjector().getInstance(CoreMemberManager.class);
    }

    public static CoreMemberRepository<?, ?> getCoreMemberRepository() {
        return getCoreMemberManager().getPrimaryComponent();
    }

    public static ServiceManager getServiceManager() {
        if (serviceManager != null) {
            return serviceManager;
        }
        try {
            return serviceManager = (ServiceManager)
                Class.forName("org.anvilpowered.anvil.api.ServiceManagerImpl").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException("Could not find ServiceManager implementation!", e);
        }
    }
}
