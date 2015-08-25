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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link org.openremote.model.ConstraintException} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ConstraintExceptionTest
{

  @Test public void testConstructor()
  {
    ConstraintException ce = new ConstraintException("test");

    Assert.assertTrue(ce.getMessage().equals("test"));

    ce = new ConstraintException("∫åç");

    Assert.assertTrue(ce.getMessage().equals("∫åç"));
  }

  @Test public void testParameterizedMessage()
  {
    ConstraintException ce = new ConstraintException("test {0} {1} {2}", "foo", "bar", "acme");

    Assert.assertTrue(ce.getMessage().equals("test foo bar acme"));
  }
}

