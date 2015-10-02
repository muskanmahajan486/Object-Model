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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openremote.base.exception.IncorrectImplementationException;
import org.openremote.model.persistence.PersistenceException;

public class HibernateStorage<T>
{
  private SessionFactory sessionFactory;
  private Class<T> modelClass = null;

  public HibernateStorage()
  {
  }

  public HibernateStorage<T> setSessionFactory(SessionFactory sessionFactory)
  {
    this.sessionFactory = sessionFactory;
    return this;
  }

  public void init(Class<T> modelClass)
  {
    this.modelClass = modelClass;
  }

  private T instantiateJPAClass(T object) throws IncorrectImplementationException
  {
    String fullClassName = this.constructPersistentClassName();

    try
    {
      Class e = Thread.currentThread().getContextClassLoader().loadClass(fullClassName);
      Class ct = e.asSubclass(this.modelClass);
      Constructor ctor = ct.getConstructor(new Class[]{this.modelClass});
      return (T) ctor.newInstance(new Object[]{object});
    }
    catch (InvocationTargetException var6)
    {
      throw new IncorrectImplementationException(var6.getMessage(), var6);
    }
    catch (InstantiationException var7)
    {
      throw new IncorrectImplementationException(var7.getMessage(), var7);
    }
    catch (IllegalAccessException var8)
    {
      throw new IncorrectImplementationException(var8.getMessage(), var8);
    }
    catch (NoSuchMethodException var9)
    {
      throw new IncorrectImplementationException(var9.getMessage(), var9);
    }
    catch (ClassNotFoundException var10)
    {
      throw new IncorrectImplementationException("Persistence class \'\'{0}\'\' was not found.", new Object[]{fullClassName});
    }
  }

  private String constructPersistentClassName()
  {
    return this.getClass().getPackage().getName() + "." + this.constructEntityName();
  }

  private String constructEntityName()
  {
    return "Persistent" + this.modelClass.getSimpleName();
  }

  public void create(T... object)
  {
  }

  public void create(T object) throws PersistenceException, IncorrectImplementationException
  {
    Object entity = this.instantiateJPAClass(object);
    Session hibernate = null;
    Transaction tx = null;

    try
    {
      hibernate = this.sessionFactory.getCurrentSession();
      tx = hibernate.beginTransaction();
      hibernate.saveOrUpdate(entity);
      tx.commit();
    }
    catch (HibernateException var6)
    {
      if(tx != null)
      {
        tx.rollback();
      }

      throw new PersistenceException("Unable to save or update instance {0} : {1}", var6, new Object[]{object, var6.getMessage()});
    }
    catch (Exception var7)
    {
      if(tx != null)
      {
        tx.rollback();
      }

      throw new PersistenceException("Unable to save or update instance {0} : {1}", var7, new Object[]{object, var7.getMessage()});
    }
  }

  public Iterator<T> read() throws PersistenceException
  {
    Session hibernate = null;
    Transaction tx = null;

    try
    {
      hibernate = this.sessionFactory.getCurrentSession();
      tx = hibernate.beginTransaction();
      Query e = hibernate.createQuery("from " + this.constructEntityName());
      return e.iterate();
    }
    catch (HibernateException var4)
    {
      if(tx != null)
      {
        tx.rollback();
      }

      throw new PersistenceException("Unable to create a query to retrieve all instances of \'\'{0}\'\' entities : {1}",
              var4, new Object[]{this.modelClass.getSimpleName(), var4.getMessage()});
    }
    catch (Exception var5)
    {
      if(tx != null)
      {
        tx.rollback();
      }

      throw new PersistenceException("Unable to create a query to retrieve all instances of \'\'{0}\'\' entities : {1}",
              var5, new Object[]{this.modelClass.getSimpleName(), var5.getMessage()});
    }
  }

  public void delete(T object) throws PersistenceException, IncorrectImplementationException
  {
    Object entity = this.instantiateJPAClass(object);
    Session hibernate = null;
    Transaction tx = null;

    try
    {
      hibernate = this.sessionFactory.getCurrentSession();
      tx = hibernate.beginTransaction();
      hibernate.delete(entity);
      tx.commit();
    }
    catch (HibernateException var6)
    {
      if(tx != null)
      {
        tx.rollback();
      }

      throw new PersistenceException("Unable to delete instance {0} : {1}", var6, new Object[]{object, var6.getMessage()});
    }
    catch (Exception var7)
    {
      if(tx != null)
      {
        tx.rollback();
      }

      throw new PersistenceException("Unable to delete instance {0} : {1}", var7, new Object[]{object, var7.getMessage()});
    }
  }
}
