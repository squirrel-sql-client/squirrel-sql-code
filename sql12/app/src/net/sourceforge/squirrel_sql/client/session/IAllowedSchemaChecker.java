package net.sourceforge.squirrel_sql.client.session;

public interface IAllowedSchemaChecker
{
   String[] getAllowedSchemas(ISession session);
}
