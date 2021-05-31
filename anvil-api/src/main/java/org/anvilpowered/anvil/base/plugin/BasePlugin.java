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

package org.anvilpowered.anvil.base.plugin;

import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A helper class for quickly creating an environment. While not strictly necessary, it can
 * simplify the start up process in most cases.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class BasePlugin {

    protected Environment environment;

    protected BasePlugin(
        String name,
        @Nullable Injector rootInjector,
        @Nullable Module module,
        Key<?>... earlyServices
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this);
    }

    protected BasePlugin(
        String name,
        @Nullable Injector rootInjector,
        @Nullable Module module,
        Class<?>... earlyServices
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this);
    }

    protected BasePlugin(
        String name,
        @Nullable Injector rootInjector,
        @Nullable Module module,
        TypeLiteral<?>... earlyServices
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this);
    }

    protected BasePlugin(
        String name,
        @Nullable Injector rootInjector,
        @Nullable Module module,
        TypeToken<?>... earlyServices
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this);
    }

    protected BasePlugin(
        String name,
        @Nullable Injector rootInjector,
        @Nullable Module module
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .register(this);
    }

    protected Environment.Builder createDefaultBuilder(
        String name,
        @Nullable Injector rootInjector,
        @Nullable Module module
    ) {
        Environment.Builder builder = Anvil.getEnvironmentBuilder()
            .setName(name)
            .setRootInjector(rootInjector)
            .whenLoaded(this::whenLoaded)
            .whenReady(e -> environment = e)
            .whenReady(this::whenReady)
            .whenReloaded(this::whenReloaded);
        if (module != null) {
            builder.addModules(module);
        }
        applyToBuilder(builder);
        return builder;
    }

    protected void applyToBuilder(Environment.Builder builder) {
    }

    private void sendLoaded(String status) {
        PluginInfo pluginInfo = environment.getPluginInfo();
        environment.getTextService().builder()
            .append(pluginInfo.getPrefix())
            .green().append(pluginInfo.getVersion())
            .aqua().append(" by ")
            .appendJoining(", ", pluginInfo.getAuthors())
            .append(" - ", status, "!")
            .sendToConsole();
    }

    protected void whenLoaded(Environment environment) {
    }

    protected void whenReady(Environment environment) {
        sendLoaded("Loaded");
    }

    protected void whenReloaded(Environment environment) {
        sendLoaded("Reloaded");
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", environment == null ? "null" : environment.getName())
            .toString();
    }
}
