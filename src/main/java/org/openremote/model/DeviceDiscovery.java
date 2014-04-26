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

import org.openremote.base.Version;
import org.openremote.model.serialization.json.DeviceDiscoveryTransformer;
import org.openremote.model.serialization.json.JSONHeader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a plain object model for device discovery data structure. Each discovered device
 * must contain device name, device protocol and device model information. Additionally,
 * a device type attribute (such as 'switch', 'dimmer', etc.) may be optionally included. Other
 * arbitrary device attributes may be included in the {@link #deviceAttributes} key,value map.
 *
 * Persistence for this object model can be found in {@link org.openremote.model.persistence}
 * package and its sub-packages. <p>
 *
 * The JSON serialization format for this class is described in the project's resources/json
 * directory.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DeviceDiscovery
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * The serialization format version implemented by this class. The version value should only
   * be changed when incompatible serialization format changes are introduced to this class.
   */
  public static final Version JSON_SERIAL_VERSION = new Version(1, 0, 0);

  /**
   * Constraint for the value string size in device attributes map. This is currently
   * derived from the constraints in the persistence model defined in
   * {@link org.openremote.model.persistence.jpa.PersistentDeviceDiscovery}
   */
  public static final int DEVICE_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT = 1000;

  /**
   * The default string attribute length in the relational persistence model when no
   * specific configuration is given.
   */
  public static final int DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT = 255;


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
  protected Map<String, String> deviceAttributes = new ConcurrentHashMap<String, String>(0);


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
      this.deviceAttributes = new ConcurrentHashMap<String, String>(copy.deviceAttributes);
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

    deviceName = deviceName.trim();
    deviceProtocol = deviceProtocol.trim();
    model = model.trim();

    if (deviceName.length() > DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT)
    {
      throw new ConstraintException(
          "Device name can be at most {0} characters long, name ''{1}'' is {2} characters long.",
          DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT, deviceName, deviceName.length()
      );
    }

    if (deviceProtocol.length() > DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT)
    {
      throw new ConstraintException(
          "Device protocol string can be at most {0} characters, protocol ''{1}'' is {2} " +
          "characters long.",
          DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT, deviceProtocol, deviceProtocol.length()
      );
    }

    if (model.length() > DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT)
    {
      throw new ConstraintException(
          "Device model string can be at most {0} characters, model string ''{1}'' is {2} " +
          "characters long.",
          DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT, model, model.length()
      );
    }

    this.deviceName = deviceName;
    this.protocol = deviceProtocol;
    this.model = model;
  }


  /**
   * Constructs a new device discovery data structure with a given device name, protocol,
   * model and device attributes.
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
   *            Device attributes which are copied to this instance.
   */
  public DeviceDiscovery(String deviceName, String deviceProtocol, String model,
                         Map<String, String> attributes)
  {
    this(deviceName, deviceProtocol, model);

    // TODO :
    //
    //   Should recommend to use the {@link #addAttribute(String, String)} instead of
    //   this constructor. In some multi-threaded scenarios with obscure corner cases
    //   the addAttribute() call may behave more consistently. So should probably remove
    //   this constructor altogether.

    if (attributes != null)
    {
      this.deviceAttributes = new ConcurrentHashMap<String, String>(attributes.size());

      Set<Map.Entry<String, String>> entries = attributes.entrySet();

      for (Map.Entry<String, String> entry : entries)
      {
        addAttribute(entry.getKey(), entry.getValue());
      }
    }

    else
    {
      this.deviceAttributes = new ConcurrentHashMap<String, String>(0);
    }
  }

  /**
   * Constructs a new device discovery data structure with a given device name, protocol,
   * model, type and device attributes.
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
   *
   * @throws  ConstraintException
   *            if deviceName, deviceProtocol, model or type strings exceed the length defined
   *            in {@link #DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT}, or if any of the key
   *            or value strings in attribute map exceed their length constraints
   */
  public DeviceDiscovery(String deviceName, String deviceProtocol, String model,
                         String type, Map<String, String> attributes)
  {
    // TODO :
    //
    //   Should recommend to use the {@link #addAttribute(String, String)} instead of
    //   this constructor. In some multi-threaded scenarios with obscure corner cases
    //   the addAttribute() call may behave more consistently. So should probably remove
    //   this constructor altogether and replace with one that only takes the additional type.

    this(deviceName, deviceProtocol, model, attributes);

    type = (type == null)
          ? ""
          : type.trim();

    if (type.length() > DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT)
    {
      throw new ConstraintException(
          "Device type attribute can be at most {0} characters, type attribute ''{1}'' is {2} " +
          "characters long",
          DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT, type, type.length()
      );
    }

    this.type = type;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Adds a device attribute to this device discovery structure. If a key value is <tt>null</tt>
   * or empty string then this call will return without changes. Null values are converted to
   * empty strings. All key and value strings are trimmed of white-space characters before adding
   * the attribute. <p>
   *
   * The constraints limit the key string length to at most 255 characters and value strings at
   * most 1000 characters in length.
   *
   * @param name
   *          device attribute name
   *
   * @param value
   *          device attribute value
   *
   * @return
   *          reference to this updated device discovery structure to enable method chaining
   *
   * @throws  ConstraintException
   *            If the name or value strings are not within defined constraints:
   *            {@link #DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT} and
   *            {@link #DEVICE_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT}
   */
  public DeviceDiscovery addAttribute(String name, String value)
  {
    if (name == null || name.equals(""))
    {
      return this;
    }

    if (value == null)
    {
      value = "";
    }

    name = name.trim();
    value = value.trim();

    if (name.length() > DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT)
    {
      throw new ConstraintException(
          "Device attribute name string can be at most {0} characters, name string ''{1}'' " +
          "is {2} characters long.",
          DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT, name, name.length()
      );
    }

    if (value.length() > DEVICE_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT)
    {
      throw new ConstraintException(
          "Device attribute value string can be at most {0} characters, value string ''{1}'' " +
          "is {2} characters long",
          DEVICE_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT, value, value.length()
      );
    }

    deviceAttributes.put(name, value);

    return this;
  }

  // Serialization --------------------------------------------------------------------------------

  /**
   * Serializes this object to a JSON format.  <p>
   *
   * See the project's resources/json directory for an Orderly definition of the data exchange
   * format and the corresponding JSON schema.
   *
   * @return a JSON structure for transferring this device discovery information
   */
  public String toJSONString()
  {
    return JSONHeader.toJSON(this, JSON_SERIAL_VERSION, new DeviceDiscoveryTransformer());
  }
}

