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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openremote.base.Version;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.model.Model;
import org.openremote.model.testengine.OpenRemoteTest;

/**
 * Unit tests for {@link JSONTransformer} class.
 *
 * @author Juha Lindfors
 */
public class JSONTransformerTest extends OpenRemoteTest
{

  // Test Lifecycle -------------------------------------------------------------------------------

  private String basicJSON;
  private String noSchemaJSON;
  private String incorrectLibJSON;
  private String nestedJSON;
  private String booleanJSON;
  private String stringArrayJSON;
  private String numberJSON;
  private String numberArrayJSON;
  private String booleanArrayJSON;
  private String whitespaceStringArrayJSON;


  /**
   * Set up tests with loading sample JSON documents to compare to.
   */
  @BeforeClass public void loadJsonTestFiles()
  {
    try
    {
      basicJSON = loadResourceTextFile(new URI("json-transformer/basic-model.json"));
      noSchemaJSON = loadResourceTextFile(new URI("json-transformer/no-schema.json"));
      incorrectLibJSON = loadResourceTextFile(new URI("json-transformer/incorrect-lib.json"));
      nestedJSON = loadResourceTextFile(new URI("json-transformer/nested.json"));
      booleanJSON = loadResourceTextFile(new URI("json-transformer/boolean.json"));
      stringArrayJSON = loadResourceTextFile(new URI("json-transformer/string-array.json"));
      numberJSON = loadResourceTextFile(new URI("json-transformer/number.json"));
      numberArrayJSON = loadResourceTextFile(new URI("json-transformer/number-array.json"));
      booleanArrayJSON = loadResourceTextFile(new URI("json-transformer/boolean-array.json"));
      whitespaceStringArrayJSON = loadResourceTextFile(new URI("json-transformer/string-whitespace-array.json"));
    }

    catch (Throwable t)
    {
      System.err.println(String.format("%n%n !!! TEST SETUP FAILURE !!! %n%n "));

      t.printStackTrace();
    }
  }


  // Read/Deserialize Tests -----------------------------------------------------------------------


  /**
   * Tests a basic non-defined model read to deserialize.
   *
   * @throws Exception  if test fails
   */
  @Test public void testRead() throws Exception
  {
    Deserializer deserializer = new Deserializer()
    {
      @Override public void deserialize(JSONModel model)
      {
        Assert.assertTrue(model.containsSchema(new Version(0, 0, 0)));

        Assert.assertTrue(model.getModelClass().equals("MyModel"));

        Assert.assertTrue(model.getModel().hasAttributes());
        Assert.assertTrue(model.getModel().hasAttribute("name", "value"));
        Assert.assertTrue(!model.getModel().hasObjects());
      }
    };

    MyModelTransformer mmt = new MyModelTransformer(deserializer);

    ByteArrayInputStream bain = new ByteArrayInputStream(basicJSON.getBytes(DEFAULT_CHARSET));

    mmt.read(new InputStreamReader(bain));
  }


  /**
   * Test basic model with nested object.
   *
   * @throws Exception  if test fails
   */
  @Test public void testReadWithNestedObject() throws Exception
  {
    Deserializer deserializer = new Deserializer()
    {
      @Override public void deserialize(JSONModel model)
      {
        Assert.assertTrue(model.containsSchema(new Version(1, 2, 3)));

        Assert.assertTrue(model.getModelClass().equals("org.openremote.test.MyModel"));

        Assert.assertTrue(model.getModel().hasAttributes());
        Assert.assertTrue(model.getModel().hasAttribute("name", "value"));
        Assert.assertTrue(model.getModel().hasObjects());
        Assert.assertTrue(model.getModel().hasObject("object"));

        ModelObject o = model.getModel().getObject("object");

        Assert.assertTrue(o.hasAttribute("foo", "bar"));
        Assert.assertTrue(!o.hasObjects());
      }
    };

    MyModelTransformer mmt = new MyModelTransformer(deserializer);

    ByteArrayInputStream bain = new ByteArrayInputStream(nestedJSON.getBytes(DEFAULT_CHARSET));

    mmt.read(new InputStreamReader(bain));
  }


  /**
   * Tests model read behavior when incoming JSON document is missing a schema version header.
   *
   * @throws Exception  if test fails
   */
  @Test public void testReadNoSchema() throws Exception
  {
    Deserializer deserializer = new Deserializer()
    {
      @Override public void deserialize(JSONModel model)
      {
        Assert.assertTrue(!model.containsSchema(new Version(0, 0, 0)));
        Assert.assertTrue(model.containsSchema(Version.UNKNOWN));

        Assert.assertTrue(model.getModelClass().equals("MyModel"));

        Assert.assertTrue(model.getModel().hasAttributes());
        Assert.assertTrue(model.getModel().hasAttribute("name", "value"));
        Assert.assertTrue(!model.getModel().hasObjects());
      }
    };

    MyModelTransformer mmt = new MyModelTransformer(deserializer);

    ByteArrayInputStream bain = new ByteArrayInputStream(noSchemaJSON.getBytes(DEFAULT_CHARSET));

    mmt.read(new InputStreamReader(bain));
  }


