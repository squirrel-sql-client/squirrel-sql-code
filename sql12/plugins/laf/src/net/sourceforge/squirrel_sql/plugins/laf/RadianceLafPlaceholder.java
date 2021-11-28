package net.sourceforge.squirrel_sql.plugins.laf;

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

/**
 * This is simply a placeholder class that allows SQuirreL to populate the LAF
 * chooser with the name "Substance". This is done because the Substance LAF
 * doesn't have one skinnable LAF class that identifies itself as "Substance",
 * but rather defines a LAF class per "skin", each LAF class identifying itself
 * by it's skin name. Since there are many of these, they would clutter up the
 * LAF chooser if we listed them in LAFPluginResources.properties. So, rather
 * than display a particular Substance LAF class to the user, this LAF sub-class
 * has a generic name of "Substance". Even though technically, the "Autumn" LAF
 * is the only one used, it is fine, because "Autumn" will be the first skin
 * displayed in the skin chooser, when the Substance LAF is chosen. Any change
 * to the skin chooser will result in the Autumn LAF's setSkin method being
 * called, transforming it into the LAF representing the chosen skin.
 */
public class RadianceLafPlaceholder extends PlaceholderLookAndFeel {

	public RadianceLafPlaceholder() {
		super("Radiance");
	}

}
