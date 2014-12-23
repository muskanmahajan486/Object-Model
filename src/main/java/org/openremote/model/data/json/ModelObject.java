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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 * This top level name, value pairs can be seen as the frame or headers of the document that are
 * not part of the domain or model object. The nested 'model' JSON object represents the JSON
 * serialized version of the Java object model instance. <p>
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
 *
 * @author Juha Lindfors
 */
public class ModelObject
{

  // IMPLEMENTATION NOTE:
  //
  //   - This implementation is intended as a convenience type interface between the fairly
  //     generic model offered by FlexJSON model transformer implementations
  //     used to serialize and deserialize JSON document notation. It's not intended
  //     as a full JSON model representation, and is only implemented to the extent as required
  //     by current OR object model schemas.



  // Instance Fields ----------------------------------------------------------------------------

  /**
   * List of JSON 'attributes' in an OpenRemote model JSON representation. An attribute represents
   * a string, number or boolean value. Any non-object value in JSON representation. <p>
   *
   * Note: using concrete map type to get access to read-only elements() enumeration.
   *
   * @see #objects
   */
  private ConcurrentHashMap<String, Attribute> attributes =
      new ConcurrentHashMap<String, Attribute>(1);

  /**
   * Map of named model JSON objects nested within the root 'model' object.
   */
  private Map<String, ModelObject> objects = new ConcurrentHashMap<String, ModelObject>(1);

  /**
   * The JSON object name this instance represents.
   */
  private String name = "";


  // Constructors -------------------------------------------------------------------------------

  /**
   * Constructs a new model object instance with a given JSON object name.
   *
   * @param name
   *          name value that represents this object in the model's JSON representation
   */
  protected ModelObject(String name)
  {
    this.name = name;
  }


  // Public Instance Methods --------------------------------------------------------------------

  /**
   * Indicates if this object instance contains any attributes (JSON name, value pairs that
   * are not object types, i.e. string, boolean or number).
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
   * Indicates if this object instance contains a named attribute with a given value. This
   * implementation compares string values, or boolean and number values using their string
   * conversions. Array value string representations start and end with square brackets [] and
   * individual elements are comma separated.
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
    if (attributes.keySet().contains(name) && attributes.get(name).isArray())
    {
      // compare array type string representations by stripping whitespaces, this makes the
      // API usage a bit more flexible without whitespace mismatches...

      final String whitespace = "\\s";

      return attributes.keySet().contains(name) &&
             attributes.get(name).getValue().replaceAll(whitespace, "")
                 .equals(value.trim().replaceAll(whitespace, ""));
    }

    return attributes.keySet().contains(name) &&
           attributes.get(name).getValue().equals(value);
  }

  /**
   * Indicates if this object instance contains a named attribute with a boolean value.
   *
   * @see #hasAttribute(String, String)
   *
   * @param name
   *              name of the attribute
   *
   * @param value
   *              expected value of the attribute
   *
   * @return
   *          true if this instance has a named attribute with the given boolean value,
   *          false otherwise
   */
  public boolean hasAttribute(String name, boolean value)
  {
    Attribute attr = attributes.get(name);

    if (attr != null && attr instanceof BooleanAttribute)
    {
      // safe cast given the check above...

      return ((BooleanAttribute)attr).getBoolean() == value;
    }

    else
    {
      return false;
    }
  }

  /**
   * Indicates if this object instance contains a named attribute with a number value.
   *
   * @see #hasAttribute(String, String)
   *
   * @param name
   *              name of the attribute
   *
   * @param value
   *              expected value of the attribute
   *
   * @return
   *          true if this instance has a named attribute with the given number value,
   *          false otherwise
   */
  public boolean hasAttribute(String name, Number value)
  {
    Attribute attr = attributes.get(name);

    if (attr != null && attr instanceof NumberAttribute)
    {
      // safe cast given the check above...

      return new Double(((NumberAttribute)attr).getNumber().doubleValue())
          .compareTo(value.doubleValue()) == 0;
    }

    else
    {
      return false;
    }
  }


