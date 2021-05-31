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

package org.anvilpowered.anvil.common.coremember;

import org.anvilpowered.anvil.api.coremember.CoreMemberRepository;
import org.anvilpowered.anvil.api.model.coremember.CoreMember;
import org.anvilpowered.anvil.base.datastore.BaseRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class CommonCoreMemberRepository<
  TKey,
  TDataStore>
  extends BaseRepository<TKey, CoreMember<TKey>, TDataStore>
  implements CoreMemberRepository<TKey, TDataStore> {

  @Override
  @SuppressWarnings("unchecked")
  public Class<CoreMember<TKey>> getTClass() {
    return (Class<CoreMember<TKey>>) getDataStoreContext().getEntityClassUnsafe("coremember");
  }

  @Override
  public CompletableFuture<Optional<CoreMember<TKey>>> getOneOrGenerateForUser(UUID userUUID, String userName, String ipAddress) {
    return getOneOrGenerateForUser(userUUID, userName, ipAddress, new boolean[]{false, false, false, false, false, false, false, false});
  }

  @Override
  public boolean checkBanned(CoreMember<?> coreMember) {
    if (coreMember.isBanned() && coreMember.getBanEndUtc().isAfter(OffsetDateTime.now(ZoneOffset.UTC).toInstant())) {
      return true;
    } else if (coreMember.isBanned()) {
      unBanUser(coreMember.getUserUUID());
    }
    return false;
  }

  @Override
  public CompletableFuture<Boolean> checkBanned(TKey id) {
    return getOne(id).thenApplyAsync(o -> o.map(this::checkBanned).orElse(false));
  }

  @Override
  public CompletableFuture<Boolean> checkBannedForUser(UUID userUUID) {
    return getOneForUser(userUUID).thenApplyAsync(o -> o.map(this::checkBanned).orElse(false));
  }

  @Override
  public CompletableFuture<Boolean> checkBannedForUser(String userName) {
    return getOneForUser(userName).thenApplyAsync(o -> o.map(this::checkBanned).orElse(false));
  }

  @Override
  public boolean checkMuted(CoreMember<?> coreMember) {
    if (coreMember.isMuted() && coreMember.getMuteEndUtc().isAfter(OffsetDateTime.now(ZoneOffset.UTC).toInstant())) {
      return true;
    } else if (coreMember.isMuted()) {
      unMuteUser(coreMember.getUserUUID());
    }
    return false;
  }

  @Override
  public CompletableFuture<Boolean> checkMuted(TKey id) {
    return getOne(id).thenApplyAsync(o -> o.map(this::checkMuted).orElse(false));
  }

  @Override
  public CompletableFuture<Boolean> checkMutedForUser(UUID userUUID) {
    return getOneForUser(userUUID).thenApplyAsync(o -> o.map(this::checkMuted).orElse(false));
  }

  @Override
  public CompletableFuture<Boolean> checkMutedForUser(String userName) {
    return getOneForUser(userName).thenApplyAsync(o -> o.map(this::checkMuted).orElse(false));
  }
}
