package com.schoste.ddd.infrastructure.dal.v2.services.hibernate;

import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;
import com.schoste.ddd.infrastructure.dal.v2.services.GenericDAOTest;
import com.schoste.ddd.infrastructure.dal.v2.services.GenericDataAccessObject;

/**
 * Generic test class to test implementations of the GenericSerializationDAO class.
 * This class provides standard test methods.
 * Calls to methods which manipulate data objects on the storage are annotated with
 * {@link @Transactional}.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 * @param <DO> The class of the data object
 * @param <DAO> The class of the data access object
 */
public abstract class GenericHibernateDAOTest <DO extends GenericDataObject, DAO extends GenericDataAccessObject<DO>> extends GenericDAOTest<DO, DAO>
{	

}
