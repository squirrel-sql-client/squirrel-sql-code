/*
 * Copyright (C) 2008 Michael Romankiewicz
 * mirommail(at)web.de
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
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui;

/**
 * @author Michael Romankiewicz
 *
 */
public class FirebirdManagerGrantDbObject {
	private String name;
	private String owner;
	private String description;
	
	public FirebirdManagerGrantDbObject(String name,
			String owner, String description) {
		this.name = name;
		this.owner = owner;
		this.description = description;
	}

	/**
	 * @return name of the database object
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name name of the database object
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return owner of the database object
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner owner of the database object
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return description of the database object
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description description of the database object
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
