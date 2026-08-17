package com.schoste.ddd.infrastructure.dal.v2.services;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.hibernate.Session;

import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;
import com.schoste.ddd.infrastructure.dal.v2.models.HibernateDO;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Example implementation of the {@link LazyLoader} interface for Hibernate DAOs.
 */
public class LazyLoaderImpl extends GenericLazyLoader<Integer, GenericDataObject>
{
    protected Function<Integer, GenericDataObject> conversionFunction;
    protected Session session;
    protected Stream<HibernateDO> dataStream;
    protected Iterator<HibernateDO> dsIterator;

    public LazyLoaderImpl(Function<Integer, GenericDataObject> conversionFunction, Session openSession)
    {
        super(conversionFunction);

        if (openSession == null) throw new IllegalArgumentException();
        if (!openSession.isOpen()) throw new IllegalArgumentException();

        this.session = openSession;

		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<HibernateDO> criteriaQuery = session.getCriteriaBuilder().createQuery(HibernateDO.class);
		Root<HibernateDO> root = criteriaQuery.from(HibernateDO.class);
		Predicate notDeleted = criteriaBuilder.isFalse(root.get("isDeleted"));

        this.dataStream = session.createQuery(criteriaQuery.select(root).where(notDeleted)).stream();
        this.dsIterator = this.dataStream.iterator();
    }

    @Override
    public void close()
    {
        if (this.dataStream != null) this.dataStream.close();
        if (this.session != null) this.session.close();
    }

    @Override
    public boolean tryAdvance(Consumer<? super GenericDataObject> action) 
    {
        if (!this.dsIterator.hasNext()) return false;

        GenericDataObject dataObject = this.dsIterator.next();

        action.accept(dataObject);

        return true;
    }

    @Override
    public long estimateSize()
    {
        return Long.MAX_VALUE;
    }
}
