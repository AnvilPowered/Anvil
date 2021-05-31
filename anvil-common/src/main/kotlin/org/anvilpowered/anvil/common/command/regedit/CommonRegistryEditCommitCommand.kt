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

class CommonRegistryEditCommitCommand<TUser, TPlayer, TCommandSource>
  : CommonRegistryEditBaseCommand<TUser, TPlayer, TCommandSource>() {

  @Inject
  private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TCommandSource>

  override fun execute(source: TCommandSource, context: Array<String>) {
    val uuid = userService.getUUIDSafe(source)
    val stage = registryEditRootCommand.stages[uuid]
    if (stage == null) {
      textService.send(registryEditRootCommand.notInStage, source)
      return
    }
    if (stage.commit(source)) {
      registryEditRootCommand.stages.remove(uuid)
    }
  }
}
