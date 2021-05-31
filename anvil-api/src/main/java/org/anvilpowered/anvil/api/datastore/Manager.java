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

package org.anvilpowered.anvil.api.datastore;

import com.google.inject.name.Named;
import net.kyori.adventure.text.Component;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.TextService;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * A module consists of a {@link Manager} and a (single) {@link DBComponent}
 * for every data storage implementation.
 * </p>
 * <p>
 * The {@link Manager} of a module is its metaphorical gateway. Interactions with
 * other modules should (almost always) be done through the {@link Manager}.
 * There are, however, some cases where direct access to a component is required.
 * One such case is inter-{@link Repository} access that requires compile time
 * type safety. Because the {@link DBComponent#getTKeyClass()} type is not known
 * to the manager, code that interacts with {@code TKey} must be placed in a
 * {@link DBComponent}.
 * </p>
 * <p>
 * One of the primary functions of a {@link Manager} is to provide the correct
 * {@link DBComponent} implementation via {@link #getPrimaryComponent()}.
 * </p>
 * <p>
 * Implementations of {@link Manager} should consist of methods similar to the
 * following:
 * </p>
 * <ul>
 *   <li><code>{@link CompletableFuture}&lt;{@link Component}&gt; create(UUID userUUID);</code></li>
 *   <li><code>{@link CompletableFuture}&lt;{@link Component}&gt; invite(UUID userUUID, UUID targetUserUUID);</code></li>
 *   <li><code>{@link CompletableFuture}&lt;{@link Component}&gt; kick(UUID userUUID, UUID targetUserUUID);</code></li>
 *   <li><code>{@link CompletableFuture}&lt;List&lt;{@link Component}&gt;&gt; list(String query);</code></li>
 * </ul>
 * <p>
 * {@link Component} is the base return type for the methods in a {@link Manager}.
 * To build these results use {@link TextService.Builder} or the static methods in {@link Component}.
 * </p>
 * <p>
 * All methods (with some exceptions) in {@link Manager} should return a form of {@link Component}
 * to be displayed directly to the end user. Normally, the return type, {@link Component}, is wrapped in
 * a {@link java.util.concurrent.CompletableFuture} in order to keep the main game thread
 * free from IO. It is sometimes necessary to further wrap the {@link Component} in a {@link java.util.List}
 * when the result is more than a single line. In this case, pagination can be used to display the result
 * to the end user.
 * </p>
 * <p>
 * The following is an example of a typical {@link Manager} and {@link Repository} combination:
 * </p>
 * <pre><code>
 * public interface FooManager
 *   extends Manager&lt;FooRepository&lt;?, ?&gt;&gt; {...}
 *
 * public interface FooRepository&lt;TKey, TDataStore&gt;
 *   extends Repository&lt;TKey, Foo&lt;TKey&gt;, TDataStore&gt; {...}
 * </code></pre>
 *
 * @param <C> Base {@link DBComponent} type for this manager.
 *            Must be implemented by all components in this module
 * @see Repository
 * @see DBComponent
 * @see TextService
 */
public interface Manager<C extends DBComponent<?, ?>> {

    /**
     * Provides the current {@link DBComponent} as defined by {@link Keys#DATA_STORE_NAME} in the current {@link Registry}.
     * <p>
     * The current {@link DBComponent} implementation is defined as the implementation provided by Guice that meets the following
     * criteria:
     * </p>
     * <br>
     * <p>
     * The value for {@link Keys#DATA_STORE_NAME} found by the the current {@link Registry} must match (ignored case) a registered
     * datastore implementation. This can be one of the following predefined values:
     * </p>
     * <ul>
     *   <li>{@code "mongodb"}</li>
     *   <li>{@code "xodus"}</li>
     * </ul>
     * <p>
     * or a custom value defined by your guice module.
     * </p>
     * <br>
     * <p>
     * For example, 'mongodb' (or any capitalization thereof) will match
     * a {@link DBComponent} annotated with {@link Named}{@code (value = "mongodb")}
     * </p>
     *
     * @return The current {@link DBComponent} implementation
     * @throws IllegalStateException If the config has not been loaded yet, or if no implementation was found
     * @see DBComponent
     */
    C getPrimaryComponent();
}
