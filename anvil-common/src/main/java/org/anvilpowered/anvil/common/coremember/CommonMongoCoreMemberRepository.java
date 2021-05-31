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
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import org.anvilpowered.anvil.api.coremember.MongoCoreMemberRepository;
import org.anvilpowered.anvil.api.model.coremember.CoreMember;
import org.anvilpowered.anvil.base.datastore.BaseMongoRepository;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommonMongoCoreMemberRepository
  extends CommonCoreMemberRepository<ObjectId, Datastore>
  implements BaseMongoRepository<CoreMember<ObjectId>>,
  MongoCoreMemberRepository {

  @Override
  public CompletableFuture<Optional<CoreMember<ObjectId>>> getOneOrGenerateForUser(UUID userUUID, String userName, String ipAddress, boolean[] flags) {
    final int length = flags.length;
    if (length != 8) throw new IllegalArgumentException("Flags must be an array of length 8");
    for (int i = 0; i < length; i++) {
      flags[i] = false;
    }
    return getOneForUser(userUUID).thenApplyAsync(optionalMember -> {
      if (!optionalMember.isPresent()) {
        CoreMember<ObjectId> member = generateEmpty();
        member.setUserUUID(userUUID);
        member.setUserName(userName);
        member.setIpAddress(ipAddress);
        member.setLastJoinedUtc(OffsetDateTime.now(ZoneOffset.UTC).toInstant());
        flags[0] = true;
        return insertOne(member).join();
      }

      CoreMember<ObjectId> member = optionalMember.get();
      UpdateOperations<CoreMember<ObjectId>> updateOperations = createUpdateOperations();
      boolean updateName = false;
      boolean updateIpAddress = false;
      if (!userName.equals(member.getUserName())) {
        updateOperations.set("userName", userName);
        updateName = true;
      }
      if (!ipAddress.equals(member.getIpAddress())) {
        updateOperations.set("ipAddress", ipAddress);
        updateIpAddress = true;
      }
      updateOperations.set("lastJoinedUtc", OffsetDateTime.now(ZoneOffset.UTC).toInstant());
      if (getDataStoreContext().getDataStore()
        .update(asQuery(member.getId()), updateOperations)
        .getUpdatedCount() > 0) {
        if (updateName) {
          member.setUserName(userName);
          flags[1] = true;
        }
        if (updateIpAddress) {
          member.setIpAddress(ipAddress);
          flags[2] = true;
        }
      }
      return optionalMember;
    });
  }

  @Override
  public CompletableFuture<Optional<CoreMember<ObjectId>>> getOneForUser(UUID userUUID) {
    return getOne(asQuery(userUUID));
  }

  @Override
  public CompletableFuture<Optional<CoreMember<ObjectId>>> getOneForUser(String userName) {
    return getOne(asQuery(userName));
  }

  @Override
  public CompletableFuture<List<CoreMember<ObjectId>>> getForIpAddress(String ipAddress) {
    return getAll(asQueryForIpAddress(ipAddress));
  }

  @Override
  public CompletableFuture<Boolean> ban(ObjectId id, Instant endUtc, String reason) {
    return ban(asQuery(id), endUtc, reason);
  }

  @Override
  public CompletableFuture<Boolean> banUser(UUID userUUID, Instant endUtc, String reason) {
    return ban(asQuery(userUUID), endUtc, reason);
  }

  @Override
  public CompletableFuture<Boolean> banUser(String userName, Instant endUtc, String reason) {
    return ban(asQuery(userName), endUtc, reason);
  }

  @Override
  public CompletableFuture<Boolean> banIpAddress(String ipAddress, Instant endUtc, String reason) {
    return ban(asQueryForIpAddress(ipAddress), endUtc, reason);
  }

  @Override
  public CompletableFuture<Boolean> unBan(ObjectId id) {
    return unBan(asQuery(id));
  }

  @Override
  public CompletableFuture<Boolean> unBanUser(UUID userUUID) {
    return unBan(asQuery(userUUID));
  }

  @Override
  public CompletableFuture<Boolean> unBanUser(String userName) {
    return unBan(asQuery(userName));
  }

  @Override
  public CompletableFuture<Boolean> unBanIpAddress(String ipAddress) {
    return unBan(asQueryForIpAddress(ipAddress));
  }

  @Override
  public CompletableFuture<Boolean> mute(ObjectId id, Instant endUtc, String reason) {
    return mute(asQuery(id), endUtc, reason);
  }

  @Override
  public CompletableFuture<Boolean> muteUser(UUID userUUID, Instant endUtc, String reason) {
    return mute(asQuery(userUUID), endUtc, reason);
  }

  @Override
  public CompletableFuture<Boolean> muteUser(String userName, Instant endUtc, String reason) {
    return mute(asQuery(userName), endUtc, reason);
  }

  @Override
  public CompletableFuture<Boolean> muteIpAddress(String ipAddress, Instant endUtc, String reason) {
    return mute(asQueryForIpAddress(ipAddress), endUtc, reason);
  }

  @Override
  public CompletableFuture<Boolean> unMute(ObjectId id) {
    return unMute(asQuery(id));
  }

  @Override
  public CompletableFuture<Boolean> unMuteUser(UUID userUUID) {
    return unMute(asQuery(userUUID));
  }

  @Override
  public CompletableFuture<Boolean> unMuteUser(String userName) {
    return unMute(asQuery(userName));
  }

  @Override
  public CompletableFuture<Boolean> unMuteIpAddress(String ipAddress) {
    return unMute(asQueryForIpAddress(ipAddress));
  }

  @Override
  public CompletableFuture<Boolean> setNickName(ObjectId id, String nickName) {
    return setNickName(asQuery(id), nickName);
  }

  @Override
  public CompletableFuture<Boolean> setNickNameForUser(UUID userUUID, String nickName) {
    return setNickName(asQuery(userUUID), nickName);
  }

  @Override
  public CompletableFuture<Boolean> setNickNameForUser(String userName, String nickName) {
    return setNickName(asQuery(userName), nickName);
  }

  @Override
  public CompletableFuture<Boolean> deleteNickName(ObjectId id) {
    return deleteNickName(asQuery(id));
  }

  @Override
  public CompletableFuture<Boolean> deleteNickNameForUser(UUID userUUID) {
    return deleteNickName(asQuery(userUUID));
  }

  @Override
  public CompletableFuture<Boolean> deleteNickNameForUser(String userName) {
    return deleteNickName(asQuery(userName));
  }

  @Override
  public Query<CoreMember<ObjectId>> asQuery(UUID userUUID) {
    return asQuery().field("userUUID").equal(userUUID);
  }

  @Override
  public Query<CoreMember<ObjectId>> asQuery(String userName) {
    return asQuery().field("userName").containsIgnoreCase(userName);
  }

  @Override
  public Query<CoreMember<ObjectId>> asQueryForIpAddress(String ipAddress) {
    return asQuery().field("ipAddress").equal(ipAddress);
  }

  @Override
  public CompletableFuture<Boolean> ban(Query<CoreMember<ObjectId>> query, Instant endUtc, String reason) {
    return update(query, set("banned", true)
      .set("banEndUtc", endUtc)
      .set("banReason", reason)
    );
  }

  @Override
  public CompletableFuture<Boolean> unBan(Query<CoreMember<ObjectId>> query) {
    return update(query, set("banned", false));
  }

  @Override
  public CompletableFuture<Boolean> mute(Query<CoreMember<ObjectId>> query, Instant endUtc, String reason) {
    return update(query, set("muted", true)
      .set("muteEndUtc", endUtc)
      .set("muteReason", reason)
    );
  }

  @Override
  public CompletableFuture<Boolean> unMute(Query<CoreMember<ObjectId>> query) {
    return update(query, set("muted", false));
  }

  @Override
  public CompletableFuture<Boolean> setNickName(Query<CoreMember<ObjectId>> query, String nickName) {
    return update(query, set("nickName", nickName));
  }

  @Override
  public CompletableFuture<Boolean> deleteNickName(Query<CoreMember<ObjectId>> query) {
    return update(query, unSet("nickName"));
  }
}
