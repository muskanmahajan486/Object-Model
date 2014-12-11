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
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.openremote.model.Model;
import org.openremote.model.User;


/**
 * Unit tests for {@link org.openremote.model.data.json.UserTransformer} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class UserTransformerTest
{

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


    Assert.assertTrue(tu != null);

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

    @Override public ExtendedUser deserialize(ModelPrototype prototype)
    {
      return new ExtendedUser();
    }
  }
}

