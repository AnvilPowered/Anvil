package org.anvilpowered.anvil.luckperms.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.anvilpowered.anvil.api.util.ContextualDataService;
import org.anvilpowered.anvil.api.util.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class LuckPermsContextualDataService<TPlayer> implements ContextualDataService {

    @Inject
    private LuckPerms luckPerms;

    @Inject
    private UserService<TPlayer, TPlayer> userService;

    private Map<UUID, CachedMetaData> cachedDataMap = new HashMap<>();

    @Override
    public Optional<String> getPrefix(UUID userUUID) {
        if(userService.getPlayer(userUUID).isPresent()) {
            return getCachedPlayerData(userUUID).map(CachedMetaData::getPrefix);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getPrefix(String name) {
        if(userService.getPlayer(name).isPresent()) {
            UUID userUUID = userService.getUUID(userService.getPlayer(name).get());
            return getCachedPlayerData(userUUID).map(CachedMetaData::getPrefix);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getSuffix(UUID userUUID) {
        if(userService.getPlayer(userUUID).isPresent()) {
            return getCachedPlayerData(userUUID).map(CachedMetaData::getSuffix);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getSuffix(String name) {
        if(userService.getPlayer(name).isPresent()) {
            UUID userUUID = userService.getUUID(userService.getPlayer(name).get());
            return getCachedPlayerData(userUUID).map(CachedMetaData::getSuffix);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getNameColor(UUID userUUID) {
        if (getCachedPlayerData(userUUID).isPresent()) {
            return getCachedPlayerData(userUUID).map(d -> d.getMetaValue("name-color"));
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getNameColor(String name) {
        if (userService.getPlayer(name).isPresent()) {
            UUID userUUID = userService.getUUID(userService.getPlayer(name).get());
            if (getCachedPlayerData(userUUID).isPresent()) {
                return getCachedPlayerData(userUUID).map(d -> d.getMetaValue("name-color"));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getChatColor(UUID userUUID) {
        if (getCachedPlayerData(userUUID).isPresent()) {
            return getCachedPlayerData(userUUID).map(d -> d.getMetaValue("chat-color"));
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getChatColor(String name) {
        if (userService.getPlayer(name).isPresent()) {
            UUID userUUID = userService.getUUID(userService.getPlayer(name).get());
            if (getCachedPlayerData(userUUID).isPresent()) {
                return getCachedPlayerData(userUUID).map(d -> d.getMetaValue("chat-color"));
            }
        }
        return Optional.empty();
    }

    @Override
    public void addPlayerToCache(String name) {
        Optional<User> user = Optional.ofNullable(luckPerms.getUserManager().getUser(name));

        if (user.isPresent()) {
            cachedDataMap.put(user.get().getUniqueId(), user.get().getCachedData().getMetaData(getQueryOptions(user.get())));
        } else {
            throw new IllegalStateException("Failed to find the user " + name + " inside the Luckperms database.");
        }
    }

    @Override
    public void addPlayerToCache(UUID userUUID) {
        userService.getPlayer(userUUID).ifPresent(p -> addPlayerToCache(userService.getUserName(p)));
    }

    @Override
    public void removePlayerFromCache(String name) {
        userService.getPlayer(name).ifPresent(p -> cachedDataMap.remove(userService.getUUID(p)));
    }

    @Override
    public void removePlayerFromCache(UUID userUUID) {
        userService.getPlayer(userUUID).ifPresent(p -> cachedDataMap.remove(userUUID));
    }

    private QueryOptions getQueryOptions(User user) {
        final ContextManager contextManager = luckPerms.getContextManager();
        return contextManager.getQueryOptions(user).orElseGet(contextManager::getStaticQueryOptions);
    }

    private Optional<CachedMetaData> getCachedPlayerData(UUID userUUID) {
        return Optional.of(cachedDataMap.get(userUUID));
    }
}
