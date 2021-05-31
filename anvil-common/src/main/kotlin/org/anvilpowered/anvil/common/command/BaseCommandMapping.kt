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

package org.anvilpowered.anvil.common.command

import org.anvilpowered.anvil.api.command.CommandMapping
import java.util.Optional

open class BaseCommandMapping<C>(
  aliases: List<String>,
  override val command: C,
) : CommandMapping<C> {

  init {
    require(aliases.isNotEmpty()) { "aliases is empty" }
  }

  override val name: String = aliases[0]
  override val otherAliases: List<String> = aliases.drop(1)
  override var parentCommand: CommandMapping<C>? = null
  override val subCommands: List<CommandMapping<C>> = listOf()

  @JvmName("setParentCommand1")
  fun setParentCommand(command: CommandMapping<C>) {
    parentCommand = command
  }

  fun getParentCommand(): Optional<CommandMapping<C>> = Optional.ofNullable(parentCommand)
}
