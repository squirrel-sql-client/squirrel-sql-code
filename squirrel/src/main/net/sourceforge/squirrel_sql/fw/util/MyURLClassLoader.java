package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MyURLClassLoader extends URLClassLoader {
    private Map _classes = new HashMap();

    public MyURLClassLoader(String fileName) throws IOException {
        this(new File(fileName).toURL());
    }

    public MyURLClassLoader(URL url) {
        this(new URL[] {url});
    }

    public MyURLClassLoader(URL[] urls) {
        super(urls);
    }

    public Class loadClass(String className) throws ClassNotFoundException {
        Class cls = (Class)_classes.get(className);
        if (cls == null) {
            cls = super.loadClass(className);
            classHasBeenLoaded(cls);
            _classes.put(className, cls);
        }
        return cls;
    }

    public Class[] getAssignableClasses(Class type, Logger logger) throws IOException {
        List classes = new ArrayList();
        URL[] urls = getURLs();
        for (int i = 0; i < urls.length; ++i) {
            URL url = urls[i];
            File file = new File(url.getFile());
            if (!file.isDirectory() && file.exists() && file.canRead()) {
                ZipFile zipFile = null;
                try {
                    zipFile = new ZipFile(file);
                } catch (IOException ex) {
                    logger.showMessage(Logger.ILogTypes.ERROR, "Error occured trying to load: " + file.getAbsolutePath());
                    logger.showMessage(Logger.ILogTypes.ERROR, ex);
                }
                for (Enumeration en = zipFile.entries(); en.hasMoreElements();) {
                    Class cls = null;
                    String entryName = ((ZipEntry)en.nextElement()).getName();
                    String className = Utilities.changeFileNameToClassName(entryName);
                    if (className != null) {
                        try {
                            cls = loadClass(className);
                        } catch (Throwable th) {
//                          logger.showMessage(Logger.ILogTypes.ERROR, "Error loading class " + className);
//                          logger.showMessage(Logger.ILogTypes.ERROR, th);
                        }
                        if (cls != null) {
                            if (type.isAssignableFrom(cls)) {
                                classes.add(cls);
                            }
                        }
                    }
                }
            }
        }
        return (Class[])classes.toArray(new Class[classes.size()]);
    }

    protected Class findClass(String className) throws ClassNotFoundException {
        Class cls = (Class)_classes.get(className);
        if (cls == null) {
            cls = super.findClass(className);
            _classes.put(className, cls);
        }
        return cls;
    }

    protected void classHasBeenLoaded(Class cls) {
    }
}
