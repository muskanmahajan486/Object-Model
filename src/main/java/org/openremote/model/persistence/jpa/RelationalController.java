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

import org.openremote.model.Controller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "Controller")
@Table(name = "controller")
public class RelationalController extends Controller
{
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "oid")
  @Id
  private Long id;

  @ManyToOne
  @JoinColumn(name = "account_oid")
  private RelationalAccount account;

  protected RelationalController()
  {
  }

  public RelationalController(RelationalAccount acct, Controller copy)
  {
    super(copy);
    this.account = acct;
  }
}
