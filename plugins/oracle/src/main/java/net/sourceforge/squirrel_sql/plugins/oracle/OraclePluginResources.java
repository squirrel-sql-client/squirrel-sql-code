package net.sourceforge.squirrel_sql.plugins.oracle;
/*
 * Copyright (C) 2004 Jason Height
 * jmheight@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

public class OraclePluginResources extends PluginResources
{
	public OraclePluginResources(String rsrcBundleBaseName, IPlugin plugin)
	{
		super(rsrcBundleBaseName, plugin);
	}

   public interface IKeys
   {
      String USERS_IMAGE = "users";
      String FUNCTION_IMAGE = "function";
      String FUNCTIONS_IMAGE = "functions";
      String PACKAGE_IMAGE = "package";
      String PACKAGES_IMAGE = "packages";
      String SESSION_IMAGE = "session";
      String SESSIONS_IMAGE = "sessions";

      String INSTANCE_IMAGE = "instance";
      String INSTANCES_IMAGE = "instances";

      String TYPE_IMAGE = "datatype";
      String TYPES_IMAGE = "datatypes";
      String CONSUMERGROUPS_IMAGE = "consumergroups";
      String INDEXES_IMAGE = "indexes";
      String LOBS_IMAGE = "lobs";
      String SQUENCES_IMAGE = "sequences";
      String TRIGGERS_IMAGE = "triggers";
      String CONSTRAINTS_IMAGE = "constraints";
      String CONSUMERGROUP_IMAGE = "consumergroup";
      String LOB_IMAGE = "lob";
      String CONSTRAINT_IMAGE = "constraint";
   }
}
