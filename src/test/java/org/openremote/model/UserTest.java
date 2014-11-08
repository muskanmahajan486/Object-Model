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

import org.openremote.model.data.json.JSONHeader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit tests for {@link org.openremote.model.User} class.
 *
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class UserTest
{

  // Test Lifecycle -------------------------------------------------------------------------------

  private String apiVersion = "";

  @BeforeClass public void getConfiguredAPIVersion()
  {
    apiVersion = System.getProperty("openremote.project.api.version");

    Assert.assertTrue(apiVersion != null && !apiVersion.equals(""));
  }


  // Base constructor tests -----------------------------------------------------------------------

  /**
   * Base constructor test.
   */
  @Test public void testBasicConstructor() throws Exception
  {
    User user = new User("username", "email@myhost.domain");

    Assert.assertTrue(user.username.equals("username"));
    Assert.assertTrue(user.email.equals("email@myhost.domain"));
    Assert.assertTrue(user.userAttributes != null);
    Assert.assertTrue(user.userAttributes.size() == 0);
  }


  // Base constructor user name validation tests --------------------------------------------------

  /**
   * Null username test on base constructor using default validator.
   */
  @Test(expectedExceptions = User.ValidationException.class)

  public void testBasicConstructorNullUserNameArgDefaultValidator() throws Exception
  {
    new User(null, "email");
  }


  /**
   * Empty username string test on base constructor using default validator.
   */
  @Test(expectedExceptions = User.ValidationException.class)

  public void testBasicConstructorEmptyUserNameDefaultValidator() throws Exception
  {
    new User("", "email");
  }


  /**
   * Empty username string test on base constructor using default validator.
   */
  @Test(expectedExceptions = User.ValidationException.class)

  public void testBasicConstructorEmptyUserNameDefaultValidator2() throws Exception
  {
    new User("  ", "email");
  }

  /**
   * Empty username string test on base constructor using default validator.
   */
  @Test(expectedExceptions = User.ValidationException.class)

  public void testBasicConstructorEmptyUserNameDefaultValidator3() throws Exception
  {
    new User("\r", "email");
  }

  /**
   * Empty username string test on base constructor using default validator.
   */
  @Test(expectedExceptions = User.ValidationException.class)

  public void testBasicConstructorEmptyUserNameDefaultValidator4() throws Exception
  {
    new User("\n", "email");
  }

  /**
   * Empty username string test on base constructor using default validator.
   */
  @Test(expectedExceptions = User.ValidationException.class)

  public void testBasicConstructorEmptyUserNameDefaultValidator5() throws Exception
  {
    new User("\t", "email");
  }


  // Base constructor user name validation tests with custom validator ----------------------------

  /**
   * Empty username string test on base constructor using custom validator.
   */
  @Test(expectedExceptions = User.ValidationException.class)

  public void testBasicConstructorEmptyUserNameCustomValidator() throws Exception
  {
    User.setNameValidator(new Model.Validator<String>()
    {
      @Override public void validate(String attribute) throws Model.ValidationException
      {
        // accept everything
      }
    });

    try
    {
      new User("  ", "email");
    }

    finally
    {
      User.setNameValidator(User.defaultNameValidator);
    }
  }


  /**
   * Null username test on base constructor using custom validator.
   */
  @Test(expectedExceptions = User.ValidationException.class)

  public void testBasicConstructorNullUserNameCustomValidator() throws Exception
  {
    User.setNameValidator(new Model.Validator<String>()
    {
      @Override public void validate(String attribute) throws Model.ValidationException
      {
        // accept everything
      }
    });

    try
    {
      new User(null, "email");
    }

    finally
    {
      User.setNameValidator(User.defaultNameValidator);
    }
  }


  /**
   * Username test on base constructor with custom validator.
   */
  @Test public void testBasicConstructorCustomNameValidator() throws Exception
  {
    User.setNameValidator(new Model.Validator<String>()
    {
      @Override public void validate(String attribute) throws Model.ValidationException
      {
        if (attribute.startsWith("foo"))
        {
          return;
        }

        throw new Model.ValidationException("name did not start with foo");
      }
    });

    try
    {
      new User("foobar", "email@at.my.place");
    }

    finally
    {
      User.setNameValidator(User.defaultNameValidator);
    }
  }


  /**
   * Invalid username test on base constructor with custom validator.
   */
  @Test(expectedExceptions = User.ValidationException.class)

  public void testBasicConstructorCustomNameValidatorInvalidName() throws Exception
  {
    User.setNameValidator(new Model.Validator<String>()
    {
      @Override public void validate(String attribute) throws Model.ValidationException
      {
        if (attribute.startsWith("foo"))
        {
          return;
        }

        throw new Model.ValidationException("name did not start with foo");
      }
    });

    try
    {
      new User("bar", "email");
    }

    finally
    {
      User.setNameValidator(User.defaultNameValidator);
    }
  }





}

