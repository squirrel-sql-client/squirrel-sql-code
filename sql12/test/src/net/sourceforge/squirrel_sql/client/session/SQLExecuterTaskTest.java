package net.sourceforge.squirrel_sql.client.session;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terdims of the GNU Lesser General Public
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
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.sql.Connection;

import junit.framework.TestCase;
import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

public class SQLExecuterTaskTest extends BaseSQuirreLTestCase {

    private ISession session = null;
    private IQueryTokenizer tokenizer = null;
    private ISQLDatabaseMetaData md = null;
    private ISQLDriver driver = null;
    private Connection con = null;
    private SQLConnection sqlCon = null;
    
    
    protected void setUp() throws Exception {
        super.setUp();
        session = createMock(ISession.class);
        tokenizer = createMock(IQueryTokenizer.class);
        md = createMock(ISQLDatabaseMetaData.class);
        con = createMock(Connection.class);
        driver = createMock(ISQLDriver.class);
        sqlCon = new SQLConnection(con, null, driver);
        session.setQueryTokenizer(tokenizer);
        expect(session.getMetaData()).andReturn(md);
        expect(session.getSQLConnection()).andReturn(sqlCon);
        expect(session.getQueryTokenizer()).andReturn(tokenizer);
        replay(session);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNullSQL() {
        SQLExecuterTask task = new SQLExecuterTask(session, null, null, null);
        task.run();
    }
}
