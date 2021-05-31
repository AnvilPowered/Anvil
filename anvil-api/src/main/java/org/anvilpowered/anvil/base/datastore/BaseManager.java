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

package org.anvilpowered.anvil.base.datastore;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.anvilpowered.anvil.api.datastore.DBComponent;
import org.anvilpowered.anvil.api.datastore.Manager;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.registry.RegistryScoped;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Map;

@SuppressWarnings({"UnstableApiUsage", "unchecked"})
public abstract class BaseManager<C extends DBComponent<?, ?>> implements Manager<C> {

    protected Registry registry;

    private final TypeToken<C> componentType;

    protected BaseManager(Registry registry) {
        this.registry = registry;
        registry.whenLoaded(this::registryLoaded).register();
        componentType = new TypeToken<C>(getClass()) {
        };
    }

    @Inject
    private Injector injector;

    @Inject
    private Logger logger;

    @Nullable
    @RegistryScoped
    private C currentComponent;

    private void registryLoaded() {
        currentComponent = null;
    }

    private void loadComponent() {
        String dataStoreName = registry.getExtraSafe(Keys.DATA_STORE_NAME)
            .toLowerCase(Locale.ENGLISH);
        String type = componentType.getRawType().getCanonicalName();
        Named named = Names.named(dataStoreName);
        for (Map.Entry<Key<?>, Binding<?>> entry : injector.getBindings().entrySet()) {
            Key<?> k = entry.getKey();
            if (k.getTypeLiteral().getType().getTypeName().contains(type)
                && named.equals(k.getAnnotation())) {
                currentComponent = (C) entry.getValue().getProvider().get();
                return;
            }
        }
        String message = "Anvil: Could not find requested data store: \"" + dataStoreName
            + "\". Did you bind it correctly?";
        logger.error(message, new IllegalStateException(message));
    }

    @Override
    @RegistryScoped
    public C getPrimaryComponent() {
        try {
            if (currentComponent == null) {
                loadComponent();
            }
            return Preconditions.checkNotNull(currentComponent,
                "An error occurred while loading current component");
        } catch (RuntimeException e) {
            String message = "Anvil: DataStoreName has not been loaded yet!" +
                "Make sure your Registry and ConfigurationService implementations" +
                "are annotated with @Singleton!";
            logger.error(message);
            throw new IllegalStateException(message, e);
        }
    }
}
