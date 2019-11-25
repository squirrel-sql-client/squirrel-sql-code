package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2002-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.Component;

import net.sourceforge.squirrel_sql.client.session.ObjectTreePosition;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This is the tab that contains the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeTab extends BaseMainPanelTab
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectTreeTab.class);
    
	/** Component to be displayed. */
	private ObjectTreePanel _comp;

	public ObjectTreeTab()
	{
	}

	/**
	 * @see IMainPanelTab#getTitle()
	 */
	public String getTitle()
	{
        // i18n[ObjectTreeTab.title=Objects]
		return s_stringMgr.getString("ObjectTreeTab.title");
	}

	/**
	 * @see IMainPanelTab#getHint()
	 */
	public String getHint()
	{
        // i18n[ObjectTreeTab.hint=Show database objects]
		return s_stringMgr.getString("ObjectTreeTab.hint");
	}

	/**
	 * Return the component to be displayed in this tab.
	 *
	 * @return	the component to be displayed in this tab.
	 */
	public synchronized Component getComponent()
	{
		if (_comp == null)
		{
			_comp = new ObjectTreePanel(getSession(), ObjectTreePosition.MAIN_SESSION_OBJECT_TREE);
		}
		return _comp;
	}

	@Override
	public SQLPanel getSqlPanelOrNull()
	{
		return null;
	}
}
