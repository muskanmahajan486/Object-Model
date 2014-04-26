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


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * A numeric identifier of the major version. Valid in range of [0...999]. A mandatory field
   * in version information.
   */
  private int majorVersion;

  /**
   * A numeric identifier of the minor version. Valid in range of [0...999]. A mandatory field
   * in version information. Will be separated by dot '.' character from the major version number.
   */
  private int minorVersion;

  /**
   * A numeric identifier of a bug fix version. Valid in range of [0...9999] (note larger range
   * than other identifiers). A mandatory field in version information. Will be separated by
   * dot '.' character from the minor version number.
   */
  private int bugfixVersion;

  /**
   * An arbitrary build identifier string attached to any build version, separated by an
   * underscore '_' character from the bugfix version number. This is an optional field in
   * version information.
   */
  private String buildIdentifier = "";


  // Constructors ---------------------------------------------------------------------------------

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
    if (major > 999 || major < 0)
    {
      throw new IllegalArgumentException(
          "Invalid major version value " + major + ". Valid range is [0...999]."
      );
    }

    if (minor > 999 || minor < 0)
    {
      throw new IllegalArgumentException(
          "Invalid minor version value " + minor + ". Valid range is [0...999]."
      );
    }

    if (bugfix > 9999 || bugfix < 0)
    {
      throw new IllegalArgumentException(
          "Invalid bugfix version value " + bugfix + ". Valid range is [0...9999]."
      );
    }

    this.majorVersion = major;
    this.minorVersion = minor;
    this.bugfixVersion = bugfix;
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
   * @param buildIdentifier
   *          An arbitrary string identifier for the version information.
   *
   * @throws IllegalArgumentException
   *          if any of the version number values are out of range
   */
  public Version(int major, int minor, int bugfix, String buildIdentifier)
  {
    this(major, minor,  bugfix);

    if (buildIdentifier == null)
    {
      buildIdentifier = "";
    }

    this.buildIdentifier = buildIdentifier;
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
}

