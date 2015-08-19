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
package org.openremote.model.data.json;

import org.openremote.base.Version;
import org.openremote.model.DeviceDiscovery;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link org.openremote.model.data.json.JSONHeader} class
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class JSONHeaderTest
{
  // NOTE:
  //
  //   At the time of the writing, the JSON schema verification tools were still somewhat
  //   immature (class incompatibility errors from JSON parser) and bulky (5+ MB in size)
  //   so have not included them yet.
  //                                                                                [JPL]


  // ToJSON Tests ---------------------------------------------------------------------------------

  /**
   * Basic test with String as model class.
   */
  @Test public void testToJSONString()
  {
    Version schemaVersion = new Version(1, 0, 0);
    String json = JSONHeader.toJSON("foo", schemaVersion).trim();

    assertDefaultHeaders(json, schemaVersion, String.class);

    Assert.assertTrue(json.contains("\"model\""));
    Assert.assertTrue(json.contains("\"foo\""));
  }

  /**
   * Basic test with DeviceDiscovery as model class.
   */
  @Test public void testToJSONDeviceDiscovery()
  {
    String json = JSONHeader.toJSON(
        new DeviceDiscovery("identifier", "mydevice", "myprotocol", "mymodel"),
        DeviceDiscovery.JSON_SCHEMA_VERSION, new DeviceDiscoveryTransformer()
    );

    assertDefaultHeaders(json, DeviceDiscovery.JSON_SCHEMA_VERSION, DeviceDiscovery.class);

    Assert.assertTrue(json.contains("\"identifier\""), json);
    Assert.assertTrue(json.contains("\"mydevice\""), json);
    Assert.assertTrue(json.contains("\"myprotocol\""), json);
    Assert.assertTrue(json.contains("\"mymodel\""), json);
  }


  // Helper Methods -------------------------------------------------------------------------------

  /**
   * Asserts the common JSON header properties.
   */
  private void assertDefaultHeaders(String json, Version schemaVersion, Class clazz)
  {
    Assert.assertTrue(json.startsWith("{"));
    Assert.assertTrue(json.endsWith("}"));

    Assert.assertTrue(json.contains("\"libraryName\""));
    Assert.assertTrue(json.contains("\"" + JSONHeader.LIBRARY_NAME + "\""));

    Assert.assertTrue(json.contains("\"javaFullClassName\""));
    Assert.assertTrue(json.contains("\"" + clazz.getName() + "\""));

    Assert.assertTrue(json.contains("\"schemaVersion\""));
    Assert.assertTrue(json.contains("\"" + schemaVersion + "\""));

    Assert.assertTrue(json.contains("\"apiVersion\""));
    Assert.assertTrue(json.contains("\"" + JSONHeader.API_VERSION + "\""));

    Assert.assertTrue(json.contains("\"model\""));
  }
}

