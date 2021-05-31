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

import java.util.function.Predicate
import java.util.function.Supplier

interface SimpleCommandService<TCommandSource> {

  /**
   * Attempts to register the provided command to the instance bound to the plugin type in the current injector.
   * The definition of the "plugin type" depends on the platform. Here are some examples:
   * <ul>
   *   <li>Spigot - org.bukkit.plugin.java.JavaPlugin</li>
   *   <li>Sponge - org.spongepowered.api.plugin.PluginContainer</li>
   *   <li>Velocity - com.velocitypowered.api.plugin</li>
   * </ul>
   * <h3>A few platform-specific tips</h3>
   * <ul>
   *   <li>
   *     Spigot does not support defining commands at runtime. The provided primaryAlias must be defined in the plugin.yml file.
   *     If the provided primaryAlias is not defined in the plugin.yml, an {@link IllegalStateException} is thrown.
   *     Additionally, otherAliases has no effect on Spigot because it does not support that either.
   *   </li>
   * </ul>
   *
   * @throws IllegalStateException If the plugin instance is not bound
   * @throws IllegalStateException If there was a platform-specific error (see tips above)
   */
  fun register(mapping: CommandMapping<out SimpleCommand<TCommandSource>>)

  /**
   * Generates a command that runs a child command if its alias
   * matches the first argument of the generated command.
   *
   * <p>
   * If {@code childCommandFallback} is false
   * and the first argument does not match a child, a syntax error message will
   * be shown to the source. Otherwise, the root command executor will run.
   * </p>
   *
   * @param rootCommand                 The command to run if no child was specified.
   * @param subCommands             A map of child commands and their aliases.
   * @param childCommandFallback whether to fall back to the root command if the first
   *                             argument did not match a child command alias
   * @return A routing command
   */
  fun mapRouting(
    aliases: List<String>,
    rootCommand: SimpleCommand<TCommandSource>?,
    subCommands: List<CommandMapping<SimpleCommand<TCommandSource>>>,
    childCommandFallback: Boolean,
  ): CommandMapping<SimpleCommand<TCommandSource>>

  fun mapTerminal(
    aliases: List<String>,
    command: SimpleCommand<TCommandSource>,
  ): CommandMapping<SimpleCommand<TCommandSource>>

  fun generateRoot(helpUsage: String, extended: Predicate<TCommandSource>): SimpleCommand<TCommandSource>

  fun generateRoot(helpUsage: String): SimpleCommand<TCommandSource> = generateRoot(helpUsage) { true }

  fun generateVersion(helpUsage: String, extended: Predicate<TCommandSource>): SimpleCommand<TCommandSource>

  fun generateVersion(helpUsage: String) = generateVersion(helpUsage) { true }

  fun generateHelp(
    subCommandsSupplier: Supplier<List<CommandMapping<SimpleCommand<TCommandSource>>>>
  ): SimpleCommand<TCommandSource>

  fun generateReload(): SimpleCommand<TCommandSource>
}
