package net.sourceforge.squirrel_sql.client.update.gui.installer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

public class PreLaunchHelperFactoryImpl implements PreLaunchHelperFactory
{

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchHelperFactory#createPreLaunchHelper()
	 */
	public PreLaunchHelper createPreLaunchHelper() {
		String[] appCtx = new String[] {
			"classpath:net/sourceforge/squirrel_sql/fw/util/net.sourceforge.squirrel_sql.fw.util.applicationContext.xml",
			"classpath:net/sourceforge/squirrel_sql/client/update/gui/installer/net.sourceforge.squirrel_sql.client.update.gui.installer.applicationContext.xml",
			"classpath:net/sourceforge/squirrel_sql/client/update/gui/installer/event/net.sourceforge.squirrel_sql.client.update.gui.installer.event.applicationContext.xml",
			"classpath:net/sourceforge/squirrel_sql/client/update/gui/installer/util/net.sourceforge.squirrel_sql.client.update.gui.installer.util.applicationContext.xml",
			"classpath:net/sourceforge/squirrel_sql/client/update/util/net.sourceforge.squirrel_sql.client.update.util.applicationContext.xml"
		};
				
		ApplicationContext ctx = new ClassPathXmlApplicationContext(appCtx);
		return (PreLaunchHelper)ctx.getBean(PreLaunchHelper.class.getName());
	}
	
}
