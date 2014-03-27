/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
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

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for {@link org.openremote.model.DeviceDiscovery} class
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DeviceDiscoveryTest
{

  // Base constructor tests -----------------------------------------------------------------------

  /**
   * Base constructor test.
   */
  @Test public void testBasicConstructor()
  {
    DeviceDiscovery dd = new DeviceDiscovery("name", "protocol", "model");

    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 0);
  }

  /**
   * Null arg test on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorNullArg()
  {
    new DeviceDiscovery(null, "protocol", "model");
  }

  /**
   * Null arg test (protocol string) on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorNullArg2()
  {
    new DeviceDiscovery("name", null, "model");
  }

  /**
   * Empty string arg test on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorEmptyArg()
  {
    new DeviceDiscovery("", "protocol", "model");
  }

  /**
   * Empty string arg test (protocol string) on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorEmptyArg2()
  {
    new DeviceDiscovery("name", "", "model");
  }


  /**
   * Null arg test on model string. Should fall back to empty string.
   */
  @Test public void testBasicConstructorNullModel()
  {
    DeviceDiscovery dd = new DeviceDiscovery("name", "protocol", null);

    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals(""));
    Assert.assertTrue(dd.type.equals(""));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 0);
  }


  // Attribute constructor tests ------------------------------------------------------------------

  /**
   * Basic test for constructor that accepts attributes.
   */
  @Test public void testAttributeConstructor()
  {
    Map<String, String> attrs = new HashMap<String, String>(3);
    attrs.put("1", "one");
    attrs.put("2", "two");
    attrs.put("3", "three");

    DeviceDiscovery dd = new DeviceDiscovery("name", "protocol", null, attrs);

    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals(""));
    Assert.assertTrue(dd.type.equals(""));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 3);

    Assert.assertTrue(dd.deviceAttributes.keySet().contains("1"));
    Assert.assertTrue(dd.deviceAttributes.keySet().contains("2"));
    Assert.assertTrue(dd.deviceAttributes.keySet().contains("3"));

    Assert.assertTrue(dd.deviceAttributes.values().contains("one"));
    Assert.assertTrue(dd.deviceAttributes.values().contains("two"));
    Assert.assertTrue(dd.deviceAttributes.values().contains("three"));
  }

  /**
   * Test constructor with null attributes, should default to empty collection.
   */
  @Test public void testAttributeConstructorNullArg()
  {
    DeviceDiscovery dd = new DeviceDiscovery("name", "protocol", null, null);

    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals(""));
    Assert.assertTrue(dd.type.equals(""));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 0);
  }


  // Type constructor tests -----------------------------------------------------------------------

  /**
   * Basic tests for constructor that accepts type value.
   */
  @Test public void testTypeConstructor()
  {
    DeviceDiscovery dd = new DeviceDiscovery("name", "protocol", "model", "type", null);

    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals("type"));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 0);
  }

  /**
   * Test for type constructor with null arg.
   */
  @Test public void testTypeConstructorNullArg()
  {
    DeviceDiscovery dd = new DeviceDiscovery("name", "protocol", "model", null, null);

    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 0);
  }


  // Copy constructor tests -----------------------------------------------------------------------

  /**
   * Base test on copy constructor.
   */
  @Test public void testCopyConstructor()
  {
    DeviceDiscovery dd = new DeviceDiscovery("name", "protocol", "model");

    DeviceDiscovery copy = new DeviceDiscovery(dd);

    Assert.assertTrue(copy.deviceName.equals("name"));
    Assert.assertTrue(copy.protocol.equals("protocol"));
    Assert.assertTrue(copy.model.equals("model"));
    Assert.assertTrue(copy.type.equals(""));
    Assert.assertTrue(copy.deviceAttributes != null);
    Assert.assertTrue(copy.deviceAttributes.size() == 0);
  }

  /**
   * Test type field copy in copy constructor.
   */
  @Test public void testCopyConstructorType()
  {
    DeviceDiscovery dd = new DeviceDiscovery("name", "protocol", "model", "type", null);

    DeviceDiscovery copy = new DeviceDiscovery(dd);

    Assert.assertTrue(copy.deviceName.equals("name"));
    Assert.assertTrue(copy.protocol.equals("protocol"));
    Assert.assertTrue(copy.model.equals("model"));
    Assert.assertTrue(copy.type.equals("type"));
    Assert.assertTrue(copy.deviceAttributes != null);
    Assert.assertTrue(copy.deviceAttributes.size() == 0);
  }

  /**
   * Test device attribute copy in copy constructor.
   */
  @Test public void testCopyConstructorAttributes()
  {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("1", "one");
    attributes.put("2", "two");
    attributes.put("3", "three");

    DeviceDiscovery dd = new DeviceDiscovery("name", "protocol", "model", "type", attributes);

    DeviceDiscovery copy = new DeviceDiscovery(dd);

    Assert.assertTrue(copy.deviceName.equals("name"));
    Assert.assertTrue(copy.protocol.equals("protocol"));
    Assert.assertTrue(copy.model.equals("model"));
    Assert.assertTrue(copy.type.equals("type"));
    Assert.assertTrue(copy.deviceAttributes != null);
    Assert.assertTrue(copy.deviceAttributes.size() == 3);

    Assert.assertTrue(copy.deviceAttributes.keySet().contains("1"));
    Assert.assertTrue(copy.deviceAttributes.keySet().contains("2"));
    Assert.assertTrue(copy.deviceAttributes.keySet().contains("3"));

    Assert.assertTrue(copy.deviceAttributes.values().contains("one"));
    Assert.assertTrue(copy.deviceAttributes.values().contains("two"));
    Assert.assertTrue(copy.deviceAttributes.values().contains("three"));
  }

}

