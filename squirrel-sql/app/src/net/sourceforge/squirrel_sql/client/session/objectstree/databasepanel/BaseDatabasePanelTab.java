package net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel;
/*
 * Copyright (C) 2001 Colin Bell
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
import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.BaseObjectPanelTab;

/**
 * Base class for tabs to the added to <TT>DatabasePanel</TT>. If you are
 * writing a class for a tab to be added to <TT>DatabasePanel</TT> you don't
 * have to inherit from this class (only implement <TT>IDatabasePanelTab</TT>)
 * but it has convenience methods.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseDatabasePanelTab extends BaseObjectPanelTab implements IDatabasePanelTab {
}
