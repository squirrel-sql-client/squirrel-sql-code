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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * This factory provides all available configurations of {@link IWikiTableConfiguration}.
 * This is the default implementation for {@link IWikiTableConfiguration}
 * The internal data structure is divided into build-in and user-specific configurations. 
 * Squirrel's core does not provide any build-in configurations.
 * A plugin can add build-in configurations with {@link #addBuilInConfiguration(List)}. The plugin <b>WikiTableConfigurations<b> provides some configurations.
 * <p><b>Note</b></p>
 * All access methods are synchronized. This class can be used as a singleton in a multi-threaded environment.
 * @see IWikiTableConfiguration
 * @author Stefan Willinger
 *
 */
public class WikiTableConfigurationFactory implements IWikiTableConfigurationFactory {
	/**
	 * Build-in configurations.
	 */
	private  List<IWikiTableConfiguration> buildInConfigurations = null;
	
	/**
	 * User specific configurations
	 */
	private  List<IWikiTableConfiguration> userSpecificConfigurations = null;
	
	/**
	 * The instance.
	 */
	private static IWikiTableConfigurationFactory instance = null;
	
	/**
	 * Constructor for the Singleton.
	 * A Factory instantiated by this way, may contain some build-in configurations.
	 * This one is package protected for testing.
	 * Use {@link #instance} instead calling this constructor.
	 * @see #getInstance() 
	 */
	WikiTableConfigurationFactory(){
		loadConfigurations();
	}
	
	
	/**
	 * Initialize the configurations.
	 */
	private void loadConfigurations(){
		buildInConfigurations = new ArrayList<IWikiTableConfiguration>();
		userSpecificConfigurations = new ArrayList<IWikiTableConfiguration>();
		
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory#getConfigurations()
	 */
	@Override
	public synchronized   List<IWikiTableConfiguration> getConfigurations(){
		List<IWikiTableConfiguration> result = new ArrayList<IWikiTableConfiguration>();
		
		result.addAll(getBuildInConfigurations());
		result.addAll(getUserSpecificConfigurations());
		
		sortByName(result);
		return result;
	}


	/**
	 * Sorts the list with configurations by there name.
	 * @param result List, which should be sorted.
	 */
	private void sortByName(List<IWikiTableConfiguration> list) {
		// sort the collection by the name of a configuration
		Collections.sort(list, new Comparator<IWikiTableConfiguration>() {
			@Override
			public int compare(IWikiTableConfiguration o1, IWikiTableConfiguration o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}
	
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory#getUserSpecificConfigurations()
	 */
	@Override
	public synchronized   List<IWikiTableConfiguration> getUserSpecificConfigurations(){
		List<IWikiTableConfiguration> result = new ArrayList<IWikiTableConfiguration>();
		
		for (IWikiTableConfiguration config : userSpecificConfigurations) {
			result.add(config.clone());
		}
		
		sortByName(result);
		
		return result;
	}
	
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory#getBuildInConfigurations()
	 */
	@Override
	public synchronized  List<IWikiTableConfiguration> getBuildInConfigurations() {
		List<IWikiTableConfiguration> result = new ArrayList<IWikiTableConfiguration>();
		for (IWikiTableConfiguration config : buildInConfigurations) {
			result.add(config.clone());
		}
		sortByName(result);
		return result;
	}
	
	
	/**
	 * Get an instance of this factory.
	 * @return
	 */
	public static synchronized IWikiTableConfigurationFactory getInstance(){
		if(instance == null){
			instance = new WikiTableConfigurationFactory();
		}
		return instance;
	}

	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory#addBuilInConfiguration(java.util.List)
	 */
	@Override
	public synchronized void  addBuilInConfiguration(IWikiTableConfiguration additionalBuildInConfig){
		if(additionalBuildInConfig.isReadOnly() == false){
			throw new IllegalArgumentException("A not read-only configuration cannot be a build-in configuration!");
		}
		if(isNameUnique(additionalBuildInConfig)){
			this.buildInConfigurations.add(additionalBuildInConfig.clone());
		}else{
			throw new IllegalArgumentException("The name of the configuration " + additionalBuildInConfig.getName() + " is not unique!");
		}
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory#replaceBuilInConfiguration(java.util.List)
	 */
	@Override
	public synchronized void replaceBuilInConfiguration(List<IWikiTableConfiguration> configurations) {
		this.buildInConfigurations.clear();
		for (IWikiTableConfiguration config : configurations) {
			addBuilInConfiguration(config);
		}
	}
	
	/**
	 * @param aConfig
	 * @return
	 */
	private boolean isNameUnique(IWikiTableConfiguration aConfig) {
		if(isNameUnique(buildInConfigurations, aConfig) == false){
			return false;
		}
		
		if(isNameUnique(userSpecificConfigurations, aConfig) == false){
			return false;
		}
		return true;
	}
	
	/**
	 * @param aConfig
	 * @return
	 */
	private boolean isNameUnique(List<IWikiTableConfiguration> avaiableConfigs, IWikiTableConfiguration aConfig) {
		for (IWikiTableConfiguration avaiableConfig : avaiableConfigs) {
			if(StringUtils.equalsIgnoreCase(avaiableConfig.getName(), aConfig.getName()))
				return false;
		}
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory#replaceUserSpecificConfigurations(java.util.List)
	 */
	@Override 
	public synchronized void replaceUserSpecificConfigurations(List<IWikiTableConfiguration> config){
		this.userSpecificConfigurations.clear();
		for (IWikiTableConfiguration aConfig : config) {
			addUserSpecificConfigurations(aConfig);
		}	
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory#addUserSpecificConfigurations(net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration)
	 */
	@Override
	public void addUserSpecificConfigurations(IWikiTableConfiguration config) {
		if(config.isReadOnly()){
			throw new IllegalArgumentException("A read-only configuration cannot be added to the user specific configurations!");
		}
		if(isNameUnique(config)){
			this.userSpecificConfigurations.add(config.clone());
		}else{
			throw new IllegalArgumentException("The name of the configuration " + config.getName() + " is not unique!");
		}
	}

	

	/**
	 * Sets the build-in configurations.
	 * This method is not part of the interface and it will store the references instead a copy.
	 * @param buildInConfigurations build-in configuration for use.
	 */
	public synchronized void setBuildInConfigurations(List<IWikiTableConfiguration> buildInConfigurations) {
		this.buildInConfigurations = buildInConfigurations;
	}

	/**
	 * Sets the user-specific configurations.
	 * This method is not part of the interface and it will store the references instead a copy.
	 * @param userSpecificConfigurations build-in configuration for use.
	 */
	public synchronized void setUserSpecificConfigurations(List<IWikiTableConfiguration> userSpecificConfigurations) {
		this.userSpecificConfigurations = userSpecificConfigurations;
	}

	/**
	 * Merges all elements from the secondary, into the primary list.
	 * A element is only merged, if it not exists in the primary list. A element is assumed to be a existing one, if a element with the same name exists.
	 * In other words, only elements form the secondary list, where no element with the same name exists in the primary list are merged with the primary list.
	 * @param primary The primary list.
	 * @param secondary The secondary list.
	 */
	public static List<IWikiTableConfiguration> merge(List<IWikiTableConfiguration> primary, List<IWikiTableConfiguration> secondary){
		List<IWikiTableConfiguration> result = new ArrayList<IWikiTableConfiguration>(primary);
		
		List<IWikiTableConfiguration> candidates = new ArrayList<IWikiTableConfiguration>(secondary);
		
		for (IWikiTableConfiguration aPrimary : primary) {
			Iterator<IWikiTableConfiguration> it = candidates.iterator();
			while (it.hasNext()) {
				IWikiTableConfiguration  aSecondary =  it.next();
				if(StringUtils.equalsIgnoreCase(aPrimary.getName(), aSecondary.getName())){
					it.remove();
				}
			}
		}
		result.addAll(candidates);
		
		return result;
	}
	

	
	
	
}
