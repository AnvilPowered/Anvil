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

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import java.util.Arrays

class CommonAnvilPluginsCommand<TCommandSource> : SimpleCommand<TCommandSource> {

  @Inject
  private lateinit var permissionService: PermissionService

  @Inject
  private lateinit var registry: Registry

  @Inject
  private lateinit var textService: TextService<TCommandSource>

  private val DESCRIPTION: Component by lazy {
    textService.of("Anvil plugins command")
  }

  override fun execute(source: TCommandSource, context: Array<String>) {
    val values = Anvil.getEnvironmentManager()
      .environments.values
    val mappedValues = values
      .asSequence()
      .map { it.pluginInfo.name }
      .iterator()
    val names = Array(values.size) { mappedValues.next() }
    Arrays.sort(names)
    textService.builder()
      .appendPrefix()
      .green().append("Plugins (", names.size, "): ")
      .appendJoining(", ", *names)
      .sendTo(source)
  }

  override fun canExecute(source: TCommandSource): Boolean {
    return permissionService.hasPermission(source, registry.getOrDefault(Keys.PLUGINS_PERMISSION))
  }

  override fun shortDescription(source: TCommandSource): Component = DESCRIPTION
}
