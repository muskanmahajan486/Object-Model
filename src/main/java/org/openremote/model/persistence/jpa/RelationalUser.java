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
package org.openremote.model.persistence.jpa;

import org.openremote.base.Defaults;
import org.openremote.model.User;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Locale;

@Entity(name = "User")
@Table(name = "user")
public class RelationalUser extends User
{

  private static final long CREDENTIAL_CHECK_INTERVAL_MS = 1000L;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "oid")
  @Id
  private Long id;

  @Embedded
  protected RelationalUser.UserAuthentication authentication;

  @Column(name = "registration_time_utc")
  private Long registrationTime;

  protected RelationalUser()
  {
    this.registrationTime = Long.valueOf(Defaults.UTC.getTimeInMillis());
  }

  public RelationalUser(String username, String email) throws ValidationException
  {
    super(username, email);
    this.registrationTime = Long.valueOf(Defaults.UTC.getTimeInMillis());
  }

  public RelationalUser(User copy)
  {
    super(copy);
    this.registrationTime = Long.valueOf(Defaults.UTC.getTimeInMillis());
    if(this.hasAttribute("credentials"))
    {
      byte[] creds = this.getAttribute("credentials").getBytes(Defaults.DEFAULT_CHARSET);
      CredentialsEncoding credsEncoding = CredentialsEncoding.UNSPECIFIED;
      if(this.hasAttribute("authMode"))
      {
        credsEncoding = CredentialsEncoding.valueOf(this.getAttribute("authMode").toUpperCase(Locale.ENGLISH));
      }

      this.addAuthentication(new Authentication(creds, credsEncoding));
    }

    this.userAttributes.remove("credentials");
    this.userAttributes.remove("authMode");
  }

  @PrePersist
  public void initRegistrationTime()
  {
  }

  public void addAuthentication(Authentication authentication)
  {
    this.authentication = new RelationalUser.UserAuthentication(authentication);
  }

  public void link(RelationalAccount account)
  {
    this.accounts.add(account);
  }

  public boolean matchCredentials(byte[] candidate)
  {
    long startTime = System.currentTimeMillis();
    long endTime = startTime + 1000L;
    boolean result = false;
    if(candidate.length == this.authentication.credentials.length)
    {
      for(int index = 0; index < candidate.length; ++index)
      {
        if(candidate[index] != this.authentication.credentials[index])
        {
          return this.matchResult(false, endTime);
        }
      }

      result = true;
    }

    return this.matchResult(result, endTime);
  }

  public String toString()
  {
    return "Relational User (ID = " + this.id + ", account(s): " + this.accounts + ")";
  }

  private boolean matchResult(boolean result, long endTime)
  {
    while(System.currentTimeMillis() < endTime)
    {
      try
      {
        Thread.sleep(10L);
        Thread.yield();
      }
      catch (InterruptedException var5)
      {
        System.err.println("Thread interrupted in the middle of credentials check.");
        Thread.currentThread().interrupt();
      }
    }

    return result;
  }

  @Embeddable
  private static final class UserAuthentication extends Authentication
  {
    @Column(name = "credentials")
    private byte[] credentials;

    @Column(name = "auth_mode")
    @Enumerated(EnumType.STRING)
    private CredentialsEncoding encoding;

    @Column(name = "salt")
    private String salt;

    protected UserAuthentication()
    {
      this.credentials = super.getCredentials();
      this.encoding = super.encoding;
      this.salt = super.salt;
    }

    private UserAuthentication(Authentication copy)
    {
      super(copy);
      this.credentials = super.getCredentials();
      this.encoding = super.encoding;
      this.salt = super.salt;
    }
  }
}
