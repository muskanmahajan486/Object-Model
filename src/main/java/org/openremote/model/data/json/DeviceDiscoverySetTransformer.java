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
package org.openremote.model.data.json;

import org.openremote.model.DeviceDiscovery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceDiscoverySetTransformer extends JSONTransformer<Set<DeviceDiscovery>>
{
// Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a FlexJSON transformer for Set of {@link org.openremote.model.DeviceDiscovery} instances.
   */
  public DeviceDiscoverySetTransformer()
  {
    super((Class<Set<DeviceDiscovery>>) ((Class) Set.class));
  }

// JSONTransformer Overrides --------------------------------------------------------------------

  /**
   * Translates a given Set of {@link org.openremote.model.DeviceDiscovery} instance to a JSON data
   * format. The expected JSON properties and object structure is described in the
   * "Device Discovery API Proposal (BHVE04) - v2.1" document.
   *
   * @param devices set of data discovery instance to convert to JSON format
   */
  @Override
  public void write(Set<DeviceDiscovery> devices)
  {
    startObject();

    writeArray("devices", devices);

    endObject();
  }


  /**
   * Recreates a Set of {@link org.openremote.model.DeviceDiscovery} instance from a deserialized
   * JSON model prototype.
   *
   * @param json A JSON frame that represents the structures parsed from the JSON document
   *             representing a set of device discovery instances.
   *
   * @return A corresponding Set of Java DeviceDiscovery instances
   *
   * @throws DeserializationException if deserialization fails
   *
   * @see JSONModel
   */
  @Override
  protected Set<DeviceDiscovery> deserialize(JSONModel json) throws DeserializationException
  {
    try
    {
      ModelObject model = json.getModel();

      Set<DeviceDiscovery> deviceDiscoverySet = new HashSet<DeviceDiscovery>();

      List<ModelObject> devices = model.getObjectArray("devices");

      if (devices != null)
      {
        for (ModelObject d : devices)
        {
          deviceDiscoverySet.add(new DeviceDiscoveryTransformer().deserialize(d));
        }
      }
      return deviceDiscoverySet;
    }

    // TODO : convert DeviceDiscovery to throw validation exceptions instead of illegal arg exc.
    // catch (Model.ValidationException exception)
    catch (Throwable throwable)
    {
      throw new DeserializationException(
              "Cannot create new Set of DeviceDiscovery instances, received values are not valid : {0}",
              throwable, throwable.getMessage()
      );
    }
  }
}
