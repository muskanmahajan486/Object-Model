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

import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import flexjson.JSONContext;
import flexjson.JSONDeserializer;
import flexjson.TypeContext;
import flexjson.transformer.AbstractTransformer;

import org.openremote.base.APIVersion;
import org.openremote.base.Version;
import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.base.exception.OpenRemoteException;
import org.openremote.model.Model;


/**
 * This is a utility class that extends the AbstractTransformer API provided by the FlexJSON
 * framework for creating Java type transformers. It contains some common implementation that
 * is not included in the AbstractTransformer implementation and increases the API type-safety
 * in some parts. Implementations in this package should extend this class instead of the
 * AbstractTransformer in most cases.
 *
 * @param <T> the Java type to convert to JSON representation
 *
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class JSONTransformer<T> extends AbstractTransformer
{


  // Class Members --------------------------------------------------------------------------------

  /**
   * A default validator for string type JSON attributes. This implementation ensures that
   * JSON strings added to org.openremote.model instance representations do not contain
   * null values and are not longer than {@link Model#DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT}
   * value to ensure they can be added to database model without truncation.
   */
  protected static JSONValidator<String> defaultStringValidator = new JSONValidator<String>()
  {
    @Override public void validate(String attribute) throws Model.ValidationException
    {
      if (attribute == null)
      {
        throw new Model.ValidationException("String attribute has null value.");
      }

      if (attribute.length() > Model.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT)
      {
        throw new Model.ValidationException(
            "String attribute is too long. Maximum {0} characters allowed, " +
            "given string has {1} characters.",
            Model.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT, attribute.length()
        );
      }
    }
  };

  /**
   * The JSON string validator made available by this class. Defaults to
   * {@link #defaultStringValidator}.
   *
   * @see #setStringValidator(org.openremote.model.data.json.JSONTransformer.JSONValidator)
   */
  private static JSONValidator<String> jsonStringValidator = defaultStringValidator;

  /**
   * Returns the JSON string validator configured for the instances of this class.
   *
   * @see Model#DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT
   *
   * @return JSON
   */
  public static JSONValidator<String> getStringValidator()
  {
    return jsonStringValidator;
  }

  /**
   * Sets a new JSON string validator for this class. This method should in general only
   * be called at the application initialization time, and not again after, to ensure
   * predictable operation. Setting a new JSON string validator mid-flight in the application
   * will impact all the existing instances.
   *
   * @see #defaultStringValidator
   * @see #getStringValidator()
   *
   * @param newValidator    a new JSON string validator
   */
  protected static void setStringValidator(JSONValidator<String> newValidator)
  {
    JSONTransformer.jsonStringValidator = newValidator;
  }


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * The class type this JSON transformer instance is being used for (not available at runtime due
   * to Java generics type erasure).
   */
  private Class<T> clazz;

  /**
   * Flag first property in a list for correct comma handling.
   */
  private boolean firstProperty = true;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new JSON transformer for a given Java type.
   *
   * @param clazz
   *            the class definition of the Java type
   */
  protected JSONTransformer(Class<T> clazz)
  {
    this.clazz = clazz;
  }


  // AbstractTransformer Overrides ----------------------------------------------------------------

  /**
   * Overrides the transform() method from AbstractTransformer to handle the comma handling
   * for property lists
   *
   * @param object
   *          the Java object to transform to JSON format
   */
  @Override public void transform(Object object)
  {
    JSONContext ctx = getContext();

    TypeContext typeCtx = ctx.peekTypeContext();

    if (typeCtx != null && !typeCtx.isFirst())
    {
      ctx.writeComma();
    }

    try
    {
      write(clazz.cast(object));
    }

    catch (ClassCastException e)
    {
      throw new IncorrectImplementationException(
          "Implementation Error: Object transformation to JSON data format failed -- " +
          "received type ''{0}'' is not compatible with expected type ''{1}''.",
          object.getClass().getName(), clazz.getName()
      );
    }
  }


  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Adds a property with a given name and value to the JSON representation. The value is
   * transformed to JSON representation per the registered type transformers registered to
   * the FlexJSON framework. <p>
   *
   * This method can be used for JSON transformers which translate Java types to JSON objects
   * with multiple properties. See {@link #startObject()} and {@link #endObject()} on defining
   * the starting and ending boundaries for structured JSON objects.
   *
   * @param name
   *          the property name to add to the JSON representation
   *
   * @param value
   *          the value of the property
   */
  protected void writeProperty(String name, Object value)
  {
    JSONContext ctx = getContext();

    if (!firstProperty)
    {
      ctx.writeComma();
    }

    else
    {
      firstProperty = false;
    }

    ctx.writeName(name);

    writeValue(value);
  }

  /**
   * Writes a JSON value. This is useful for simple type transformers that translate to a single
   * property value strings. For types that do not include default transformers in the FlexJSON
   * framework, a matching object type transformer should be registered.
   *
   * @param object
   *          the JSON property value to transform
   */
  protected void writeValue(Object object)
  {
    JSONContext ctx = getContext();

    ctx.transform(object);
  }

  /**
   * Writes the appropriate JSON marker that begins a new JSON object.
   */
  protected void startObject()
  {
    JSONContext ctx = getContext();

    ctx.writeOpenObject();
  }

  protected void startObject(String name)
  {
    JSONContext ctx = getContext();

    ctx.writeName(name);

    ctx.writeOpenObject();
  }

  /**
   * Writes the appropriate JSON marker that ends an JSON object definition.
   */
  protected void endObject()
  {
    JSONContext ctx = getContext();

    ctx.writeCloseObject();

    // transformer is stateful (unfortunately) so make sure we reset state at the end of object...

    firstProperty = true;
  }

  /**
   * Override this method to provide the implementation of how to transform a given object
   * instance to a JSON representation. This method should be functionally equivalent to the
   * transform() method in AbstractTransformer class but adds a type-safe API. Use the helper
   * method implementations in this class to avoid having to deal with FlexJSON specific APIs
   * with regards to contexts, comma handling and so on. See {@link #writeProperty(String, Object)},
   * {@link #writeValue(Object)}, {@link #startObject()} and {@link #endObject()} for example.
   *
   * @param object
   *          the object instance to transform to JSON representation
   */
  protected abstract void write(T object);


  /**
   * Reads from a stream a JSON representation of a domain object and deserializes it to a full
   * Java type. <p>
   *
   * The reader stream should point to a beginning of a JSON object that starts with a
   * {@link JSONHeader} representation. The JSON headers are parsed first, after which the
   * model object JSON attributes and values are passed to a concrete domain object deserializer
   * via a call to {@link #deserialize(Version, String, Map)} method.
   * The concrete domain model implementation should construct the Java instance based on this
   * JSON data. <p>
   *
   * This implementation will automatically reject any JSON representation that does not
   * match the library name {@link JSONHeader#LIBRARY_NAME} in its header fields.
   *
   * @param   reader
   *            a reference to the stream reader
   *
   * @throws  DeserializationException
   *            if the domain object cannot be resolved from the JSON representation
   *
   * @return  initialized domain object instance
   */
  protected T read(Reader reader) throws DeserializationException
  {
    // Use flex JSON to deserialize representation to a JSON prototype with header fields...

    JSONPrototype prototype;

    try
    {
      prototype = new JSONDeserializer<JSONPrototype>()
          .deserialize(new BufferedReader(reader), JSONPrototype.class);
    }

    catch (Throwable throwable)
    {
      throw new DeserializationException(
          "JSON representation could not be resolved to a proper prototype from the stream : {0}",
          throwable, throwable.getMessage()
      );
    }


    // Reject the JSON if it doesn't belong to this library...

    if (!prototype.isValidLibrary())
    {
      throw new DeserializationException(
          "Ignoring JSON object with library identifier '{0}'.", prototype.libraryName
      );
    }

    // Attempt to resolve the JSON schema version value to a Java version type...

    Version schemaVersion;

    try
    {
      schemaVersion = new Version(prototype.schemaVersion);
    }

    catch (IllegalArgumentException exception)
    {
      // TODO : log

      schemaVersion = Version.UNKNOWN;
    }

    // Resolve the domain object model into a Java instance (via concrete subclass impl.)...

    if (prototype.modelPrototype == null)
    {
      throw new DeserializationException(
          "Model object JSON representation did not resolve correctly. " +
          "Class: '{1}', Schema : {1}, API : {2}",
          prototype.javaFullClassName, prototype.schemaVersion, prototype.apiVersion
      );
    }

    return deserialize(schemaVersion, prototype.javaFullClassName, prototype.modelPrototype);
  }

  /**
   * Override this method to provide the implementation of how to deserialize a given set of
   * JSON attribute names and values into a typed Java domain object instance.
   *
   * TODO : include API version
   *
   * @param schemaVersion
   *          The schema version advertized in the JSON representation and that should have
   *          corresponding structure in the given JSON attributes parameter
   *
   * @param fullJavaClassName
   *          The Java class name advertized in the JSON representation that should match the
   *          the expected domain object implementation class name and to which the JSON
   *          structure given in JSON attributes parameter should conform to
   *
   * @param jsonAttributes
   *          Map of JSON attribute names and values that were included in the JSON domain
   *          model representation. The expected attribute names and data types should correspond
   *          to the given schema version and its concrete JSON schema definition.
   */
  protected abstract T deserialize(Version schemaVersion,
                                   String fullJavaClassName,
                                   Map<String, String> jsonAttributes)
      throws DeserializationException;



  // Nested Interfaces ----------------------------------------------------------------------------

  /**
   * A validator interface that can be used by implementations that want to check the incoming
   * values adhere to given constraints before they are written to JSON serialization documents.
   *
   * @param <T>   Java type of the JSON attribute to be validated
   */
  public static interface JSONValidator<T>
  {
    public void validate(T attribute) throws Model.ValidationException;
  }



  // Nested Classes -------------------------------------------------------------------------------

  /**
   * A deserialization data container used by FlexJSON deserializer to create a JSON prototype
   * parsing the expected JSON data structure. This container still includes header fields such
   * as library name and schema version that can be used to determine how to deserialize and
   * construct the model objects contained within.
   */
  private static class JSONPrototype
  {

    // Instance Fields ----------------------------------------------------------------------------

    private Map<String, String> modelPrototype;

    private String schemaVersion;
    private String apiVersion;
    private String libraryName;
    private String javaFullClassName;


    // Constructors -------------------------------------------------------------------------------

    /**
     * A no-args constructor required by the FlexJSON deserialization framework.
     */
    private JSONPrototype()
    {

    }


    // Instance Methods ---------------------------------------------------------------------------

    /**
     * Checks the given library name in the header fields of this JSON representation against
     * the library name this implementation belongs to.
     *
     * @return  true if library names match
     */
    private boolean isValidLibrary()
    {
      return libraryName != null && libraryName.equalsIgnoreCase(JSONHeader.LIBRARY_NAME);
    }

    /**
     * Private setter required by the FlexJSON framework to deserialize a JSONPrototype instance.
     */
    private void setModel(Map<String, String> model)
    {
      // TODO : note on type safety

      this.modelPrototype = model;
    }

    /**
     * Private setter required by the FlexJSON framework to deserialize a JSONPrototype instance.
     */
    private void setSchemaVersion(String schema)
    {
      this.schemaVersion = schema;
    }

    /**
     * Private setter required by the FlexJSON framework to deserialize a JSONPrototype instance.
     */
    private void setApiVersion(String api)
    {
      this.apiVersion = api;
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


  /**
   * An instance of this class represents JSON objects contained within the OpenRemote Object
   * Model JSON schema. A template OpenRemote Object Model JSON document contains a named object
   * 'model' as a nested JSON object which an instance of this class represents. Furthermore,
   * any additional nested JSON objects within the 'model' instance are represented by the
   * instances of this class.  <p>
   *
   * An OpenRemote Object Model JSON document template follows the pattern below:
   *
   * <pre>
   * {
   *   "libraryName": "OpenRemote Object Model",
   *   "javaFullClassName": "org.openremote.model.[classname]",
   *   "schemaVersion": "[schema version this JSON document corresponds to]",
   *   "apiVersion": "[API version of the class that generated this JSON document]",
   *
   *   "model":
   *   {
   *     [model content that maps to the class name specified in the header]
   *   }
   * }
   * </pre>
   *
   * This object represents the object named 'model' in this schema (or other JSON objects
   * within the 'model' object). Therefore the root 'model' instance contains the full
   * model object description without the header fields (library name, schema version, etc.). <p>
   *
   * This class provides method to access the named attributes (JSON name, value pairs that are
   * not nested JSON objects) and named JSON objects within this model's JSON structure.
   *
   * @see #hasObject(String)
   * @see #getObject(String)
   * @see #hasAttribute(String, String)
   * @see #getAttribute(String) 
   */
  public static class ModelObject
  {

    // Instance Fields ----------------------------------------------------------------------------

    // TODO
    private Map<String, String> attributes = new HashMap<String, String>(1);

    /**
     * Map of named model JSON objects nested within the root 'model' object.
     */
    private Map<String, ModelObject> objects = new HashMap<String, ModelObject>(1);

    /**
     * The JSON object name this instance represents.
     */
    private String name = "";


    // Constructors -------------------------------------------------------------------------------

    private ModelObject(String name)
    {
      this.name = name;
    }


    // Public Instance Methods --------------------------------------------------------------------

    /**
     * Indicates if this object instance contains any attributes (JSON name, value pairs that
     * are not object types).
     *
     * @see #hasObjects()
     *
     * @return
     *          true if this instance has named attributes, false otherwise
     */
    public boolean hasAttributes()
    {
      return !attributes.isEmpty();
    }

    /**
     * Indicates if this object instance contains a named attribute with a given value.
     *
     * @see #hasObject(String)
     *
     * @param name
     *              name of the attribute
     *
     * @param value
     *              expected value of the attribute
     *
     * @return
     *          true if this instance has a named attribute with the given value, false otherwise
     */
    public boolean hasAttribute(String name, String value)
    {
      return attributes.keySet().contains(name) && attributes.get(name).equals(value);
    }

    /**
     * TODO
     *
     * @param name
     * @return
     */
    public String getAttribute(String name)
    {
      return attributes.get(name);
    }

    /**
     * Indicates if this object instance contains any nested JSON objects.
     *
     * @return
     *          true if there are nested JSON objects within this instance, false otherwise
     */
    public boolean hasObjects()
    {
      return !objects.isEmpty();
    }

    /**
     * Indicates if a given named JSON object is contained within this instance.
     *
     * @param name
     *              JSON object name to check
     *
     * @return
     *              true if a JSON object with the given name exists within this instance's
     *              structure, false otherwise
     */
    public boolean hasObject(String name)
    {
      return objects.keySet().contains(name);
    }

    /**
     * Returns an object within the model object JSON representation with the given name.
     *
     * @param name
     *              JSON object name within this instance's JSON structure
     *
     * @return
     *              a model object instance representing the nested JSON object
     */
    public ModelObject getObject(String name)
    {
      return objects.get(name);
    }


    // Object Overrides ---------------------------------------------------------------------------

    @Override public String toString()
    {
      return "Name: " + name + ", Attributes: " + attributes.toString() +
             ", Objects: " + objects.toString();
    }
  }


  public static class DeserializationException extends OpenRemoteException
  {
    public DeserializationException(String msg)
    {
      super(msg);
    }

    public DeserializationException(String msg, Object... params)
    {
      super(msg, params);
    }

    public DeserializationException(String msg, Throwable rootCause)
    {
      super(msg, rootCause);
    }

    public DeserializationException(String msg, Throwable rootCause, Object... params)
    {
      super(msg, rootCause, params);
    }
  }
}

