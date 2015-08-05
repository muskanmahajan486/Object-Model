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
package org.openremote.base;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Defaults
{
  public static final Charset UTF8;
  public static final Charset DEFAULT_CHARSET;
  public static final Calendar UTC;

  private Defaults()
  {
  }

  static
  {
    try
    {
      System.out.println("OROM-000001: Setting UTF-8 based system output and error streams.");
      System.setOut(new PrintStream(System.out, true, "UTF-8"));
      System.setErr(new PrintStream(System.err, true, "UTF-8"));
    } catch (Throwable var1)
    {
      System.err.println("Failed to install UTF-8 system streams " + var1.getMessage());
    }

    UTF8 = Charset.forName("UTF-8");
    DEFAULT_CHARSET = UTF8;
    UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
  }
}
