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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A specific version implementation that resolves implementation version from a package's
 * manifest file. This extends the {@link org.openremote.base.Version} implementation with
 * the exception that it support a short API versioning matching the pattern 'major.minor'
 * in the version number. <p>
 *
 * API major version should be increased when backwards compatibility to earlier versions is
 * broken. Minor version number should be increased when new features are added or implementation
 * is changed but backwards compatibility to earlier versions with the same major version
 * number is maintained.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class APIVersion extends Version
{

  //
  // IMPLEMENTATION NOTES:
  //
  //   - class should remain immutable
  //


  // Constants ------------------------------------------------------------------------------------

  /**
   * Regular expression that specifies the exact format of allowed API version strings: {@value}
   * <p>
   * Generic format for an API version string is 'major.minor' where major and minor
   * version numbers must be present and within range of [0..999]. Anything included after the
   * minor version number string is ignored.
   */
  public static final String API_VERSION_REGEX = "^([0-9]){1,3}.([0-9]){1,3}.*$";


  // Class Members --------------------------------------------------------------------------------

  /**
   * A regular expression pattern that specifies the precise format of allowed API version strings.
   */
  protected static Pattern versionPattern = Pattern.compile(API_VERSION_REGEX);


  /**
   * Attempt to resolve the API version by looking at the package's manifest properties.
   * If the proper 'Specification-Version' property is not found, falls back to looking at
   * 'openremote.project.api.version' system property which is typically set for unit tests
   * (unit tests may not be running against packaged version of classes).
   *
   * @param pkg
   *          package of which version to resolve
   *
   * @return
   *          version data or {@link Version#UNKNOWN} if version cannot be resolved
   */
  private static Version resolveVersion(Package pkg)
  {
    if (pkg == null)
    {
      throw new IllegalArgumentException("null package");
    }

    // Attempt to resolve from package manifest...

    String specVersion = pkg.getSpecificationVersion();

    if (specVersion != null)
    {
      Matcher matcher = versionPattern.matcher(specVersion);

      if (matcher.matches())
      {
        String major = matcher.group(1);
        String minor = matcher.group(2);

        try
        {
          return new Version(Integer.parseInt(major), Integer.parseInt(minor), 0);
        }

        catch (Exception e)
        {
          // todo log.warn
          System.err.println(
              "Unable to parse Java manifest Specification-Version '" + specVersion + "', " +
              "it does not follow the expected version format 'major.minor'."
          );
        }
      }
    }

    // If package failed, check the system property set by the build process when running
    // unit tests -- this is mainly intended for use with unit tests only that may be running
    // classes outside of the JAR package. Not retrieving properties within a privileged block
    // so it's possible to set up a security manager that denies this access...

    try
    {
      String value = System.getProperty("openremote.project.api.version").trim();

      if (value != null && !value.equals(""))
      {
        Matcher matcher = versionPattern.matcher(value);

        try
        {
          if (!matcher.matches())
          {
            throw new OpenRemoteException(
                "Version value ''{0}'' does not match ''{1}''", value, versionPattern
            );
          }

          String major = matcher.group(1);
          String minor = matcher.group(2);

          return new Version(Integer.parseInt(major), Integer.parseInt(minor), 0);
        }

        catch (Exception e)
        {
          // todo log.warn
          System.err.println(
              "Found property 'openremote.project.api.version' but could not resolve value '" +
              value + "' to a proper version instance. Defaulting to unknown version value..."
          );

          return Version.UNKNOWN;
        }
      }
    }

    catch (SecurityException e)
    {
      // todo log.warn
      System.err.println(
          "Could not retrieve property 'openremote.project.api.version' due to security " +
          "restrictions: " + e.getMessage() + ". Default to unknown version value..."
      );

      return Version.UNKNOWN;
    }

    // todo log.warn
    System.err.println("No version info was found for package '" + pkg.getName() + "'.");

    return Version.UNKNOWN;
  }


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new API version from the 'Specification-Version' attribute in the package's
   * manifest file. <p>
   *
   * This constructor assumes that (1) the proper manifest file has been included with the
   * Java archive the package's classes were loaded from and that it includes the appropriate
   * 'Specification-Version' property and (2) the classes from the requested package has been
   * loaded (note that Java lazy-loads class definitions so the manifest information is not
   * present until the package has been defined in the classloader) and are available in the
   * caller's classloader.
   *
   * @param pkg
   *          the package which API (a.k.a specification) version will be resolved
   */
  public APIVersion(Package pkg)
  {
    super(resolveVersion(pkg));
  }


  // Object Overrides -----------------------------------------------------------------------------

  /**
   * Constructs a string representation of this API version in the format 'major.minor'.
   *
   * @return  an API version string
   */
  @Override public String toString()
  {
    return majorVersion + "." + minorVersion;
  }

}