  /**
   * Indicates if this object instance contains a named attribute with an array value.
   *
   * @see #hasAttribute(String, String)
   *
   * @param name
   *              name of the attribute
   *
   * @param value
   *              expected value of the attribute
   *
   * @return
   *          true if this instance has a named attribute with the given number value,
   *          false otherwise
   */
  public <T> boolean hasAttribute(String name, List<T> value)
  {
    Attribute attr = attributes.get(name);

    if (attr == null || !attr.isArray)
    {
      return false;
    }

    try
    {
      @SuppressWarnings("unchecked") ArrayAttribute<T> array = (ArrayAttribute<T>)attr;

      return array.compare(value);
    }

    catch (ClassCastException exception)
    {
      // TODO : log

      System.err.println("Type error: " + exception.getMessage());

      return false;
    }
  }

  /**
   * Returns a string representation of this model object's attribute. JSON numbers and boolean
   * types are converted to Java strings. Arrays are converted to string beginning and ending
   * with square brackets [] and with each array element separated with a comma and space character.
   * Note that for string arrays, white space and line change characters are preserved if present
   * in the JSON document.
   *
   * @param name
   *            name of the attribute
   *
   * @return    string value of the given attribute, or null if attribute is not present
   */
  public String getAttribute(String name)
  {
    Attribute attr = attributes.get(name);

    return (attr == null) ? null : attr.getValue();
  }

  /**
   * Returns a boolean attribute value.
   *
   * @param name
   *            name of the attribute
   *
   * @return    true, false, or null if attribute is not present or is not boolean type
   */
  public Boolean getBooleanAttribute(String name)
  {
    Attribute attr = attributes.get(name);

    if (attr != null && attr instanceof BooleanAttribute)
    {
      return ((BooleanAttribute)attr).getBoolean();
    }

    else
    {
      return null;
    }
  }

  /**
   * Returns a number attribute value.
   *
   * @param name
   *            name of the attribute
   *
   * @return    number or null if attribute is not present or is not number type
   */
  public Number getNumberAttribute(String name)
  {
    // NOTE:
    //        returning a generic number type here instead of more specific types -- while it
    //        would be possible to make a distinction between integer and decimal numbers, it
    //        would depend on how the number is specified in the JSON document,
    //        e.g. '1' vs. '1.0' in the JSON would translate either to Long or Double type,
    //        which could cause confusion and errors in code if numbers are not consistently
    //        specified by type in JSON document. While use of Number type is clumsy, it moves
    //        the responsibility of typing to the user of API, i.e. whether they decide to treat
    //        the number with intValue() or doubleValue().
    //
    //        That's the behavior at least for now until there's an argument for changing it.

    Attribute attr = attributes.get(name);

    if (attr != null && attr instanceof NumberAttribute)
    {
      return ((NumberAttribute)attr).getNumber();
    }

    return null;
  }

  /**
   * Returns a string array attribute.
   *
   * @param name
   *            name of the attribute
   *
   * @return    a list of strings in the array, or null if the attribute is not present or is
   *            not of string array type
   */
  public List<String> getStringArray(String name)
  {
    Attribute attr = attributes.get(name);

    try
    {
      if (attr != null && attr instanceof ArrayAttribute)
      {
        @SuppressWarnings("unchecked") ArrayAttribute<String> array = (ArrayAttribute<String>)attr;

        if (array.isStringArray)
        {
          return new ArrayList<String>(array.value);
        }
      }
    }

    catch (ClassCastException exception)
    {
      // TODO : log

      System.err.println("Attribute " + name + " is not string array type.");
    }

    return null;
  }

