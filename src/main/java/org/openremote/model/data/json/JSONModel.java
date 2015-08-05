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

import java.util.List;
import java.util.Map;

import org.openremote.base.APIVersion;
import org.openremote.base.Version;


/**
 * A deserialization data container used by FlexJSON deserializer. The deserializer creates a
 * model frame by parsing the expected JSON document schema. This container still includes
 * JSON header fields such as library name and schema version that can be used to determine how
 * to deserialize and construct the model objects contained within. <p>
 *
 * This frame instance contains the JSON document header fields (for example,
 * schema version, generating implementation API version, etc.) that can be used to attempt
 * to deserialize the JSON structure into a Java instance. The JSON structure that represents
 * the model object (without headers) can be retrieved via a call to {@link #getModel()} method.
 *
 * @author Juha Lindfors
 */
public class JSONModel
{

  // Constants ----------------------------------------------------------------------------------

  /**
   * Class name value used if none is defined in the incoming JSON document (which is an error
   * for this mandatory header property).
   */
  public static final String UNDEFINED_CLASS = "<undefined>";


  // Instance Fields ----------------------------------------------------------------------------

  /**
   * Library identifier for a collection of JSON schemas that represent this object model,
   * see {@link org.openremote.model.data.json.JSONHeader#LIBRARY_NAME}.
   *
   * @see #isValidLibrary()
   */
  protected String libraryName;

  /**
   * JSON schema version this frame's model corresponds to.
   */
  protected Version schema = Version.UNKNOWN;

  /**
   * Implementation API version that generated the JSON document this frame's model represents.
   */
  protected APIVersion api;   // TODO

  /**
   * Class name this frame's model structure represents.
   */
  protected String javaFullClassName = UNDEFINED_CLASS;

  /**
   * JSON structure of the model object.
   */
  private ModelObject prototype = new ModelObject("");


  // Constructors -------------------------------------------------------------------------------

  /**
   * A no-args constructor required by the FlexJSON deserialization framework.
   */
  private JSONModel()
  {

  }


  // Public Instance Methods --------------------------------------------------------------------

  /**
   * Indicates if this frame corresponds to the given schema version.
   *
   * @param schema
   *          schema version to match with this frame's schema
   *
   * @return
   *          true if the schema versions are equal
   */
  public boolean containsSchema(Version schema)
  {
    return this.schema.equals(schema);
  }


  /**
   * Returns the classname of the expected model implementation.
   *
   * @return
   *          The fully qualified class name of the model this JSON document and schema
   *          represents.
   */
  public String getModelClass()
  {
    return javaFullClassName;
  }


  /**
   * Returns a JSON representation of the included model object.
   *
   * @return
   *          a JSON structure that represents the 'model' object of this OpenRemote Object
   *          Model JSON document.
   */
  public ModelObject getModel()
  {
    return prototype;
  }



  // Object Overrides ---------------------------------------------------------------------------


  @Override public String toString()
  {
    if (prototype == null)
    {
      return "Model prototype : (null)";
    }

    return "Model prototype for " + javaFullClassName + ", schema : " + schema +
           ", API : " + apiVersion + " (" + libraryName + ")";
  }


  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Checks the given library name in the header fields of this JSON representation against
   * the library name this implementation belongs to.
   *
   * @return  true if library names match
   */
  protected boolean isValidLibrary()
  {
    return libraryName != null && libraryName.equalsIgnoreCase(JSONHeader.LIBRARY_NAME);
  }


  // Private Instance Methods -------------------------------------------------------------------

  private ModelObject constructModel(String name, Map<String, Object> structure)
  {
    // TODO :
    //
    //      The FlexJSON does little to document the structure it returns from its deserializer,
    //      using a very generic String, Object map only. In order to make it slightly easier
    //      and less error prone for subclasses to implement deserialization logic, attempt
    //      to convert the late-bound types to compile-time types for known JSON structures.
    //
    //      JSON primitives string, boolean, number are supported. Partial support for array
    //      types (as string representation), no specific handling for JSON null types at the
    //      moment.


    ModelObject json = new ModelObject(name);

    for (String valueName : structure.keySet())
    {
      Object value = structure.get(valueName);

      if (value instanceof Map)
      {
        // Assume map type from FlexJSON deserializer indicates a nested object...

        @SuppressWarnings("unchecked") Map<String, Object> objectStructure = (Map)value;

        ModelObject nested = constructModel(valueName, objectStructure);

        json.addObject(nested);
      }

      else if (value instanceof String)
      {
        json.addAttribute(new ModelObject.Attribute(valueName, ((String)value).trim()));
      }

      else if (value instanceof Boolean)
      {
        json.addAttribute(new ModelObject.BooleanAttribute(valueName, (Boolean)value));
      }

      else if (value instanceof Number)
      {
        json.addAttribute(new ModelObject.NumberAttribute(valueName, (Number)value));
      }

      else if (value instanceof List)
      {
        // TODO :
        //          note that the array support is not complete, nested arrays are not supported
        //          at the moment. Mixed type arrays that JSON allows are not supported, and maybe
        //          won't be.

        @SuppressWarnings("unchecked") List<Object> values = (List)value;

        json.addAttribute(new ModelObject.ArrayAttribute<Object>(valueName, values));
      }

      else
      {
        // TODO : log

        System.err.println("Unrecognized JSON structure: " + value);
      }
    }

    return json;
  }



  // FlexJSON Setters ---------------------------------------------------------------------------


  // These fields are still required by the FlexJSON deserializer (for unknown reasons)
  // despite the fact that corresponding setters have been defined (and that do not assign
  // values to these types). Therefore they need to be present, even they are unused.

  private String schemaVersion;
  private Map<String, Object> model;
  private String apiVersion;


  /**
   * Private setter required by the FlexJSON framework to deserialize a JSONPrototype instance.
   */
  private void setModel(Map<String, Object> json)
  {
    prototype = constructModel("model", json);
  }

  /**
   * Private setter required by the FlexJSON framework to deserialize a JSONPrototype instance.
   */
  private void setSchemaVersion(String schema)
  {
    try
    {
      this.schema = new Version(schema);
    }

    catch (IllegalArgumentException exception)
    {
      // TODO : log
      System.err.println(exception.getMessage());
    }
  }

  /**
   * Private setter required by the FlexJSON framework to deserialize a JSONPrototype instance.
   */
  private void setApiVersion(String api)
  {
    // this.api = new APIVersion()  TODO
  }

  /**
   * Private setter required by the FlexJSON framework to deserialize a JSONPrototype instance.
   */
  private void setLibraryName(String name)
  {
    this.libraryName = name;
  }

  /**
   * Private setter required by the FlexJSON framework to deserialize a JSONPrototype instance.
   */
  private void setJavaFullClassName(String name)
  {
    this.javaFullClassName = name;
  }
}

