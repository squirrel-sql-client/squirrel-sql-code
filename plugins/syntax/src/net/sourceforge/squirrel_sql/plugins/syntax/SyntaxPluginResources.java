package net.sourceforge.squirrel_sql.plugins.syntax;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

public final class SyntaxPluginResources extends PluginResources
{
	SyntaxPluginResources(IPlugin plugin)
	{
		super(SyntaxPluginResources.class.getName(), plugin);
	}

	public interface IKeys
	{
		String BACKGROUND_IMAGE = "Background";
		String BOLD_IMAGE = "Bold";
		String COLOR_SELECTOR_IMAGE = "ColorSelector";
		String FOREGROUND_IMAGE = "Foreground";
		String ITALIC_IMAGE = "Italic";
	}
}
