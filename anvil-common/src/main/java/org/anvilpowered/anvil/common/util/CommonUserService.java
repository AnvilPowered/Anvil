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

package org.anvilpowered.anvil.common.util;

import com.google.common.collect.ImmutableList;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.model.coremember.CoreMember;
import org.anvilpowered.anvil.api.util.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public abstract class CommonUserService<TUser, TPlayer> implements UserService<TUser, TPlayer> {

    private static final UUID constant = UUID.nameUUIDFromBytes(new byte[0]);

    private final Class<TUser> userClass;

    protected CommonUserService(Class<TUser> userClass) {
        this.userClass = userClass;
    }

    @Override
    public List<String> matchPlayerNames(String[] context, int index, int length) {
        if (context.length == length) {
            return matchPlayerNames(context[index]);
        }
        return ImmutableList.of();
    }

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(String userName) {
        return Anvil.getCoreMemberRepository()
            .getOneForUser(userName).thenApplyAsync(c -> c.map(CoreMember::getUserUUID));
    }

    @Override
    public CompletableFuture<Optional<String>> getUserName(UUID userUUID) {
        return Anvil.getCoreMemberRepository()
            .getOneForUser(userUUID).thenApplyAsync(c -> c.map(CoreMember::getUserName));
    }

    @NotNull
    @Override
    public UUID getUUIDSafe(@Nullable Object object) {
        if (userClass.isInstance(object)) {
            return getUUID((TUser) object);
        } else {
            return constant;
        }
    }
}
