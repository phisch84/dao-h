package com.schoste.ddd.infrastructure.dal.v2.services.hibernate;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
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
	@Override
	public void integrate(Metadata metadata, BootstrapContext bootstrapContext, SessionFactoryImplementor sessionFactory) 
	{
		EventListenerRegistry eventListenerRegistry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

		GenericDataObjectListener listener = new GenericDataObjectListener();

		eventListenerRegistry.appendListeners(EventType.PRE_INSERT, listener);
		eventListenerRegistry.appendListeners(EventType.PRE_UPDATE, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) 
	{
	}
}