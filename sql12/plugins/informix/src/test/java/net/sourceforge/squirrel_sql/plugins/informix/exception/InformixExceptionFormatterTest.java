package net.sourceforge.squirrel_sql.plugins.informix.exception;

import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.util.AbstractExceptionFormatterTest;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;


/*
 * Copyright (C) 2010 Rob Manning
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

@RunWith(MockitoJUnitRunner.class)
public class InformixExceptionFormatterTest extends AbstractExceptionFormatterTest
{

	@Mock
	private ISession mockSession;
	
	@Mock
	private ISQLEntryPanel mockIsqlEntryPanel;

	@Mock
	private ISQLPanelAPI mockPanelAPI;

	@Mock
	private IApplication mockApplication;
	
	@Mock
	private SessionManager mockSessionManager;
	
	@Override
	protected ExceptionFormatter getExceptionFormatterToTest() throws Exception
	{
		when(mockSession.getSQLPanelAPIOfActiveSessionWindow()).thenReturn(mockPanelAPI);
		when(mockPanelAPI.getSQLEntryPanel()).thenReturn(mockIsqlEntryPanel);
		when(mockSession.getApplication()).thenReturn(mockApplication);
		when(mockApplication.getSessionManager()).thenReturn(mockSessionManager);
		return new InformixExceptionFormatter(mockSession);
	}

}
