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

package org.openremote.model.persistence.jpa.beehive;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.openremote.base.Defaults;
import org.openremote.model.User;
import org.openremote.model.Model.ValidationException;
import org.openremote.model.User.Authentication;
import org.openremote.model.User.CredentialsEncoding;
import org.openremote.model.persistence.jpa.RelationalAccount;
import org.openremote.model.persistence.jpa.RelationalUser;

@Entity(name = "BeehiveUser")
@Table(name = "user")
public class BeehiveUser extends RelationalUser
{

  @ManyToOne
  @JoinColumn(name = "account_oid")
  protected RelationalAccount account;

  @Column(name = "valid")
  protected Boolean valid = Boolean.valueOf(true);

  @Column(name = "token", length = 255)
  protected String token = null;

  @Basic
  @Column(name = "register_time")
  protected Date registerTime = new Date(System.currentTimeMillis());

  @Column(name = "password", nullable = false, length = 255)
  protected String password = null;

  protected BeehiveUser()
  {
  }

  public BeehiveUser(RelationalAccount account, User user, byte[] password) throws ValidationException
  {
    super(user);
    this.account = account;
    this.addAuth(new BeehiveUser.LegacyBeehiveAuthentication(this, password));
  }

  private void addAuth(BeehiveUser.LegacyBeehiveAuthentication authentication)
  {
    this.password = this.toHexString(authentication.credentials);
    super.addAuthentication(authentication);
  }

  private String toHexString(byte[] bytes)
  {
    StringBuilder builder = new StringBuilder(bytes.length);
    byte[] arr$ = bytes;
    int len$ = bytes.length;

    for(int i$ = 0; i$ < len$; ++i$)
    {
      byte b = arr$[i$];
      builder.append(String.format("%1$02x", new Object[]{Byte.valueOf(b)}));
    }

    return builder.toString();
  }

  private final class LegacyBeehiveAuthentication extends Authentication
  {
    private byte[] credentials;

    private LegacyBeehiveAuthentication(User user, byte[] credentials)
    {
      super(credentials, CredentialsEncoding.LEGACY_BEEHIVE);
      this.credentials = super.getCredentials();
      byte[] creds = super.getCredentials();
      byte[] salt = ("{" + user.getName() + "}").getBytes(Defaults.UTF8);
      byte[] saltedCreds = new byte[creds.length + salt.length];
      System.arraycopy(creds, 0, saltedCreds, 0, creds.length);
      System.arraycopy(salt, 0, saltedCreds, creds.length, salt.length);

      try
      {
        MessageDigest exception = MessageDigest.getInstance("MD5");
        byte[] md5creds = exception.digest(saltedCreds);
        this.credentials = md5creds;
      }
      catch (NoSuchAlgorithmException var9)
      {
        var9.printStackTrace();
      }

    }
  }
}
