package net.sourceforge.squirrel_sql.fw.datasetviewer;

/**
 * This is to give some fw classes access to informations
 * that belong to a Session.
 *
 * Care should be taken that this class interface does
 * reveal only appropriate information of a session.
 */
public interface IDataModelImplementationDetails
{
   String getStatementSeparator();
}
