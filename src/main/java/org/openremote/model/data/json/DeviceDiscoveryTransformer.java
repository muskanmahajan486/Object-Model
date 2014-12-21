/*
 * Copyright 2013-2015, Juha Lindfors.
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

import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.model.DeviceDiscovery;

import java.util.Map;

/**
 * A FlexJSON transformer for {@link DeviceDiscovery} implementation.
 *
 * The expected JSON structure is defined in schema documents located in project's
 * /resources/json directory.
 *
 * @author Juha Lindfors
 */
public class DeviceDiscoveryTransformer extends JSONTransformer<DeviceDiscovery>
{

  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a FlexJSON transformer for {@link org.openremote.model.DeviceDiscovery} instances.
   */
  public DeviceDiscoveryTransformer()
  {
    super(DeviceDiscovery.class);
  }


  // JSONTransformer Overrides --------------------------------------------------------------------

  /**
   * Translates a given {@link org.openremote.model.DeviceDiscovery} instance to a JSON data
   * format. The expected JSON properties and object structure is described in the JSON schema
   * documents in project's /resources/json directory.
   *
   * @param discovery
   *            data discovery instance to convert to JSON format
   */
  @Override public void write(DeviceDiscovery discovery)
  {
    DeviceDiscoveryData data = new DeviceDiscoveryData(discovery);

    startObject();

    writeProperty("deviceName", data.deviceName);
    writeProperty("deviceProtocol", data.deviceProtocol);
    writeProperty("deviceModel", data.model);

    if (!data.type.equals(""))
    {
      writeProperty("deviceType", data.type);
    }

    if (!data.attributes.isEmpty())
    {
      writeProperty("deviceAttributes", data.attributes);
    }

    endObject();
  }


  @Override protected DeviceDiscovery deserialize(JSONModel model)
  {
    // TODO

    throw new IncorrectImplementationException("Not Implemented.");
  }


  // Nested Classes -------------------------------------------------------------------------------

  /**
   * This private class makes the data fields in {@link DeviceDiscovery} class visible to
   * this implementation. <p>
   *
   * Note that this class does not make a copy of the mutable device attributes collection.
   * It is up to the enclosing class not to mess things up.
   */
  private static class DeviceDiscoveryData extends DeviceDiscovery
  {
    private String deviceName = super.deviceName;
    private String deviceProtocol = super.protocol;
    private String model = super.model;
    private String type = super.type;
    private Map<String, String> attributes = super.deviceAttributes;

    /**
     * Copy constructor.
     *
     * @param discovery
     *          device discovery data to copy
     */
    private DeviceDiscoveryData(DeviceDiscovery discovery)
    {
      super(discovery);
    }
  }

}

