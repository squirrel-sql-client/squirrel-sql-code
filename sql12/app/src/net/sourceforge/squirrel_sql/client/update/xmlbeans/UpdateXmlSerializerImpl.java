/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;

/**
 * This class contains utility methods that can be used to read from and write
 * to files containing release xml beans. The decision to use Java's XML beans
 * support over nanoxml was difficult. If I go with nanoxml, I believe I would
 * need a beaninfo class and some data structures might not work. Also, if we
 * want to eliminate dependency on nanoxml, using the inherent capabilities in
 * Java 5 seemed to be a wiser approach.
 * 
 * @author manningr
 */
public class UpdateXmlSerializerImpl implements UpdateXmlSerializer {

   /** Allows for encoding enums. */
   private EnumPersistenceDelegate enumDelegate = new EnumPersistenceDelegate();
   
   private IOUtilities _iou = new IOUtilitiesImpl();
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer#write(net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean, java.lang.String)
    */
   public void write(ChannelXmlBean channelBean, String filename)
         throws FileNotFoundException {
      XMLEncoder os = getXmlEncoder(filename);
      os.writeObject(channelBean);
      os.close();
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer#write(net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean, java.lang.String)
    */
   public void write(ChangeListXmlBean changeBean, String filename)
         throws FileNotFoundException {
      XMLEncoder os = getXmlEncoder(filename);
      os.writeObject(changeBean);
      os.close();
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer#write(net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean, net.sourceforge.squirrel_sql.fw.util.FileWrapper)
    */
   public void write(ChangeListXmlBean changeBean, FileWrapper file)
         throws FileNotFoundException {
      XMLEncoder os = getXmlEncoder(file.getAbsolutePath());
      os.writeObject(changeBean);
      os.close();
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer#readChannelBean(java.lang.String)
    */
   public ChannelXmlBean readChannelBean(String filename) throws FileNotFoundException,
         IOException {
      if (filename == null) {
         throw new IllegalArgumentException("filename cannot be null");
      }
      return readChannelBean(new FileInputStream(filename));
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer#readChannelBean(net.sourceforge.squirrel_sql.fw.util.FileWrapper)
    */
   public ChannelXmlBean readChannelBean(FileWrapper fileWrapper)  throws FileNotFoundException,
   	IOException {
   	ChannelXmlBean result = null;
   	BufferedInputStream is = null;
   	try {
	   	is = new BufferedInputStream(new FileInputStream(fileWrapper.getAbsolutePath()));
	      result = readChannelBean(is);
   	} finally {
   		_iou.closeInputStream(is);
   	}
   	return result;
   }
   
   /**
    * @see net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer#readChannelBean(java.io.InputStream)
    */
   public ChannelXmlBean readChannelBean(InputStream is) throws IOException {
      XMLDecoder bis = null;
      Object result = null;
      try {
         bis = new XMLDecoder(new BufferedInputStream(is));
         result = bis.readObject();
      } finally {
         if (is != null) {
            is.close();
         }
      }
      return (ChannelXmlBean) result;
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer#readChangeListBean(net.sourceforge.squirrel_sql.fw.util.FileWrapper)
    */
   public ChangeListXmlBean readChangeListBean(FileWrapper file)
         throws FileNotFoundException {
      XMLDecoder bis = null;
      FileInputStream fis = null;
      Object result = null;
      try {
         fis = new FileInputStream(new File(file.getAbsolutePath()));
         bis = new XMLDecoder(new BufferedInputStream(fis));
         result = bis.readObject();
      } finally {
         _iou.closeInputStream(fis);
         if (bis != null) {
            bis.close();
         }
      }
      return (ChangeListXmlBean) result;      
   }
   
   private XMLEncoder getXmlEncoder(String filename)
         throws FileNotFoundException {
      XMLEncoder result = null;
      BufferedOutputStream os = 
         new BufferedOutputStream(new FileOutputStream(filename));
      result = new XMLEncoder(os);
      result.setPersistenceDelegate(ArtifactAction.class, enumDelegate);
      return result;
   }
}
