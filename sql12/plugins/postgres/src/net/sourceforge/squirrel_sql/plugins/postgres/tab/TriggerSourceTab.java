package net.sourceforge.squirrel_sql.plugins.postgres.tab;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This class will display the source for an PostgreSQL trigger.
 *
 * @author manningr
 */
public class TriggerSourceTab extends PostgresSourceTab
{
	/** SQL that retrieves the source of a stored procedure. */
	private static String SQL =
        "select 'create trigger ' || t.trigger_name || ' ' || condition_timing " +
        "|| ' ' || v.manip || ' on ' || event_object_table " +
        "|| ' for each ' || action_orientation || ' ' || action_statement as trigdef " +
        "FROM information_schema.triggers t, " +
        "(select  trigger_schema, " +
        "        trigger_name, " +
        "        rtrim( " +
        "            max(case when pos=0 then manip else '' end)|| " +
        "            max(case when pos=1 then manip else '' end)|| " +
        "            max(case when pos=2 then manip else '' end), ' or ' " +
        "            ) as manip " +
        "from ( " +
        "    select a.trigger_schema, " +
        "           a.trigger_name, " +
        "           a.event_manipulation||' or ' as manip, " +
        "           d.cnt, " +
        "           a.rnk as pos " +
        "    from (  select trigger_name, " +
        "                   trigger_schema, " +
        "                   event_manipulation, " +
        "                   (select count(distinct is1.event_manipulation) " +
        "                    from information_schema.triggers is1 " +
        "                    where is2.event_manipulation < is1.event_manipulation) as rnk " +
        "            from information_schema.triggers is2 " +
        "          ) a, " +
        "         (select trigger_schema, trigger_name, count(event_manipulation) as cnt " +
        "          from ( " +
        "                select trigger_schema, " +
        "                       trigger_name, " +
        "                       event_manipulation, " +
        "                       (select count(distinct is3.event_manipulation) " +
        "                        from information_schema.triggers is3 " +
        "                        where is4.event_manipulation < is3.event_manipulation) as rnk " +
        "                from information_schema.triggers is4 " +
        "                ) y " +
        "          group by trigger_schema, trigger_name) d " +
        "    where d.trigger_name = a.trigger_name " +
        "    and d.trigger_schema = a.trigger_schema " +
        ") x " +
        "group by trigger_schema, trigger_name " +
        "order by 1) v " +
        "where t.trigger_schema = v.trigger_schema " +
        "and t.trigger_name = v.trigger_name " +
        "and t.trigger_schema = ? " +
        "and t.trigger_name = ? " +
        "group by trigdef ";
    
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerSourceTab.class);

	public TriggerSourceTab(String hint)
	{
		super(hint);
        sourceType = TRIGGER_TYPE;
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL: "+SQL);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("trigname="+doi.getSimpleName());
        }
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
