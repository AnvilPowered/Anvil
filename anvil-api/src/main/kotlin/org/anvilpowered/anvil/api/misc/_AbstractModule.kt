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

package org.anvilpowered.anvil.api.misc

import com.google.common.reflect.TypeToken
import com.google.inject.Binder
import com.google.inject.Key
import com.google.inject.TypeLiteral
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.binder.LinkedBindingBuilder
import com.google.inject.binder.ScopedBindingBuilder
import dev.morphia.Datastore
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.datastore.DataStoreContext
import org.anvilpowered.anvil.api.datastore.MongoContext
import org.anvilpowered.anvil.api.datastore.XodusContext
import org.bson.types.ObjectId

inline fun <reified T> Binder.bind(declaring: Any? = null): AnnotatedBindingBuilder<T> =
  bind(object : TypeToken<T>(declaring?.javaClass ?: javaClass) {}.toTypeLiteral())

inline fun <reified T> LinkedBindingBuilder<in T>.to(declaring: Any? = null): ScopedBindingBuilder {
  return to(object : TypeToken<T>(declaring?.javaClass ?: javaClass) {}.toTypeLiteral())
}

inline fun <reified T> LinkedBindingBuilder<in T>.toInternalProvider(declaring: Any? = null): ScopedBindingBuilder {
  return toProvider(
    Anvil.getEnvironment().injector.getProvider(
      Key.get(object : TypeToken<T>(declaring?.javaClass ?: javaClass) {}.toTypeLiteral())
    )
  )
}

// TODO(0.4): Remove
fun <T> TypeToken<T>.toTypeLiteralNoInline(): TypeLiteral<T> = TypeLiteral.get(getType()) as TypeLiteral<T>
inline fun <reified T> TypeToken<T>.toTypeLiteral(): TypeLiteral<T> = TypeLiteral.get(getType()) as TypeLiteral<T>
fun Binder.withMongoDB() = bind<DataStoreContext<ObjectId, Datastore>>().to<MongoContext>()
fun Binder.withXodus() = bind<DataStoreContext<EntityId, PersistentEntityStore>>().to<XodusContext>()
