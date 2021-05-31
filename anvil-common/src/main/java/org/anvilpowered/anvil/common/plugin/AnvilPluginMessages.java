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

package org.anvilpowered.anvil.common.plugin;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.TimeFormatService;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class AnvilPluginMessages {

    @Inject
    protected PluginInfo pluginInfo;

    @Inject
    protected TextService<?> textService;

    @Inject
    protected TimeFormatService timeFormatService;

    public Component getBanMessage(String reason, Instant endUtc) {
        return textService.builder()
            .appendPrefix()
            .red().append("You have been banned for: ", textService.deserializeAmpersand(reason))
            .yellow().append("\n\nFor another ", timeFormatService.format(Duration.between(OffsetDateTime.now(ZoneOffset.UTC).toInstant(), endUtc)))
            .append("\n\nUntil ", timeFormatService.format(endUtc))
            .build();
    }

    public Component getMuteMessage(String reason, Instant endUtc) {
        return textService.builder()
            .appendPrefix()
            .red().append("You have been muted for: ", textService.deserializeAmpersand(reason))
            .yellow().append("\nFor another ", timeFormatService.format(Duration.between(OffsetDateTime.now(ZoneOffset.UTC).toInstant(), endUtc)))
            .build();
    }
}
