/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2015, OpenRemote Inc.
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

import org.openremote.base.Version;
import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.model.data.json.JSONHeader;
import org.openremote.model.data.json.UserTransformer;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * TODO
 *
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class User extends Model
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * The JSON schema version implemented by this class. The version value should only
   * be changed when incompatible JSON format changes are introduced to this class.
   */
  public static final Version JSON_SCHEMA_VERSION = new Version(2, 0, 0);

  /**
   * Constraint for the value string size in user attributes map. This is currently
   * derived from the constraints in the persistence model defined in
   * {@link org.openremote.model.persistence.jpa.PersistentUser}
   */
  public static final int USER_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT = 1000;


  /**
   * This implementation delegates to
   * {@link org.openremote.model.data.json.UserTransformer#getEmailValidator()}
   */
  public static final Validator<String> DEFAULT_EMAIL_VALIDATOR = new DefaultEmailValidator();

  /**
   * This implementation delegates to
   * {@link org.openremote.model.data.json.UserTransformer#getNameValidator()}
   */
  public static final Validator<String> DEFAULT_NAME_VALIDATOR = new NameValidator();

  public static final String CREDENTIALS_ATTRIBUTE_NAME = "credentials";

  public static final String AUTHMODE_ATTRIBUTE_NAME = "authMode";

  // Class Members --------------------------------------------------------------------------------


  /**
   * Current name validator in use.
   */
  private static Validator<String> userNameValidator = DEFAULT_NAME_VALIDATOR;

  /**
   * Current email validator in use.
   */
  private static Validator<String> emailValidator = DEFAULT_EMAIL_VALIDATOR;


  /**
   * Sets a custom name validator for User instances. This method should only be called once
   * at application initialization time. It will affect all User instances. <p>
   *
   * A default implementation delegates to {@link UserTransformer#defaultUserNameValidator}.
   *
   * @see Model.Validator
   *
   * @param usernameValidator
   *          new user name validator
   */
  public static void setNameValidator(Validator<String> usernameValidator)
  {
    if (usernameValidator != null)
    {
      User.userNameValidator = usernameValidator;
    }
  }

  /**
   * Returns the current user name validator instance.
   *
   * @return  user name validator for {@link org.openremote.model.User} instances.
   */
  public static Validator<String> getNameValidator()
  {
    return userNameValidator;
  }


  /**
   * Sets a custom email validator for User instances. This method should only be called once
   * at application initialization time. It will affect all User instances. <p>
   *
   * A default implementation delegates to {@link UserTransformer#defaultEmailValidator}
   *
   * @see Model.Validator
   *
   * @param emailValidator
   *          new user email validator
   */
  public static void setEmailValidator(Validator<String> emailValidator)
  {
    if (emailValidator != null)
    {
      User.emailValidator = emailValidator;
    }
  }

  /**
   * Returns the current email validator instance.
   *
   * @return email validator for {@link org.openremote.model.User} instances.
   */
  public static Validator<String> getEmailValidator()
  {
    return emailValidator;
  }


  // Instance Fields ------------------------------------------------------------------------------

  protected String username = "<undefined>";

  protected String email = "";

  protected Set<Account> accounts = new CopyOnWriteArraySet<Account>();

  /**
   * A key,value map for storing an arbitrary number of data entries describing a user.
   */
  protected Map<String, String> userAttributes = new ConcurrentHashMap<String, String>(0);

  private transient boolean isNullEmail = false;


  // Constructors ---------------------------------------------------------------------------------

  protected User()
  {
    super(new UserTransformer());
    this.username = "<undefined>";
    this.email = "";
    this.accounts = new CopyOnWriteArraySet<Account>();
    this.userAttributes = new ConcurrentHashMap<String, String>(0);
    this.isNullEmail = false;
  }


  /**
   * Copy constructor.
   *
   * @param copy
   *          the user instance to copy
   */
  protected User(User copy)
  {
    super((copy == null) ? null : copy.jsonTransformer);

    if (copy == null)
    {
      throw new IncorrectImplementationException("null user copy");
    }

    this.username = copy.username;
    this.email = copy.email;

    if (copy.userAttributes != null)
    {
      // shallow copy ok, strings are immutable

      this.userAttributes = new ConcurrentHashMap<String, String>(copy.userAttributes);
    }

    // Validate... if we allow subclasses to copy, it's possible they haven't been validated.
    // Alternatively disallow subclass copies.

    try
    {
      validate(username, email, copy.isNullEmail);
    }

    catch (ValidationException exception)
    {
      throw new IncorrectImplementationException(
          "Incompatible User instances for copy : {0}", exception, exception.getMessage()
      );
    }

    // TODO : need deep copy of linked accounts, if bidirectional references are maintained
  }


  /**
   * Constructs a user with a given username and email strings. Both username and email must
   * match the default or configured validation rules. Email may be optional, in which case
   * a null reference for email address should be used (note that a null email reference will
   * be converted to an empty string by this implementation).
   *
   * @param username
   *          A unique username matching {@link #DEFAULT_NAME_VALIDATOR default validation rules}
   *          or {@link #setNameValidator(org.openremote.model.Model.Validator) configured name
   *          validation rules}.
   *
   * @param email
   *          User email address as string or <tt>null</tt> if no email is required. If an email
   *          address is provided it must match the {@link #DEFAULT_EMAIL_VALIDATOR default
   *          email validation rules} or
   *          {@link #setEmailValidator(org.openremote.model.Model.Validator) configured email
   *          validation rules}. Note that a <tt>null</tt> reference for email address will be
   *          converted to an empty string by this implementation.
   *
   * @throws  ValidationException
   *            if username or email values cannot be validated
   */
  public User(String username, String email) throws ValidationException
  {
    this();

    validate(username, email, isNullEmail);
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Adds a user attribute to this user. If a key value is <tt>null</tt> or empty string then this
   * call will return without changes. Null values are converted to empty strings. All key and
   * value strings are trimmed of white-space characters before adding the attribute. <p>
   *
   * The constraints limit the key length to at most 255 characters and value strings at
   * most 1000 characters in length.
   *
   * @param name
   *          user attribute name
   *
   * @param value
   *          user attribute value
   *
   * @return
   *          reference to this updated user instance to enable method chaining
   *
   * @throws  ConstraintException
   *            If the name or value strings are not within defined constraints:
   *            {@link #DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT} and
   *            {@link #USER_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT}
   */
  public User addAttribute(String name, String value)
  {
    if (name == null || name.equals(""))
    {
      return this;
    }

    if (value == null)
    {
      value = "";
    }

    name = name.trim();
    value = value.trim();

    if (name.length() > DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT)
    {
      throw new ConstraintException(
          "User attribute name string can be at most {0} characters, name string ''{1}'' " +
          "is {2} characters long.",
          DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT, name, name.length()
      );
    }

    if (value.length() > USER_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT)
    {
      throw new ConstraintException(
          "User attribute value string can be at most {0} characters, value string ''{1}'' " +
          "is {2} characters long",
          USER_ATTRIBUTE_VALUE_LENGTH_CONSTRAINT, value, value.length()
      );
    }

    userAttributes.put(name, value);

    return this;
  }

  public String getAttribute(String attributeName)
  {
    return (String) this.userAttributes.get(attributeName);
  }

  public boolean hasAttribute(String attributeName)
  {
    return this.userAttributes.containsKey(attributeName);
  }

  public String getName()
  {
    return this.username;
  }

  public Set<Account> getAccounts()
  {
    return accounts;
  }

  // Serialization --------------------------------------------------------------------------------

  /**
   * Serializes this object to a JSON format.  <p>
   *
   * See the project's resources/json directory for an Orderly definition of the data exchange
   * format and the corresponding JSON schema.
   *
   * @return a JSON structure for transferring this user's information
   */
  public String toJSONString()
  {
    return JSONHeader.toJSON(this, JSON_SCHEMA_VERSION, jsonTransformer);
  }


  // Object Overrides -----------------------------------------------------------------------------

  @Override public String toString()
  {
    return username + ", " + email;
  }


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Validates the username and email string values for this instance.
   *
   * @param usernameCandidate
   *          username for this class' name validator
   *
   * @param emailCandidate
   *          email for this class' email validator
   *
   * @throws ValidationException
   *          if values cannot be validated
   */
  private void validate(String usernameCandidate, String emailCandidate, boolean isNull)
      throws ValidationException
  {
    this.username = (usernameCandidate == null) ? null : usernameCandidate.trim();

    if (username == null || username.equals(""))
    {
      throw new ValidationException("Null or empty usernames are never accepted.");
    }

    if (username.length() > Model.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT)
    {
      throw new ValidationException(
          "Username cannot exceed {0} characters.", Model.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT
      );
    }

    userNameValidator.validate(username);


    // Note: null emails are always converted to empty strings -- this avoids having to distinguish
    // between null and empty string when serializing to JSON, etc.

    email = (emailCandidate == null) ? null : emailCandidate.trim();

    emailValidator.validate((isNull) ? null : email);

    if (email == null)
    {
      this.email = "";

      isNullEmail = true;
    }

    if (email.length() > Model.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT)
    {
      throw new ValidationException(
          "Email cannot exceed {0} characters.", Model.DEFAULT_STRING_ATTRIBUTE_LENGTH_CONSTRAINT
      );
    }
  }



  // Nested Classes -------------------------------------------------------------------------------

  /**
   * This implementation accepts nulls as valid emails (making email field optional). For
   * the rest, delegates to
   * {@link org.openremote.model.data.json.UserTransformer#getEmailValidator()}
   */
  private static class DefaultEmailValidator implements Validator<String>
  {
    @Override public void validate(String email) throws ValidationException
    {
      if (email == null)
      {
        return;
      }

      UserTransformer.getEmailValidator().validate(email);
    }

    @Override public String toString()
    {
      return "Default Email Validator: accepts null or format [email]@[host].[domain]";
    }
  }


  /**
   * This implementation delegates everything to
   * {@link org.openremote.model.data.json.UserTransformer#getNameValidator()}
   */
  private static class NameValidator implements Validator<String>
  {
    @Override public void validate(String username) throws ValidationException
    {
      UserTransformer.getNameValidator().validate(username);
    }

    @Override public String toString()
    {
      return "Default Name Validator: must be non-null, non-empty string";
    }
  }

  public static class Authentication
  {
    private transient byte[] credentials;
    protected User.CredentialsEncoding encoding;
    protected String salt = "";

    protected Authentication()
    {
    }

    protected Authentication(User.Authentication copy)
    {
      this.credentials = copy.credentials;
      this.encoding = User.CredentialsEncoding.valueOf(copy.encoding.name());
      this.salt = copy.salt;
    }

    public Authentication(byte[] credentials, User.CredentialsEncoding encoding)
    {
      this.credentials = credentials;
      this.encoding = encoding;
      this.salt = UUID.randomUUID().toString();
    }

    public void clear()
    {
      byte[] arr$ = this.credentials;
      int len$ = arr$.length;

      for (int i$ = 0; i$ < len$; ++i$)
      {
        byte var10000 = arr$[i$];
        boolean b = false;
      }

    }

    protected byte[] getCredentials()
    {
      return this.credentials;
    }
  }

  public static enum CredentialsEncoding
  {
    SCRYPT,
    LEGACY_BEEHIVE,
    UNSPECIFIED;

    public static final User.CredentialsEncoding DEFAULT;

    private CredentialsEncoding()
    {
    }

    public String toString()
    {
      return this.getEncodingName();
    }

    public String getEncodingName()
    {
      return this.name().toLowerCase();
    }

    static
    {
      DEFAULT = SCRYPT;
    }
  }

}

