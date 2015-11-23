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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Straightfoward mapping to role table, not taking into account any relationships.
 */
@Entity
@Table(name = "role")
@NamedQuery(
        name="findRoleByName",
        query="SELECT r FROM MinimalBeehiveRole r WHERE r.name = :roleName"
)
public class MinimalBeehiveRole
{
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "oid")
  @Id
  private Long id;

  @Column(name="name")
  private String name;

  public Long getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

}
