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

package org.anvilpowered.anvil.api.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import java.net.URL
import java.util.UUID
import java.util.function.Consumer

interface TextService<TCommandSource> {

  /**
   * @return A new [Builder] instance
   */
  fun builder(): Builder<TCommandSource>

  /**
   * @return A new [PaginationBuilder] instance
   */
  fun paginationBuilder(): PaginationBuilder<TCommandSource>

  /**
   * Create a [Component] with the provided contents
   *
   * @param contents The contents to add to the result
   * @return A built [Component]
   */
  fun of(vararg contents: Any): Component {
    return builder().append(*contents).build()
  }

  /**
   * Create a [Component] with the provided contents
   *
   * @param contents The contents to add to the result
   * @return A built [Component]
   */
  fun of(vararg contents: CharSequence): Component {
    return builder().append(*contents).build()
  }

  /**
   * Sends the provided [text][Component] to the provided [receiver][TCommandSource].
   *
   * @param text     The [text][Component] to send
   * @param receiver The [receiver][TCommandSource] to send the provided text to
   */
  fun send(text: Component, receiver: TCommandSource)

  fun send(text: Component, audience: Audience)

  /**
   * Sends the provided [text][Component] to the provided [receiver][TCommandSource],
   * originating from the provided [sourceUUID][UUID].
   *
   * @param text       The [text][Component] to send
   * @param receiver   The [receiver][TCommandSource] to send the provided text to
   * @param sourceUUID The [UUID] of the source of the message
   */
  fun send(text: Component, receiver: TCommandSource, sourceUUID: UUID)

  /**
   * Sends the provided [text][Component] to the provided [receiver][TCommandSource],
   * originating from the provided source. Attempts to extract a [UUID]
   * from the provided source to server as the identity.
   *
   * @param text     The [text][Component] to send
   * @param receiver The [receiver][TCommandSource] source to send the provided text to
   * @param source   The source of the message
   */
  fun send(text: Component, receiver: TCommandSource, source: Any)

  /**
   * Send the provided [text][Component] to the console
   *
   * @param text The [text][Component] to send
   */
  fun sendToConsole(text: Component) = send(text, console)

  /**
   * @return The server console
   */
  val console: TCommandSource

  /**
   * Deserializes the provided [String] using the character &#39;&amp;&#39; to determine styles.
   *
   * @param text [String] text to deserialize
   * @return The [Component] result of the deserialization
   */
  fun deserializeAmpersand(text: String): Component

  /**
   * Deserializes the provided [String] using the character &#39;§&#39; to determine styles.
   *
   * @param text [String] text to deserialize
   * @return The [Component] result of the deserialization
   */
  fun deserializeSection(text: String): Component

  /**
   * Serializes the provided [String] using the character &#39;§&#39; to serialize styles.
   *
   * @param text [Component] text to serialize
   * @return The [String] result of the serialization
   */
  fun serializeAmpersand(text: Component): String

  /**
   * Serializes the provided [String] using the character &#39;§&#39; to serialize styles.
   *
   * @param text [Component] text to serialize
   * @return The [String] result of the serialization
   */
  fun serializeSection(text: Component): String

  /**
   * Serializes the provided [String] and ignores all styles.
   *
   * @param text [Component] text to serialize
   * @return The [String] result of the serialization
   */
  fun serializePlain(text: Component): String

  /**
   * Removes all styles codes from the provided [String] using the character &#39;&amp;&#39; to determine styles
   *
   * For example, `"&l&bhello &aworld"` becomes `"hello world"`
   *
   * @param text [String] text to remove color from
   * @return The provided [String] without any color codes
   */
  fun toPlain(text: String): String

  /**
   * Counts the number of lines in the provided [Component]
   *
   * @param text [Component] text to count lines
   * @return The number of lines in the provided [Component]. -1 if null. 0 if empty.
   */
  fun lineCount(text: Component?): Int

