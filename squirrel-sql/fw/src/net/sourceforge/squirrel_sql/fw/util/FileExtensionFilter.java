package net.sourceforge.squirrel_sql.fw.util;
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
import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilter
				extends javax.swing.filechooser.FileFilter
				implements java.io.FileFilter, FilenameFilter {

	private String _description;
	private String[] _exts;

	public FileExtensionFilter(String description, String[] exts) {
		super();
		_exts = exts;
		StringBuffer buf = new StringBuffer(description);
		buf.append(" (");
		for (int i = 0; i < _exts.length; ++i) {
			buf.append("*").append(_exts[i]);
			if (i != (_exts.length - 1)) {
				buf.append(", ");
			}
		}
		buf.append(")");
		_description = buf.toString();
	}

	public boolean accept(File dir, String name) {
		return checkFileName(name.toLowerCase());
	}

	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		return checkFileName(file.getName().toLowerCase());
	}

	public String getDescription() {
		return _description;
	}

	private boolean checkFileName(String name) {
		for (int i = 0; i < _exts.length; ++i) {
			if (name.endsWith(_exts[i])) {
				return true;
			}
		}
		return false;
	}
}