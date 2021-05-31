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

package org.anvilpowered.anvil.common.plugin

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.util.TextService

class FallbackPluginInfo<TCommandSource> : PluginInfo {
    companion object {
        const val version = "v0"
        const val description = "description"
        const val url = "URL"
        val authors = arrayOf("author")
        const val organizationName = "organizationName"
        const val buildDate = "last night"
    }

    @Inject
    private lateinit var environment: Environment

    lateinit var pluginPrefix: Component

    @Inject
    fun setPluginPrefix(textService: TextService<TCommandSource>) {
        pluginPrefix = textService.builder().gold().append("[", name, "] ").build()
    }

    override val id: String = environment.name
    override val name: String = environment.name
    override val version: String = Companion.version
    override val description: String = Companion.description
    override val url: String = Companion.url
    override val authors: Array<String> = Companion.authors
    override val organizationName: String = Companion.organizationName
    override val buildDate: String = Companion.buildDate
    override val prefix: Component = pluginPrefix
}
