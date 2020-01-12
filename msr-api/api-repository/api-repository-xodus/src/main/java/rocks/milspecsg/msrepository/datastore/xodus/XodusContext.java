/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository.datastore.xodus;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import rocks.milspecsg.msrepository.BasicPluginInfo;
import rocks.milspecsg.msrepository.api.MSRepository;
import rocks.milspecsg.msrepository.api.data.key.Keys;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.xodus.annotation.XodusEmbedded;
import rocks.milspecsg.msrepository.datastore.xodus.annotation.XodusEntity;
import rocks.milspecsg.msrepository.model.data.dbo.Mappable;

import java.io.File;
import java.nio.file.Paths;

@Singleton
public class XodusContext extends DataStoreContext<EntityId, PersistentEntityStore> {

    @Inject
    private BasicPluginInfo basicPluginInfo;

    @Inject
    public XodusContext(Registry registry) {
        super(registry);
    }

    @Override
    protected void closeConnection(PersistentEntityStore dataStore) {
        dataStore.close();
    }

    @Override
    protected void registryLoaded(Object plugin) {
        if (!MSRepository.resolveForSharedEnvironment(Keys.DATA_STORE_NAME, registry).equalsIgnoreCase("xodus")) {
            requestCloseConnection();
            return;
        }

        /* === Initialize storage location === */
        File dbFilesLocation = Paths.get(basicPluginInfo.getId() + "/data/xodus").toFile();
        if (!dbFilesLocation.exists()) {
            if (!dbFilesLocation.mkdirs()) {
                throw new IllegalStateException("Unable to create xodus directory");
            }
        }

        /* === Initialize Xodus === */
        setDataStore(PersistentEntityStores.newInstance(dbFilesLocation.getPath()));

        /* === Find objects to map === */
        Class<?>[] entityClasses = calculateEntityClasses(registry.getOrDefault(Keys.BASE_SCAN_PACKAGE), XodusEntity.class, XodusEmbedded.class);

        /* === Create collections if not present === */
        for (Class<?> entityClass : entityClasses) {
            if (Mappable.class.isAssignableFrom(entityClass)) {
                try {
                    entityClass.getDeclaredMethod("writeTo", Entity.class);
                    entityClass.getDeclaredMethod("readFrom", Entity.class);
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Xodus entity class " + entityClass.getName() + " must implement Mappable#writeTo(T) and Mappable#readFrom(T)", e);
                }
            } else if (entityClass.isAnnotationPresent(XodusEntity.class)){
                throw new IllegalStateException("Xodus entity class " + entityClass.getName() + " must extend rocks.milspecsg.msrepository.model.data.dbo.Mappable");
            }
        }

        setTKeyClass(EntityId.class);
    }
}