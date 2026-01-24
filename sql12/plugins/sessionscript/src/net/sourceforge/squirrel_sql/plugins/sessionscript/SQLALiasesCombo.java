package net.sourceforge.squirrel_sql.plugins.sessionscript;
/*
 * Copyright (C) 2002 Colin Bell
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

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import org.apache.commons.lang3.StringUtils;

/**
 * This <TT>JComboBox</TT> will display all aliases.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLALiasesCombo extends JComboBox
{

	/**
	 * Default ctor. Builds an empty combo box.
	 */
	public SQLALiasesCombo()
	{
		super();
	}

	public SQLAlias getSelectedSQLAlias()
	{
		return (SQLAlias)getSelectedItem();
	}

	/**
	 * Load control with all the aliases in the system.
	 */
	public void load(AliasScriptCache scriptsCache)
	{
		removeAllItems();

		List<SQLAlias> aliasListClone= new ArrayList<>(Main.getApplication().getAliasesAndDriversManager().getAliasList());

		aliasListClone.sort((a1, a2) -> compareAliases(scriptsCache, a1, a2));

		aliasListClone.forEach(a -> addItem(a));
	}

   private int compareAliases(AliasScriptCache scriptsCache, SQLAlias a1, SQLAlias a2)
   {
		if(false == StringUtils.isBlank(scriptsCache.get(a1).getSQL()) && StringUtils.isBlank(scriptsCache.get(a2).getSQL()))
		{
			return -1;
		}
		else if(StringUtils.isBlank(scriptsCache.get(a1).getSQL()) && false == StringUtils.isBlank(scriptsCache.get(a2).getSQL()))
		{
			return 1;
		}

		return StringUtils.compareIgnoreCase(a1.getName(), a2.getName());

   }
}
