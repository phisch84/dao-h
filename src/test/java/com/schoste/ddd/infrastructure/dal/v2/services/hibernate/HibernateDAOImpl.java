package com.schoste.ddd.infrastructure.dal.v2.services.hibernate;

import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;
import com.schoste.ddd.infrastructure.dal.v2.models.HibernateDO;
import com.schoste.ddd.infrastructure.dal.v2.services.HibernateDAO;
import com.schoste.ddd.infrastructure.dal.v2.services.LazyLoader;

/**
 * Example file system data object used in unit testing of the GenericSerializationDAO implementation
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 */
public class HibernateDAOImpl extends GenericHibernateDAO<HibernateDO> implements HibernateDAO
{
	@Autowired
	protected ApplicationContext applicationContext;

	@Autowired
	protected SessionFactory sessionFactory;
	
	protected SessionFactory getSessionFactory()
	{
		return this.sessionFactory;
	}

	protected HibernateDO lazyLoadingConversionFunction(Integer dataObjId)
	{
		return null;
	}

	/**
	 * Creates a new data object
	 * 
	 * @return an instance to a new data object
	 */
	@Override
	public HibernateDO createDataObject()
	{
		HibernateDO dataObject = (HibernateDO) this.applicationContext.getBean(HibernateDO.class);
		
		return dataObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected LazyLoader<Integer, HibernateDO> createLazyLoader() throws Exception 
	{
		Function<Integer, GenericDataObject> conversionFunction = id -> this.lazyLoadingConversionFunction(id);
		Session session = this.getSessionFactory().openSession();
		LazyLoader<Integer, HibernateDO> ll = this.applicationContext.getBean(LazyLoader.class, conversionFunction, session);

		return ll;
	}
}
