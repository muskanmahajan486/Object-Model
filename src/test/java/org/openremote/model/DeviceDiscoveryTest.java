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

import org.openremote.model.data.json.JSONHeader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@link org.openremote.model.DeviceDiscovery} class
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DeviceDiscoveryTest
{

  private String apiVersion = "";

  @BeforeClass public void getConfiguredAPIVersion()
  {
    apiVersion = System.getProperty("openremote.project.api.version");

    Assert.assertTrue(apiVersion != null && !apiVersion.equals(""));
  }


  // Base constructor tests -----------------------------------------------------------------------

  /**
   * Base constructor test.
   */
  @Test public void testBasicConstructor()
  {
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "model");

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 0);
  }

  /**
   * Null arg test (identifier) on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorNullIdentifierArg()
  {
    new DeviceDiscovery(null, "device", "protocol", "model");
  }

  /**
   * Null arg test (device name) on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorNullNameArg()
  {
    new DeviceDiscovery("idenfitier", null, "protocol", "model");
  }

  /**
   * Null arg test (protocol string) on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorNullProtocolArg()
  {
    new DeviceDiscovery("identifier", "name", null, "model");
  }

  /**
   * Empty string arg test (identifier) on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorEmptyIdentifierArg()
  {
    new DeviceDiscovery("", "name", "protocol", "model");
  }

  /**
   * Empty string arg test (device name) on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorEmptyNameArg()
  {
    new DeviceDiscovery("identifier", "", "protocol", "model");
  }

  /**
   * Empty string arg test (protocol string) on base constructor.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testBasicConstructorEmptyProtocolArg()
  {
    new DeviceDiscovery("identifier", "name", "", "model");
  }


  /**
   * Null arg test on model string. Should fall back to empty string.
   */
  @Test public void testBasicConstructorNullModel()
  {
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals(""));
    Assert.assertTrue(dd.type.equals(""));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 0);
  }

  /**
   * Test for basic constructor with too long argument values
   */
  @Test public void testBasicConstructorConstraints()
  {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < DeviceDiscovery.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT; ++i)
    {
      builder.append('a');
    }

    // Test limit values...

    DeviceDiscovery dd = new DeviceDiscovery(builder.toString(), "name", "protocol", "model");

    Assert.assertTrue(dd.deviceIdentifier.equals(builder.toString()));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));

    dd = new DeviceDiscovery("identifier", builder.toString(), "protocol", "model");

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals(builder.toString()));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));

    dd = new DeviceDiscovery("identifier", "name", builder.toString(), "model");

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals(builder.toString()));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));


    dd = new DeviceDiscovery("identifier", "name", "protocol", builder.toString());

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals(builder.toString()));
    Assert.assertTrue(dd.type.equals(""));


    // Test above limit values...

    builder.append('a');

    try
    {
      new DeviceDiscovery(builder.toString(), "name", "protocol", "model");

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", builder.toString(), "protocol", "model");

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", "name", builder.toString(), "model");

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", "name", "protocol", builder.toString());

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }
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

    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", null, attrs);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
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
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", null, null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals(""));
    Assert.assertTrue(dd.type.equals(""));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 0);
  }


  /**
   * Test for attribute constructor with too long argument values (redundant, in case the ctor
   * implementation is modified).
   */
  @Test public void testAttributeConstructorConstraints()
  {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < DeviceDiscovery.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT; ++i)
    {
      builder.append('a');
    }

    // Test limit values...

    DeviceDiscovery dd = new DeviceDiscovery(builder.toString(), "name", "protocol", "model", null);

    Assert.assertTrue(dd.deviceIdentifier.equals(builder.toString()));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));


    dd = new DeviceDiscovery("identifier", builder.toString(), "protocol", "model", null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals(builder.toString()));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));


    dd = new DeviceDiscovery("identifier", "name", builder.toString(), "model", null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals(builder.toString()));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));


    dd = new DeviceDiscovery("identifier", "name", "protocol", builder.toString(), null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals(builder.toString()));
    Assert.assertTrue(dd.type.equals(""));


    // Test above limit values...

    builder.append('a');

    try
    {
      new DeviceDiscovery(builder.toString(), "name", "protocol", "model", null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", builder.toString(), "protocol", "model", null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", builder.toString(), "protocol", "model", null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", "name", builder.toString(), "model", null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", "name", "protocol", builder.toString(), null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }
  }


  // Type constructor tests -----------------------------------------------------------------------

  /**
   * Basic tests for constructor that accepts type value.
   */
  @Test public void testTypeConstructor()
  {
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "model", "type", null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
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
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "model", null, null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(""));
    Assert.assertTrue(dd.deviceAttributes != null);
    Assert.assertTrue(dd.deviceAttributes.size() == 0);
  }

  /**
   * Test for type constructor with too long type string.
   */
  @Test public void testTypeConstructorConstraint()
  {
    StringBuilder builder = new StringBuilder(DeviceDiscovery.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT + 1);

    // test limit value...

    for (int i = 0; i < DeviceDiscovery.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT; ++i)
    {
      builder.append('a');
    }

    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "model", builder.toString(), null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals(builder.toString()));


    // test one above limit...

    builder.append('a');

    try
    {
      new DeviceDiscovery("identifier", "a", "b", "c", builder.toString(), null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }
  }

  /**
   * Test for type constructor with too long argument values (redundant, in case the ctor
   * implementation is modified).
   */
  @Test public void testTypeConstructorConstraints2()
  {
    StringBuilder builder = new StringBuilder(DeviceDiscovery.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT + 1);

    for (int i = 0; i < DeviceDiscovery.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT; ++i)
    {
      builder.append('a');
    }

    // Test limit values...

    DeviceDiscovery dd = new DeviceDiscovery(builder.toString(), "name", "protocol", "model", "type", null);

    Assert.assertTrue(dd.deviceIdentifier.equals(builder.toString()));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals("type"));


    dd = new DeviceDiscovery("identifier", builder.toString(), "protocol", "model", "type", null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals(builder.toString()));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals("type"));


    dd = new DeviceDiscovery("identifier", "name", builder.toString(), "model", "type", null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals(builder.toString()));
    Assert.assertTrue(dd.model.equals("model"));
    Assert.assertTrue(dd.type.equals("type"));


    dd = new DeviceDiscovery("identifier", "name", "protocol", builder.toString(), "type", null);

    Assert.assertTrue(dd.deviceIdentifier.equals("identifier"));
    Assert.assertTrue(dd.deviceName.equals("name"));
    Assert.assertTrue(dd.protocol.equals("protocol"));
    Assert.assertTrue(dd.model.equals(builder.toString()));
    Assert.assertTrue(dd.type.equals("type"));


    // Test above limit values...

    builder.append('a');

    try
    {
      new DeviceDiscovery(builder.toString(), "name", "protocol", "model", "type", null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", builder.toString(), "protocol", "model", "type", null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", builder.toString(), "b", "c", "d", null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", "a", builder.toString(), "c", "d", null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }

    try
    {
      new DeviceDiscovery("identifier", "a", "b", builder.toString(), "d", null);

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }
  }


  // Copy constructor tests -----------------------------------------------------------------------

  /**
   * Base test on copy constructor.
   */
  @Test public void testCopyConstructor()
  {
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "model");

    DeviceDiscovery copy = new DeviceDiscovery(dd);

    Assert.assertTrue(copy.deviceIdentifier.equals("identifier"));
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
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "model", "type", null);

    DeviceDiscovery copy = new DeviceDiscovery(dd);

    Assert.assertTrue(copy.deviceIdentifier.equals("identifier"));
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

    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "model", "type", attributes);

    DeviceDiscovery copy = new DeviceDiscovery(dd);

    Assert.assertTrue(copy.deviceIdentifier.equals("identifier"));
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

  /**
   * Test that we guard against concurrent modification exception.
   *
   * @throws Exception  if test fails
   */
  @Test public void testCopyConstructorConcurrency() throws Exception
  {
    final DeviceDiscovery disco = new DeviceDiscovery("identifier", "name", "protocol", "model");
    disco.addAttribute("foo", "bar");
    disco.addAttribute("acme", "ecma");

    final DeviceDiscovery dd = new DeviceDiscovery(disco);

    Iterator<String> iterator = dd.deviceAttributes.keySet().iterator();

    iterator.next();

    // Run another thread while iterator is still open...

    Runnable r = new Runnable()
    {
      @Override public void run()
      {
        dd.addAttribute("nana", "nono");
      }
    };

    Thread t = new Thread(r);
    t.start();
    t.join();

    // This should not break with the concurrent modification from our other thread...

    iterator.next();
  }


  // ToJSONString Tests ---------------------------------------------------------------------------

  /**
   * Basic object to JSON data exchange format.
   */
  @Test public void testToJSONString()
  {
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "mark1");

    String json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"libraryName\""));
    Assert.assertTrue(json.contains("\"OpenRemote Object Model\""));
    Assert.assertTrue(json.contains("\"javaFullClassName\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.class.getName()));
    Assert.assertTrue(json.contains("\"schemaVersion\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.JSON_SCHEMA_VERSION.toString()));

    Assert.assertTrue(json.contains("\"apiVersion\""));
    Assert.assertTrue(
        json.contains("\"" + apiVersion + "\""),
        "API version is " + apiVersion + "\n" + json);

    Assert.assertTrue(apiVersion.equals(JSONHeader.API_VERSION.toString()));

    Assert.assertTrue(json.contains("\"model\""));

    Assert.assertTrue(json.contains("\"deviceIdentifier\""));
    Assert.assertTrue(json.contains("\"identifier\""));
    Assert.assertTrue(json.contains("\"deviceName\""));
    Assert.assertTrue(json.contains("\"name\""));
    Assert.assertTrue(json.contains("\"deviceProtocol\""));
    Assert.assertTrue(json.contains("\"protocol\""));
    Assert.assertTrue(json.contains("\"deviceModel\""));
    Assert.assertTrue(json.contains("\"mark1\""));

    Assert.assertTrue(!json.contains("deviceType"));
    Assert.assertTrue(!json.contains("deviceAttributes"));
  }

  /**
   * Object to JSON data exchange format with type attribute included.
   */
  @Test public void testToJSONStringWithType()
  {
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "mark1", "type", null);

    String json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"libraryName\""));
    Assert.assertTrue(json.contains("\"OpenRemote Object Model\""));
    Assert.assertTrue(json.contains("\"javaFullClassName\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.class.getName()));
    Assert.assertTrue(json.contains("\"schemaVersion\""));
    Assert.assertTrue(json.contains("\"" + DeviceDiscovery.JSON_SCHEMA_VERSION.toString() + "\""));

    Assert.assertTrue(json.contains("\"apiVersion\""));
    Assert.assertTrue(json.contains("\"" + apiVersion + "\""), " API version is " + apiVersion);

    Assert.assertTrue(apiVersion.equals(JSONHeader.API_VERSION.toString()));

    Assert.assertTrue(json.contains("\"model\""));

    Assert.assertTrue(json.contains("\"deviceIdentifier\""));
    Assert.assertTrue(json.contains("\"identifier\""));
    Assert.assertTrue(json.contains("\"deviceName\""));
    Assert.assertTrue(json.contains("\"name\""));
    Assert.assertTrue(json.contains("\"deviceProtocol\""));
    Assert.assertTrue(json.contains("\"protocol\""));
    Assert.assertTrue(json.contains("\"deviceModel\""));
    Assert.assertTrue(json.contains("\"mark1\""));
    Assert.assertTrue(json.contains("\"deviceType\""));
    Assert.assertTrue(json.contains("\"type\""));

    Assert.assertTrue(!json.contains("deviceAttributes"));
  }

  /**
   * Object to JSON data exchange format with device attributes included.
   */
  @Test public void testToJSONStringWithAttributes()
  {
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "mark1", null, null);
    dd.addAttribute("foo", "bar");
    dd.addAttribute("acme", "ecma");

    String json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"libraryName\""));
    Assert.assertTrue(json.contains("\"OpenRemote Object Model\""));
    Assert.assertTrue(json.contains("\"javaFullClassName\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.class.getName()));
    Assert.assertTrue(json.contains("\"schemaVersion\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.JSON_SCHEMA_VERSION.toString()));

    Assert.assertTrue(json.contains("\"apiVersion\""));
    Assert.assertTrue(json.contains("\"" + apiVersion + "\""), " API version is " + apiVersion);

    Assert.assertTrue(apiVersion.equals(JSONHeader.API_VERSION.toString()));

    Assert.assertTrue(json.contains("\"model\""));

    Assert.assertTrue(json.contains("\"deviceIdentifier\""));
    Assert.assertTrue(json.contains("\"identifier\""));
    Assert.assertTrue(json.contains("\"deviceName\""));
    Assert.assertTrue(json.contains("\"name\""));
    Assert.assertTrue(json.contains("\"deviceProtocol\""));
    Assert.assertTrue(json.contains("\"protocol\""));
    Assert.assertTrue(json.contains("\"deviceModel\""));
    Assert.assertTrue(json.contains("\"mark1\""));

    Assert.assertTrue(json.contains("deviceAttributes"));
    Assert.assertTrue(json.contains("\"foo\""));
    Assert.assertTrue(json.contains("\"bar\""));
    Assert.assertTrue(json.contains("\"acme\""));
    Assert.assertTrue(json.contains("\"ecma\""));

    Assert.assertTrue(!json.contains("\"deviceType\""));
  }

  /**
   * Object to JSON data exchange format. Empty attributes collection should be ignored.
   */
  @Test public void testToJSONStringWithEmptyAttributes()
  {
    DeviceDiscovery dd = new DeviceDiscovery(
        "identifier", "name", "protocol", "mark1", null, new HashMap<String, String>()
    );

    String json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"libraryName\""));
    Assert.assertTrue(json.contains("\"OpenRemote Object Model\""));
    Assert.assertTrue(json.contains("\"javaFullClassName\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.class.getName()));
    Assert.assertTrue(json.contains("\"schemaVersion\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.JSON_SCHEMA_VERSION.toString()));

    Assert.assertTrue(json.contains("\"apiVersion\""));
    Assert.assertTrue(json.contains("\"" + apiVersion + "\""), " API version is " + apiVersion);

    Assert.assertTrue(apiVersion.equals(JSONHeader.API_VERSION.toString()));

    Assert.assertTrue(json.contains("\"model\""));

    Assert.assertTrue(json.contains("\"deviceIdentifier\""));
    Assert.assertTrue(json.contains("\"identifier\""));
    Assert.assertTrue(json.contains("\"deviceName\""));
    Assert.assertTrue(json.contains("\"name\""));
    Assert.assertTrue(json.contains("\"deviceProtocol\""));
    Assert.assertTrue(json.contains("\"protocol\""));
    Assert.assertTrue(json.contains("\"deviceModel\""));
    Assert.assertTrue(json.contains("\"mark1\""));

    Assert.assertTrue(!json.contains("deviceAttributes"));
    Assert.assertTrue(!json.contains("\"deviceType\""));
  }

  /**
   * Object to JSON data exchange format with unicode characters.
   */
  @Test public void testToJSONStringWithUnicode()
  {
    DeviceDiscovery dd = new DeviceDiscovery("éèç", "äöüåß", "巨人", "mark1");

    String json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"libraryName\""));
    Assert.assertTrue(json.contains("\"OpenRemote Object Model\""));
    Assert.assertTrue(json.contains("\"javaFullClassName\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.class.getName()));
    Assert.assertTrue(json.contains("\"schemaVersion\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.JSON_SCHEMA_VERSION.toString()));

    Assert.assertTrue(json.contains("\"apiVersion\""));
    Assert.assertTrue(json.contains("\"" + apiVersion + "\""), " API version is " + apiVersion);

    Assert.assertTrue(apiVersion.equals(JSONHeader.API_VERSION.toString()));

    Assert.assertTrue(json.contains("\"model\""));

    Assert.assertTrue(json.contains("\"deviceIdentifier\""));
    Assert.assertTrue(json.contains("\"éèç\""));
    Assert.assertTrue(json.contains("\"deviceName\""));
    Assert.assertTrue(json.contains("\"äöüåß\""));
    Assert.assertTrue(json.contains("\"deviceProtocol\""));
    Assert.assertTrue(json.contains("\"巨人\""));
    Assert.assertTrue(json.contains("\"deviceModel\""));
    Assert.assertTrue(json.contains("\"mark1\""));

    Assert.assertTrue(!json.contains("deviceAttributes"));
    Assert.assertTrue(!json.contains("\"deviceType\""));
  }

  /**
   * Object to JSON data exchange format with special characters that should get escaped:
   * double quotes inside the string escaped to \u0022, <, &, > FlexJSON escapes these as
   * \u003c, \u0026 and \u003e respectively. A linefeed control character in middle of string
   * is escaped as \n in the JSON.
   */
  @Test public void testToJSONStringEscaping()
  {
    DeviceDiscovery dd = new DeviceDiscovery("identifier", "a\"a", "<a&b>", "foo\nbar");

    String json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"deviceIdentifier\""));
    Assert.assertTrue(json.contains("\"identifier\""));
    Assert.assertTrue(json.contains("\"deviceName\""));
    Assert.assertTrue(json.contains("\"a\\u0022a\""), json);
    Assert.assertTrue(json.contains("\"deviceProtocol\""));
    Assert.assertTrue(json.contains("\"\\u003ca\\u0026b\\u003e\""), json);
    Assert.assertTrue(json.contains("\"deviceModel\""));
    Assert.assertTrue(json.contains("\"foo\\nbar\""), json);
  }

  /**
   * We trim whitespace around variables so they don't show up on JSON format either.
   */
  @Test public void testToJSONStringValueTrimming()
  {
    DeviceDiscovery dd = new DeviceDiscovery("  identifier \t", "\t\taa\n", " ab ", "foo\n", "\nacme\n", null);
    dd.addAttribute("\t\tnana", "\n\nnono\n\n");

    String json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"deviceIdentifier\""));
    Assert.assertTrue(json.contains("\"identifier\""));
    Assert.assertTrue(json.contains("\"deviceName\""));
    Assert.assertTrue(json.contains("\"aa\""), json);
    Assert.assertTrue(json.contains("\"deviceProtocol\""));
    Assert.assertTrue(json.contains("\"ab\""), json);
    Assert.assertTrue(json.contains("\"deviceModel\""));
    Assert.assertTrue(json.contains("\"foo\""), json);
    Assert.assertTrue(json.contains("\"deviceType\""));
    Assert.assertTrue(json.contains("\"acme\""), json);
    Assert.assertTrue(json.contains("\"nana\""), json);
    Assert.assertTrue(json.contains("\"nono\""), json);
  }

  /**
   * Make sure we clone the attributes if the map constructor is used (it would be better to
   * use addAttribute() method to avoid all potential race issues)
   */
  @Test public void testToJSONStringAttributeMapImmutability()
  {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("koko", "momo");

    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "model", null, attributes);

    String json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"koko\""), json);
    Assert.assertTrue(json.contains("\"momo\""), json);

    attributes.clear();

    json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"koko\""), json);
    Assert.assertTrue(json.contains("\"momo\""), json);
  }

  /**
   * Codify the implementation to strip whitespaces from device attribute key and value strings.
   */
  @Test public void testToJSONStringAttributeTrim()
  {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("\t\tkoko\r", "momo\n");

    DeviceDiscovery dd = new DeviceDiscovery("identifier", "name", "protocol", "model", null, attributes);

    String json = dd.toJSONString();

    Assert.assertTrue(json.contains("\"koko\""), json);
    Assert.assertTrue(json.contains("\"momo\""), json);
  }

  /**
   * Set of DeviceDiscovery to JSON data exchange format.
   */
  @Test public void testToJSONStringSetOfDeviceDiscovery()
  {
    Set<DeviceDiscovery> devices = new HashSet<DeviceDiscovery>();
    devices.add(new DeviceDiscovery("1", "name1", "protocol1", "model1"));
    devices.add(new DeviceDiscovery("2", "name2", "protocol2", "model2"));

    String json = DeviceDiscovery.toJSONString(devices);

    Assert.assertTrue(json.contains("\"libraryName\""));
    Assert.assertTrue(json.contains("\"OpenRemote Object Model\""));
    Assert.assertTrue(json.contains("\"javaFullClassName\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.class.getName()));
    Assert.assertTrue(json.contains("\"schemaVersion\""));
    Assert.assertTrue(json.contains(DeviceDiscovery.JSON_SCHEMA_VERSION.toString()));

    Assert.assertTrue(json.contains("\"apiVersion\""));
    Assert.assertTrue(
            json.contains("\"" + apiVersion + "\""),
            "API version is " + apiVersion + "\n" + json);

    Assert.assertTrue(apiVersion.equals(JSONHeader.API_VERSION.toString()));

    Assert.assertTrue(json.contains("\"model\""));

    Assert.assertTrue(json.contains("\"deviceIdentifier\""));
    Assert.assertTrue(json.contains("\"1\""));
    Assert.assertTrue(json.contains("\"2\""));
    Assert.assertTrue(json.contains("\"deviceName\""));
    Assert.assertTrue(json.contains("\"name1\""));
    Assert.assertTrue(json.contains("\"name2\""));
    Assert.assertTrue(json.contains("\"deviceProtocol\""));
    Assert.assertTrue(json.contains("\"protocol1\""));
    Assert.assertTrue(json.contains("\"protocol2\""));
    Assert.assertTrue(json.contains("\"deviceModel\""));
    Assert.assertTrue(json.contains("\"model1\""));
    Assert.assertTrue(json.contains("\"model2\""));

    Assert.assertTrue(!json.contains("deviceType"));
    Assert.assertTrue(!json.contains("deviceAttributes"));
  }

  @Test public void testJSONSerializerAPIVersion()
  {
    // TODO : integration test
    //Assert.assertTrue(json.contains("\"apiVersion\""));
  }


  // AddAttribute Tests ---------------------------------------------------------------------------

  /**
   * Basic tests for addAttribute()...
   */
  @Test public void testAddAttribute()
  {
    TestDeviceDiscoveryAttributes discovery = new TestDeviceDiscoveryAttributes(
        new DeviceDiscovery("identifier", "name", "protocol", "model")
    );

    discovery.addAttribute("foo", "bar");

    Assert.assertTrue(discovery.getAttribute("foo").equals("bar"));

    // Allow null value, converted to empty string...

    discovery.addAttribute("acme", null);

    Assert.assertTrue(discovery.getAttribute("acme").equals(""));

    // Allow empty string value...

    discovery.addAttribute("ecma", "");

    Assert.assertTrue(discovery.getAttribute("ecma").equals(""));

    // Replace existing value...

    discovery.addAttribute("foo", "foo");

    Assert.assertTrue(discovery.getAttribute("foo").equals("foo"));

    discovery.addAttribute("foo", null);

    Assert.assertTrue(discovery.getAttribute("foo").equals(""));
  }

  /**
   * AddAttribute() tests with a null key. Add should be ignored.
   */
  @Test public void testAddAttributeNullKey()
  {
    TestDeviceDiscoveryAttributes discovery = new TestDeviceDiscoveryAttributes(
        new DeviceDiscovery("identifier", "name", "protocol", "model")
    );

    // should be no op

    discovery.addAttribute(null, "bar");

    Assert.assertTrue(discovery.deviceAttributesIsEmpty());

    // should be no op

    discovery.addAttribute("", "foo");

    Assert.assertTrue(discovery.deviceAttributesIsEmpty());
  }

  /**
   * Test attribute string trimming.
   */
  @Test public void testAddAttributeTrim()
  {
    TestDeviceDiscoveryAttributes discovery = new TestDeviceDiscoveryAttributes(
        new DeviceDiscovery("identifier", "name", "protocol", "model")
    );

    discovery.addAttribute("foo", " bar ");

    Assert.assertTrue(discovery.getAttribute("foo").equals("bar"));

    discovery.addAttribute("\tacme\n", "ecma");

    Assert.assertTrue(discovery.getAttribute("acme").equals("ecma"));
  }

  /**
   * Test attribute name constraint checking.
   */
  @Test public void testAddAttributeNameConstraint()
  {
    TestDeviceDiscoveryAttributes discovery = new TestDeviceDiscoveryAttributes(
        new DeviceDiscovery("identifier", "name", "protocol", "model")
    );

    // Test limit length...

    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < DeviceDiscovery.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT; ++i)
    {
      builder.append('a');
    }

    discovery.addAttribute(builder.toString(), " bar ");

    Assert.assertTrue(discovery.getAttribute(builder.toString()).equals("bar"));

    // Test above limit value...

    builder.append('b');

    try
    {
      discovery.addAttribute(builder.toString(), "foo");

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }
  }

  /**
   * Test attribute value constraint checking.
   */
  @Test public void testAddAttributeValueConstraint()
  {
    TestDeviceDiscoveryAttributes discovery = new TestDeviceDiscoveryAttributes(
        new DeviceDiscovery("identifier", "name", "protocol", "model")
    );

    // Test value limit...

    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < DeviceDiscovery.DEVICE_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT; ++i)
    {
      builder.append('a');
    }

    discovery.addAttribute("foo", builder.toString());

    Assert.assertTrue(discovery.getAttribute("foo").equals(builder.toString()));

    // Test above limit...

    builder.append('b');

    try
    {
      discovery.addAttribute("bar", builder.toString());

      Assert.fail("should not get here...");
    }

    catch (ConstraintException e)
    {
      // expected...
    }
  }


  /**
   * Test that we guard against concurrent modification exception.
   *
   * @throws Exception  if test fails
   */
  @Test public void testAddAttributeConcurrency() throws Exception
  {
    final DeviceDiscovery discovery = new DeviceDiscovery("identifier", "name", "protocol", "model");
    discovery.addAttribute("foo", "bar");
    discovery.addAttribute("acme", "ecma");

    Iterator<String> iterator = discovery.deviceAttributes.keySet().iterator();

    iterator.next();

    // Run another thread while iterator is still open...

    Runnable r = new Runnable()
    {
      @Override public void run()
      {
        discovery.addAttribute("nana", "nono");
      }
    };

    Thread t = new Thread(r);
    t.start();
    t.join();

    // This should not break with the concurrent modification from our other thread...

    iterator.next();
  }

  // Nested Classes -------------------------------------------------------------------------------

  private static class TestDeviceDiscoveryAttributes extends DeviceDiscovery
  {
    private TestDeviceDiscoveryAttributes(DeviceDiscovery copy)
    {
      super(copy);
    }

    private String getAttribute(String name)
    {
      return deviceAttributes.get(name);
    }

    private boolean deviceAttributesIsEmpty()
    {
      return deviceAttributes.isEmpty();
    }
  }
}
