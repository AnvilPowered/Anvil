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
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import org.anvilpowered.anvil.api.misc._AbstractModuleKt;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.registry.RegistryScope;
import org.anvilpowered.anvil.common.PlatformImpl;
import org.anvilpowered.anvil.common.module.PlatformModule;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class EnvironmentBuilderImpl implements Environment.Builder {

    private String name;
    @Nullable
    private Injector rootInjector;
    private Object plugin;
    private Supplier<?> loggerSupplier;
    private boolean withRootCommand = false;
    private final Collection<Module> modules;
    private final Map<Key<?>, Consumer<?>> earlyServices;
    private final Collection<Consumer<Environment>> loadedListeners;
    private final Collection<Consumer<Environment>> readyListeners;
    private final Collection<Consumer<Environment>> reloadedListeners;
    private static final Collection<EnvironmentBuilderImpl> builders = new ArrayList<>();
    private static boolean alreadyCompleted = false;

    public static void completeInitialization(Module platformModule, Module fallbackModule) {
        if (alreadyCompleted) {
            throw new IllegalStateException("This method should only be called exactly once (in Anvil Common)");
        }
        alreadyCompleted = true;
        Map<String, Collection<Consumer<Environment>>> loadedListeners = new HashMap<>();
        Map<String, Collection<Consumer<Environment>>> readyListeners = new HashMap<>();
        Map<String, Collection<Consumer<Environment>>> reloadedListeners = new HashMap<>();
        List<EnvironmentImpl> environments = builders.stream().map(builder -> {
            final String name = builder.name;
            loadedListeners.put(name, builder.loadedListeners);
            readyListeners.put(name, builder.readyListeners);
            reloadedListeners.put(name, builder.reloadedListeners);
            List<Module> modules = new ArrayList<>();
            modules.add(Modules.override(fallbackModule).with(builder.modules));
            return new EnvironmentImpl(
                builder.rootInjector,
                name,
                builder.plugin,
                builder.loggerSupplier,
                builder.withRootCommand,
                modules,
                builder.earlyServices
            );
        }).collect(Collectors.toList());
        AbstractModule commonModule = new AbstractModule() {
            @Override
            protected void configure() {
                for (EnvironmentImpl environment : environments) {
                    bind(Environment.class)
                        .annotatedWith(Names.named(environment.getName()))
                        .toInstance(environment);
                }
            }
        };
        environments.sort(Comparator.naturalOrder());
        for (EnvironmentImpl environment : environments) {
            environment.addModule(platformModule);
            environment.addModule(commonModule);
            environment.addModule(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(ClassLoader.class).toInstance(environment.getPlugin()
                        .getClass().getClassLoader());
                    bind(Environment.class).toInstance(environment);
                    if (platformModule instanceof PlatformModule) {
                        Platform platform = ((PlatformModule) platformModule).getPlatform();
                        if (platform instanceof PlatformImpl) {
                            ((PlatformImpl) platform).bindLoggerOptionally(environment, binder());
                        }
                    }
                }
            });
            Injector injector = environment.getInjector();
            if (injector != null) {
                injector = injector.createChildInjector(environment.getModules());
            } else {
                injector = Guice.createInjector(environment.getModules());
            }
            environment.setInjector(injector);
            if ("anvil".equals(environment.getName())) {
                Anvil.environment = environment;
                ((ServiceManagerImpl) Anvil.getServiceManager())
                    .setInjector(injector);
            }
            ServiceManagerImpl.environmentManager
                .registerEnvironment(environment, environment.getPlugin());
            for (Map.Entry<Key<?>, Consumer<?>> entry
                : environment.getEarlyServices().entrySet()) {
                ((Consumer) entry.getValue())
                    .accept(injector.getInstance(entry.getKey()));
            }
            Registry registry = injector.getInstance(Registry.class);
            for (Consumer<Environment> listener
                : loadedListeners.get(environment.getName())) {
                registry.whenLoaded(() -> listener.accept(environment)).register();
            }
            registry.load(RegistryScope.DEEP);
            for (Consumer<Environment> listener
                : reloadedListeners.get(environment.getName())) {
                registry.whenLoaded(() -> listener.accept(environment)).register();
            }
            for (Consumer<Environment> listener
                : readyListeners.get(environment.getName())) {
                listener.accept(environment);
            }
        }
    }

    EnvironmentBuilderImpl() {
        modules = new ArrayList<>();
        earlyServices = new HashMap<>();
        loadedListeners = new ArrayList<>();
        readyListeners = new ArrayList<>();
        reloadedListeners = new ArrayList<>();
    }

    @Override
    public Environment.Builder addModules(Module... modules) {
        this.modules.addAll(Arrays.asList(modules));
        return this;
    }

    @Override
    public Environment.Builder addModules(Iterable<Module> modules) {
        modules.forEach(this.modules::add);
        return this;
    }

    private <T> Environment.Builder addEarlyServices(
        Stream<T> keys,
        Function<T, Key<?>> keyTransformer
    ) {
        keys.forEach(key -> earlyServices.put(keyTransformer.apply(key), t -> {
        }));
        return this;
    }

    @Override
    public Environment.Builder addEarlyServices(Key<?>... keys) {
        return addEarlyServices(Stream.of(keys), Function.identity());
    }

    @Override
    public Environment.Builder addEarlyServices(Iterable<Key<?>> keys) {
        return addEarlyServices(StreamSupport.stream(keys.spliterator(), false),
            Function.identity());
    }

    @Override
    public Environment.Builder addEarlyServices(Class<?>... classes) {
        return addEarlyServices(Stream.of(classes), Key::get);
    }

    @Override
    public Environment.Builder addEarlyServices(TypeLiteral<?>... typeLiterals) {
        return addEarlyServices(Stream.of(typeLiterals), Key::get);
    }

    @Override
    public Environment.Builder addEarlyServices(TypeToken<?>... typeTokens) {
        return addEarlyServices(Stream.of(typeTokens), typeToken -> Key.get(_AbstractModuleKt.toTypeLiteralNoInline(typeToken)));
    }

    @Override
    public <T> Environment.Builder addEarlyServices(Key<T> key, Consumer<T> initializer) {
        earlyServices.put(key, initializer);
        return this;
    }

    @Override
    public <T> Environment.Builder addEarlyServices(Class<T> clazz, Consumer<T> initializer) {
        earlyServices.put(Key.get(clazz), initializer);
        return this;
    }

    @Override
    public <T> Environment.Builder addEarlyServices(TypeLiteral<T> typeLiteral, Consumer<T> initializer) {
        earlyServices.put(Key.get(typeLiteral), initializer);
        return this;
    }

    @Override
    public <T> Environment.Builder addEarlyServices(TypeToken<T> typeToken, Consumer<T> initializer) {
        earlyServices.put(Key.get(_AbstractModuleKt.toTypeLiteralNoInline(typeToken)), initializer);
        return this;
    }

    @Override
    public Environment.Builder setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("Name may not be null or empty");
        }
        this.name = name;
        return this;
    }

    @Override
    public Environment.Builder setRootInjector(@Nullable Injector rootInjector) {
        this.rootInjector = rootInjector;
        return this;
    }

    @Override
    public Environment.Builder setLoggerSupplier(Supplier<?> logger) {
        this.loggerSupplier = logger;
        return this;
    }

    @Override
    public Environment.Builder whenLoaded(Consumer<Environment> listener) {
        loadedListeners.add(listener);
        return this;
    }

    @Override
    public Environment.Builder whenReady(Consumer<Environment> listener) {
        readyListeners.add(listener);
        return this;
    }

    @Override
    public Environment.Builder whenReloaded(Consumer<Environment> listener) {
        reloadedListeners.add(listener);
        return this;
    }

    @Override
    public void register(Object plugin) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(plugin, "plugin");
        this.plugin = plugin;
        builders.add(this);
    }
}
