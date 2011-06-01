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

import java.util.Collection;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Factory, which creates the {@link CopyWikiTableAction} menu structure.
 * This is the default implementation for {@link ICopyWikiTableActionFactory}
 * This class is intended to be used as a singleton.
 * @see ICopyWikiTableActionFactory
 * @author Stefan Willinger
 *
 */
public class CopyWikiTableActionFactory implements ICopyWikiTableActionFactory {
	
	/**
	 * Instance for the singleton
	 */
	private static CopyWikiTableActionFactory instance = null;
	
	/**
	 * Factory to get the available configurations
	 */
	private IWikiTableConfigurationFactory configurationFactory = WikiTableConfigurationFactory.getInstance();
	
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TablePopupMenu.class);
	
	/**
	 * Constructor for the Singleton.
	 * This one is package protected for testing.
	 * Use {@link #instance} instead calling this constructor.
	 * @see #getInstance() 
	 */
	CopyWikiTableActionFactory() {
		super();
	}

	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.ICopyWikiTableActionFactory#createMenueItem(net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.ITableActionCallback)
	 */
	@Override
	public JMenuItem createMenueItem(ITableActionCallback callback){
		@SuppressWarnings("unchecked")
		Collection<IWikiTableConfiguration> configurations = CollectionUtils.select(configurationFactory.getConfigurations(), new EnabledConfigurationPredicate());
		if(configurations.isEmpty()){
			return createMenuForEmptyConfigurations();
		}else if(configurations.size() == 1){
			return createMenuForExactlyOneConfiguration(callback, configurations.iterator().next());
		}else{
			return createMenuForAListOfConfiguration(callback, configurations);
		}
	}


	/**
	 * Creates a sub menu for a list of configurations.
	 * @param callback Callback for the {@link JTable}
	 * @param configurations List of configurations to use.
	 * @return A sub menu containing a {@link CopyWikiTableAction} for each {@link IWikiTableConfiguration}
	 */
	private JMenuItem createMenuForAListOfConfiguration(ITableActionCallback callback,
			Collection<IWikiTableConfiguration> configurations) {
		JMenu submenue = new JMenu(s_stringMgr.getString("TablePopupMenu.copyaswikitable"));
		for (IWikiTableConfiguration config : configurations) {
			submenue.add(new CopyWikiTableAction(config.getName(), config, callback));
		}
		return submenue;
	}


	/**
	 * Creates a {@link JMenuItem} for a specific Configuration.
	 * The name of the {@link JMenuItem} depends on the name of the configuration.
	 * @param callback callback which provides the {@link JTable}
	 * @param configuration Configuration to use.
	 * @return A single {@link JMenuItem} for the {@link CopyWikiTableAction}
	 */
	private JMenuItem createMenuForExactlyOneConfiguration(ITableActionCallback callback,
			IWikiTableConfiguration configuration) {
		String actionName = s_stringMgr.getString("TablePopupMenu.copyaswikitableSpecific", configuration.getName());
		CopyWikiTableAction action = new CopyWikiTableAction(actionName, configuration, callback);
		return new JMenuItem(action);
	}


	/**
	 * Creates a {@link JMenuItem}, for the case, that no configuration is available.
	 * This menu item is disabled.
	 * @return A disabled JMenueItem
	 */
	private JMenuItem createMenuForEmptyConfigurations() {
		JMenuItem item = new JMenuItem(s_stringMgr.getString("TablePopupMenu.copyaswikitable"));
		item.setEnabled(false);
		return item;
	}
	
	/**
	 * Get an instance of this factory.
	 * @return
	 */
	public static synchronized ICopyWikiTableActionFactory getInstance(){
		if(instance == null){
			instance = new CopyWikiTableActionFactory();
		}
		return instance;
	}


	public IWikiTableConfigurationFactory getConfigurationFactory() {
		return configurationFactory;
	}


	public void setConfigurationFactory(IWikiTableConfigurationFactory configurationFactory) {
		this.configurationFactory = configurationFactory;
	}

	/**
	 * Predicate, for selecting only enabled configurations.
	 * @author Stefan Willinger
	 * @see IWikiTableConfiguration#isEnabled()
	 */
	private static class EnabledConfigurationPredicate implements Predicate{

		/**
		 * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
		 */
		@Override
		public boolean evaluate(Object input) {
			IWikiTableConfiguration config = (IWikiTableConfiguration) input;
			return config.isEnabled();
		}
		
	}
	
}