  /**
   * Counts the number of characters in the provided [Component] excluding color codes.
   *
   * @param text [Component] text to count characters
   * @return The number of characters in the provided [Component]. -1 if null. 0 if empty.
   */
  fun length(text: Component?): Int {
    return if (text == null) -1 else serializePlain(text).length
  }

  interface Builder<TCommandSource> {
    /**
     * Sets the current color to aqua. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun aqua(): Builder<TCommandSource>

    /**
     * Sets the current color to black. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun black(): Builder<TCommandSource>

    /**
     * Sets the current color to blue. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun blue(): Builder<TCommandSource>

    /**
     * Sets the current color to dark aqua. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun dark_aqua(): Builder<TCommandSource>

    /**
     * Sets the current color to dark blue. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun dark_blue(): Builder<TCommandSource>

    /**
     * Sets the current color to dark gray. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun dark_gray(): Builder<TCommandSource>

    /**
     * Sets the current color to dark green. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun dark_green(): Builder<TCommandSource>

    /**
     * Sets the current color to dark purple. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun dark_purple(): Builder<TCommandSource>

    /**
     * Sets the current color to dark red. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun dark_red(): Builder<TCommandSource>

    /**
     * Sets the current color to gold. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun gold(): Builder<TCommandSource>

    /**
     * Sets the current color to gray. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun gray(): Builder<TCommandSource>

    /**
     * Sets the current color to green. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun green(): Builder<TCommandSource>

    /**
     * Sets the current color to light purple. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun light_purple(): Builder<TCommandSource>

    /**
     * Sets the current color to red. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun red(): Builder<TCommandSource>

    /**
     * Resets the current style to default. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun reset(): Builder<TCommandSource>

    /**
     * Sets the current color to white. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun white(): Builder<TCommandSource>

    /**
     * Sets the current color to yellow. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun yellow(): Builder<TCommandSource>

    /**
     * Sets the current style to bold. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun bold(): Builder<TCommandSource>

    /**
     * Sets the current style to italic. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun italic(): Builder<TCommandSource>

    /**
     * Sets the current style to obfuscated. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun obfuscated(): Builder<TCommandSource>

    /**
     * Sets the current style to strikethrough. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun strikethrough(): Builder<TCommandSource>

    /**
     * Sets the current style to underlined. Only applies to text appended after this call.
     *
     * @return `this`
     */
    fun underlined(): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder.
     *
     * @param contents [Object] contents to append
     * @return `this`
     */
    fun append(vararg contents: Any): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder.
     *
     * @param contents The [CharSequence] contents to append
     * @return `this`
     */
    fun append(vararg contents: CharSequence): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder `count` times.
     *
     * This method is the same as:
     *
     * for (int i = 0; i < count; i++) {
     * builder.append(contents);
     * }
     *
     * @param count    The number of times to append the provided contents
     * @param contents The [Object] contents to append
     * @return `this`
     */
    fun appendCount(count: Int, vararg contents: Any): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder `count` times.
     *
     * This method is the same as:
     *
     * for (int i = 0; i < count; i++) {
     * builder.append(contents);
     * }
     *
     * @param count    The number of times to append the provided contents
     * @param contents The [CharSequence] contents to append
     * @return `this`
     */
    fun appendCount(count: Int, vararg contents: CharSequence): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder and add the provided padding on the left side
     * until the result is **at most** `width` wide.
     *
     * An example of usage is:
     *
     * builder.appendWithPaddingLeft(15, '-', "hello");
     *
     * which appends the result `"----------hello"`
     *
     * If the value for padding is longer than one character, the appended result may not reach the full provided `width`.
     * This can be seen in the following example:
     *
     * builder.appendWithPaddingLeft(15, "abc", "hello");
     *
     * which appends the result `"abcabcabchello"` (14 characters instead of 15)
     *
     * This happens because the remaining width is not a multiple of the length of the provided padding, which makes it
     * impossible to fill the full length of the appended result.
     *
     * Note: The appended result will be at most `width` wide, or an exception will be thrown
     *
     * @param width    The maximum total width after padding
     * @param padding  The padding to fill the left side with
     * @param contents The contents to append
     * @return `this`
     * @throws IllegalArgumentException If width &lt; 1
     * @throws IllegalArgumentException If padding length &lt; 1
     * @throws IllegalArgumentException If padding length &gt; width
     * @throws IllegalArgumentException If contents length &gt; width
     */
    fun appendWithPaddingLeft(width: Int, padding: Any, vararg contents: Any): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder and add the provided padding on the left side until the result is
     * **at most** `width` wide.
     *
     * An example of usage is:
     *
     * builder.appendWithPaddingLeft(15, '-', "hello");
     *
     * which appends the result `"----------hello"`
     *
     * If the value for padding is longer than one character, the appended result may not reach the full provided `width`.
     * This can be seen in the following example:
     *
     * builder.appendWithPaddingLeft(15, "abc", "hello");
     *
     * which appends the result `"abcabcabchello"` (14 characters instead of 15)
     *
     * This happens because the remaining width is not a multiple of the length of the provided padding, which makes it
     * impossible to fill the full length of the appended result.
     *
     * Note: The appended result will be at most `width` wide, or an exception will be thrown
     *
     * @param width    The maximum total width after padding
     * @param padding  The padding to fill the left side with
     * @param contents The contents to append
     * @return `this`
     * @throws IllegalArgumentException If width &lt; 1
     * @throws IllegalArgumentException If padding length &lt; 1
     * @throws IllegalArgumentException If padding length &gt; width
     * @throws IllegalArgumentException If contents length &gt; width
     */
    fun appendWithPaddingLeft(width: Int, padding: Any, vararg contents: CharSequence): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder and add the provided padding on both the left and right side until the
     * result is **at most** `width` wide.
     *
     * An example of usage is:
     *
     * builder.appendWithPaddingAround(15, '-', "hello");
     *
     * which appends the result `"-----hello-----"`
     *
     * If the value for padding is longer than one character, the appended result may not reach the full provided `width`.
     * This can be seen in the following example:
     *
     * builder.appendWithPaddingAround(15, "abc", "hello");
     *
     * which appends the result `"abchelloabc"` (11 characters instead of 15)
     *
     * This happens because the remaining width is not a multiple of the length of the provided padding, which makes it
     * impossible to fill the full length of the appended result.
     *
     * Note: The appended result will be at most `width` wide, or an exception will be thrown
     * Note: The amount of padding on the left and right side will always be the same
     *
     * @param width    The maximum total width after padding
     * @param padding  The padding to fill the left and right side with
     * @param contents The contents to append
     * @return `this`
     * @throws IllegalArgumentException If width &lt; 1
     * @throws IllegalArgumentException If padding length &lt; 1
     * @throws IllegalArgumentException If padding length &gt; width
     * @throws IllegalArgumentException If contents length &gt; width
     */
    fun appendWithPaddingAround(width: Int, padding: Any, vararg contents: Any): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder and add the provided padding on both the left and right side until the
     * result is **at most** `width` wide.
     *
     * An example of usage is:
     *
     * builder.appendWithPaddingAround(15, '-', "hello");
     *
     * which appends the result `"-----hello-----"`
     *
     * If the value for padding is longer than one character, the appended result may not reach the full provided `width`.
     * This can be seen in the following example:
     *
     * builder.appendWithPaddingAround(15, "abc", "hello");
     *
     * which appends the result `"abchelloabc"` (11 characters instead of 15)
     *
     * This happens because the remaining width is not a multiple of the length of the provided padding, which makes it
     * impossible to fill the full length of the appended result.
     *
     * Note: The appended result will be at most `width` wide, or an exception will be thrown
     * Note: The amount of padding on the left and right side will always be the same
     *
     * @param width    The maximum total width after padding
     * @param padding  The padding to fill the left and right side with
     * @param contents The contents to append
     * @return `this`
     * @throws IllegalArgumentException If width &lt; 1
     * @throws IllegalArgumentException If padding length &lt; 1
     * @throws IllegalArgumentException If padding length &gt; width
     * @throws IllegalArgumentException If contents length &gt; width
     */
    fun appendWithPaddingAround(width: Int, padding: Any, vararg contents: CharSequence): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder and add the provided padding on the right side until the result is
     * **at most** `width` wide.
     *
     * An example of usage is:
     *
     * builder.appendWithPaddingRight(15, '-', "hello");
     *
     * which appends the result `"hello----------"`
     *
     * If the value for padding is longer than one character, the appended result may not reach the full provided `width`.
     * This can be seen in the following example:
     *
     * builder.appendWithPaddingRight(15, "abc", "hello");
     *
     * which appends the result `"helloabcabcabc"` (14 characters instead of 15)
     *
     * This happens because the remaining width is not a multiple of the length of the provided padding, which makes it
     * impossible to fill the full length of the appended result.
     *
     * Note: The appended result will be at most `width` wide, or an exception will be thrown
     *
     * @param width    The maximum total width after padding
     * @param padding  The padding to fill the right side with
     * @param contents The contents to append
     * @return `this`
     * @throws IllegalArgumentException If width &lt; 1
     * @throws IllegalArgumentException If padding length &lt; 1
     * @throws IllegalArgumentException If padding length &gt; width
     * @throws IllegalArgumentException If contents length &gt; width
     */
    fun appendWithPaddingRight(width: Int, padding: Any, vararg contents: Any): Builder<TCommandSource>

