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

package org.anvilpowered.anvil.common.command.regedit

import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.registry.Key
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode

val anvilAlias by lazy { CommonAnvilCommandNode.ALIAS }

val whitespace = "\\s+".toRegex()

fun <TCommandSource> TextService<TCommandSource>.undo(cmd: String): Component {
  return builder()
    .light_purple().append("[ Undo ]")
    .onHoverShowText(builder()
      .light_purple().append("Undo this action\n")
      .gray().append(cmd)
    ).onClickRunCommand(cmd)
    .build()
}

fun <T, TCommandSource> TextService<TCommandSource>.info(key: Key<T>, registry: Registry): Component {
  return builder()
    .gold().append(key, " >").gray()
    .append("\nValue: ", printValueYellow(key, registry.get(key).orElse(null)))
    .append("\nDefault: ", printValueYellow(key, registry.getDefault(key)))
    .append("\nFallback: ", printValueYellow(key, key.fallbackValue))
    .build()
}

fun <T, TCommandSource> TextService<TCommandSource>.printValueGreen(key: Key<T>, value: T?): Component {
  if (value == null) {
    return builder().red().append("none").build()
  }
  val primary = key.toString(value)
  val secondary = value.toString()
  val builder = builder().green().append(primary)
  if (primary != secondary) {
    builder.gray().append(" (", secondary, ")")
  }
  return builder.build()
}

fun <T, TCommandSource> TextService<TCommandSource>.printValueYellow(key: Key<T>, value: T?): Component {
  if (value == null) {
    return builder().red().append("none").build()
  }
  val primary = key.toString(value)
  val secondary = value.toString()
  val builder = builder().yellow().append(primary)
  if (primary != secondary) {
    builder.gray().append(" (", secondary, ")")
  }
  return builder.build()
}
