/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

import java.time.Duration;
import java.time.Instant;

public interface TaskService {

    /**
     * Stop all tasks belonging to this plugin
     */
    void stopAll();

    /**
     * Stop all tasks that match the provided name
     * @param name {@link String} name of task
     */
    void stop(String name);

    Builder builder();

    interface Builder {

        Builder async();

        Builder delay(Duration duration);

        Builder delay(int ticks);

        Builder interval(Duration duration);

        Builder interval(int ticks);

        Builder startAtUtc(Instant instantUtc);

        Builder executor(Runnable runnable);

        Builder name(String name);

        void submit();
    }
}
