package com.schoste.ddd.infrastructure.dal.v2.services;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;

/**
 * Listener class for data objects which is used by Hibernate
 * 
 * @author Philipp Schosteritsch <s.philipp@schoste.com>
 */
public class GenericDataObjectListener implements PreInsertEventListener, PreUpdateEventListener
{
	private static final long serialVersionUID = -8040039880326528598L;

	/**
	 * Updates the modification and creation time fields of a data object
	 * before the data object is manipulated.
	 * 
	 * @param event event arguments (containing the entity to be manipulated)
	 * @return false if everything is okay (no-veto). True if something went wrong (veto against processing)
	 */
	@Override
	public boolean onPreUpdate(PreUpdateEvent event)
	{
		Object entity = event.getEntity();
		
		if (entity == null) return true;
		
		GenericDataObject dataObject = (GenericDataObject) entity;
		
		dataObject.updateTimeStamps();
		
		return false;
	}

	/**
	 * Updates the modification and creation time fields of a data object
	 * before the data object is manipulated.
	 * 
	 * @param event event arguments (containing the entity to be manipulated)
	 * @return false if everything is okay (no-veto). True if something went wrong (veto against processing)
	 */
	@Override
	public boolean onPreInsert(PreInsertEvent event) 
	{
		Object entity = event.getEntity();
		
		if (entity == null) return true;
		
		GenericDataObject dataObject = (GenericDataObject) entity;
		
		dataObject.updateTimeStamps();
		
		return false;
	}
}
