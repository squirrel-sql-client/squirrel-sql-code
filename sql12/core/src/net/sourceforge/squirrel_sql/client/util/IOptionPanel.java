package net.sourceforge.squirrel_sql.client.util;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
/**
 * This interface speicifes the behaviour expected of an option panel.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IOptionPanel
{
	/**
	 * User has clicked OK or otherwise indicated that their changes
	 * should be saved.
	 */
	void applyChanges();

	/**
	 * Retrieve the title for this option panel.
	 *
	 * @return	Panel's title.
	 */
	String getTitle();

	/**
	 * Retrieve the hint to be used for a tooltip for this option panel.
	 *
	 * @return	Panel's hint.
	 */
	String getHint();

	/**
	 * Retrieve the actual component to display as the option panel.
	 *
	 * @return		The component.
	 */
	Component getPanelComponent();
}
