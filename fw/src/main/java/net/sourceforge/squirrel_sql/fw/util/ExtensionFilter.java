package net.sourceforge.squirrel_sql.fw.util;

/*
 * Copyright (C) 2004 Ryan Walberg <generalpf@yahoo.com>
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

import javax.swing.filechooser.FileFilter;
import java.util.ArrayList;
import java.lang.StringBuffer;

public class ExtensionFilter extends FileFilter {
    
    /* these arrays are parallel. */
    private ArrayList<String> _descriptions;
    private ArrayList<String> _extensions;
    
    /** Creates a new instance of ExtensionFilter */
    public ExtensionFilter() {
        _descriptions = new ArrayList<String>();
        _extensions = new ArrayList<String>();
    }
    
    public void addExtension(String description, String extension) {
        _descriptions.add(description);
        _extensions.add(extension);
    }
    
    public boolean accept(java.io.File f) {
        if (f.isDirectory())
            return true;
        for (int i = 0; i < _extensions.size(); i++) {
            String ext = _extensions.get(i);
            if (f.getName().endsWith("." + ext))
                return true;
        }
        return false;
    }
    
    public String getDescription() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < _extensions.size(); i++) {
            buf.append(_descriptions.get(i));
            buf.append(" (*.");
            buf.append(_extensions.get(i));
            buf.append(")");
            if (i < _extensions.size() - 1)
                buf.append("; ");
        }
        return buf.toString();
    }
    
}
