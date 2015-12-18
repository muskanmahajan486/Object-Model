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

import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.model.Controller;
import org.openremote.model.persistence.jpa.RelationalController;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Implementation of the Transformer interface for the FlexJSON library that handles
 * serialization and deserialization of the {@link org.openremote.model.Controller}
 * instances.
 *
 * @author Juha Lindfors
 */
public class ControllerTransformer extends JSONTransformer<Controller>
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * The JSON property name value used in controller JSON document for controller identities:
   * {@value}
   */
  public static final String IDENTITY_JSON_PROPERTY_NAME = "identity";

  /**
   * The JSON property name value used in controller JSON document for controller hardware
   * MAC addresses: {@value}
   */
  public static final String MAC_ADDRESSES_JSON_PROPERTY_NAME = "macAddresses";

  /**
   * The JSON property name value used in controller JSON document for controller's user
   * readable name: {@value}
   */
  public static final String NAME_JSON_PROPERTY_NAME = "name";

  /**
   * The JSON property name value used in controller JSON document for controller's user
   * readable description: {@value}
   */
  public static final String DESCRIPTION_JSON_PROPERTY_NAME = "description";

  /**
   * The JSON property name value used in controller JSON document for controller's
   * configuration: {@value}
   */
  public static final String CONFIGURATION_JSON_PROPERTY_NAME = "configurations";

  /**
   * The JSON property name value used in controller JSON document for controller's
   * attributes: {@value}
   */
  public static final String CONTROLLER_ATTRIBUTES_JSON_PROPERTY_NAME = "controllerAttributes";


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a FlexJSON transformer for {@link org.openremote.model.Controller} instances.
   */
  public ControllerTransformer()
  {
    super(Controller.class);
  }


  // JSONTransformer Overrides --------------------------------------------------------------------

  /**
   * Translates a given {@link org.openremote.model.Controller} instance to a JSON data
   * format. The expected JSON properties and object structure is described in the JSON schema
   * documents in project's /resources/json directory.
   *
   * @param controller
   *            controller instance to convert to JSON format
   */
  @Override public void write(Controller controller)
  {
    ControllerData data = new ControllerData(controller);

    startObject();

    writeProperty("controllerId", Long.toString(data.id));

    writeProperty(IDENTITY_JSON_PROPERTY_NAME, data.identity);

    if (!data.macs.isEmpty())
    {
      writeArray(MAC_ADDRESSES_JSON_PROPERTY_NAME, data.macs);
    }

    if (data.name != null)
    {
      writeProperty(NAME_JSON_PROPERTY_NAME, data.name);
    }

    if (data.description != null)
    {
      writeProperty(DESCRIPTION_JSON_PROPERTY_NAME, data.description);
    }

    if (!data.configs.isEmpty())
    {
      writeProperty(CONFIGURATION_JSON_PROPERTY_NAME, "NOT YET IMPLEMENTED"); // TODO : NYI
    }

    extendProperties(controller);

    endObject();
  }

  protected void extendProperties(Controller controller)
  {

  }


  /**
   * Recreates an {@link org.openremote.model.Controller} instance from a deserialized JSON model
   * prototype.
   *
   * @see   JSONModel
   *
   * @param json
   *          A JSON frame that represents the structures parsed from the JSON document
   *          representing a controller instance.
   *
   * @return  A corresponding Java Controller instance
   *
   * @throws  DeserializationException if deserialization fails
   */
  @Override protected Controller deserialize(JSONModel json) throws DeserializationException
  {
    ModelObject model = json.getModel();

    String id = model.getAttribute(IDENTITY_JSON_PROPERTY_NAME);
    List<String> macs = model.getStringArray(MAC_ADDRESSES_JSON_PROPERTY_NAME);
    String name = model.getAttribute(NAME_JSON_PROPERTY_NAME);
    String description = model.getAttribute(DESCRIPTION_JSON_PROPERTY_NAME);

    Controller controller = new Controller(id, new HashSet<String>(macs), name, description);

    if (model.hasObject(CONTROLLER_ATTRIBUTES_JSON_PROPERTY_NAME))
    {
      ModelObject attributes = model.getObject(CONTROLLER_ATTRIBUTES_JSON_PROPERTY_NAME);

      Enumeration<ModelObject.Attribute> elements = attributes.getAttributes();

      while (elements.hasMoreElements())
      {
        ModelObject.Attribute attr = elements.nextElement();

        controller.addAttribute(attr.getName(), attr.getValue());
      }
    }

    if (model.hasObject(CONFIGURATION_JSON_PROPERTY_NAME))
    {
      throw new IncorrectImplementationException("CONTROLLER CONFIGURATIONS NYI");  // TODO
    }

    return controller;
  }



  // Nested Classes -------------------------------------------------------------------------------

  private static class ControllerData extends Controller
  {
    private Long id;
    private Set<String> macs;
    private String identity = super.identity;
    private String name = super.name;
    private String description = super.description;
    private Map<String, Map<Configuration.Category, Configuration>> configs = super.configurations;

    private ControllerData(Controller controller)
    {
      super(controller);

      if (controller instanceof RelationalController)
      {
        id = ((RelationalController)controller).getId();
      }
      macs = super.macAddresses;
    }
  }
}

