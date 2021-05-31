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

class Change<T, TCommandSource>(
  private val registry: Registry,
  private val textService: TextService<TCommandSource>,
  val key: Key<T>,
  var newValue: T? = null,
) {

  private val remove: Component by lazy {
    textService.builder()
      .red().append("[R]")
      .onHoverShowText(
        textService.builder()
          .red().append("Remove this change\n")
          .gray().append("/$anvilAlias regedit key $key unstage")
      ).onClickRunCommand("/$anvilAlias regedit key $key unstage")
      .build()
  }

  constructor(stage: Stage<TCommandSource>, key: Key<T>, newValue: T? = null)
    : this(stage.registry.second, stage.textService, key, newValue)

  fun apply(registry: Registry) {
    registry.set(key, newValue)
  }

  fun <T : Comparable<T>> max(a: T, b: T): T {
    return if (a > b) a else b
  }

  fun print(): Component {
    return textService.builder()
      .append(remove, " ")
      .gold().append(key.name, " > ")
      .append(textService.printValueYellow(key, registry.get(key).orElse(null)))
      .gray().append(" -> ")
      .append(textService.printValueGreen(key, newValue))
      .build()
  }
}
