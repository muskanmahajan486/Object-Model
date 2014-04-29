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
package org.openremote.base;

import org.openremote.base.exception.OpenRemoteException;
import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.base.exception.OpenRemoteRuntimeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class representing an OpenRemote component or library version in a type-safe manner.
 * This class can be used to ensure all version information is stored in a common, specified
 * format.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Version
{

  // TODO : Should be moved to a base package once one is created   [JPL]


  // Constants ------------------------------------------------------------------------------------

  /**
   * A special version instance that is used when a version information cannot be resolved.
   * Will resolve to version string '0.0.0_UNKNOWN'.
   */
  public static final Unknown UNKNOWN = new Unknown();

  /**
   * Regular expression that specifies the exact format of allowed version strings: {@value}
   * <p>
   * Generic format for a version string is 'major.minor.bugfix_build' where major and minor
   * version numbers must be present and within range of [0..999], bugfix version number must
   * be present and within range [0..9999] and an optional arbitrary build identifier string
   * can be included and must be separated with a '_' character from the bugfix version number.
   *
   */
  public static final String VERSION_REGEX = "^([0-9]){1,3}.([0-9]){1,3}.([0-9]){1,4}(_.*)?$";


  // Class Members --------------------------------------------------------------------------------

  /**
   * A regular expression pattern that specifies the precise format of allowed version strings.
   */
  protected static final Pattern versionPattern = Pattern.compile(VERSION_REGEX);


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * A numeric identifier of the major version. Valid in range of [0...999]. A mandatory field
   * in version information.
   */
  protected int majorVersion;

  /**
   * A numeric identifier of the minor version. Valid in range of [0...999]. A mandatory field
   * in version information. Will be separated by dot '.' character from the major version number.
   */
  protected int minorVersion;

  /**
   * A numeric identifier of a bug fix version. Valid in range of [0...9999] (note larger range
   * than other identifiers). A mandatory field in version information. Will be separated by
   * dot '.' character from the minor version number.
   */
  protected int bugfixVersion;

  /**
   * An arbitrary build identifier string attached to any build version, separated by an
   * underscore '_' character from the bugfix version number. This is an optional field in
   * version information.
   */
  protected String buildIdentifier = "";


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Private constructor that is used internally by nested classes.
   */
  private Version()
  {

  }

  /**
   * Copy constructor for subclasses.
   *
   * @param version
   *          the version instance whose fields are copied to this copy instance
   *
   * @throws IllegalArgumentException
   *          if the version values are out of range
   */
  protected Version(Version version)
  {
    this.majorVersion = version.majorVersion;
    this.minorVersion = version.minorVersion;
    this.bugfixVersion = version.bugfixVersion;
    this.buildIdentifier = version.buildIdentifier;

    try
    {
      invariant();
    }

    catch (InvariantException e)
    {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }


  /**
   * Constructs a new version number information. The version number must always contain three
   * mandatory elements, the major, minor and bugfix version numbers. For an optional build
   * identifier included in version information, see {@link #Version(int, int, int, String)}.  <p>
   *
   * The string representation of a version will separate major, minor and bugfix numbers by
   * a dot '.' character therefore giving a format 'major.minor.bugfix'.
   *
   * @param major
   *          Major version number. Must be a value in range [0...999].
   *
   * @param minor
   *          Minor version number. Must be a value in range [0...999].
   *
   * @param bugfix
   *          Bugfix version number. Must be a value in range [0...9999].
   *
   *
   * @throws IllegalArgumentException
   *          if any of the version number values are out of range
   */
  public Version(int major, int minor, int bugfix)
  {
    this.majorVersion = major;
    this.minorVersion = minor;
    this.bugfixVersion = bugfix;

    try
    {
      invariant();
    }

    catch (InvariantException e)
    {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }


  /**
   * Constructs a new version number information. The version number must always contain three
   * mandatory elements, the major, minor and bugfix version numbers. An optional build
   * identifier can be included in the version information, as a arbitrary string value. <p>
   *
   * The string representation of a version will separate major, minor and bugfix numbers by
   * a dot '.' character therefore giving a format 'major.minor.bugfix'. The build identifier
   * is separated from bugfix version with an underscore '_' character, giving a version format
   * of 'major.minor.bugfix_buildidentifier'.
   *
   * @param major
   *          Major version number. Must be a value in range [0...999].
   *
   * @param minor
   *          Minor version number. Must be a value in range [0...999].
   *
   * @param bugfix
   *          Bugfix version number. Must be a value in range [0...9999].
   *
   * @param buildId
   *          An arbitrary string identifier for the version information.
   *
   * @throws IllegalArgumentException
   *          if any of the version number values are out of range
   */
  public Version(int major, int minor, int bugfix, String buildId)
  {
    this(major, minor,  bugfix);

    this.buildIdentifier = buildId;

    try
    {
      invariant();
    }

    catch (InvariantException e)
    {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * Constructs a new version information from a given version string. The version string
   * format is specified in {@link #VERSION_REGEX} constant.
   *
   * @param versionString
   *          a version string that is parsed to a version instance
   *
   * @throws IllegalArgumentException
   *          if the version string cannot be parsed
   */
  public Version(String versionString)
  {
    if (versionString == null)
    {
      throw new IllegalArgumentException("Null version string.");
    }

    versionString = versionString.trim();

    Matcher matcher = versionPattern.matcher(versionString);

    if (!matcher.matches())
    {
      throw new IllegalArgumentException(
          "Version string '" + versionString + "' does not match the expected version string " +
          "format: major{0..999}.minor{0..999}.bugfix{0..9999}[_buildIdentifier]"
      );
    }

    try
    {
      majorVersion = Integer.parseInt(matcher.group(1));
      minorVersion = Integer.parseInt(matcher.group(2));
      bugfixVersion = Integer.parseInt(matcher.group(3));
    }

    catch (NumberFormatException e)
    {
      throw new IncorrectImplementationException(
          "Cannot parse version integers from version string ''{0}'': {1}",
          e, versionString, e.getMessage()
      );
    }

    if (matcher.groupCount() == 4)
    {
      String identifier = matcher.group(4).trim();
      buildIdentifier = identifier.substring(1, identifier.length());
    }

    try
    {
      invariant();
    }

    catch (InvariantException e)
    {
      throw new IncorrectImplementationException(
          "Unable to construct version instance from string ''{0}''. Invariant error : {1}",
          e, versionString, e.getMessage()
      );
    }
  }


  // Object Overrides -----------------------------------------------------------------------------

  /**
   * Constructs a string representation of this version in the format
   * 'major.minor.bugfix_buildidentifier'. When a build identifier is omitted, the format is
   * 'major.minor.bugfix'.
   *
   * @return  a version string
   */
  @Override public String toString()
  {
    StringBuilder builder = new StringBuilder(100);
    builder.append(majorVersion);
    builder.append(".");
    builder.append(minorVersion);
    builder.append(".");
    builder.append(bugfixVersion);

    if (!buildIdentifier.trim().equals(""))
    {
      if (!buildIdentifier.startsWith("_"))
      {
        builder.append("_");
      }

      builder.append(buildIdentifier);
    }

    return builder.toString();
  }


  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Checks that the version information in this instance is valid.
   */
  protected void invariant() throws InvariantException
  {
    invariant(this.majorVersion, this.minorVersion, this.bugfixVersion, this.buildIdentifier);
  }

  /**
   * Checks that the given version information is valid for constructing a version instance.
   *
   * @param major
   *          mandatory major version number, should be in range [0...999]
   *
   * @param minor
   *          mandatory minor version number, should be in range [0...999]
   *
   * @param bugfix
   *          mandatory bugfix version number, should be in range [0...9999]
   *
   * @param buildId
   *          optional build identifier, may be a null reference or any arbitrary string
   *
   * @throws IllegalArgumentException
   *          if any of the version values are out of range
   */
  protected void invariant(int major, int minor, int bugfix, String buildId)
      throws InvariantException
  {
    if (major > 999 || major < 0)
    {
      throw new InvariantException(
          "Invalid major version value {0}. Valid range is [0...999].", major
      );
    }

    if (minor > 999 || minor < 0)
    {
      throw new InvariantException(
          "Invalid minor version value {0}. Valid range is [0...999].", minor
      );
    }

    if (bugfix > 9999 || bugfix < 0)
    {
      throw new InvariantException(
          "Invalid bugfix version value {0}. Valid range is [0...9999].", bugfix
      );
    }

    if (buildId == null)
    {
      buildIdentifier = "";
    }
  }


  // Nested Classes -------------------------------------------------------------------------------

  /**
   * A specialized version type that is used in error cases where the version cannot be
   * determined. A version identifier '0.0.0_UNKNOWN' is reserved for those cases.
   */
  private static class Unknown extends Version
  {
    private Unknown()
    {
      super.majorVersion = 0;
      super.minorVersion = 0;
      super.bugfixVersion = 0;
      super.buildIdentifier = "UNKNOWN";

      try
      {
        invariant();
      }

      catch (InvariantException e)
      {
        // TODO : log error

        throw new OpenRemoteRuntimeException(
            "Unable to construct 'UNKNOWN' version instance due to invariant violation: {0}",
            e, e.getMessage()
        );
      }
    }
  }

  /**
   * Exception type to indicate the internal invariant of this class has been violated.
   */
  public static class InvariantException extends OpenRemoteException
  {

    private InvariantException(String msg)
    {
      super(msg);
    }

    private InvariantException(String msg, Object... params)
    {
      super(msg, params);
    }
  }

}

