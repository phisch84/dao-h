package com.schoste.ddd.infrastructure.dal.v2.services.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.schoste.ddd.infrastructure.dal.v2.models.HibernateDO;
import com.schoste.ddd.infrastructure.dal.v2.services.HibernateDAO;
import com.schoste.ddd.infrastructure.dal.v2.services.hibernate.GenericHibernateDAO;

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
	
	/**
	 * Creates a new data object
	 * 
	 * @return an instance to a new data object
	 */
	public HibernateDO createDataObject()
	{
		HibernateDO dataObject = (HibernateDO) this.applicationContext.getBean(HibernateDO.class);
		
		return dataObject;
	}
}
