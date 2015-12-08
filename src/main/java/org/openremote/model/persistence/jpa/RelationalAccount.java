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
package org.openremote.model.persistence.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.openremote.model.Account;
import org.openremote.model.Model.ValidationException;

@Entity(name = "Account")
@Table(name = "account")
public class RelationalAccount extends Account
{
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "oid")
  @Id
  private Long id;

  public RelationalAccount()
  {
    super(new Account());
  }

  public RelationalAccount(String identifier) throws ValidationException
  {
    super(identifier);
  }

  public RelationalAccount(Account copy)
  {
    super(copy);
  }

  public Long getId()
  {
    return id;
  }

  public String toString()
  {
    return "JPA Account (ID = " + this.id + ")";
  }

  public int hashCode()
  {
    return (int)this.id.longValue();
  }

  public boolean equals(Object other)
  {
    if(other == null)
    {
      return false;
    }
    else if(other.getClass() != this.getClass())
    {
      return false;
    }
    else
    {
      RelationalAccount acct = (RelationalAccount)other;
      return this.id.longValue() == acct.id.longValue();
    }
  }
}
