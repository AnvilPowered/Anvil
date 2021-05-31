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

package org.anvilpowered.anvil.common.entity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.entity.RestrictionCriteria;
import org.anvilpowered.anvil.api.entity.RestrictionService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class CommonRestrictionService implements RestrictionService {

    @Inject
    private EntityUtils entityUtils;

    private final Map<Object, RestrictionCriteria> entityRestrictions;
    private final Map<UUID, RestrictionCriteria> uuidRestrictions;

    public CommonRestrictionService() {
        entityRestrictions = new HashMap<>();
        uuidRestrictions = new HashMap<>();
    }

    @Override
    public void put(Object entity, RestrictionCriteria criteria) {
        UUID uuid = entityUtils.extractUUID(entity);
        if (uuid != null) {
            // register with UUID instead, we don't want to persist the instances of identifiable entities
            put(uuid, criteria);
            return;
        }
        if (criteria.equals(RestrictionCriteria.none())) {
            entityRestrictions.remove(entity);
        }
        entityRestrictions.put(entity, criteria);
    }

    @Override
    public void put(UUID uuid, RestrictionCriteria criteria) {
        if (criteria.equals(RestrictionCriteria.none())) {
            uuidRestrictions.remove(uuid);
        }
        uuidRestrictions.put(uuid, criteria);
    }

    @Override
    public Optional<RestrictionCriteria> remove(Object entity) {
        UUID uuid = entityUtils.extractUUID(entity);
        if (uuid != null) {
            return remove(uuid);
        }
        return Optional.ofNullable(entityRestrictions.remove(entity));
    }

    @Override
    public Optional<RestrictionCriteria> remove(UUID uuid) {
        return Optional.ofNullable(uuidRestrictions.remove(uuid));
    }

    @Override
    public RestrictionCriteria get(Object entity) {
        UUID uuid = entityUtils.extractUUID(entity);
        if (uuid != null) {
            return get(uuid);
        }
        return entityRestrictions.getOrDefault(entity, RestrictionCriteria.none());
    }

    @Override
    public RestrictionCriteria get(UUID uuid) {
        return uuidRestrictions.getOrDefault(uuid, RestrictionCriteria.none());
    }
}
