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

import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.anvilpowered.anvil.api.misc._AbstractModuleKt;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "unused", "UnstableApiUsage"})
class ServiceManagerImpl implements ServiceManager {

    private final Map<TypeToken<?>, Supplier<?>> supplierBindings;
    private final Map<TypeToken<?>, Function<?, ?>> functionBindings;
    private Injector injector;
    static final EnvironmentManagerImpl environmentManager
        = new EnvironmentManagerImpl();

    public ServiceManagerImpl() {
        supplierBindings = new HashMap<>();
        functionBindings = new HashMap<>();
        registerBinding(EnvironmentManager.class, () -> environmentManager);
        registerBinding(Environment.Builder.class, EnvironmentBuilderImpl::new);
    }

    void setInjector(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T> Supplier<T> provideSupplier(TypeToken<T> typeToken) {
        Supplier<T> supplier = (Supplier<T>) supplierBindings.get(typeToken);
        if (supplier != null) {
            return supplier;
        }
        return () -> injector.getInstance(Key.get(_AbstractModuleKt.toTypeLiteralNoInline(typeToken)));
    }

    @Override
    public <T> Supplier<T> provideSupplier(Class<T> clazz) {
        return provideSupplier(TypeToken.of(clazz));
    }

    @Override
    public <T> Supplier<T> provideSupplier(String name) {
        Supplier<T>[] suppliers = new Supplier[1];
        for (Map.Entry<TypeToken<?>, Supplier<?>> entry : supplierBindings.entrySet()) {
            if (entry.getKey().getRawType().getName().equalsIgnoreCase(name)) {
                suppliers[0] = (Supplier<T>) entry.getValue();
                break;
            }
        }
        if (suppliers[0] != null) {
            return suppliers[0];
        }
        return () -> Environment.getInstance(name, injector);
    }

    @Override
    public <T, R> Function<T, R> provideFunction(TypeToken<R> typeToken) {
        Function<T, R> function = (Function<T, R>) functionBindings.get(typeToken);
        if (function != null) {
            return function;
        }
        return injector -> ((Injector) injector)
            .getInstance(Key.get(_AbstractModuleKt.toTypeLiteralNoInline(typeToken)));
    }

    @Override
    public <T, R> Function<T, R> provideFunction(Class<R> clazz) {
        return provideFunction(TypeToken.of(clazz));
    }

    @Override
    public <T, R> Function<T, R> provideFunction(String name) {
        Function<T, R>[] functions = new Function[1];
        for (Map.Entry<TypeToken<?>, Function<?, ?>> entry : functionBindings.entrySet()) {
            if (entry.getKey().getRawType().getName().equalsIgnoreCase(name)) {
                functions[0] = (Function<T, R>) entry.getValue();
                break;
            }
        }
        if (functions[0] != null) {
            return functions[0];
        }
        return injector -> Environment.getInstance(name, (Injector) injector);
    }

    @Override
    public <T> T provide(TypeToken<T> typeToken) {
        return provideSupplier(typeToken).get();
    }

    @Override
    public <T> T provide(Class<T> clazz) {
        return provideSupplier(clazz).get();
    }

    @Override
    public <T> T provide(String name) {
        return this.<T>provideSupplier(name).get();
    }

    @Override
    public <T, R> R provide(TypeToken<R> typeToken, T input) {
        return provideFunction(typeToken).apply(input);
    }

    @Override
    public <T, R> R provide(Class<R> clazz, T input) {
        return provideFunction(clazz).apply(input);
    }

    @Override
    public <T, R> R provide(String name, T input) {
        return this.<T, R>provideFunction(name).apply(input);
    }

    @Override
    public <T> void registerBinding(TypeToken<T> typeToken, Supplier<T> supplier) {
        supplierBindings.put(typeToken, supplier);
    }

    @Override
    public <T> void registerBinding(Class<T> clazz, Supplier<T> supplier) {
        supplierBindings.put(TypeToken.of(clazz), supplier);
    }

    @Override
    public <T, R> void registerBinding(TypeToken<R> typeToken, Function<T, R> function) {
        functionBindings.put(typeToken, function);
    }

    @Override
    public <T, R> void registerBinding(Class<R> clazz, Function<T, R> function) {
        functionBindings.put(TypeToken.of(clazz), function);
    }
}
