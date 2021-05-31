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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.anvilpowered.anvil.api.plugin.PluginInfo;

public class AnvilPluginInfo implements PluginInfo {
    public static final String id = "anvil";
    public static final String name = "Anvil";
    public static final String version = "$modVersion";
    public static final String description = "A cross-platform Minecraft plugin framework";
    public static final String url = "https://github.com/AnvilPowered/Anvil";
    public static final String organizationName = "AnvilPowered";
    public static final String[] authors = {organizationName};
    public static final String buildDate = "$buildDate";
    public Component pluginPrefix = Component.text()
      .append(Component.text("[", NamedTextColor.BLUE))
      .append(Component.text(name, NamedTextColor.AQUA))
      .append(Component.text("] ", NamedTextColor.BLUE))
      .build();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String[] getAuthors() {
        return authors;
    }

    @Override
    public String getOrganizationName() {
        return organizationName;
    }

    @Override
    public String getBuildDate() {
        return buildDate;
    }

    @Override
    public Component getPrefix() {
        return pluginPrefix;
    }
}
