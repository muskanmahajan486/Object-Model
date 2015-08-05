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

import org.openremote.base.Version;
import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.model.data.json.ControllerTransformer;
import org.openremote.model.data.json.JSONHeader;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class Controller extends Model
{
  public static final Version JSON_SCHEMA_VERSION = new Version(4, 0, 0);
  public static final int CONTROLLER_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT = 1000;
  protected Set<String> macAddresses = new CopyOnWriteArraySet();
  protected Map<String, String> controllerAttributes = new ConcurrentHashMap(0);
  protected String name = null;
  protected String description = null;
  protected String identity = UUID.randomUUID().toString();
  protected transient Map<String, Map<Controller.Configuration.Category, Controller.Configuration>> configurations =
          new ConcurrentHashMap();

  protected Controller(Controller copy)
  {
    super(new ControllerTransformer());
    this.identity = copy.identity;
    this.name = copy.name;
    this.description = copy.description;
    if (copy.macAddresses != null)
    {
      this.macAddresses = new CopyOnWriteArraySet(copy.macAddresses);
    }

    if (copy.controllerAttributes != null)
    {
      this.controllerAttributes = new ConcurrentHashMap(copy.controllerAttributes);
    }

    if (copy.configurations != null)
    {
      this.configurations = new ConcurrentHashMap(copy.configurations);
    }

  }

  public Controller()
  {
    super(new ControllerTransformer());
  }

  public Controller(String id, Set<String> macs, String name, String description)
  {
    super(new ControllerTransformer());
    if (id != null && !id.equals(""))
    {
      this.identity = id;
      Iterator i$ = macs.iterator();

      while (i$.hasNext())
      {
        String mac = (String) i$.next();
        this.addMacAddress(mac);
      }

      this.name = name;
      this.description = description;
    } else
    {
      throw new IncorrectImplementationException(
              "Controller identity is a mandatory property -- received null or empty id.");
    }
  }

  public void addMacAddress(int[] bytes)
  {
    Byte[] mac = new Byte[bytes.length];

    for (int index = 0; index < bytes.length; ++index)
    {
      mac[index] = Byte.valueOf((byte) (bytes[index] & 255));
    }

    this.addMacAddress(mac);
  }

  public void addMacAddress(Byte[] mac)
  {
    StringBuilder builder = new StringBuilder();

    for (int index = 0; index < mac.length; ++index)
    {
      builder.append(String.format("%1$02X", new Object[]{mac[index]}));
      if (index != mac.length - 1)
      {
        builder.append(":");
      }
    }

    this.addMacAddress(builder.toString());
  }

  public void addMacAddress(String mac)
  {
    if (mac != null)
    {
      mac = mac.trim();
      if (mac.contains("-"))
      {
        mac = mac.replaceAll("-", ":");
      }

      this.macAddresses.add(mac);
    }
  }

  public String getMacAddresses()
  {
    return this.getMacAddresses(":");
  }

  public String getMacAddresses(String separator)
  {
    StringBuilder builder = new StringBuilder();
    Iterator it = this.macAddresses.iterator();

    for (int addressNumber = 0; addressNumber < this.macAddresses.size(); ++addressNumber)
    {
      builder.append((String) it.next());
      if (it.hasNext())
      {
        builder.append(",");
      }
    }

    return builder.toString().replaceAll(":", separator);
  }

  public Controller addAttribute(String name, String value)
  {
    if (name != null && !name.equals(""))
    {
      if (value == null)
      {
        value = "";
      }

      name = name.trim();
      value = value.trim();
      if (name.length() > 255)
      {
        throw new ConstraintException("Controller attribute name string can be at most {0} characters,"
                + " name string \'\'{1}\'\' is {2} characters long.",
                new Object[]{Integer.valueOf(255), name, Integer.valueOf(name.length())});
      } else if (value.length() > 1000)
      {
        throw new ConstraintException("Controller attribute value string can be at most {0} characters,"
                + " value string \'\'{1}\'\' is {2} characters long",
                new Object[]{Integer.valueOf(1000), value, Integer.valueOf(value.length())});
      } else
      {
        this.controllerAttributes.put(name, value);
        return this;
      }
    } else
    {
      return this;
    }
  }

  public boolean hasAttribute(String attributeName)
  {
    return this.controllerAttributes.containsKey(attributeName);
  }

  public String toJSONString()
  {
    return JSONHeader.toJSON(this, JSON_SCHEMA_VERSION, this.jsonTransformer);
  }

  public static class Configuration
  {
    public Configuration()
    {
    }

    public static class Category
    {
      public Category()
      {
      }
    }
  }
}
