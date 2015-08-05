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
package org.openremote.model.testengine;

import java.io.*;
import java.net.URI;
import java.util.Scanner;

public class OpenRemoteTest
{

  private static final String TEST_FIXTURE_BASE_PATH = "resources/test";

  public String loadResourceTextFile(URI fileURI)
  {
    String res = "";
    InputStream is = null;
    try
    {
      is = new FileInputStream(new File(new File(TEST_FIXTURE_BASE_PATH), fileURI.toString()));
      Scanner s = new Scanner(is).useDelimiter("\\A");
      res = s.hasNext() ? s.next() : "";
    } catch (FileNotFoundException e)
    {
    } finally
    {
      if (is != null)
      {
        try
        {
          is.close();
        } catch (IOException e)
        {
        }
      }
    }
    return res;
  }
}