  /**
   * Returns a number array attribute.
   *
   * @param name
   *            name of the attribute
   *
   * @return    a list of numbers in the array, or null if the attribute is not present or is
   *            not of number array type
   */
  public List<Number> getNumberArray(String name)
  {
    Attribute attr = attributes.get(name);

    try
    {
      if (attr != null && attr instanceof ArrayAttribute)
      {
        @SuppressWarnings("unchecked") ArrayAttribute<Number> array = (ArrayAttribute<Number>)attr;

        if (array.isNumberArray)
        {
          return new ArrayList<Number>(array.value);
        }
      }
    }

    catch (ClassCastException exception)
    {
      // TODO : log

      System.err.println("Attribute " + name + " is not number array type.");
    }

    return null;
  }

  /**
   * Returns a boolean array attribute.
   *
   * @param name
   *            name of the attribute
   *
   * @return    a list of booleans in the array, or null if the attribute is not present or is
   *            not of number array type
   */
  public List<Boolean> getBooleanArray(String name)
  {
    Attribute attr = attributes.get(name);

    try
    {
      if (attr != null && attr instanceof ArrayAttribute)
      {
        @SuppressWarnings("unchecked") ArrayAttribute<Boolean> array = (ArrayAttribute<Boolean>)attr;

        if (array.isBooleanArray)
        {
          return new ArrayList<Boolean>(array.value);
        }
      }
    }

    catch (ClassCastException exception)
    {
      // TODO : log

      System.err.println("Attribute " + name + " is not boolean array type.");
    }

    return null;
  }