  /**
   * Tests read behavior when incoming JSON document is not part of Object Model lib.
   *
   * @throws Exception    if test fails
   */
  @Test (expectedExceptions = DeserializationException.class)

  public void testReadIncorrectLib() throws Exception
  {
    MyModelTransformer mmt = new MyModelTransformer();

    ByteArrayInputStream bain = new ByteArrayInputStream(incorrectLibJSON.getBytes(DEFAULT_CHARSET));

    mmt.read(new InputStreamReader(bain));
  }


  /**
   * Deserialization test with boolean primitives.
   *
   * @throws Exception  if test fails
   */
  @Test public void testReadBoolean() throws Exception
  {
    Deserializer deserializer = new Deserializer()
    {
      @Override public void deserialize(JSONModel model)
      {
        Assert.assertTrue(model.containsSchema(new Version(1, 2, 3)));

        Assert.assertTrue(model.getModelClass().equals("org.openremote.test.MyModel"));

        Assert.assertTrue(model.getModel().hasAttributes());
        Assert.assertTrue(model.getModel().hasAttribute("boolean", "true"));
        Assert.assertTrue(!model.getModel().hasAttribute("boolean", "false"));
        Assert.assertTrue(model.getModel().hasAttribute("boolean", true));
        Assert.assertTrue(!model.getModel().hasAttribute("boolean", false));

        Assert.assertTrue(model.getModel().getBooleanAttribute("boolean").equals(true));

        Assert.assertTrue(model.getModel().hasAttribute("name", "value"));
        Assert.assertTrue(!model.getModel().hasObjects());
      }
    };

    MyModelTransformer mmt = new MyModelTransformer(deserializer);

    ByteArrayInputStream bain = new ByteArrayInputStream(booleanJSON.getBytes(Model.UTF8));

    mmt.read(new InputStreamReader(bain));
  }


  /**
   * Deserialization test with number primitives.
   *
   * @throws Exception  if test fails
   */
  @Test public void testReadNumber() throws Exception
  {
    Deserializer deserializer = new Deserializer()
    {
      @Override public void deserialize(JSONModel model)
      {
        Assert.assertTrue(model.containsSchema(new Version(1, 2, 3)));

        Assert.assertTrue(model.getModelClass().equals("org.openremote.test.MyModel"));

        Assert.assertTrue(model.getModel().hasAttributes());

        Assert.assertTrue(model.getModel().hasAttribute("1st number", "1"));
        Assert.assertTrue(!model.getModel().hasAttribute("1st number", "2"));
        Assert.assertTrue(model.getModel().hasAttribute("1st number", 1));
        Assert.assertTrue(!model.getModel().hasAttribute("1st number", 2));

        Assert.assertTrue(model.getModel().hasAttribute("2nd number", "2.0"));
        Assert.assertTrue(!model.getModel().hasAttribute("2nd number", "-2.0"));
        Assert.assertTrue(model.getModel().hasAttribute("2nd number", 2.0));
        Assert.assertTrue(!model.getModel().hasAttribute("2nd number", 3.0));

        Assert.assertTrue(model.getModel().hasAttribute("3rd number", "340.0"));
        Assert.assertTrue(!model.getModel().hasAttribute("3rd number", "-340.0"));
        Assert.assertTrue(model.getModel().hasAttribute("3rd number", 340.0));
        Assert.assertTrue(!model.getModel().hasAttribute("3rd number", 340.1));

        Assert.assertTrue(model.getModel().hasAttribute("4th number", "0.5"));
        Assert.assertTrue(!model.getModel().hasAttribute("4th number", "0.6"));
        Assert.assertTrue(model.getModel().hasAttribute("4th number", .5));
        Assert.assertTrue(!model.getModel().hasAttribute("4th number", 0.6));

        Assert.assertTrue(model.getModel().hasAttribute("5th number", "-0.6"));
        Assert.assertTrue(!model.getModel().hasAttribute("5th number", "0.6"));
        Assert.assertTrue(model.getModel().hasAttribute("5th number", -0.6));
        Assert.assertTrue(!model.getModel().hasAttribute("5th number", 0.6));

        Assert.assertTrue(model.getModel().hasAttribute("6th number", "-0.78"));
        Assert.assertTrue(!model.getModel().hasAttribute("6th number", "0.78"));
        Assert.assertTrue(model.getModel().hasAttribute("6th number", -7.8E-1));
        Assert.assertTrue(!model.getModel().hasAttribute("6th number", 0.79));

        Assert.assertTrue(!model.getModel().hasObjects());
      }
    };

    MyModelTransformer mmt = new MyModelTransformer(deserializer);

    ByteArrayInputStream bain = new ByteArrayInputStream(numberJSON.getBytes(Model.UTF8));

    mmt.read(new InputStreamReader(bain));
  }


