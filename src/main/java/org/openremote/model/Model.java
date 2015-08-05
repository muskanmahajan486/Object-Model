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

import java.nio.charset.Charset;

import org.openremote.base.exception.OpenRemoteException;
import org.openremote.model.data.json.JSONTransformer;

public class Model
{
  public static final int DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT = 255;
  public static final Charset UTF8 = Charset.forName("UTF-8");
  public static final Charset DEFAULT_CHARSET;
  protected transient JSONTransformer<? extends Model> jsonTransformer;

  protected Model(JSONTransformer<? extends Model> transformer)
  {
    this.jsonTransformer = transformer;
  }

  static
  {
    DEFAULT_CHARSET = UTF8;
  }

  public static class ValidationException extends OpenRemoteException
  {
    public ValidationException(String msg)
    {
      super(msg);
    }

    public ValidationException(String msg, Object... params)
    {
      super(msg, params);
    }

    public ValidationException(String msg, Throwable cause)
    {
      super(msg, cause);
    }

    public ValidationException(String msg, Throwable cause, Object... params)
    {
      super(msg, cause, params);
    }
  }

  public interface Validator<T>
  {
    void validate(T var1) throws Model.ValidationException;
  }
}
