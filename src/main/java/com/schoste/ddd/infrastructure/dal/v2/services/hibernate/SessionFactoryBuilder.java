package com.schoste.ddd.infrastructure.dal.v2.services.hibernate;

import java.util.Collection;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Static builder for the {@link SessionFactory}.
 * {@link SessionFactoryBuilder#setMetadataSourcesResources(String)} and {@link SessionFactoryBuilder#buildSessionFactory(Map)} are supposed to be called
 * by Spring as configured in the module.xml.
 * In previous versions of Hibernate the session factory could be configured in Spring's XML. From Hibernate 5/6 on it must be done programmatically.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 */
public class SessionFactoryBuilder
{
    private static Collection<String> metaDataSourcesResources;

    public static void setMetadataSourcesResources(Collection<String> entitiesXML)
    {
        SessionFactoryBuilder.metaDataSourcesResources = entitiesXML;
    }

    public static SessionFactory buildSessionFactory(Map<String, Object> settings) 
    {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();

        if (settings != null)
        {
            Collection<String> keys = settings.keySet();

            for (String key : keys)
            {
                Object value = settings.get(key);

                builder.applySetting(key, value);
            }
        }

        StandardServiceRegistry registry = builder.build();
        MetadataSources sources = new MetadataSources(registry);

        for (String metaDataSourcesResource : SessionFactoryBuilder.metaDataSourcesResources)
        {
            sources.addResource(metaDataSourcesResource);  
        }

        Metadata metadata = sources.buildMetadata();

        return metadata.buildSessionFactory();
    }
}