  /**
   * Deserialization test with string array.
   *
   * @throws Exception  if test fails
   */
  @Test public void testReadStringArray() throws Exception
  {
    Deserializer deserializer = new Deserializer()
    {
      @Override public void deserialize(JSONModel json)
      {
        Assert.assertTrue(json.containsSchema(new Version(1, 2, 3)));

        Assert.assertTrue(json.getModelClass().equals("org.openremote.test.MyModel"));

        ModelObject model = json.getModel();

        Assert.assertTrue(model.hasAttributes());

        Assert.assertTrue(model.hasAttribute("name", "[a, b, c]"));

        List<String> testList = new ArrayList<String>();
        testList.add("a");
        testList.add("b");
        testList.add("c");

        Assert.assertTrue(model.hasAttribute("name", testList));

        testList.add("d");

        Assert.assertTrue(!model.hasAttribute("name", testList));

        List<Long> numbers = new ArrayList<Long>();

        Assert.assertTrue(!model.hasAttribute("name", numbers));

        Assert.assertTrue(!model.hasObjects());
      }
    };

    MyModelTransformer mmt = new MyModelTransformer(deserializer);

    ByteArrayInputStream bain = new ByteArrayInputStream(stringArrayJSON.getBytes(Model.UTF8));

    mmt.read(new InputStreamReader(bain));
  }

  /**
   * Deserialization test with string array that contains whitespace. Make sure they're preserved.
   *
   * @throws Exception  if test fails
   */
  @Test public void testReadWhitespaceStringArray() throws Exception
  {
    Deserializer deserializer = new Deserializer()
    {
      @Override public void deserialize(JSONModel json)
      {
        ModelObject model = json.getModel();

        Assert.assertTrue(model.hasAttributes());

        Assert.assertTrue(model.hasAttribute("name", "[test, foobar ]"));

        List<String> testList = new ArrayList<String>();
        testList.add(" test ");
        testList.add(" foo  \nbar ");

        Assert.assertTrue(model.hasAttribute("name", testList));

        Assert.assertTrue(!model.hasObjects());
      }
    };

    MyModelTransformer mmt = new MyModelTransformer(deserializer);

    ByteArrayInputStream bain = new ByteArrayInputStream(whitespaceStringArrayJSON.getBytes(Model.UTF8));

    mmt.read(new InputStreamReader(bain));
  }


  /**
   * Deserialization test with number array.
   *
   * @throws Exception  if test fails
   */
  @Test public void testReadNumberArray() throws Exception
  {
    Deserializer deserializer = new Deserializer()
    {
      @Override public void deserialize(JSONModel json)
      {
        ModelObject model = json.getModel();

        Assert.assertTrue(model.hasAttributes());

        // note the scientific exponent notation gets truncated by
        // Long.parseLong(String) -> Long.toString() conversion so not
        // exact match to JSON document: 9.18E+09 -> 9.18E9

        Assert.assertTrue(model.hasAttribute("name", "[-1, -2.3, 4, 5.0, 0.6, 9.18E9]"));

        List<Number> testList = new ArrayList<Number>();
        testList.add(-1L);    // Note FlexJSON converts all integers to Long types
        testList.add(-2.3);
        testList.add(4L);     // Note FlexJSON converts all integers to Long types
        testList.add(5.0);
        testList.add(.6);
        testList.add(9.18E+09);

        Assert.assertTrue(model.hasAttribute("name", testList));

        testList.add(8E2);

        Assert.assertTrue(!model.hasAttribute("name", testList));

        List<Long> numbers = new ArrayList<Long>();

        Assert.assertTrue(!model.hasAttribute("name", numbers));

        Assert.assertTrue(!model.hasObjects());
      }
    };

    MyModelTransformer mmt = new MyModelTransformer(deserializer);

    ByteArrayInputStream bain = new ByteArrayInputStream(numberArrayJSON.getBytes(Model.UTF8));

    mmt.read(new InputStreamReader(bain));
  }

