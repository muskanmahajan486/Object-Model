/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.model.data.json;

import org.openremote.base.exception.OpenRemoteException;

/**
 * Exception type that indicates errors in the deserialization process when attempting
 * to convert JSON document instances into Java models.
 *
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DeserializationException extends OpenRemoteException
{
  /**
   * Constructs a new exception with a given message.
   *
   * @param msg
   *              exception message
   */
  public DeserializationException(String msg)
  {
    super(msg);
  }

  /**
   * Constructs a new exception message with a given message and message parameters.
   *
   * @param msg
   *                exception message formatted according to description in
   *                {@link org.openremote.base.exception.OpenRemoteException}
   *
   * @param params
   *                message parameters
   */
  public DeserializationException(String msg, Object... params)
  {
    super(msg, params);
  }

  /**
   * Constructs a new exception message with a given root cause.
   *
   * @param msg
   *              exception message
   *
   * @param rootCause
   *              original exception that caused this error
   */
  public DeserializationException(String msg, Throwable rootCause)
  {
    super(msg, rootCause);
  }

  /**
   * Constructs a new exception message with a given root cause and message parameters.
   *
   * @param msg
   *            exception message formatted according to description in
   *            {@link org.openremote.base.exception.OpenRemoteException}
   *
   * @param rootCause
   *            original exception that caused this error
   *
   * @param params
   *            message parameters
   */
  public DeserializationException(String msg, Throwable rootCause, Object... params)
  {
    super(msg, rootCause, params);
  }
}

