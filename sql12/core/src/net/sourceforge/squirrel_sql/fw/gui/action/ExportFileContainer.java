/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action;

import java.util.HashSet;
import java.util.Set;
import java.io.File;

/**
 * This class stores all target files of currently running exports.
 * @author Stefan Willinger
 *
 */
public class ExportFileContainer {
	private static ExportFileContainer instance;
	
	private Set<File> fileSet = new HashSet<File>();
	
	private ExportFileContainer(){
		
	}
	
	
	public static synchronized ExportFileContainer getInstance(){
		if(instance == null){
			instance = new ExportFileContainer();
		}
		return instance;
	}
	
	public synchronized boolean add(File file){
		return fileSet.add(file);
	}
	
	public synchronized boolean remove(File file){
		return fileSet.remove(file);
	}
}