  /**
   * Returns a read-only enumeration of the attributes contained within this model's JSON
   * representation.
   *
   * @return
   *          enumeration of model attributes
   */
  public Enumeration<Attribute> getAttributes()
  {
    return attributes.elements();
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


  // Object Overrides -----------------------------------------------------------------------------

  @Override public String toString()
  {
    return "Name: " + name + ", Attributes: " + attributes.toString() +
           ", Objects: " + objects.toString();
  }



  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Adds a JSON attribute value to this model object.
   *
   * @see Attribute
   * @see ArrayAttribute
   *
   * @param attr
   *          attribute to add
   */
  protected void addAttribute(Attribute attr)
  {
    attributes.put(attr.name, attr);
  }

  /**
   * Adds a JSON object to this model.
   *
   * @see #addAttribute
   *
   * @param object
   *          object to add
   */
  protected void addObject(ModelObject object)
  {
    objects.put(object.name, object);
  }


  // Nested Classes -------------------------------------------------------------------------------

  /**
   * This is a container class for JSON attributes in model objects. It represents a non-object
   * JSON attribute: string, number or boolean, or an array of these basic types. <p>
   *
   * This implementation returns a string representation of an attribute. Specific subclasses
   * can provide an attribute-specific and type-specific APIs.
   */
  public static class Attribute
  {
    /**
     * Indicates whether this attribute represents an array of attribute values.
     */
    protected boolean isArray = false;

    /**
     * Attribute value.
     */
    private String value;

    /**
     * Attribute name.
     */
    private String name;


    // Constructors -------------------------------------------------------------------------------

    protected Attribute(String name, String value)
    {
      this.name = name;
      this.value = value;
    }

    /**
     * Returns a string representation of JSON non-object primitives: string, number, boolean,
     * or an array of these types. Arrays are converted to string beginning and ending
     * with square brackets [] and with each array element separated with a comma and
     * space character.
     *
     * @return  A string representation of JSON value.
     */
    public String getValue()
    {
      return value;
    }

    /**
     * Returns the name of this attribute.
     */
    public String getName()
    {
      return name;
    }

    /**
     * Indicates whether this attribute represents an array type.
     */
    public boolean isArray()
    {
      return isArray;
    }
  }

  /**
   * A typed class representing a JSON boolean type attribute.
   */
  public static class BooleanAttribute extends Attribute
  {
    private Boolean value;

    protected BooleanAttribute(String name, Boolean value)
    {
      super(name, value.toString());

      this.value = value;
    }

    public Boolean getBoolean()
    {
      return value;
    }
  }

  /**
   * A typed class representing a JSON number attribute.
   */
  public static class NumberAttribute extends Attribute
  {
    private Number value;

    protected NumberAttribute(String name, Number number)
    {
      super(name, number.toString());

      this.value = number;
    }

    public Number getNumber()
    {
      return value;
    }
  }

  /**
   * A typed class representing a JSON array.
   *
   * TODO :
   *
   *    Not intended as a complete implementation in it's current state.
   *
   *     - Will not (and maybe should not) support arrays with JSON mixed types, e.g.
   *       string elements with objects in an array. May never support these mixed type
   *       arrays unless there's a really compelling reason to do so.
   *     - Does not currently support nested arrays. Should be added for completeness.
   */
  public static class ArrayAttribute<T> extends Attribute
  {

    // Class Members ------------------------------------------------------------------------------

    /**
     * Creates a string representation of this JSON array that begins and ends the string with
     * brackets [] and has array elements in a comma separated list.
     *
     * @param array
     *            array to build a string representation for
     *
     * @return
     *            string in the format [element, element, element] where element may be a string
     *            value, boolean value or JSON number value
     */
    private static String buildArrayString(List<?> array)
    {
      StringBuilder builder = new StringBuilder();
      builder.append('[');

      Iterator it = array.iterator();

      while (it.hasNext())
      {
        builder.append(it.next());

        if (it.hasNext())
        {
          builder.append(", ");
        }
      }

      builder.append(']');

      return builder.toString();
    }


    // Instance Fields ----------------------------------------------------------------------------

    /**
     * The values in the array.
     */
    private AbstractList<T> value;

    /**
     * Array type: indicates the elements in the array (all of them) are of JSON string type.
     */
    private boolean isStringArray = false;

    /**
     * Array type: indicates the elements in the array (all of them) are of JSON number type.
     */
    private boolean isNumberArray = false;

    /**
     * Array type: indicates the elements in the array (all of them) are of JSON boolean type.
     */
    private boolean isBooleanArray = false;


    // Constructors -------------------------------------------------------------------------------

    protected ArrayAttribute(String name, List<T> array)
    {
      super(name, buildArrayString(array));

      isArray = true;

      determineArrayType(array);

      value = new ArrayList<T>(array);
    }


    // Public Instance Methods --------------------------------------------------------------------

    /**
     * Compares the given list to the contents of this array.
     *
     * @param array
     *            the list that is compared to this array
     *
     * @return
     *            true of false based on list equality to this array's values
     */
    public boolean compare(List<T> array)
    {
      return value.equals(array);
    }


    // Private Instance Methods -------------------------------------------------------------------


    /**
     * Attempts to resolve the array type from array elements (as resolved to Java types by
     * the FlexJSON framework). Will not accept mixed JSON type arrays. All elements in array
     * must be of the same type.
     *
     * @param array
     *            array elements to inspect
     */
    private void determineArrayType(List<T> array)
    {
      // TODO : add nested arrays if/when necessary

      for (Object o : array)
      {
        if (o instanceof String)
        {
          if (isBooleanArray || isNumberArray)
          {
            throw new IncorrectImplementationException(
                "Mixed JSON type arrays are not supported. Array {0} contains strings combined " +
                "with boolean or number types.",
                array
            );
          }

          isStringArray = true;
        }

        else if (o instanceof Number)
        {
          if (isStringArray || isBooleanArray)
          {
            throw new IncorrectImplementationException(
                "Mixed JSON type arrays are not supported. Array {0} contains numbers combined " +
                "with boolean or string types.",
                array
            );
          }

          isNumberArray = true;
        }

        else if (o instanceof Boolean)
        {
          if (isStringArray || isNumberArray)
          {
            throw new IncorrectImplementationException(
                "Mixed JSON type arrays are not supported. Array {0} contains booleans combined " +
                "with string or number types.",
                array
            );
          }

          isBooleanArray = true;
        }
      }
    }
  }

}

