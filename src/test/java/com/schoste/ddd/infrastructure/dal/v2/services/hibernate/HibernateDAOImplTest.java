package com.schoste.ddd.infrastructure.dal.v2.services.hibernate;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.schoste.ddd.infrastructure.dal.v2.models.HibernateDO;
import com.schoste.ddd.infrastructure.dal.v2.services.HibernateDAO;

/**
 * Test class of the HibernateDAOImpl implementation
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 */
@ContextConfiguration(locations = {
		"file:src/test/resources/META-INF/module.xml",
		"file:src/test/resources/db-setup.xml",
		"file:src/test/resources/HibernateDAOImplTest.xml",
		})
@RunWith(SpringJUnit4ClassRunner.class)
public class HibernateDAOImplTest extends GenericHibernateDAOTest<HibernateDO, HibernateDAO>
{
	@Autowired
	protected HibernateDAO hibernateDAO;

	@Before
	public void clear() throws Exception
	{
		this.getDataAccessObject().clear();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HibernateDAO getDataAccessObject() 
	{
		return this.hibernateDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HibernateDO createDataObject(Object... parameters) 
	{
		HibernateDO dataObject = this.hibernateDAO.createDataObject();
		
		if ((parameters == null) || (parameters.length < 1)) return dataObject;	
		if (parameters.length > 0) dataObject.setId((Integer) parameters[0]);
		if (parameters.length <= 1) return dataObject;
		if (!(parameters[1] instanceof String)) throw new IllegalArgumentException("parameters");
		
		String callingMethod = (String) parameters[1];
		
		switch (callingMethod)
		{
			// This test method requires two identical DOs
			case "testSaveExisting":
				dataObject.setId(1);
				dataObject.setExampleStringProperty(String.format("%s%s", parameters[0], parameters[1]));
				break;
				
			default: dataObject.setExampleStringProperty((String) parameters[1]);
		}
		
		return dataObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modifyDataObject(HibernateDO dataObject, Object... parameters) 
	{
		if (dataObject == null) return;
		if ((parameters == null) || (parameters.length < 1)) return;	
		
		String exampleStringProperty = (String) parameters[0];
		
		dataObject.setExampleStringProperty(exampleStringProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HibernateDO getExistingDataObject(int id)
	{
		try
		{
			return this.getDataAccessObject().get(id);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean clearRepositorySucceeded() throws Exception 
	{
		this.getDataAccessObject().clear();
		
		return true;
	}

	@Override
	protected boolean assertDefaultGetListenersBeforeGet(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultGetListenersAfterGet(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultSaveListenersBeforeSave(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultSaveListenersAfterSave(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultDeleteListenersBeforeDelete(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultDeleteListenersAfterDelete(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean assertDefaultReloadListenersAfterReload(int numOfexpectedDOs) {
		// TODO Auto-generated method stub
		return true;
	}
}
