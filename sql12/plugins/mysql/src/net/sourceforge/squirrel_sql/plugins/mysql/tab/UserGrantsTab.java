package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/*
 * Copyright (C) 2003 Colin Bell
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
/**
 * This tab will display the results of an "SHOW TABLE STATUS" command for a database.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class UserGrantsTab extends BaseSQLTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(UserGrantsTab.class);


	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[mysql.grants=MySQL Grants]
		String TITLE = s_stringMgr.getString("mysql.grants");
		// i18n[mysql.hintGrants=(MySQL) Grants]
		String HINT = s_stringMgr.getString("mysql.hintGrants");
	}

	public UserGrantsTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected String getSQL()
	{
		final String db = getDatabaseObjectInfo().getQualifiedName();
        //String newdb = fixQuotes(db);
        //System.out.println("show grants for "+newdb);
		//return "show grants for "+newdb;
        return "show grants for " + db;
	}
    
    private static String fixQuotes(String user) {
        String[] parts = user.split("\\@");
        String first = "";
        if (parts[0].length() > 1) {
            first = parts[0] + "'";
        } else {
            first = "'%'";
        }
        String last = "";
        if (parts[1].length() > 1) {
            last = "'" + parts[1];
        } else {
            last = "'%'";
        }
        return first + "@" + last;
        
    }
    
    public static void main(String[] args) {
        String newString = fixQuotes("'root@'");
        System.out.println("fixQuotes(@localhost)="+newString);
    }
}
