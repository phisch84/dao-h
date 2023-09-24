package com.schoste.ddd.infrastructure.dal.v2.services;

import com.schoste.ddd.infrastructure.dal.v2.models.HibernateDO;

/**
 * Example interface to the implementation of the DAO
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 */
public interface HibernateDAO extends GenericDataAccessObject<HibernateDO> 
{
	/**
	 * Creates a new instance of a data object
	 * 
	 * @return a new data object
	 */
	HibernateDO createDataObject();
}
