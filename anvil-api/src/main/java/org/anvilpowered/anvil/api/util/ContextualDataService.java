/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.anvil.api.util;

import java.util.Optional;
import java.util.UUID;

public interface ContextualDataService {

    /**
     * @param userUUID {@link UUID} of user
     * @return prefix of player
     */
    Optional<String> getPrefix(UUID userUUID);

    /**
     * @param name Group or player name
     * @return prefix of group or player
     */
    Optional<String> getPrefix(String name);

    /**
     * @param userUUID {@link UUID} of user
     * @return suffix of player
     */
    Optional<String> getSuffix(UUID userUUID);

    /**
     * @param name Group or player name
     * @return suffix of group or player
     */
    Optional<String> getSuffix(String name);

    /**
     * @param userUUID {@link UUID} of user
     * @return name color of player
     */
    Optional<String> getNameColor(UUID userUUID);

    /**
     * @param name Group or player name
     * @return name color of group or player
     */
    Optional<String> getNameColor(String name);

    /**
     * @param userUUID {@link UUID} of user
     * @return chat color of player
     */
    Optional<String> getChatColor(UUID userUUID);

    /**
     * @param name Group or player name
     * @return chat color of group or player
     */
    Optional<String> getChatColor(String name);

    /**
     * @param name player name
     */
    void addPlayerToCache(String name);

    /**
     *
     * @param userUUID {@link UUID} of user
     */
    void addPlayerToCache(UUID userUUID);

    /**
     *
     * @param name player name
     */
    void removePlayerFromCache(String name);

    /**
     *
     * @param userUUID {@link UUID} of user
     */
    void removePlayerFromCache(UUID userUUID);
}