    /**
     * Append the provided contents to this builder and add the provided padding on the right side until the result is
     * **at most** `width` wide.
     *
     * An example of usage is:
     *
     * builder.appendWithPaddingRight(15, '-', "hello");
     *
     * which appends the result `"hello----------"`
     *
     * If the value for padding is longer than one character, the appended result may not reach the full provided `width`.
     * This can be seen in the following example:
     *
     * builder.appendWithPaddingRight(15, "abc", "hello");
     *
     * which appends the result `"helloabcabcabc"` (14 characters instead of 15)
     *
     * This happens because the remaining width is not a multiple of the length of the provided padding, which makes it
     * impossible to fill the full length of the appended result.
     *
     * Note: The appended result will be at most `width` wide, or an exception will be thrown
     *
     * @param width    The maximum total width after padding
     * @param padding  The padding to fill the right side with
     * @param contents The contents to append
     * @return `this`
     * @throws IllegalArgumentException If width &lt; 1
     * @throws IllegalArgumentException If padding length &lt; 1
     * @throws IllegalArgumentException If padding length &gt; width
     * @throws IllegalArgumentException If contents length &gt; width
     */
    fun appendWithPaddingRight(width: Int, padding: Any, vararg contents: CharSequence): Builder<TCommandSource>

    /**
     * Append the provided contents if `condition` is true.
     *
     * This method is the same as:
     *
     * if (condition) {
     * builder.append(contents);
     * }
     *
     * @param condition The condition to check
     * @param contents  The contents to append
     * @return `this`
     */
    fun appendIf(condition: Boolean, vararg contents: Any): Builder<TCommandSource>

