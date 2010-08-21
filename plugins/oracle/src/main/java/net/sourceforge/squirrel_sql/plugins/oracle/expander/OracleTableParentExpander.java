package net.sourceforge.squirrel_sql.plugins.oracle.expander;
/*
 * Copyright (C) 2002-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableTypeExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;
/**
 * This class is an expander for Oracle table nodes which could potentially 
 * include flashback recycle bin tables.  This will exclude those tables if the
 * plugin is so configured.
 *
 * @author manningr
 */
public class OracleTableParentExpander extends TableTypeExpander 
{
    
    private OraclePreferenceBean _prefs = null;
    
	/**
	 * Default ctor.
	 */
	public OracleTableParentExpander(OraclePreferenceBean prefs)
	{
		super();
        _prefs = prefs;
	}

	/**
	 * Create the child nodes for the passed parent node and return them. Note
	 * that this method should <B>not</B> actually add the child nodes to the
	 * parent node as this is taken care of in the caller.
	 * 
	 * @param	session	Current session.
	 * @param	node	Node to be expanded.
	 * 
	 * @return	A list of <TT>ObjectTreeNode</TT> objects representing the child
	 *			nodes for the passed node.
	 */
    public List<ObjectTreeNode> createChildren(ISession session, 
                                               ObjectTreeNode parentNode) 
        throws SQLException
	{
		final List<ObjectTreeNode> childNodes = super.createChildren(session, parentNode);
        List<ObjectTreeNode> result = new ArrayList<ObjectTreeNode> ();
		for (ObjectTreeNode childNode : childNodes) {
            if (_prefs.isExcludeRecycleBinTables()) {
                if (!childNode.getUserObject().toString().startsWith("BIN$")) {
                    result.add(childNode);
                }                
            } else {
                result.add(childNode);
            }
        }
        return result;
	}
}
