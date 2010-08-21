package net.sourceforge.squirrel_sql.plugins.dataimport.gui;

/*
 * Copyright (C) 2007 Thorsten Mürell
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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * @author Thorsten Mürell
 */
public enum SpecialColumnMapping {
	//i18n[SpecialColumnMapping.SKIP=Skip]
	SKIP,
	//i18n[SpecialColumnMapping.NULL=NULL]
	NULL,
	//i18n[SpecialColumnMapping.FIXED_VALUE=Fixed value]
	FIXED_VALUE,
	//i18n[SpecialColumnMapping.AUTO_INCREMENT=Auto increment]
	AUTO_INCREMENT;
	
	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(SpecialColumnMapping.class);
	
	public String getVisibleString() {
		return stringMgr.getString("SpecialColumnMapping." + this.name());
	}

}
