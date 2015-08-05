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
import org.openremote.model.data.json.AccountTransformer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class Account extends Model
{
  public static final Version JSON_SCHEMA_VERSION = new Version(2, 0, 0);
  public static final int ACCOUNT_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT = 1000;
  protected static Validator<String> accountIdValidator = new Validator<String>()
  {
    public void validate(String accountIdentifier) throws ValidationException
    {
      AccountTransformer.getIdValidator().validate(accountIdentifier);
    }
  };

  protected String accountIdentifier;
  protected Map<String, String> accountAttributes;
  protected Set<User> users;

  protected Account(Account copy)
  {
    super(copy.jsonTransformer);
    this.accountAttributes = new ConcurrentHashMap(0);
    this.users = new CopyOnWriteArraySet();
    this.accountIdentifier = copy.accountIdentifier;
    if (copy.accountAttributes != null)
    {
      this.accountAttributes = new ConcurrentHashMap(copy.accountAttributes);
    }

    if (copy.users != null)
    {
      this.users = new CopyOnWriteArraySet();
      Iterator i$ = copy.users.iterator();

      while (i$.hasNext())
      {
        User user = (User) i$.next();
        this.users.add(new User(user));
      }
    }

  }

  public Account()
  {
    super(new AccountTransformer());
    this.accountAttributes = new ConcurrentHashMap(0);
    this.users = new CopyOnWriteArraySet();
  }

  public Account(String accountIdentifier) throws ValidationException
  {
    this();
    this.accountIdentifier = accountIdentifier == null ? null : accountIdentifier.trim();
    accountIdValidator.validate(accountIdentifier);
  }

  public Account addAttribute(String name, String value)
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
        throw new ConstraintException("Account attribute name string can be at most {0} characters,"
                + " name string \'\'{1}\'\' is {2} characters long.",
                new Object[]{Integer.valueOf(255), name, Integer.valueOf(name.length())});
      } else if (value.length() > 1000)
      {
        throw new ConstraintException("Account attribute value string can be at most {0} characters,"
                + " value string \'\'{1}\'\' is {2} characters long",
                new Object[]{Integer.valueOf(1000), value, Integer.valueOf(value.length())});
      } else
      {
        this.accountAttributes.put(name, value);
        return this;
      }
    } else
    {
      return this;
    }
  }

  public boolean hasAttribute(String attributeName)
  {
    return this.accountAttributes.containsKey(attributeName);
  }
}
