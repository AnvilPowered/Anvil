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
import com.google.common.reflect.TypeToken;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import org.anvilpowered.anvil.api.misc.Named;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.TextService;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"UnstableApiUsage", "unchecked"})
public interface Environment extends Named, Comparable<Environment> {

    static <T> Binding<T> getBinding(String name, Injector injector) {
        long hash = ((long) name.hashCode()) * ((long) injector.hashCode());
        Binding<?>[] binding = {Anvil.bindingsCache.get(hash)};
        if (binding[0] != null) {
            return (Binding<T>) binding[0];
        }
        injector.getBindings().forEach((k, v) -> {
            if (k.getTypeLiteral().getType().getTypeName().contains(name)) {
                binding[0] = v;
            }
        });
        Binding<T> result = Preconditions.checkNotNull(
            (Binding<T>) binding[0],
            "Could not find binding for service: " + name + " in injector " + injector
        );
        Anvil.bindingsCache.put(hash, result);
        return result;
    }

    static <T> Key<T> getKey(String name, Injector injector) {
        return Environment.<T>getBinding(name, injector).getKey();
    }

    static <T> Provider<T> getProvider(String name, Injector injector) {
        return Environment.<T>getBinding(name, injector).getProvider();
    }

    static <T> T getInstance(String name, Injector injector) {
        return Environment.<T>getProvider(name, injector).get();
    }

    default <T> Binding<T> getBinding(String name) {
        return getBinding(name, getInjector());
    }

    default <T> Key<T> getKey(String name) {
        return getKey(name, getInjector());
    }

    default <T> Provider<T> getProvider(String name) {
        return getProvider(name, getInjector());
    }

    default <T> T getInstance(String name) {
        return getInstance(name, getInjector());
    }

    void reload();

    Injector getInjector();

    Object getPlugin();

    PluginInfo getPluginInfo();

    <TCommandSource> TextService<TCommandSource> getTextService();

    Registry getRegistry();

    interface Builder {

        Builder addModules(Module... modules);

        Builder addModules(Iterable<Module> modules);

        Builder addEarlyServices(Key<?>... keys);

        Builder addEarlyServices(Iterable<Key<?>> keys);

        Builder addEarlyServices(Class<?>... classes);

        Builder addEarlyServices(TypeLiteral<?>... typeLiterals);

        Builder addEarlyServices(TypeToken<?>... typeTokens);

        <T> Builder addEarlyServices(Key<T> key, Consumer<T> initializer);

        <T> Builder addEarlyServices(Class<T> clazz, Consumer<T> initializer);

        <T> Builder addEarlyServices(TypeLiteral<T> typeLiteral, Consumer<T> initializer);

        <T> Builder addEarlyServices(TypeToken<T> typeToken, Consumer<T> initializer);

        /**
         * Sets the name for this environment builder.
         *
         * @param name {@link String} Name to set.
         * @return {@code this}
         */
        Builder setName(String name);

        /**
         * Sets the root injector for this environment builder.
         *
         * @param rootInjector {@link Injector} to set. Pass {@code null} to unset.
         * @return {@code this}
         */
        Builder setRootInjector(@Nullable Injector rootInjector);

        /**
         * Sets the logger, currently only necessary on Nukkit.
         *
         * <p>
         * The provided logger is adapted to the {@link org.slf4j.Logger}.
         * If no logger is provided on Nukkit, Anvil's logger will be used.
         * (This means logs will be prefixed with "Anvil" instead of your plugin name)
         * </p>
         *
         * <pre><code>
         *     setLoggerSupplier(MyNukkitPlugin.this::getLogger);
         * </code></pre>
         *
         * @param logger to set.
         */
        Builder setLoggerSupplier(Supplier<?> logger);

        /**
         * Called when the {@link Environment} is loaded.
         *
         * <p>
         * This {@link Consumer} will be invoked when the {@link Environment}
         * is first loaded and on subsequent reloads.
         * </p>
         *
         * <p>
         * This method can be called multiple times on one builder.
         * Preexisting listeners will be used and will not be overridden.
         * </p>
         *
         * @param listener {@link Consumer} to run when this environment is loaded
         * @return {@code this}
         */
        Builder whenLoaded(Consumer<Environment> listener);

        /**
         * Called when the {@link Environment} is loaded for the first time.
         *
         * <p>
         * This {@link Consumer} will only be invoked when the {@link Environment}
         * is loaded for the first time.
         * </p>
         *
         * <p>
         * This method can be called multiple times on one builder.
         * Preexisting listeners will be used and will not be overridden.
         * </p>
         *
         * @param listener {@link Consumer} to run when this environment is ready
         * @return {@code this}
         */
        Builder whenReady(Consumer<Environment> listener);

        /**
         * Called when the {@link Environment} is reloaded.
         *
         * <p>
         * This {@link Consumer} will only be invoked when the {@link Environment}
         * is reloaded, but not when it is first loaded.
         * </p>
         *
         * <p>
         * This method can be called multiple times on one builder.
         * Preexisting listeners will be used and will not be overridden.
         * </p>
         *
         * @param listener {@link Consumer} to run when this environment is reloaded
         * @return {@code this}
         */
        Builder whenReloaded(Consumer<Environment> listener);

        /**
         * Builds an {@link Environment} and registers it.
         *
         * @param plugin The owner for this environment
         */
        void register(Object plugin);
    }
}
