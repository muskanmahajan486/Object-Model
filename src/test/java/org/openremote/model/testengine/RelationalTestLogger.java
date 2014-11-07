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
package org.openremote.model.testengine;

import org.openremote.model.persistence.jpa.RelationalTest;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A small utility listener for TestNG framework that directs Hibernate logging to its own
 * directory (see {@link RelationalTest#getHibernateLogDirectoryBaseURI}), broken down to
 * a log file per test in the afore-mentioned directory. <p>
 *
 * This listener implementation is installed by the {@link RelationalTest} implementation and
 * its subclasses.
 *
 * @see RelationalTest
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class RelationalTestLogger implements IInvokedMethodListener
{

  private FileHandler fileHandler;

  /**
   * For test methods, create a hibernate log file for logging category
   * {@link RelationalTest#HIBERNATE_ROOT_LOGGING_CATEGORY} and its sub-categories. <p>
   *
   * Logs will be directed to a file under {@link RelationalTest#getHibernateLogDirectoryBaseURI}.
   * Each individual test method will have its own log file.
   */
  @Override public void beforeInvocation(IInvokedMethod method, ITestResult result)
  {
    if (method.isTestMethod())
    {
      String testName = method.getTestMethod().getMethodName();

      Logger log = Logger.getLogger(RelationalTest.HIBERNATE_ROOT_LOGGING_CATEGORY);

      String logFileName = testName + ".log";
      File logFile = new File(new File(RelationalTest.getHibernateLogDirectoryBaseURI()), logFileName);

      try
      {
        fileHandler = new FileHandler(logFile.getAbsolutePath());
        fileHandler.setFormatter(new LogFormatter());
        log.addHandler(fileHandler);
      }

      catch (Throwable t)
      {
        System.err.println("Cannot create hibernate log file at " + logFile);

        t.printStackTrace();
      }
    }
  }

  /**
   * Clean up.
   */
  @Override public void afterInvocation(IInvokedMethod method, ITestResult result)
  {
    Logger log = Logger.getLogger(RelationalTest.HIBERNATE_ROOT_LOGGING_CATEGORY);

    log.removeHandler(fileHandler);
  }


  // Nested Classes -------------------------------------------------------------------------------

  private static class LogFormatter extends Formatter
  {
    private static Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

    @Override public String format(LogRecord record)
    {
      utc.setTimeInMillis(record.getMillis());

      String utcDate = String.format(
          "%1$s/%2$s/%3$s %4$s:%5$s:%6$s.%7$s %8$s",

          utc.get(Calendar.YEAR),
          utc.get(Calendar.MONTH),
          utc.get(Calendar.DAY_OF_MONTH),

          utc.get(Calendar.HOUR_OF_DAY),
          utc.get(Calendar.MINUTE),
          utc.get(Calendar.SECOND),
          utc.get(Calendar.MILLISECOND),

          utc.getTimeZone().getID()
      );

      return String.format(
          "%1$s %2$s %3$s%n",
          utcDate,
          record.getLevel(),
          record.getMessage());
    }
  }
}

