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

package org.anvilpowered.anvil.api.command

import net.kyori.adventure.text.Component

interface SimpleCommand<TCommandSource> {

  fun execute(source: TCommandSource, context: Array<String>)

  fun suggest(source: TCommandSource, context: Array<String>): List<String> = listOf()

  fun canExecute(source: TCommandSource): Boolean = true

  fun shortDescription(source: TCommandSource): Component? = null

  fun longDescription(source: TCommandSource): Component? = null

  fun usage(source: TCommandSource): Component? = null
}
