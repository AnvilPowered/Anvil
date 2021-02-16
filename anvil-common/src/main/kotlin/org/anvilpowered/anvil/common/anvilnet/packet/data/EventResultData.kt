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

package org.anvilpowered.anvil.common.anvilnet.packet.data

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.common.event.EventPostResultImpl

class EventResultData<E : Event> : DataContainer {

  lateinit var tree: EventPostResultImpl.Tree<E>
    private set

  constructor(tree: EventPostResultImpl.Tree<E>) {
    this.tree = tree
  }

  constructor(input: ByteArrayDataInput) {
    read(input)
  }

  override fun read(input: ByteArrayDataInput) {
    tree = EventPostResultImpl.Tree(input)
  }

  override fun write(output: ByteArrayDataOutput) {
    tree.write(output)
  }
}