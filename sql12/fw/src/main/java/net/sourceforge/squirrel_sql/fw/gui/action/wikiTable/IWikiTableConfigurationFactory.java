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
package net.sourceforge.squirrel_sql.fw.gui.action.wikiTable;

import java.util.List;

/**
 * This factory provides all available configurations of {@link IWikiTableConfiguration}.
 * The available configurations are divided in
 * <li>Build-in configurations</li>
 * <li>User specific configurations</li>
 * Build-in configurations are read-only by convention.
 * These Factory is a singleton and each configuration provided by this factory, will be a copy of the original. 
 * So changes to a configuration does not affect the configurations cached in this factory. 
 * <P><b>Build-in configuration</b></P>
 * A Plugin may add some build-in configurations to the factory by calling  {@link #addBuilInConfiguration(List)}. 
 * <P><b>User specific configuration</b></P>
 * User specific configurations can be added by calling  {@link #replaceUserSpecificConfigurations(List)}. 
 * All current available user specific configurations will be replaced.
 * <P><b>Warning</b></P>
 * Be aware, that a name of a configuration must be unique over both, build-in and user specific configurations.
 * @author Stefan Willinger
 *
 */
public interface IWikiTableConfigurationFactory {

	/**
	 * Provides all available configurations.
	 * The returned instances are copies. So changes to a configuration will not affect the configurations cached by this factory. 
	 * Some configurations maybe read-only.
	 * The returned list is sorted by the name of the configuration.
	 * @return A list with copies of all available configurations.
	 */
	public abstract List<IWikiTableConfiguration> getConfigurations();

	/**
	 * Provides all available user specific configurations.
	 * The returned instances are copies. So changes to a configuration will not affect the configurations cached by this factory. 
	 * Some configurations maybe read-only.
	 * The returned list is sorted by the name of the configuration.
	 * @return A list with copies of all available user specific configurations.
	 */
	public abstract List<IWikiTableConfiguration> getUserSpecificConfigurations();

	/**
	 * Provides all available build-in configurations.
	 * The returned instances are copies. So changes to a configuration will not affect the configurations cached by this factory. 
	 * These configurations are read-only.
	 * The returned list is sorted by the name of the configuration.
	 * @return A list with copies of all available build-in configurations.
	 */
	public abstract List<IWikiTableConfiguration> getBuildInConfigurations();

	/**
	 * Adds the additional build-in config to the configuration.
	 * The name of the provided config must be unique within the complete configuration.
	 * The additional configuration must be read-only.
	 * @param additionalBuildInConfig configuration to be added.
	 * @throws IllegalArgumentException if the configuration is not read-only
	 * @throws IllegalArgumentException if the name of the configuration is not unique
	 */
	public abstract void addBuilInConfiguration(IWikiTableConfiguration additionalBuildInConfig);
	
	/**
	 * Replaces the current build-in configurations with the new one.
	 * The name of each provided config must be unique within the complete configuration.
	 * Each provided configuration must be read-only.
	 * @param configurations the new build-in configurations.
	 * @throws IllegalArgumentException if a configuration is not read-only
	 * @throws IllegalArgumentException if the name of a configuration is not unique
	 * @see #addBuilInConfiguration(IWikiTableConfiguration)
	 */
	public abstract void replaceBuilInConfiguration(List<IWikiTableConfiguration> configurations);


	/**
	 * Replaces the user specific configuration with a new list.
	 * @param config the new config to use.
	 * @throws IllegalArgumentException if a name of a new config is not unique
	 * @throws IllegalArgumentException if a new config is read only.
	 * @see #addUserSpecificConfigurations(IWikiTableConfiguration)
	 */
	public abstract void replaceUserSpecificConfigurations(List<IWikiTableConfiguration> config);
	
	/**
	 * Adds a user specific configuration.
	 * @param config The configuration to add.
	 * @throws IllegalArgumentException if a name of a new config is not unique
	 * @throws IllegalArgumentException if a new config is read only.
	 */
	public abstract void addUserSpecificConfigurations(IWikiTableConfiguration config);

}