    /**
     * Append the provided contents if `condition` is true.
     *
     * This method is the same as:
     *
     * if (condition) {
     * builder.append(contents);
     * }
     *
     * @param condition The condition to check
     * @param contents  The contents to append
     * @return `this`
     */
    fun appendIf(condition: Boolean, vararg contents: CharSequence): Builder<TCommandSource>

    /**
     * Append the provided contents, putting the provided delimiter between each element
     *
     * builder.appendJoining(", ", "Bob", "Alice", "Steven");
     *
     * is the same as
     *
     * builder.append("Bob", ", ", "Alice", ", ", "Steven");
     *
     * They both append: `"Bob, Alice, Steven"`
     *
     * @param delimiter The delimiter to put between each element
     * @param contents  The contents to append
     * @return `this`
     */
    fun appendJoining(delimiter: Any, vararg contents: Any): Builder<TCommandSource>

    /**
     * Append the provided contents, putting the provided delimiter between each element
     *
     * builder.appendJoining(", ", "Bob", "Alice", "Steven");
     *
     * is the same as
     *
     * builder.append("Bob", ", ", "Alice", ", ", "Steven");
     *
     * They both append: `"Bob, Alice, Steven"`
     *
     * @param delimiter The delimiter to put between each element
     * @param contents  The contents to append
     * @return `this`
     */
    fun appendJoining(delimiter: Any, vararg contents: CharSequence): Builder<TCommandSource>

