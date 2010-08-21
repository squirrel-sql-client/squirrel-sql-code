/*
 * Copyright (C) 2009 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.netezza.tab;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.codereformat.ICodeReformator;

/**
 * The goal of this class is to format the stored procedure source code which comes from Netezza. For example,
 * this nicely formatted stored procedure: <code>
 * CREATE OR REPLACE PROCEDURE num() RETURNS BOOL LANGUAGE NZPLSQL AS
 * BEGIN_PROC
 * DECLARE
 * 	n NUMERIC; 
 * BEGIN
 * 	n := 2147483647;
 * 	RAISE NOTICE 'n is %', n;
 * 	n := 2147483647 + 1;
 * 	RAISE NOTICE 'n is %', n;
 * 	n := 2147483647::numeric + 1;
 * 	RAISE NOTICE 'n is %', n;
 * 	n := 2147483647::bigint + 1;
 * 	RAISE NOTICE 'n is %', n;
 * 	n := 2147483647;
 * 	n := n + 1;
 * 	RAISE NOTICE 'n is %', n;
 * 	END;
 * END_PROC;
 * 
 * comes out of the "proceduresource" column of the _v_procedure system table looking like this:
 * 
 * CREATE OR REPLACE PROCEDURE num() RETURNS BOOL LANGUAGE NZPLSQL AS BEGIN_PROC /nDECLARE/n	n NUMERIC;
 * BEGIN/n	n := 2147483647;RAISE NOTICE 'n is %', n;n := 2147483647 + 1;RAISE NOTICE 
 * 'n is %', n;n := 2147483647::numeric + 1;RAISE NOTICE 'n is %', n;n := 2147483647::bigint + 1;RAISE NOTICE 
 * 'n is %', n;n := 2147483647;n := n + 1;RAISE NOTICE 'n is %', n;END;END_PROC;
 * 
 * (Just one long line)
 */
public class NetezzaProcedureFormator implements ICodeReformator
{

	private final String stmtSep;
	
	public NetezzaProcedureFormator(String stmtSep) {
		this.stmtSep = stmtSep;
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.codereformat.ICodeReformator#reformat(java.lang.String)
	 */
	@Override
	public String reformat(String in)
	{
		StringBuilder result = new StringBuilder();
		String newLinesStripped = in.replace("/n", " ");
		List<String> parts = breakApartNewLines(newLinesStripped.split("\\s+"), stmtSep);
		boolean foundAs = false;
		boolean inDeclaration = false;
		boolean inMainSection = false;
		boolean inCreateSection = true;
		for (String part : parts)
		{
			if (inCreateSection && !part.equalsIgnoreCase("AS"))
			{
				result.append(part);
				result.append(" ");
				continue;
			}
			if (part.equalsIgnoreCase("AS") && !foundAs)
			{
				foundAs = true;
				inCreateSection = false;
				result.append("AS\n");
				continue;
			}
			if (part.equalsIgnoreCase("BEGIN_PROC") && !inMainSection)
			{
				result.append("BEGIN_PROC\n");
				continue;
			}
			if (part.equalsIgnoreCase("DECLARE"))
			{
				result.append("DECLARE\n");
				inDeclaration = true;
				continue;
			}
			if (inDeclaration && !part.equalsIgnoreCase("BEGIN"))
			{
				if (!part.endsWith(stmtSep))
				{
					result.append("\t");
				}
				result.append(part);
				if (part.endsWith(stmtSep))
				{
					result.append("\n");
				}
				else
				{
					result.append(" ");
				}
				continue;
			}
			if (inDeclaration && part.equalsIgnoreCase("BEGIN"))
			{
				result.append("BEGIN\n\t");
				inDeclaration = false;
				inMainSection = true;
				continue;
			}
			if (inMainSection)
			{
				if (part.equalsIgnoreCase("END"+stmtSep) 
					|| part.equalsIgnoreCase("END"))
				{
					inMainSection = false;
					// remove the previously added tab character
					result.setLength(result.length() - 1);
					result.append(part);
					if (!part.endsWith(stmtSep)) {
						result.append(stmtSep);
					}
					result.append("\n");
				}
				else
				{
					result.append(part);
					result.append(" ");
					if (part.endsWith(stmtSep))
					{
						result.append("\n\t");
					}
				}
				continue;
			}
			result.append(part);
			result.append(stmtSep);
			result.append("\n");
		}
		return result.toString();
	}

	private List<String> breakApartNewLines(String[] in, String stmtSep)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (String part : in)
		{
			if (part.contains(stmtSep))
			{
				String[] subparts = part.split(stmtSep);
				int i = 1;
				for (String subpart : subparts)
				{
					if (i++ < subparts.length)
					{
						result.add(subpart + stmtSep);
					}
					else
					{
						result.add(subpart);
					}
				}
			}
			else
			{
				result.add(part);
			}
		}
		return result;
	}
}
