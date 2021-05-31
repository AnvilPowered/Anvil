/*
 * Anvil - AnvilPowered
 *   Copyright (C) 2020
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package org.anvilpowered.anvil.sponge8.util

import com.google.inject.Binder
import com.google.inject.Inject
import org.anvilpowered.anvil.api.Environment
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.slf4j.Marker
import org.slf4j.helpers.MarkerIgnoringBase
import org.slf4j.spi.LocationAwareLogger

open class Log4jAdapter(name: String) : MarkerIgnoringBase(), LocationAwareLogger {

  @Inject
  private lateinit var logger: Logger

  init {
    this.name = name
  }

  companion object {
    fun bindLogger(environment: Environment, binder: Binder) {
      binder.bind(org.slf4j.Logger::class.java).toInstance(object : Log4jAdapter(environment.name) {
      })
    }
  }

  override fun isTraceEnabled(): Boolean = logger.isTraceEnabled

  override fun trace(msg: String) = logger.trace(msg)
  override fun trace(format: String, arg: Any) = logger.trace(format, arg)
  override fun trace(format: String, arg1: Any, arg2: Any) = logger.trace(format, arg1, arg2)
  override fun trace(format: String, vararg arguments: Any) = logger.trace(format, arguments)
  override fun trace(msg: String, t: Throwable) = logger.trace(msg, t)

  override fun isDebugEnabled(): Boolean = logger.isDebugEnabled
  override fun debug(msg: String) = logger.debug(msg)
  override fun debug(format: String, arg: Any) = logger.debug(format, arg)
  override fun debug(format: String, arg1: Any, arg2: Any) = logger.debug(format, arg1, arg2)
  override fun debug(format: String, vararg arguments: Any) = logger.debug(format, arguments)
  override fun debug(msg: String, t: Throwable) = logger.debug(msg, t)

  override fun isInfoEnabled(): Boolean = logger.isInfoEnabled
  override fun info(msg: String) = logger.info(msg)
  override fun info(format: String, arg: Any) = logger.info(format, arg)
  override fun info(format: String, arg1: Any, arg2: Any) = logger.info(format, arg1, arg2)
  override fun info(format: String, vararg arguments: Any) = logger.info(format, arguments)
  override fun info(msg: String, t: Throwable) = logger.info(msg, t)

  override fun isWarnEnabled(): Boolean = logger.isWarnEnabled
  override fun warn(msg: String) = logger.warn(msg)
  override fun warn(format: String, arg: Any) = logger.warn(format, arg)
  override fun warn(format: String, vararg arguments: Any) = logger.warn(format, arguments)
  override fun warn(format: String, arg1: Any, arg2: Any) = logger.warn(format, arg1, arg2)
  override fun warn(msg: String, t: Throwable) = logger.warn(msg, t)

  override fun isErrorEnabled(): Boolean = logger.isErrorEnabled
  override fun error(msg: String) = logger.error(msg)
  override fun error(format: String, arg: Any) = logger.error(format, arg)
  override fun error(format: String, arg1: Any, arg2: Any) = logger.error(format, arg1, arg2)
  override fun error(format: String, vararg arguments: Any) = logger.error(format, arguments)
  override fun error(msg: String, t: Throwable) = logger.error(msg, t)

  override fun log(
    marker: Marker,
    fqcn: String,
    level: Int,
    message: String,
    argArray: Array<out Any>,
    t: Throwable
  ) {
    var level0: Level = Level.INFO
    when (level) {
      LocationAwareLogger.TRACE_INT -> {
        level0 = Level.TRACE
      }
      LocationAwareLogger.DEBUG_INT -> {
        level0 = Level.DEBUG
      }
      LocationAwareLogger.INFO_INT -> {
        level0 = Level.INFO
      }
      LocationAwareLogger.WARN_INT -> {
        level0 = Level.WARN
      }
      LocationAwareLogger.ERROR_INT -> {
        level0 = Level.ERROR
      }
    }
    logger.log(level0, fqcn, level, message, argArray, t)
  }
}
