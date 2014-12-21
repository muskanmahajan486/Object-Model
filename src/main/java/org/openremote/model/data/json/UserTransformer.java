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

import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openremote.model.Model;
import org.openremote.model.User;


/**
 * Implementation of the Transformer interface for the FlexJSON library that handles
 * serialization and deserialization of the {@link org.openremote.model.User} instances.
 *
 * @author Juha Lindfors
 */
public class UserTransformer extends JSONTransformer<User>
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Returns current validator used for validating user names in {@link org.openremote.model.User}
   * instances.
   *
   * @return
   *          current user name validator instance
   */
  public static JSONValidator<String> getNameValidator()
  {
    return defaultUserNameValidator;
  }

  /**
   * Returns current validator used for validating user email addressed in
   * {@link org.openremote.model.User} instances.
   *
   * @return
   *          current email validator instance
   */
  public static JSONValidator<String> getEmailValidator()
  {
    return defaultEmailValidator;
  }


  /**
   * Reference to the default username validator used by this instance.
   */
  protected static JSONValidator<String> defaultUserNameValidator = new JSONValidator<String>()
  {
    @Override public void validate(String username) throws Model.ValidationException
    {
      defaultValidator.validateUserName(username);
    }
  };

  /**
   * Reference to the default email validator used by this instance.
   */
  protected static JSONValidator<String> defaultEmailValidator = new JSONValidator<String>()
  {
    @Override public void validate(String email) throws Model.ValidationException
    {
      defaultValidator.validateEmail(email);
    }
  };

  /**
   * Delegate default validations to this nested implementation.
   */
  private static UserTransformer.AttributeValidator defaultValidator =
        new UserTransformer.AttributeValidator();


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a FlexJSON transformer for {@link org.openremote.model.User} instances.
   */
  public UserTransformer()
  {
    super(User.class);
  }


  // JSONTransformer Overrides --------------------------------------------------------------------

  /**
   * Translates a given {@link org.openremote.model.User} instance to a JSON data
   * format. The expected JSON properties and object structure is described in the JSON schema
   * documents in project's /resources/json directory.
   *
   * @param user
   *            user instance to convert to JSON format
   */
  @Override public void write(User user)
  {
    UserData data = new UserData(user);

    startObject();

    writeProperty("username", data.userName);
    writeProperty("email", data.email);

    // Extension point for subclasses...

    writeExtendedProperties(user);

    if (!data.attributes.isEmpty())
    {
      writeProperty("userAttributes", data.attributes);
    }

    endObject();
  }



  /**
   * Recreates an {@link org.openremote.model.User} instance from a deserialized JSON model
   * prototype.
   *
   * @see   JSONModel
   *
   * @param json
   *          A JSON frame that represents the structures parsed from the JSON document
   *          representing an user instance.
   *
   * @return  A corresponding Java User instance
   *
   * @throws  DeserializationException if deserialization fails
   */
  @Override protected User deserialize(JSONModel json) throws DeserializationException
  {
    try
    {
      ModelObject model = json.getModel();

      // User model will deal with validating the incoming data...

      User user = new User(model.getAttribute("username"), model.getAttribute("email"));

      ModelObject attributes = model.getObject("userAttributes");

      if (attributes != null)
      {
        Enumeration<ModelObject.Attribute> enumeration = attributes.getAttributes();

        while (enumeration.hasMoreElements())
        {
          ModelObject.Attribute attr = enumeration.nextElement();

          user.addAttribute(attr.getName(), attr.getValue());
        }
      }

      return user;
    }

    catch (Model.ValidationException exception)
    {
      throw new DeserializationException(
          "Cannot create new User instance, received values are not valid : {0}",
          exception, exception.getMessage()
      );
    }
  }



  // Protected Methods ----------------------------------------------------------------------------

  protected void writeExtendedProperties(User user)
  {

  }


  // Nested Classes -------------------------------------------------------------------------------

  public static class AttributeValidator
  {

    // Constants ----------------------------------------------------------------------------------

    public static final int MINIMUM_USERNAME_LENGTH = 1;

    public static final String DEFAULT_EMAIL_PATTERN = ".+@.+\\..{2,}+";

    private static final Pattern EMAIL_REGEXP = Pattern.compile(DEFAULT_EMAIL_PATTERN);



    public void validateUserName(String username) throws Model.ValidationException
    {
      username = (username == null) ? username : username.trim();

      // checks for null, length limit defined in DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT
      // which currently matches the value defined in resources/json/User.orderly schema
      // and the relational persistence model limit...

      getStringValidator().validate(username);

      // Username specific checks, as per the resources/json/User.orderly schema...

      if (username.length() < MINIMUM_USERNAME_LENGTH)
      {
        throw new Model.ValidationException(
            "Username value is too short, must be at least {0} characters. " +
            "Given username argument '{1}' was only {2} characters in length.",
            MINIMUM_USERNAME_LENGTH, username, username.length()
        );
      }
    }

    public void validateEmail(String email) throws Model.ValidationException
    {
      email = (email == null) ? email : email.trim();

      getStringValidator().validate(email);

      Matcher m = EMAIL_REGEXP.matcher(email);

      if (!m.matches())
      {
        throw new Model.ValidationException(
            "Email address ''{0}'' does not match expected pattern 'email@host.domain'.", email
        );
      }
    }
  }



  /**
   * This private class makes the data fields in {@link User} class visible to
   * this implementation. <p>
   *
   * Note that this class does not make a copy of the mutable device attributes collection.
   * It is up to the enclosing class not to mess things up.
   */
  private static class UserData extends User
  {
    private String userName = super.username;
    private String email = super.email;

    private Map<String, String> attributes = super.userAttributes;

    /**
     * Copy constructor.
     *
     * @param user
     *          user data to copy
     */
    private UserData(User user)
    {
      super(user);
    }
  }

}

