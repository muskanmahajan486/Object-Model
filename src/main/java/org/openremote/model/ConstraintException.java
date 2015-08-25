/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2015, OpenRemote Inc.
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
package org.openremote.model;

import org.openremote.base.exception.OpenRemoteRuntimeException;

/**
 * A constraint exception that can be used to indicate when domain object attribute
 * values are not within their designed constraints. This type can be used to differentiate
 * from regular {@link IllegalArgumentException} that can be thrown by any third party code in
 * addition to OpenRemote libraries. In addition it supports parameterization of the
 * exception message to simplify formatting. <p>
 *
 * This is an unchecked runtime exception which the client must handle specifically.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ConstraintException extends OpenRemoteRuntimeException
{

  /**
   * Constructs a new constraint exception with a given message.
   *
   * @param msg  human-readable error message
   */
  public ConstraintException(String msg)
  {
    super(msg);
  }

  /**
   * Constructs a new constraint exception with a parameterized message.
   *
   * @param msg     human-readable error message
   * @param params  exception message parameters -- message parameterization must be
   *                compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public ConstraintException(String msg, Object... params)
  {
    super(msg, params);
  }
}

