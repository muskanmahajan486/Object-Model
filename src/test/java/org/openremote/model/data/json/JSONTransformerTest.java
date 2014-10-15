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

import org.openremote.base.Version;
import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.model.Model;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Unit tests for {@link JSONTransformer} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class JSONTransformerTest
{

  /**
   * Test the error handling in case of conflicting types.
   */
  @Test public void testInvalidType()
  {
    TestTransformer tt = new TestTransformer();

    try
    {
      tt.transform(new Integer(10));

      Assert.fail("should not get here...");
    }

    catch (IncorrectImplementationException e)
    {
      // expected...
    }
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

    @Override public String deserialize(Version schemaVersion, String className,
                                        Map<String, String> jsonProperties)
    {
      throw new IncorrectImplementationException("Not implemented.");
    }
  }
}

