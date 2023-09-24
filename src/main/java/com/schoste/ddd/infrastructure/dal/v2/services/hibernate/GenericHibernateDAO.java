package com.schoste.ddd.infrastructure.dal.v2.services.hibernate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;
import com.schoste.ddd.infrastructure.dal.v2.services.GenericDAO;
import com.schoste.ddd.infrastructure.dal.v2.services.GenericDataAccessObject;

/**
 * Version 1 implementation of the GenericDataAccessObject interface to persist data objects
 * via Hibernate.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 * @param <T> the class of the data object to persist
 */
public abstract class GenericHibernateDAO <T extends GenericDataObject> extends GenericDAO<T> implements GenericDataAccessObject<T> 
{
	protected abstract SessionFactory getSessionFactory();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void doDelete(T dataObject) throws Exception 
	{
		dataObject.setIsDeleted(true);
		
		if (dataObject.getId() <= 0) return;
		
		Session session = this.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		
		try
		{
			session.update(dataObject);
			session.delete(dataObject);
			
			transaction.commit();			
		}
		catch (Exception e)
		{
			transaction.rollback();
			
			throw e;
		}
		finally
		{
			session.flush();
			session.close();	
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDelete(Collection<T> dataObjects) throws Exception
	{
		Session session = this.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		try
		{
			for (T dataObject : dataObjects)
			{
				dataObject.setIsDeleted(true);
				
				if (dataObject.getId() <= 0) continue;

				session.update(dataObject);
				session.delete(dataObject);
			}

			transaction.commit();			
		}
		catch (Exception e)
		{
			transaction.rollback();
			
			throw e;
		}
		finally
		{
			session.flush();
			session.close();	
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doDelete(int[] dataObjectIds) throws Exception
	{
		Session session = this.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		try
		{
			for (int dataObjectId : dataObjectIds)
			{
				T dataObject = (T) session.load(this.getDataObjectClass(), dataObjectId);
				session.update(dataObject);
				session.delete(dataObject);
			}

			transaction.commit();			
		}
		catch (Exception e)
		{
			transaction.rollback();
			
			throw e;
		}
		finally
		{
			session.flush();
			session.close();	
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected synchronized T doGet(int id) throws Exception
	{
		Session session = this.getSessionFactory().openSession();
		
		T dataObject = (T) session.get(this.getDataObjectClass(), id);

		session.close();

		return dataObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected synchronized Collection<T> doGet(int[] ids) throws Exception 
	{
		Session session = this.getSessionFactory().openSession();
		Query query = null;
		Collection<T> dataObjects = null;

		if (ids == null)
		{
			query = session
				.createQuery(String.format("SELECT e FROM %s e WHERE (e.modifiedTimeStamp > :ts) AND (e.isDeleted = false)", this.getDataObjectClass().getSimpleName()))
				.setParameter("ts", this.latestModificationTimeStamp);
			
			dataObjects = (Collection<T>) query.list();
		}
		else
		{
			List<Integer> listOfIds = Arrays.stream(ids).boxed().collect(Collectors.toList());

			dataObjects = session.createCriteria(this.getDataObjectClass()).add(Restrictions.in("id", listOfIds)).list();			
		}

		session.close();

		this.updateLatestModificationDate(dataObjects);

		return dataObjects;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized Collection<T> doReloadAll() throws Exception
	{
		this.latestModificationTimeStamp = Integer.MIN_VALUE;
		
		return this.getAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void doSave(T dataObject) throws Exception 
	{
		if (dataObject.getId() < 0) dataObject.setId(0);

		Session session = this.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		
		try
		{
			session.saveOrUpdate(dataObject);
			transaction.commit();			
		}
		catch (Exception e)
		{
			transaction.rollback();
			
			throw e;
		}
		finally
		{
			session.flush();
			session.close();	
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void doSave(Collection<T> dataObjects) throws Exception
	{
		Session session = this.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		try
		{
			for (T dataObject : dataObjects)
			{
				if (dataObject.getId() < 0) dataObject.setId(0);

				session.saveOrUpdate(dataObject);
			}

			transaction.commit();							
		}
		catch (Exception e)
		{
			transaction.rollback();
			
			throw e;
		}
		finally
		{
			session.flush();
			session.close();	
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This method will only reset the time stamp!
	 */
	@Override
	protected void doClear() throws Exception 
	{
		this.latestModificationTimeStamp = Long.MIN_VALUE;
	}
}
