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

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * An utility class that can be used to create a Hibernate session factory configurations
 * for various H2 database setups.
 *
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class H2
{

  // TODO:
  //     Currently uses Hibernate API to establish persistence sessions due to Hibernate
  //     offering a more convenient programmatic configuration API. These could (should?)
  //     be migrated to JPA EntityManager API instead.



  // Public Class Members -------------------------------------------------------------------------

  /**
   * Creates a persistence session factory with a configuration for a single persistence class
   * using a given persistence mapping file.
   *
   * @param persistenceMode
   *          see {@link PersistenceMode}
   *
   * @param mappingFile
   *          name of the domain object's relational entity mapping file under
   *          /resources/jpa directory, e.g. "Account.xml"
   *
   * @param entityClass
   *          relational entity class
   *
   * @return  Hibernate session factory configured to a H2 database with given entity mapping
   *          configuration
   */
  public static SessionFactory createConfiguration(PersistenceMode persistenceMode,
                                                   String mappingFile, Class<?> entityClass)
  {
    Set<String> files = new HashSet<String>();
    files.add(mappingFile);

    Set<Class<?>> entities = new HashSet<Class<?>>();
    entities.add(entityClass);

    return createConfiguration(persistenceMode, files, entities);
  }


  /**
   * Creates a persistence session factory with a configuration for a set persistence classes
   * using corresponding persistence mapping files.
   *
   * @param persistenceMode
   *          see {@link PersistenceMode}
   *
   * @param mappingFiles
   *          names of the domain objects' relational entity mapping files under
    *          /resources/jpa directory, e.g. "Account.xml", "User.xml"
   *
   * @param entityClasses
   *          relational entity classes
   *
   * @return  Hibernate session factory configured to a H2 database with given entity mapping
   *          configuration
   */
  public static SessionFactory createConfiguration(PersistenceMode persistenceMode,
                                                   Set<String> mappingFiles,
                                                   Set<Class<?>> entityClasses)
  {
    boolean useLegacyBeehiveSchema = false;

    return createConfiguration(persistenceMode, mappingFiles, entityClasses, useLegacyBeehiveSchema);
  }



  /**
   * Creates a H2 session factory that maps a set of object model entities to a legacy Beehive 3.0
   * relational model.
   *
   * @param persistenceMode
   *          see {@link PersistenceMode}
   *
   * @param mappingFiles
   *          names of the domain objects' relational entity mapping files under
   *          /resources/jpa/beehive30 directory,
   *          see {@link org.openremote.model.persistence.jpa.RelationalTest#getBeehiveResourceDirectoryURI()}
   *
   * @param entityClasses
   *          the relational entity classes
   *
   * @return
   *          Hibernate session factory
   */
  public static SessionFactory createLegacyBeehiveConfiguration(PersistenceMode persistenceMode,
                                                                Set<String> mappingFiles,
                                                                Set<Class<?>> entityClasses)
  {
    boolean useLegacyBeehiveSchema = true;

    return createConfiguration(persistenceMode, mappingFiles, entityClasses, useLegacyBeehiveSchema);
  }


  /**
   * Creates a H2 session factory that maps an object model entity to a legacy Beehive 3.0
   * relational model.
   *
   * @param persistenceMode
   *          see {@link PersistenceMode}
   *
   * @param mappingFile
   *          name of the domain object's relational entity mapping file under
   *          /resources/jpa/beehive30 directory,
   *          see {@link org.openremote.model.persistence.jpa.RelationalTest#getBeehiveResourceDirectoryURI()}
   *
   * @param entityClass
   *          the relational entity class
   *
   * @return
   *          Hibernate session factory
   */
  public static SessionFactory createLegacyBeehiveConfiguration(PersistenceMode persistenceMode,
                                                                String mappingFile,
                                                                Class<?> entityClass)
  {
    boolean useLegacyBeehiveSchema = true;

    return createConfiguration(persistenceMode, mappingFile, entityClass, useLegacyBeehiveSchema);
  }



  // Private Class Members ------------------------------------------------------------------------

  /**
   * Creates a Hibernate session factory for a given single domain object and its relational
   * entity model.
   *
   * @param persistenceMode
   *          see {@link PersistenceMode}
   *
   * @param mappingFile
   *          name of the domain object's relational entity mapping file under
   *          /resources/jpa or /resources/jpa/beehive30 directory, depending if use legacy
   *          Beehive schema value is true or not.
   *
   * @param entityClass
   *          the relational entity class
   *
   * @param useLegacyBeehiveSchema
   *          Set to true to use mapping files corresponding to legacy Beehive 3.0 schema, false
   *          otherwise.
   *          See {@link org.openremote.model.persistence.jpa.RelationalTest#getBeehiveResourceDirectoryURI()}
   *          and {@link org.openremote.model.persistence.jpa.RelationalTest#getJPAResourceDirectoryURI()}
   *
   * @return
   *          Hibernate session factory
   */
  private static SessionFactory createConfiguration(PersistenceMode persistenceMode,
                                                    String mappingFile,
                                                    Class<?> entityClass,
                                                    boolean useLegacyBeehiveSchema)
  {
    Set<String> file = new HashSet<String>();
    file.add(mappingFile);

    Set<Class<?>> entity = new HashSet<Class<?>>();
    entity.add(entityClass);

    return createConfiguration(persistenceMode, file, entity, useLegacyBeehiveSchema);
  }

  /**
   * Creates a Hibernate session factory for given domain objects and their relational
   * entity models.
   *
   * @param persistenceMode
   *          see {@link PersistenceMode}
   *
   * @param mappingFiles
   *          name of the domain objects' relational entity mapping files under
   *          /resources/jpa or /resources/jpa/beehive30 directory, depending if use legacy
   *          Beehive schema value is true or not.
   *
   * @param entityClasses
   *          the relational entity classes
   *
   * @param useLegacyBeehiveSchema
   *          Set to true to use mapping files corresponding to legacy Beehive 3.0 schema, false
   *          otherwise.
   *          See {@link org.openremote.model.persistence.jpa.RelationalTest#getBeehiveResourceDirectoryURI()}
   *          and {@link org.openremote.model.persistence.jpa.RelationalTest#getJPAResourceDirectoryURI()}
   *
   * @return
   *          Hibernate session factory
   */
  private static SessionFactory createConfiguration(PersistenceMode persistenceMode,
                                                    Set<String> mappingFiles,
                                                    Set<Class<?>> entityClasses,
                                                    boolean useLegacyBeehiveSchema)
  {
    Configuration config = new Configuration();

    addClasses(config, entityClasses);

    if (useLegacyBeehiveSchema)
    {
      addBeehiveFiles(config, mappingFiles);

      if (persistenceMode == PersistenceMode.ON_DISK)
      {
        return createSessionFactory("~/BeehiveTest", config);
      }

      else
      {
        return createSessionFactory(config);
      }
    }

    else
    {
      addFiles(config, mappingFiles);

      if (persistenceMode == PersistenceMode.ON_DISK)
      {
        return createSessionFactory("~/AccountManagerTest", config);
      }

      else
      {
        return createSessionFactory(config);
      }
    }
  }


  /**
   * Create a persistent H2 configuration for a Hibernate session factory.
   *
   * @param dbName
   *          file path for the database storage
   *
   * @param config
   *          Hibernate configuration to use as the base
   *
   * @return
   *          Hibernate session factory
   */
  private static SessionFactory createSessionFactory(String dbName, Configuration config)
  {
    return createSessionFactory(PersistenceMode.ON_DISK, dbName, config);
  }

  /**
   * Create an in-memory H2 configuration for Hibernate session factory.
   *
   * @param config
   *          Hibernate configuration to use as the base
   *
   * @return
   *          Hibernate session factory
   */
  private static SessionFactory createSessionFactory(Configuration config)
  {
    return createSessionFactory(PersistenceMode.IN_MEMORY, "No Name", config);
  }

  /**
   * Creates an H2 configuration for Hibernate session factory.
   *
   * @param persistenceMode
   *          see {@link PersistenceMode}
   *
   * @param dbName
   *          file path for the database storage -- only relevant when the persistence mode
   *          is {@link PersistenceMode#ON_DISK}
   *
   * @param config
   *          Hibernate configuration to use as the base
   *
   * @return
   *          Hibernate session factory
   */
  private static SessionFactory createSessionFactory(PersistenceMode persistenceMode, String dbName,
                                                      Configuration config)
  {
    config.setProperty("hibernate.connection.url", "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE");

    if (persistenceMode == PersistenceMode.ON_DISK)
    {
      config.setProperty("hibernate.connection.url", "jdbc:h2:" + dbName);
    }

    config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
    config.setProperty("hibernate.connection.username", "sa");
    config.setProperty("hibernate.connection.pool_size", "3");
    config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.internal.NoCacheProvider");
    config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    config.setProperty("hibernate.hbm2ddl.auto", "create");

    config.setProperty("hibernate.current_session_context_class", "managed");

    ServiceRegistry registry = new StandardServiceRegistryBuilder()
        .applySettings(config.getProperties())
        .build();

    return config.buildSessionFactory(registry);
  }



  public static void dropTable(Session session, String name)
  {
    SQLQuery query = session.createSQLQuery("drop table " + name.trim());
    query.executeUpdate();

    session.close();
  }


  private static void addBeehiveFiles(Configuration config, Set<String> files)
  {
    addFiles(config, files, RelationalTest.getBeehiveResourceDirectoryURI());

  }

  private static void addFiles(Configuration config, Set<String> files)
  {
    addFiles(config, files, RelationalTest.getJPAResourceDirectoryURI());
  }

  private static void addFiles(Configuration config, Set<String> files, URI filePath)
  {
    for (String mappingFile : files)
    {
      URI ormURI = filePath.resolve(filePath.getRawPath() + "/" + mappingFile);

      config.addFile(new File(ormURI));
    }
  }

  private static void addClasses(Configuration config, Set<Class<?>> files)
  {
    for (Class<?> clazz : files)
    {
      config.addAnnotatedClass(clazz);
    }
  }



  // Nested Enums ---------------------------------------------------------------------------------

  public enum PersistenceMode
  {
    IN_MEMORY,
    ON_DISK
  }


}

