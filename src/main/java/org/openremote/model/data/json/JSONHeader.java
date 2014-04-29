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

import flexjson.JSONSerializer;
import flexjson.transformer.Transformer;
import org.openremote.base.APIVersion;
import org.openremote.base.Version;


/**
 * This implementation encapsulates an model object JSON representation within a standard
 * JSON header object that is used across all model object implementations in this package. <p>
 *
 * This header can be used to classify each JSON object and also to migrate the data structures
 * between implementation versions. Currently the headers that are included with each model object
 * JSON representation include:
 *
 * <ul>
 *   <li>libraryName</li>
 *   <li>javaFullClassName</li>
 *   <li>serialVersion</li>
 *   <li>apiVersion</li>
 * </ul>
 *
 * The library name property can be used as a quick classification mechanism in cases where
 * multiple JSON objects must be dealt with that originate from many different sources. It's
 * value is defined in {@link #LIBRARY_NAME} and is the same for every JSON object created
 * through this method. <p>
 *
 * The Java full class name property contains the fully qualified Java class name of the
 * model instance that is embedded in this JSON header object. <p>
 *
 * The serialization version property is a version number for the JSON schema that is used
 * to transfer the Java object model instance between processes. This is similar to the
 * purpose SerialVersionUID in Java serialization specification. While an object model
 * implementation may evolve (and as such have a new API version number), the serialization
 * version number does not need to change if the newer implementations can still be represented
 * by the same JSON data format. <p>
 *
 * The API version property indicates the versioning of the underlying object model
 * implementation.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class JSONHeader<T>
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * A fixed library name JSON property value used by this JSON header implementation. This field
   * can be used as a quick identifier of a category of JSON object representations that belong
   * to this OpenRemote Object Model library. Value : {@value}
   */
  public static final String LIBRARY_NAME = "OpenRemote Object Model";

  /**
   * API version value for the corresponding JSON property in this JSON header implementation.
   * This property can be used to determine the implementation API version that was used to
   * generate the JSON object instance. Note that API version is distinct from
   * {@link #serialVersion}, the latter defines the version of the data format and its
   * compatibility.
   */
  public static final APIVersion API_VERSION = new APIVersion(JSONHeader.class.getPackage());


  // Class Members --------------------------------------------------------------------------------

  /**
   * A simple main method that can be used to print an example JSON header object containing
   * a single String instance as its "model" object.
   *
   * @param args
   *          command line arguments
   */
  public static void main(String... args)
  {
    // contains model property with single string value 'test object' -- no transformers
    // required for string types, part of built-in types in FlexJSON...

    System.out.println(toJSON("test object", new Version(1, 0, 0), new Transformer[] {}));
  }


  /**
   * Utility method to convert a given object model instance to JSON data exchange format. <p>
   *
   * This method adds a set of header properties to each JSON representation that includes
   * fields for serialization version and API compatibility. This header can be used to
   * classify each JSON object and also to migrate the data structures between implementation
   * versions. <p>
   *
   * Currently the headers that are included with each model object JSON representation include:
   *
   * <ul>
   *   <li>libraryName</li>
   *   <li>javaFullClassName</li>
   *   <li>serialVersion</li>
   *   <li>apiVersion</li>
   * </ul>
   *
   * The library name property can be used as a quick classification mechanism in cases where
   * multiple JSON objects must be dealt with that originate from many different sources. It's
   * value is defined in {@link #LIBRARY_NAME} and is the same for every JSON object created
   * through this method. <p>
   *
   * The Java full class name property contains the fully qualified Java class name of the
   * model instance that is embedded in this JSON header object. <p>
   *
   * The serialization version property is a version number for the JSON schema that is used
   * to transfer the Java object model instance between processes. This is similar to the
   * purpose SerialVersionUID in Java serialization specification. While an object model
   * implementation may evolve (and as such have a new API version number), the serialization
   * version number does not need to change if the newer implementations can still be represented
   * by the same JSON data format. <p>
   *
   * The API version property indicates the versioning of the underlying object model
   * implementation.
   *
   * @param model
   *          the object model instance to embed into this JSON header
   *
   * @param serialVersion
   *          the serial version that indicates the JSON data structure version used when
   *          externalizing the object model instance to JSON format
   *
   * @param transformers
   *          FlexJSON transformers that may be required to translate the given model object
   *          instance to its JSON data format
   *
   * @param <M>
   *          the type of the model class that is transformed to JSON data format
   *
   * @return
   *          JSON string representing the given model instance
   *
   */
  public static <M> String toJSON(M model, Version serialVersion, Transformer... transformers)
  {
    JSONSerializer serializer = new JSONSerializer()
        .transform(new HeaderTransformer(), JSONHeader.class)
        .transform(new VersionTransformer(), Version.class)
        .exclude("*.class")
        .prettyPrint(true);

    for (Transformer transformer : transformers)
    {
      serializer = serializer.transform(transformer, model.getClass());
    }

    return serializer.serialize(new JSONHeader<M>(model, serialVersion));
  }


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * The fully qualified class name of the model object that is wrapped in this JSON header.
   */
  private String javaFullClassName;

  /**
   * The version of the data exchange serialization format used by the model object wrapped in
   * this JSON header. <p>
   *
   * Note that the serial version may evolve independent of the API version -- when a new release
   * of API is created that maintains JSON data exchange serialization format compatibility, this
   * serial version value should remain unchanged (where-as API version is increased with each
   * release).
   */
  private Version serialVersion;

  /**
   * The object model instance wrapped by this JSON header. Appropriate type transformer
   * should be registered with the FlexJSON framework before attempting to write this reference
   * to a JSON data exchange format.
   */
  private T model;

  // TODO : add implementation version


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs new JSON header instance for a given object model instance with a given
   * JSON data exchange serialization version.
   *
   * @param model
   *          the object model instance to externalize as part of this JSON header instance
   *
   * @param serialVersion
   *          the version information included in this JSON header that can be used to determine
   *          the schema of the externalized JSON data format
   */
  protected JSONHeader(T model, Version serialVersion)
  {
    this.javaFullClassName = model.getClass().getName();

    this.serialVersion = serialVersion;

    this.model = model;
  }


  // Nested Classes -------------------------------------------------------------------------------

  /**
   * A FlexJSON transformer for this JSON header implementation. This implementation translates
   * the {@link JSONHeader} instances to their defined JSON data exchange serialization format.
   */
  public static class HeaderTransformer extends JSONTransformer<JSONHeader>
  {

    // Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a FlexJSON transformer for {@link JSONHeader} instances.
     */
    public HeaderTransformer()
    {
      super(JSONHeader.class);
    }

    // JSONTransformer Overrides ------------------------------------------------------------------

    /**
     * Writes a JSON representation of this JSON header and the object model instance it wraps
     * according to the specified JSON schema. The JSON property values are retrieved from the
     * given JSON header instance. The implementation assumes appropriate transformers for the
     * embedded model instance have been registered with the FlexJSON framework.
     *
     * @param header  the JSON header instance to externalize in JSON data exchange format
     */
    @Override public void write(JSONHeader header)
    {
      // write opening brace for root object...

      startObject();

      // write the mandatory JSON header properties per the defined JSON schema...

      writeProperty("libraryName", JSONHeader.LIBRARY_NAME);
      writeProperty("javaFullClassName", header.javaFullClassName);
      writeProperty("serialVersion", header.serialVersion);
      writeProperty("apiVersion", JSONHeader.API_VERSION);

      // Include the wrapped model object instance in the externalized data exchange format.
      // This assumes the appropriate Java type transformers for the object model instance have
      // been registered with the FlexJSON framework...

      writeProperty("model", header.model);

      // close the root object curly brace...

      endObject();
    }
  }


  /**
   * A FlexJSON transformer for version fields used in this JSON header implementation. This
   * implementation simply converts the version fields to their specified string format and
   * includes versions as single value properties in JSON data exchange format.
   */
  public static class VersionTransformer extends JSONTransformer<Version>
  {
    /**
     * Constructs a FlexJSON transformer for {@link org.openremote.base.Version} instances.
     */
    public VersionTransformer()
    {
      super(Version.class);
    }

    // JSONTransformer Overrides ------------------------------------------------------------------

    /**
     * Writes a JSON representation of a version field used in Java objects being externalized
     * to JSON data exchange format. This implementation simply converts the given
     * {@link org.openremote.base.Version} instance to its string representation via
     * {@link org.openremote.base.Version#toString()} implementation and includes the version
     * string as a single JSON property value.
     *
     * @param version   the given version instance to convert to JSON property representation
     */
    @Override public void write(Version version)
    {
      writeValue(version.toString());
    }
  }
}