    /**
     * Append the provided contents if `condition` is true, putting the provided delimiter between each element
     *
     * This is a combination of [.appendIf] and [.appendJoining]
     *
     * builder.appendJoiningIf(condition, ", ", "Bob", "Alice", "Steven");
     *
     * is the same as
     *
     * if (condition) {
     * builder.append("Bob", ", ", "Alice", ", ", "Steven");
     * }
     *
     * They both append: `"Bob, Alice, Steven"` if `condition` is true
     *
     * @param condition The condition to check before appending
     * @param delimiter The delimiter to put between each element
     * @param contents  The contents to append
     * @return `this`
     */
    fun appendJoiningIf(condition: Boolean, delimiter: Any, vararg contents: Any): Builder<TCommandSource>

    /**
     * Append the provided contents if `condition` is true, putting the provided delimiter between each element
     *
     * This is a combination of [.appendIf] and [.appendJoining]
     *
     * builder.appendJoiningIf(condition, ", ", "Bob", "Alice", "Steven");
     *
     * is the same as
     *
     * if (condition) {
     * builder.append("Bob", ", ", "Alice", ", ", "Steven");
     * }
     *
     * They both append: `"Bob, Alice, Steven"` if `condition` is true
     *
     * @param condition The condition to check before appending
     * @param delimiter The delimiter to put between each element
     * @param contents  The contents to append
     * @return `this`
     */
    fun appendJoiningIf(condition: Boolean, delimiter: Any, vararg contents: CharSequence): Builder<TCommandSource>

    /**
     * Fetches the plugin prefix from the bound [PluginInfo] and appends it to this builder.
     *
     * @return `this`
     */
    fun appendPrefix(): Builder<TCommandSource>

    /**
     * Show the provided text as a tooltip while the mouse is hovering over this text.
     *
     * @param text The text to show on hover
     * @return `this`
     */
    fun onHoverShowText(text: Component): Builder<TCommandSource>

    /**
     * Show the provided text as a tooltip while the mouse is hovering over this text.
     *
     * @param builder The text to show on hover
     * @return `this`
     */
    fun onHoverShowText(builder: Builder<TCommandSource>): Builder<TCommandSource>

    /**
     * Suggest the provided command to the provided[TCommandSource] when they click This puts it in their chat bar but
     * does not run the command.
     *
     * @param command The command to suggest
     * @return `this`
     */
    fun onClickSuggestCommand(command: String): Builder<TCommandSource>

    /**
     * Run the provided command as the provided [TCommandSource] when they click.
     *
     * @param command The command to run
     * @return `this`
     */
    fun onClickRunCommand(command: String): Builder<TCommandSource>

    /**
     * Run the provided callback, passing the provided [TCommandSource], when they click.
     *
     * @param callback The callback to run
     * @return `this`
     */
    fun onClickExecuteCallback(callback: Consumer<TCommandSource>): Builder<TCommandSource>

    /**
     * Open a url in a browser when this text is clicked.
     *
     * @param url The url to open
     * @return `this`
     */
    fun onClickOpenUrl(url: URL): Builder<TCommandSource>

    /**
     * Open a url in a browser when this text is clicked.
     *
     * @param url The url to open
     * @return `this`
     */
    fun onClickOpenUrl(url: String): Builder<TCommandSource>

    /**
     * Creates a [Component] from this builder.
     *
     * @return The built [Component]
     */
    fun build(): Component

    /**
     * Creates a [text][Component] from this builder and sends it to the provided [receiver][TCommandSource].
     *
     * @param receiver The [receiver][TCommandSource] to send the [text][Component] to
     */
    fun sendTo(receiver: TCommandSource)

