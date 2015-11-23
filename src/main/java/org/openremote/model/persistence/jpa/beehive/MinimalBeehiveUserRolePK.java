/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2014-2015, OpenRemote Inc.
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
package org.openremote.model.persistence.jpa.beehive;

import java.io.Serializable;

public class MinimalBeehiveUserRolePK implements Serializable
{
  private Long userId;
  private Long roleId;

  public MinimalBeehiveUserRolePK()
  {
    // Class must have a no-arg constructor
  }

  public Long getUserId()
  {
    return userId;
  }

  public void setUserId(Long userId)
  {
    this.userId = userId;
  }

  public Long getRoleId()
  {
    return roleId;
  }

  public void setRoleId(Long roleId)
  {
    this.roleId = roleId;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    MinimalBeehiveUserRolePK that = (MinimalBeehiveUserRolePK) o;

    if (userId != null ? !userId.equals(that.userId) : that.userId != null)
    {
      return false;
    }
    return !(roleId != null ? !roleId.equals(that.roleId) : that.roleId != null);

  }

  @Override
  public int hashCode()
  {
    int result = userId != null ? userId.hashCode() : 0;
    result = 31 * result + (roleId != null ? roleId.hashCode() : 0);
    return result;
  }
}
