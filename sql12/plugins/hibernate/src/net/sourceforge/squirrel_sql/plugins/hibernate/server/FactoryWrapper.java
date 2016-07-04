package net.sourceforge.squirrel_sql.plugins.hibernate.server;

public class FactoryWrapper
{
   private Object _sessionFactory;
   private Object _entityManagerFactory;

   public FactoryWrapper(Object sessionFactory)
   {
      _sessionFactory = sessionFactory;
   }

   public Object getSessionFactory()
   {
      return _sessionFactory;
   }

   public Object getEntityManagerFactory()
   {
      return _entityManagerFactory;
   }

   public void setEntityManagerFactory(Object entityManagerFactory)
   {
      _entityManagerFactory = entityManagerFactory;
   }
}
