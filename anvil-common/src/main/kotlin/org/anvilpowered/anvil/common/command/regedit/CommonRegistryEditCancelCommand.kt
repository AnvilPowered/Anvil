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

import com.google.inject.Inject

class CommonRegistryEditCancelCommand<TUser, TPlayer, TCommandSource>
  : CommonRegistryEditBaseCommand<TUser, TPlayer, TCommandSource>() {

  @Inject
  private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TCommandSource>

  override fun execute(source: TCommandSource, context: Array<String>) {
    val builder = textService.builder().append(pluginInfo.prefix)
    val removed = registryEditRootCommand.stages.remove(userService.getUUIDSafe(source))
    if (removed == null) {
      builder.red().append("Could not find stage")
    } else {
      builder.green().append("Successfully cancelled changes. Didn't mean to? ")
        .append(textService.undo("/$anvilAlias regedit start ${removed.envName}"))
    }.sendTo(source)
  }
}
