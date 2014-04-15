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

import java.util.HashMap;
import java.util.Map;

/**
 * TODO : A data structure used for device discovery service.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DeviceDiscovery
{

  // Protected Instance Fields --------------------------------------------------------------------

  /**
   * A mandatory device name string. In most cases this should be a human-readable name
   * intended for the user interface.
   */
  protected String deviceName;

  /**
   * A mandatory protocol identifier string. This is used to identify which protocol implementation
   * the device will use. This should match to one of the existing identifier strings in various
   * device command builder implementations.
   */
  protected String protocol;

  /**
   * A model identifier for the device. This can be used to further identify a device class
   * beyond its name.
   */
  protected String model;

  /**
   * An optional device type string. This is a convenience field that can contain an arbitrary
   * string value used to identify a device type beyond its name and model. A type string could
   * be "Dimmer", "Light Switch", "Temperature Sensor" or something else. <p>
   *
   * When multiple key,value strings are required to describe a discovered device, use
   * {@link #deviceAttributes}. <p>
   *
   * Defaults to an empty string.
   */
  protected String type = "";

  /**
   * A key,value map for storing an arbitrary number of data entries describing the discovered
   * device.
   */
  protected Map<String, String> deviceAttributes = new HashMap<String, String>(0);


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Copy constructor.
   *
   * @param copy    the device discovery instance to copy
   */
  protected DeviceDiscovery(DeviceDiscovery copy)
  {
    this.model = copy.model;
    this.deviceName = copy.deviceName;
    this.protocol = copy.protocol;
    this.type = copy.type;

    if (copy.deviceAttributes != null)
    {
      this.deviceAttributes = new HashMap<String, String>(copy.deviceAttributes);
    }
  }

  /**
   * Constructs a new device discovery data structure with a given device name, protocol and
   * model.
   *
   * @param deviceName
   *            A descriptive device name
   *
   * @param deviceProtocol
   *            A device protocol identifier used by this device. The identifier string should
   *            match one of the configured/installed device command builder identifiers in
   *            the controller.
   *
   * @param model
   *            A device model identifier
   */
  public DeviceDiscovery(String deviceName, String deviceProtocol, String model)
  {
    if (deviceName == null || deviceName.equals(""))
    {
      throw new IllegalArgumentException("Device name cannot be null or empty string.");
    }

    if (deviceProtocol == null || deviceProtocol.equals(""))
    {
      throw new IllegalArgumentException("Device protocol identifier cannot be null or empty string.");
    }

    if (model == null)
    {
      model = "";
    }

    this.deviceName = deviceName.trim();
    this.protocol = deviceProtocol.trim();
    this.model = model.trim();
  }

  /**
   * Constructs a new device discovery data structure with a given device name, protocol,
   * model and device attributes
   *
   * @param deviceName
   *            A descriptive device name
   *
   * @param deviceProtocol
   *            A device protocol identifier used by this device. The identifier string should
   *            match one of the configured/installed device command builder identifiers in
   *            the controller.
   *
   * @param model
   *            A device model identifier
   *
   * @param attributes
   *            Device attributes
   */
  public DeviceDiscovery(String deviceName, String deviceProtocol, String model,
                         Map<String, String> attributes)
  {
    this(deviceName, deviceProtocol, model);


    this.deviceAttributes = (attributes == null)
                          ? new HashMap<String, String>(0)
                          : attributes;
  }

  /**
   * Constructs a new device discovery data structure with a given device name, protocol,
   * model, type and device attributes
   *
   * @param deviceName
   *            A descriptive device name
   *
   * @param deviceProtocol
   *            A device protocol identifier used by this device. The identifier string should
   *            match one of the configured/installed device command builder identifiers in
   *            the controller.
   *
   * @param model
   *            A device model identifier
   *
   * @param type
   *            A convenience field that can be used to store an arbitrary type information
   *            about the device. A type string could be "Dimmer", "Light Switch",
   *            "Temperature Sensor" or something else. When multiple key,value strings
   *            are required, use the attributes map instead.
   *
   * @param attributes
   *            Device attributes
   */
  public DeviceDiscovery(String deviceName, String deviceProtocol, String model,
                         String type, Map<String, String> attributes)
  {
    this(deviceName, deviceProtocol, model, attributes);

    this.type = (type == null)
              ? ""
              : type.trim();
  }

}

