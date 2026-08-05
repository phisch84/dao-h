package com.schoste.ddd.infrastructure.dal.v2.services.hibernate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;
import com.schoste.ddd.infrastructure.dal.v2.services.GenericDAO;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Version 1 implementation of the GenericDataAccessObject interface to persist data objects
 * via Hibernate.
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 *
 * @param <T> the class of the data object to persist
 */
public abstract class GenericHibernateDAO <T extends GenericDataObject> extends GenericDAO<T>
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
			session.remove(dataObject);
			
			transaction.commit();			
		}
		catch (Exception e)
		{
			transaction.rollback();
			
			throw e;
		}
		finally
		{
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

				session.remove(dataObject);
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
			session.close();	
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDelete(int[] dataObjectIds) throws Exception
	{
		Session session = this.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		try
		{
			for (int dataObjectId : dataObjectIds)
			{
				T dataObject = this.createDataObject();

                session.load(dataObject, dataObjectId);
				session.remove(dataObject);
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
		Collection<T> dataObjects = null;
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(this.getDataObjectClass());
		Root<T> root = criteriaQuery.from(this.getDataObjectClass());
		Predicate greaterThanLastModified = criteriaBuilder.gt(root.get("modifiedTimeStamp"), this.latestModificationTimeStamp);
		Predicate notDeleted = criteriaBuilder.isFalse(root.get("isDeleted"));

		if (ids == null)
		{
			criteriaQuery.select(root).where(criteriaBuilder.and(greaterThanLastModified, notDeleted));			
			dataObjects = session.createQuery(criteriaQuery).list();
		}
		else
		{
			List<Integer> listOfIds = Arrays.stream(ids).boxed().collect(Collectors.toList());
			Predicate inListOfIds = root.get("id").in(listOfIds);

            criteriaQuery.select(root).where(criteriaBuilder.and(greaterThanLastModified, notDeleted, inListOfIds));
			dataObjects = session.createQuery(criteriaQuery).list();	
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
	@SuppressWarnings("unchecked")
	@Override
	protected synchronized void doSave(T dataObject) throws Exception 
	{
		if (dataObject.getId() < 0) dataObject.setId(0);

		Session session = this.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		
		try
		{
			long unixTs = System.currentTimeMillis();

            if (Objects.isNull(session.find(this.getDataObjectClass(), dataObject.getId())))
            {
		        dataObject.setCreatedTimeStamp(unixTs);
				dataObject.setModifiedTimeStamp(unixTs);

				session.persist(dataObject);
            } 
            else 
            {
				dataObject.setModifiedTimeStamp(unixTs);

				session.merge(dataObject);
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
			session.close();	
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected synchronized void doSave(Collection<T> dataObjects) throws Exception
	{
		Session session = this.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		try
		{
			long unixTs = System.currentTimeMillis();

			for (T dataObject : dataObjects)
			{
				if (dataObject.getId() < 0) dataObject.setId(0);

                if (Objects.isNull(session.find(this.getDataObjectClass(), dataObject.getId())))
                {
					dataObject.setCreatedTimeStamp(unixTs);
					dataObject.setModifiedTimeStamp(unixTs);

					session.persist(dataObject);
                } 
                else 
                {
					dataObject.setModifiedTimeStamp(unixTs);
	                dataObject = session.merge(dataObject);
                }
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