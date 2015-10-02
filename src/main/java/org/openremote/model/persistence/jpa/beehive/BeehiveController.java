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
package org.openremote.model.persistence.jpa.beehive;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.openremote.model.Controller;
import org.openremote.model.persistence.jpa.RelationalAccount;
import org.openremote.model.persistence.jpa.RelationalController;

@Entity(name = "BeehiveController")
@Table(name = "controller")
public class BeehiveController extends RelationalController
{
  @Column(name = "linked", nullable = false, unique = false)
  private boolean linked = false;

  @Column(name = "macAddress", nullable = true, unique = false, length = 255)
  private String macAddresses;

  protected BeehiveController()
  {
  }

  public BeehiveController(RelationalAccount acct, Controller copy)
  {
    super(acct, copy);
    this.macAddresses = super.getMacAddresses("-");
    this.linked = true;
  }
}
