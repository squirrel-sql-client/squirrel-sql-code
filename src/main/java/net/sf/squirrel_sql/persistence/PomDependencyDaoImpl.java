package net.sf.squirrel_sql.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomDependencyDaoImpl implements PomDependencyDao
{
	private static final Logger log = LoggerFactory.getLogger(PomDependencyDaoImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependencyDao#insertDependency(net.sf.squirrel_sql.DependencyImpl)
	 */
	@Override
	public void insertDependency(PomDependency d) {
		if (log.isDebugEnabled()) {
			log.debug("Persisting pom dependency: "+d);
		}
		em.persist(d);
	}
}
