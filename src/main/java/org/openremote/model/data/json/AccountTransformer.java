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
package org.openremote.model.data.json;

import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.model.Account;
import org.openremote.model.Model.ValidationException;
import org.openremote.model.User;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AccountTransformer extends JSONTransformer<Account>
{
  protected static JSONValidator<String> defaultIdValidator = new JSONValidator<String>()
  {
    final int minimumAccountIdentifierLength = 1;
    final int maximumAccountIdentifierLength = 255;

    public void validate(String accountIdentifier) throws ValidationException
    {
      if (accountIdentifier == null || accountIdentifier.length() < 1 || accountIdentifier.length() > 255)
      {
        throw new ValidationException("Invalid account identifier \'{0}\'. Account identifier cannot be null,"
                + " an empty string or a string exceeding {1} characters.", new Object[]{accountIdentifier, Integer.valueOf(255)});
      }
    }
  };

  public static JSONValidator<String> getIdValidator()
  {
    return defaultIdValidator;
  }

  public AccountTransformer()
  {
    super(Account.class);
  }

  public void write(Account account)
  {
    AccountTransformer.AccountData data = new AccountTransformer.AccountData(account);
    this.startObject();
    this.writeProperty("accountIdentifier", data.accountId);
    if (!data.attributes.isEmpty())
    {
      this.writeProperty("accountAttributes", data.attributes);
    }

    if (!data.users.isEmpty())
    {
      this.writeProperty("users", data.users);
    }

    this.endObject();
  }

  public Account deserialize(JSONModel model)
  {
    throw new IncorrectImplementationException("Not Implemented.");
  }

  private static class AccountData extends Account
  {
    private String accountId;
    private Map<String, String> attributes;
    private Set<User> users;

    private AccountData(Account account)
    {
      super(account);
      this.accountId = super.accountIdentifier;
      this.attributes = Collections.unmodifiableMap(super.accountAttributes);
      this.users = Collections.unmodifiableSet(super.users);
    }
  }
}
