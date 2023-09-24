package com.schoste.ddd.infrastructure.dal.v2.services;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * Hibernate Integrator class to register the entity lister.
 * The class must be defined in META-INF/services/org.hibernate.integrator.spi.Integrator.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 */
public class HibernateListernerIntegrator implements Integrator
{
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) 
	{
		final EventListenerRegistry eventRegistry = serviceRegistry.getService(EventListenerRegistry.class);
		
		eventRegistry.prependListeners(EventType.PRE_UPDATE, GenericDataObjectListener.class);		
		eventRegistry.prependListeners(EventType.PRE_INSERT, GenericDataObjectListener.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry)
	{
		// TODO Auto-generated method stub
	}
}
