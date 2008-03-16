/*
 * Copyright (C) 2008 Dieter Engelhardt
 * dieter@ew6.org
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
package net.sourceforge.squirrel_sql.plugins.sqlreplace;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.session.MessagePanel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

/**
 * Manages replacements. Including loading and saving to a properties file.
 * 
 * @author Dieter
 *
 */
public class ReplacementManager {

	   /**
	    * The file to save/load replacements to/from
	    */
	   private File replacementFile;
	   private ArrayList<Replacement> replacements = new ArrayList<Replacement>();
	   private SQLReplacePlugin _plugin;
	   MessagePanel mpan;

	   private final static ILogger log = 
		       LoggerController.createLogger(SQLReplacePlugin.class);  

	 /**
	 * @param _plugin
	 */
	public ReplacementManager(SQLReplacePlugin _plugin) {
		try 
		{
			this._plugin = _plugin;
			replacementFile = new File(_plugin.getPluginUserSettingsFolder(), "sqlreplacement.xml");
			mpan = (MessagePanel)_plugin.getApplication().getMessageHandler();

		}
		catch (IOException e)
	    {
	         throw new RuntimeException(e);
	    }
	}
	   
	   /**
	    * Load the stored replacement.
	    */
	   protected void load() throws IOException
	   {
		  replacements.clear();
	      try
	      {
	         XMLBeanReader xmlin = new XMLBeanReader();

	         if (replacementFile.exists())
	         {
	            xmlin.load(replacementFile, getClass().getClassLoader());
	            for (Iterator<?> i = xmlin.iterator(); i.hasNext();)
	            {
	               Object bean = i.next();
	               if (bean instanceof Replacement)
	               {
	        		   replacements.add((Replacement)bean);
	               }
	            }
	         }
	      }
	      catch (XMLException e)
	      {
	         throw new RuntimeException(e);
	      }
	   }

	   /**
	    * Save the replacement.
	    */
	   protected void save()
	   {
	      try
	      {
	         XMLBeanWriter xmlout = new XMLBeanWriter();

	         for (Iterator<Replacement> i = replacements.iterator(); i.hasNext();)
	         {
	        	 Replacement rep = i.next();

	            xmlout.addToRoot(rep);
	         }

	         xmlout.save(replacementFile);
	      }
	      catch (Exception e)
	      {
	         throw new RuntimeException(e);
	      }
	   }

	   protected Iterator<Replacement> iterator()
	   {
	      return replacements.iterator();
	   }

	   public void removeAll()
	   {
		   replacements = new ArrayList<Replacement>();
	   }

	   /**
	    * Sets the content from the editor section and add it to the replacementarray
	    * @param content
	    */
	   public void setContentFromEditor(String content) 
	   {
		   String cont = content;
		   String[] lines = cont.split("\n");
           replacements.clear();
		   for (int i = 0; i < lines.length; i++)
		   {
			   if(lines[i] != null && lines[i].length() != 0)
			   {
				   String[] s = lines[i].split("=");
				   if(s[0] != null && s[0].length() > 0 && s[1] != null && s[1].length() > 0)
				   {
					   Replacement ro = new Replacement(s[0].trim(), s[1].trim());
					   replacements.add(ro);
				   }
			   }
		   }
		   this.save();
	   }
	   
	   /*
	    * 
	    */
	   public String getContent()
	   {
		   StringBuffer sb = new StringBuffer();
		   Iterator<Replacement> it = replacements.iterator();
		   while (it.hasNext())
		   {
			   Replacement r = it.next();
			   sb.append(r.toString() + "\n");
		   }
		   
		   return sb.toString();
	   }
	   
	   /**
	    * Here we replace the original SQL Statement with the Set of Vars from our ReplamentList
	    * @param buffer
	    * @return
	    */
	   public String replace(StringBuffer buffer )
	   {
		   String toReplace = buffer.toString();
		   Iterator<Replacement> it = replacements.iterator();
		   while (it.hasNext())
		   {
			   Replacement r = it.next();
			   if ( toReplace.indexOf(r.getVariable()) > 0)
			   {
				   log.info("Replace-Rule: " + r.toString());
				   mpan.showMessage("Replace-Rule: " + r.toString());
				   toReplace = toReplace.replaceAll(r.getRegexVariable(), r.getValue());
			   }
		   }
		
		   return toReplace;
	   }
}
