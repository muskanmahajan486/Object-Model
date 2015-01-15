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

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

import flexjson.JSONContext;
import flexjson.JSONDeserializer;
import flexjson.TypeContext;
import flexjson.transformer.AbstractTransformer;

import org.openremote.base.exception.IncorrectImplementationException;
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
 * @author Juha Lindfors
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
    // FlexJSON reuses transformer instances so need to make sure we reset this context
    // variable at the beginning of each call to transform()...

    firstProperty = true;

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



  /**
   * Reads from a stream a JSON representation of a domain object and deserializes it to a full
   * Java type. <p>
   *
   * The reader stream should point to a beginning of a JSON object that starts with a
   * {@link JSONHeader} representation. The JSON headers are parsed first, after which the
   * model object JSON attributes and values are passed to a concrete domain object deserializer
   * via a call to {@link #deserialize(JSONModel)} method. The concrete domain model implementation
   * should construct the Java instance based on this JSON data. <p>
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
  public T read(Reader reader) throws DeserializationException
  {
    // Use flex JSON to deserialize representation to a JSON prototype with header fields...

    JSONModel model;

    try
    {
      model = new JSONDeserializer<JSONModel>()
          .deserialize(new BufferedReader(reader), JSONModel.class);

      // TODO : log debug --> "Deserialized " + model
    }

    catch (Throwable throwable)
    {
      throw new DeserializationException(
          "JSON representation could not be resolved to a proper prototype from the stream : {0}",
          throwable, throwable.getMessage()
      );
    }


    // Reject the JSON if it doesn't belong to this library...

    if (!model.isValidLibrary())
    {
      throw new DeserializationException(
          "Ignoring JSON object with library identifier '{0}'.", model.libraryName
      );
    }

    // Reject the JSON if the model didn't resolve to any attributes or nested objects...

    if (!model.getModel().hasAttributes() && !model.getModel().hasObjects())
    {
      throw new DeserializationException(
          "Model object JSON representation did not resolve correctly. " +
          "Class: ''{0}'', Schema : {1}, API : {2}",
          model.javaFullClassName, model.schema, model.api
      );
    }

    // Resolve the domain object model into a Java instance (via concrete subclass impl.)...

    return deserialize(model);
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
   * Adds an array with a given name and array elements to the JSON representation. The
   * element values are transformed to JSON representation per the registered type transformers
   * registered to the FlexJSON framework. <p>
   *
   * @param name
   *          the array name to add to the JSON representation
   *
   * @param values
   *          the elements of the array (converted by transformers if/when present)
   */
  protected void writeArray(String name, Collection values)
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

    ctx.writeOpenArray();

    Iterator it = values.iterator();

    while (it.hasNext())
    {
      writeValue(it.next());

      if (it.hasNext())
      {
        ctx.writeComma();
      }
    }

    ctx.writeCloseArray();
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
   * Override this method to provide the implementation of how to deserialize a given set of
   * JSON attribute names and values into a typed Java domain object instance.
   *
   * @param model
   *          A model frame that represents the structures parsed from the JSON document
   *          instance. This prototype should be used to construct the deserialized Java
   *          instance the JSON represented.
   */
  protected abstract T deserialize(JSONModel model) throws DeserializationException;



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


}

