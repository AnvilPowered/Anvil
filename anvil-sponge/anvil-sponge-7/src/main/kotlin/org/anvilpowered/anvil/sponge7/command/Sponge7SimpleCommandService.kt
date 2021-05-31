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
package org.anvilpowered.anvil.sponge7.command

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.splitContext
import org.anvilpowered.anvil.common.command.CommonSimpleCommandService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.Optional

class Sponge7SimpleCommandService : CommonSimpleCommandService<CommandSource>() {

  @Inject
  private lateinit var plugin: PluginContainer

  private inner class PlatformCommand(
    private val delegate: SimpleCommand<CommandSource>,
  ) : CommandCallable {
    override fun process(source: CommandSource, context: String): CommandResult {
      delegate.execute(source, context.splitContext())
      return CommandResult.success()
    }

    override fun getSuggestions(source: CommandSource, context: String, targetPosition: Location<World>?): List<String> {
      return delegate.suggest(source, context.splitContext())
    }

    override fun testPermission(source: CommandSource): Boolean = delegate.canExecute(source)
    override fun getShortDescription(source: CommandSource): Optional<Text> = asText(delegate.shortDescription(source) ?: Component.empty())
    override fun getHelp(source: CommandSource): Optional<Text> = asText(delegate.longDescription(source) ?: Component.empty())
    override fun getUsage(source: CommandSource): Text = asText(delegate.usage(source) ?: Component.empty()).get()
    private fun asText(component: Component) =
      Optional.ofNullable(TextSerializers.FORMATTING_CODE.deserialize(LegacyComponentSerializer.legacySection().serialize(component)))
  }

  override fun register(mapping: CommandMapping<out SimpleCommand<CommandSource>>) {
    Sponge.getCommandManager().register(
      plugin,
      PlatformCommand(mapping.command), mapping.name, *mapping.otherAliases.toTypedArray()
    )
  }
}
