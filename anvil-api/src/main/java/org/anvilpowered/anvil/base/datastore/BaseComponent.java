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

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.datastore.DBComponent;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;

import java.util.Optional;

public abstract class BaseComponent<
    TKey,
    TDataStore>
    implements DBComponent<TKey, TDataStore> {

    @Inject
    private DataStoreContext<TKey, TDataStore> dataStoreContext;

    @Override
    public DataStoreContext<TKey, TDataStore> getDataStoreContext() {
        return dataStoreContext;
    }

    @Override
    public Class<TKey> getTKeyClass() {
        return dataStoreContext.getTKeyClass();
    }

    @Override
    public Optional<TKey> parse(Object object) {
        try {
            return Optional.of(parseUnsafe(object));
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            return Optional.empty();
        }
    }
}
