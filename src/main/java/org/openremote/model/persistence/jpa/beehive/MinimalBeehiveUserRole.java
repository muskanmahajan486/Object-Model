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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Straightforward mapping to the user_role table.
 * This is a bit un-usual as this is a join table for a many-to-many relationship
 * but directly manipulating it is simpler and more efficient in this version.
 */
@Entity
@Table(name = "user_role")
@IdClass(value = MinimalBeehiveUserRolePK.class)
@NamedQuery(
        name="findRolesForUser",
        query="SELECT ur FROM MinimalBeehiveUserRole ur WHERE ur.userId = :userId"
)

public class MinimalBeehiveUserRole
{
  @Id
  @Column(name = "user_oid")
  private Long userId;

  @Id
  @Column(name = "role_oid")
  private Long roleId;

  public MinimalBeehiveUserRole()
  {
  }

  public MinimalBeehiveUserRole(Long userId, Long roleId)
  {
    this.userId = userId;
    this.roleId = roleId;
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
}