    /**
     * Creates a [Component] from this builder and sends it to the console.
     */
    fun sendToConsole()
  }

  interface PaginationBuilder<TCommandSource> {
    /**
     * Sets the contents of this [PaginationBuilder].
     *
     * @param contents The contents to set
     * @return `this`
     */
    fun contents(vararg contents: Component): PaginationBuilder<TCommandSource>

    /**
     * Sets the contents of this [PaginationBuilder].
     *
     * @param contents The contents to set
     * @return `this`
     */
    fun contents(contents: Iterable<Component>): PaginationBuilder<TCommandSource>

    /**
     * Sets the title of this [PaginationBuilder].
     *
     * @param title The title to set, should be exactly one line
     * @return `this`
     */
    fun title(title: Component?): PaginationBuilder<TCommandSource>

    /**
     * Sets the title of this [PaginationBuilder].
     *
     * @param title The title to set, should be exactly one line
     * @return `this`
     */
    fun title(title: Builder<TCommandSource>?): PaginationBuilder<TCommandSource>

    /**
     * Sets the header of this [PaginationBuilder].
     *
     * If the header is not specified, or passed in as `null`, it will be omitted when displaying this pagination.
     *
     * @param header The header to set
     * @return `this`
     */
    fun header(header: Component?): PaginationBuilder<TCommandSource>

    /**
     * Sets the header of this [PaginationBuilder].
     *
     * If the header is not specified, or passed in as `null`, it will be omitted when displaying this pagination.
     *
     * @param header The header to set
     * @return `this`
     */
    fun header(header: Builder<TCommandSource>?): PaginationBuilder<TCommandSource>

    /**
     * Sets the footer of this [PaginationBuilder].
     *
     * If the footer is not specified, or passed in as `null`, it will be omitted when displaying this pagination.
     *
     * @param footer The footer to set
     * @return `this`
     */
    fun footer(footer: Component?): PaginationBuilder<TCommandSource>

    /**
     * Sets the footer of this [PaginationBuilder].
     *
     * If the footer is not specified, or passed in as `null`, it will be omitted when displaying this pagination.
     *
     * @param footer The footer to set
     * @return `this`
     */
    fun footer(footer: Builder<TCommandSource>?): PaginationBuilder<TCommandSource>

    /**
     * Sets the padding of this [PaginationBuilder].
     *
     * @param padding The padding to set
     * @return `this`
     */
    fun padding(padding: Component): PaginationBuilder<TCommandSource>

    /**
     * Sets the padding of this [PaginationBuilder].
     *
     * @param padding The padding to set
     * @return `this`
     */
    fun padding(padding: Builder<TCommandSource>): PaginationBuilder<TCommandSource>

    /**
     * Sets the maximum number of lines for this [PaginationBuilder].
     *
     * @param linesPerPage The lines per page to set
     * @return `this`
     * @throws IllegalArgumentException If linesPerPage &lt; 1
     */
    fun linesPerPage(linesPerPage: Int): PaginationBuilder<TCommandSource>

    /**
     * Creates a [Pagination] from this builder
     *
     * @return The built [Pagination]
     */
    fun build(): Pagination<TCommandSource>
  }

  interface Pagination<TCommandSource> {
    /**
     * @return The contents of this [Pagination]
     */
    val contents: Iterable<Component>

    /**
     * @return The title of this [Pagination]
     */
    val title: Component?

    /**
     * @return The header of this [Pagination]
     */
    val header: Component?

    /**
     * @return The footer of this [Pagination]
     */
    val footer: Component?

    /**
     * @return The padding of this [Pagination]
     */
    val padding: Component

    /**
     * @return The lines per page of this [Pagination]
     */
    val linesPerPage: Int

    /**
     * Sends this [Pagination] to the provided [receiver][TCommandSource]
     *
     * @param receiver The [receiver][TCommandSource] to send this [Pagination] to
     */
    fun sendTo(receiver: TCommandSource)

    /**
     * Sends this [Pagination] to the console
     */
    fun sendToConsole()
  }
}