  /**
   * Deserialization test with boolean array.
   *
   * @throws Exception  if test fails
   */
  @Test public void testReadBooleanArray() throws Exception
  {
    Deserializer deserializer = new Deserializer()
    {
      @Override public void deserialize(JSONModel json)
      {
        ModelObject model = json.getModel();

        Assert.assertTrue(model.hasAttributes());
        Assert.assertTrue(model.hasAttribute("name", " [true, false,true]"));

        List<Boolean> testList = new ArrayList<Boolean>();
        testList.add(true);
        testList.add(false);
        testList.add(true);

        Assert.assertTrue(model.hasAttribute("name", testList));

        testList.add(false);

        Assert.assertTrue(!model.hasAttribute("name", testList));

        List<Long> numbers = new ArrayList<Long>();

        Assert.assertTrue(!model.hasAttribute("name", numbers));

        Assert.assertTrue(!model.hasObjects());
      }
    };

    MyModelTransformer mmt = new MyModelTransformer(deserializer);

    ByteArrayInputStream bain = new ByteArrayInputStream(booleanArrayJSON.getBytes(Model.UTF8));

    mmt.read(new InputStreamReader(bain));
  }




  // Transform Tests ------------------------------------------------------------------------------


  /**
   * Test the error handling in case of conflicting types.
   */
  @Test (expectedExceptions = IncorrectImplementationException.class)

  public void testInvalidType()
  {
    TestTransformer tt = new TestTransformer();

    tt.transform(new Integer(10));
  }



  // Class StringValidator Tests ------------------------------------------------------------------

  /**
   * Test that default validator implementation for JSON strings gets initialized.
   */
  @Test public void testDefaultStringValidatorInitialization()
  {
    Assert.assertTrue(JSONTransformer.getStringValidator() == JSONTransformer.defaultStringValidator);
  }


  /**
   * Test default JSON string validator implementation behavior on null strings.
   *
   * @throws Exception    if test fails
   */
  @Test (expectedExceptions = Model.ValidationException.class)

  public void testDefaultStringValidatorImplementationNullString() throws Exception
  {
    JSONTransformer.getStringValidator().validate(null);
  }


  /**
   * Test string of length one on default JSON string validator implementation. Should pass...
   *
   * @throws Exception    if test fails
   */
  @Test public void testDefaultStringValidatorImplementation() throws Exception
  {
    JSONTransformer.getStringValidator().validate("a");
  }

  /**
   * Test default JSON string validator implementation against upper limit defined in
   * {@link Model#DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT}
   *
   * @throws Exception    if test fails
   */
  @Test public void testDefaultStringValidatorImplementationUpperLimit() throws Exception
  {
    StringBuilder builder = new StringBuilder(256);

    // Test upper limit...

    for (int index = 0; index < Model.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT; ++index)
    {
      builder.append("a");
    }

    JSONTransformer.getStringValidator().validate(builder.toString());


    // Test the boundary one above upper limit...

    builder.append("a");

    try
    {
      JSONTransformer.getStringValidator().validate(builder.toString());

      Assert.fail("should not get here..");
    }

    catch (Model.ValidationException exception)
    {
      // expected...
    }
  }


  /**
   * Test changing the default JSON string validator a custom impl. that restricts string
   * lengths to 8 characters...
   *
   * @throws Exception      if test fails
   */
  @Test (expectedExceptions = Model.ValidationException.class)

  public void testCustomStringValidator() throws Exception
  {
    try
    {
      JSONTransformer.setStringValidator(new JSONTransformer.JSONValidator<String>()
      {
        @Override public void validate(String attribute) throws Model.ValidationException
        {
          if (attribute == null || attribute.length() > 8)
          {
            throw new Model.ValidationException("error");
          }
        }
      });

      JSONTransformer.getStringValidator().validate(("123456789"));
    }

    finally
    {
      JSONTransformer.setStringValidator(JSONTransformer.defaultStringValidator);
    }
  }


  // Nested Classes -------------------------------------------------------------------------------

  private static class TestTransformer extends JSONTransformer<String>
  {
    private TestTransformer()
    {
      super(String.class);
    }

    @Override public void write(String s)
    {

    }

    @Override public String deserialize(JSONModel model)
    {
      throw new IncorrectImplementationException("Not implemented.");
    }
  }


  private static class MyModelTransformer extends JSONTransformer<MyModel>
  {
    private Deserializer deserializer;

    private MyModelTransformer()
    {
      super(MyModel.class);

      this.deserializer = new Deserializer() {

        @Override public void deserialize(JSONModel model)
        {

        }
      };
    }

    private MyModelTransformer(Deserializer method)
    {
      super(MyModel.class);

      this.deserializer = method;
    }

    @Override public void write(MyModel mm)
    {
      throw new IncorrectImplementationException("Not implemented.");
    }

    @Override public MyModel deserialize(JSONModel model)
    {
      deserializer.deserialize(model);

      return new MyModel();
    }
  }

  private static interface Deserializer
  {
    void deserialize(JSONModel model);
  }

  private static class MyModel
  {

  }
}

