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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;

import org.openremote.model.testengine.OpenRemoteTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.openremote.model.Model;
import org.openremote.model.User;


/**
 * Unit tests for {@link org.openremote.model.data.json.UserTransformer} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class UserTransformerTest extends OpenRemoteTest
{

  // Test Lifecycle -------------------------------------------------------------------------------

  private byte[] userJSON;
  private byte[] userNoEmailJSON;
  private byte[] userCharsetJSON;
  private byte[] userEscapeJSON;
  private byte[] userAttributesJSON;
  private byte[] userEmptyAttributesJSON;


  @BeforeClass public void loadJSONDocuments()
  {
    try
    {
      userJSON = loadResourceTextFile(new URI("user/user.json")).getBytes(Model.UTF8);
      userNoEmailJSON = loadResourceTextFile(new URI("user/user-no-email.json")).getBytes(Model.UTF8);
      userCharsetJSON = loadResourceTextFile(new URI("user/user-charset.json")).getBytes(Model.UTF8);
      userEscapeJSON = loadResourceTextFile(new URI("user/user-escaping.json")).getBytes(Model.UTF8);
      userAttributesJSON = loadResourceTextFile(new URI("user/user-attributes.json")).getBytes(Model.UTF8);
      userEmptyAttributesJSON = loadResourceTextFile(new URI("user/user-empty-attributes.json")).getBytes(Model.UTF8);
    }

    catch (Exception e)
    {
      System.err.println(" !!! TEST SETUP FAILURE !!! ");
      e.printStackTrace();
    }
  }


  // Tests ----------------------------------------------------------------------------------------

  /**
   * Basic test for producing User JSON serialization document and then reading it back to
   * another User instance.
   *
   * @throws Exception    if test fails
   */
  @Test public void testRead() throws Exception
  {
    User u = new User("username", "email@email.com");
    ByteArrayInputStream in = new ByteArrayInputStream(u.toJSONString().getBytes(Model.UTF8));

    UserTransformer t = new UserTransformer();
    TestUser tu = new TestUser(t.read(new InputStreamReader(in)));


    Assert.assertTrue(
        tu.username.equals("username"),
        "Expecting 'username', got '" + tu.username + "'"
    );

    Assert.assertTrue(
        tu.email.equals("email@email.com"),
        "Expecting 'email@email.com', got '" + tu.email + "'"
    );
  }

  /**
   * Test for producing User JSON serialization document and then reading it back to
   * another User instance when null email is used.
   *
   * @throws Exception    if test fails
   */
  @Test public void testReadNoEmail() throws Exception
  {
    Model.Validator<String> old = User.getEmailValidator();

    try
    {
      User.setEmailValidator(new Model.Validator<String>()
      {
        @Override public void validate(String attribute) throws Model.ValidationException
        {
          // anything goes...
        }
      });

      User u = new User("username", null);
      ByteArrayInputStream in = new ByteArrayInputStream(u.toJSONString().getBytes(Model.UTF8));

      UserTransformer t = new UserTransformer();
      TestUser tu = new TestUser(t.read(new InputStreamReader(in)));


      Assert.assertTrue(
          tu.username.equals("username"),
          "Expecting 'username', got '" + tu.username + "'"
      );

      Assert.assertTrue(tu.email.equals(""));
    }

    finally
    {
      User.setEmailValidator(old);
    }
  }


  /**
   * Basic test for producing User JSON serialization document with nested user attributes
   * and then reading it back to another User instance.
   *
   * @throws Exception    if test fails
   */
  @Test public void testReadWithAttributes() throws Exception
  {
    User u = new User("username", "email@email.com");
    u.addAttribute("name", "value");

    ByteArrayInputStream in = new ByteArrayInputStream(u.toJSONString().getBytes(Model.UTF8));

    UserTransformer t = new UserTransformer();
    TestUser tu = new TestUser(t.read(new InputStreamReader(in)));


    Assert.assertTrue(
        tu.username.equals("username"),
        "Expecting 'username', got '" + tu.username + "'"
    );

    Assert.assertTrue(
        tu.email.equals("email@email.com"),
        "Expecting 'email@email.com', got '" + tu.email + "'"
    );

    Assert.assertTrue(!tu.attributes.isEmpty());
    Assert.assertTrue(tu.attributes.keySet().contains("name"));
    Assert.assertTrue(tu.attributes.get("name").equals("value"));
  }

  /**
   * Basic test for producing User JSON serialization document with nested user attributes
   * and then reading it back to another User instance.
   *
   * @throws Exception    if test fails
   */
  @Test public void testReadWithAttributes2() throws Exception
  {
    User u = new User("username", "email@email.com");
    u.addAttribute("name", "value");
    u.addAttribute("address", "foo");
    u.addAttribute("phone1", "123");
    u.addAttribute("phone2", "456");


    ByteArrayInputStream in = new ByteArrayInputStream(u.toJSONString().getBytes(Model.UTF8));

    UserTransformer t = new UserTransformer();
    TestUser tu = new TestUser(t.read(new InputStreamReader(in)));


    Assert.assertTrue(
        tu.username.equals("username"),
        "Expecting 'username', got '" + tu.username + "'"
    );

    Assert.assertTrue(
        tu.email.equals("email@email.com"),
        "Expecting 'email@email.com', got '" + tu.email + "'"
    );


    Assert.assertTrue(!tu.attributes.isEmpty());
    Assert.assertTrue(tu.attributes.size() == 4);

    Assert.assertTrue(tu.attributes.keySet().contains("name"));
    Assert.assertTrue(tu.attributes.get("name").equals("value"));

    Assert.assertTrue(tu.attributes.keySet().contains("address"));
    Assert.assertTrue(tu.attributes.get("address").equals("foo"));

    Assert.assertTrue(tu.attributes.keySet().contains("phone1"));
    Assert.assertTrue(tu.attributes.get("phone1").equals("123"));

    Assert.assertTrue(tu.attributes.keySet().contains("phone2"));
    Assert.assertTrue(tu.attributes.get("phone2").equals("456"));
  }


  /**
   * Test loading one of the existing user JSON documents into deserializer.
   *
   * @throws Exception    if test fails
   */
  @Test public void testReadUser() throws Exception
  {
    ByteArrayInputStream in = new ByteArrayInputStream(userJSON);

    UserTransformer t = new UserTransformer();
    TestUser tu = new TestUser(t.read(new InputStreamReader(in)));


    Assert.assertTrue(tu.username.equals("username"));
    Assert.assertTrue(tu.email.equals("email@somewhere.com"));
  }

  /**
   * Loads a user JSON document with no email into deserializer.
   *
   * @throws Exception    if test fails
   */
  @Test public void testReadUserNoEmail() throws Exception
  {
    ByteArrayInputStream in = new ByteArrayInputStream(userNoEmailJSON);

    UserTransformer t = new UserTransformer();
    TestUser tu = new TestUser(t.read(new InputStreamReader(in)));


    Assert.assertTrue(tu.username.equals("username"));
    Assert.assertTrue(tu.email.equals(""));
  }


  /**
   * Loads a user JSON document with non-latin characters (testing charset conversions)
   * into deserializer.
   *
   * @throws Exception    if test fails
   */
  @Test public void testReadUserCharset() throws Exception
  {
    ByteArrayInputStream in = new ByteArrayInputStream(userCharsetJSON);

    UserTransformer t = new UserTransformer();
    TestUser tu = new TestUser(t.read(new InputStreamReader(in, Model.UTF8)));

    Assert.assertTrue(tu.username.equals("了IıİißÇçöâåäøö"), "got '" + tu.username + "'");
    Assert.assertTrue(tu.email.equals("email@host.domain"));
  }


  /**
   * Loads a user JSON document with escaped characters into deserializer.
   *
   * @throws Exception    if test fails
   */
  @Test public void testReadUserCharEscaping() throws Exception
  {
    ByteArrayInputStream in = new ByteArrayInputStream(userEscapeJSON);

    UserTransformer t = new UserTransformer();
    TestUser tu = new TestUser(t.read(new InputStreamReader(in, Model.UTF8)));

    Assert.assertTrue(tu.username.equals("a\"a"), "got '" + tu.username + "'");
    Assert.assertTrue(tu.email.equals("<a&b>@somewhere.com"));
  }

  /**
   * Loads a user with attributes JSON document into deserializer.
   *
   * @throws Exception    if test fails
   */
  @Test public void testReadUserAttributes() throws Exception
  {
    ByteArrayInputStream in = new ByteArrayInputStream(userAttributesJSON);

    UserTransformer t = new UserTransformer();
    TestUser tu = new TestUser(t.read(new InputStreamReader(in, Model.UTF8)));

    Assert.assertTrue(tu.username.equals("foo"));
    Assert.assertTrue(tu.email.equals("email@host.domain"));
    Assert.assertTrue(!tu.attributes.isEmpty());
    Assert.assertTrue(tu.getAttribute("credentials").equals("bar"));
  }


  /**
   * Loads a user with empty attributes object into deserializer.
   *
   * @throws Exception    if test fails
   */
  @Test public void testReadEmptyAttributes() throws Exception
  {
    ByteArrayInputStream in = new ByteArrayInputStream(userEmptyAttributesJSON);

    UserTransformer t = new UserTransformer();
    TestUser tu = new TestUser(t.read(new InputStreamReader(in, Model.UTF8)));

    Assert.assertTrue(tu.username.equals("foo"));
    Assert.assertTrue(tu.attributes.isEmpty());
  }


  // Nested Classes -------------------------------------------------------------------------------

  private static class TestUser extends User
  {
    String username = super.username;
    String email = super.email;
    Map<String, String> attributes = super.userAttributes;

    TestUser(User u)
    {
      super(u);

      new ExtendedTransformer();
    }
  }

  private static class ExtendedUser extends User
  {

  }

  private static class ExtendedTransformer extends UserTransformer
  {

    @Override public ExtendedUser deserialize(JSONModel model)
    {
      return new ExtendedUser();
    }
  }
}

