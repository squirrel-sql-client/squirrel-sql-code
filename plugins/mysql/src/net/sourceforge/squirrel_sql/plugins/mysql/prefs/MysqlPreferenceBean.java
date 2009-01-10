package net.sourceforge.squirrel_sql.plugins.mysql.prefs;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.preferences.BaseQueryTokenizerPreferenceBean;

/**
 * A bean class to store preferences for the Oracle plugin.
 */
public class MysqlPreferenceBean extends BaseQueryTokenizerPreferenceBean implements Cloneable, Serializable
{

	static final long serialVersionUID = 5818886723165356478L;

	static final String UNSUPPORTED = "Unsupported";

	public MysqlPreferenceBean()
	{
		super();
		statementSeparator = ";";
		procedureSeparator = "|";
		lineComment = "--";
		removeMultiLineComments = false;
		installCustomQueryTokenizer = true;
	}

}
