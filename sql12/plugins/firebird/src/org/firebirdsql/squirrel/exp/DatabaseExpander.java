package org.firebirdsql.squirrel.exp;

import java.util.ArrayList;
import java.util.List;

import org.firebirdsql.squirrel.FirebirdPlugin;
import org.firebirdsql.squirrel.IObjectTypes;
import org.firebirdsql.squirrel.util.IndexParentInfo;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

public class DatabaseExpander implements INodeExpander {
    /** Logger for this class. */
//  private static ILogger s_log =
//      LoggerController.createLogger(DatabaseExpander.class);

    private FirebirdPlugin _plugin;
    
    public DatabaseExpander(FirebirdPlugin plugin) {
        this._plugin = plugin;
    }
    
    /**
     * Create the child nodes for the passed parent node and return them. Note
     * that this method should <B>not</B> actually add the child nodes to the
     * parent node as this is taken care of in the caller.
     *
     * @param   session Current session.
     * @param   node    Node to be expanded.
     *
     * @return  A list of <TT>ObjectTreeNode</TT> objects representing the child
     *          nodes for the passed node.
     */
    public List createChildren(ISession session, ObjectTreeNode parentNode)
    {
        final List childNodes = new ArrayList();
        final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();

        final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
        final String catalogName = parentDbinfo.getCatalogName();
        final String schemaName = parentDbinfo.getSimpleName();
        
        DatabaseObjectInfo dboInfo;
        ObjectTreeNode node;
        /*
        // Users.
        dboInfo = new DatabaseObjectInfo(null, null, "USERS",
                                            IObjectTypes.USER_PARENT, md);
        node  = new ObjectTreeNode(session, dboInfo);
        childNodes.add(node);
        */

        /*
        // Instances.
        dboInfo = new DatabaseObjectInfo(null, null, "INSTANCES",
                                            IObjectTypes.INSTANCE_PARENT, md);
        node = new ObjectTreeNode(session, dboInfo);
        childNodes.add(node);
        */

        /*
        // Sessions.
        dboInfo = new DatabaseObjectInfo(null, null, "SESSIONS",
                                            IObjectTypes.SESSION_PARENT, md);
        node = new ObjectTreeNode(session, dboInfo);
        childNodes.add(node);
        */

        IDatabaseObjectInfo seqInfo = new DatabaseObjectInfo(catalogName,
                schemaName, "GENERATORS", IObjectTypes.GENERATOR_PARENT, md);
        node = new ObjectTreeNode(session, seqInfo);
        node.addExpander(new GeneratorParentExpander(_plugin));
        childNodes.add(node);
        
        seqInfo = new DatabaseObjectInfo(catalogName,
                schemaName, "DOMAINS", IObjectTypes.DOMAIN_PARENT, md);
        node = new ObjectTreeNode(session, seqInfo);
        node.addExpander(new DomainParentExpander(_plugin));
        childNodes.add(node);

        seqInfo = new IndexParentInfo(null, md);
        node = new ObjectTreeNode(session, seqInfo);
        childNodes.add(node);

//        seqInfo = new DatabaseObjectInfo(catalogName, schemaName, "VIEW", IObjectTypes.VIEW_PARENT, md);
//        node = new ObjectTreeNode(session, seqInfo);
//        node.addExpander(new ViewParentExpander(_plugin));
//        childNodes.add(node);
        
        return childNodes;
    }

}
