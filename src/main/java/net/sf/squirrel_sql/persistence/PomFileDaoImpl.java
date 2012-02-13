package net.sf.squirrel_sql.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PomFileDaoImpl implements PomFileDao
{
	private static final Logger log = LoggerFactory.getLogger(PomFileDaoImpl.class);

	@PersistenceContext
	EntityManager em;

	private static String groupArtifactQuery =
		"SELECT p FROM PomFileImpl p WHERE p.projectGroupId = :groupId AND p.projectArtifactId = :artifactId";

	/**
	 * @see net.sf.squirrel_sql.persistence.PomFileDao#insertPom(net.sf.squirrel_sql.persistence.PomFileImpl)
	 */
	@Transactional
	public void insertPom(PomFile pomFile)
	{
		em.persist(pomFile);
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomFileDao#findByGroupIdAndArtifactId(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	@Transactional
	public PomFile findByGroupIdAndArtifactId(String groupId, String artifactId)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Searching for PomFile using groupId=" + groupId + " and artifactId=" + artifactId);
		}
		Query query = em.createQuery(groupArtifactQuery);
		query.setParameter("groupId", groupId);
		query.setParameter("artifactId", artifactId);
		PomFile result = null;
		try
		{
			result = (PomFile) query.getSingleResult();
		}
		catch (Exception e)
		{
			if (log.isTraceEnabled()) {
				log.error("Couldn't locate PomFile using groupId=" + groupId + " and artifactId=" + artifactId
					+ ": " + e.getMessage(), e);				
			}
			else if (log.isDebugEnabled())
			{
				log.error("Couldn't locate PomFile using groupId=" + groupId + " and artifactId=" + artifactId
					+ ": " + e.getMessage());
			}
		}
		return result;
	}
}
