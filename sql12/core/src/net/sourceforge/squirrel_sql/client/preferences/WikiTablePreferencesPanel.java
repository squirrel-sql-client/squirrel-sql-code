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
package net.sourceforge.squirrel_sql.client.preferences;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.GenericWikiTableConfigurationBean;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.WikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import org.apache.commons.lang.StringUtils;

/**
 * Preferences panel for WIKI table configurations
 * @author Stefan Willinger
 *
 */
public class WikiTablePreferencesPanel extends JPanel {
	
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(WikiTablePreferencesPanel.class);

	private final Insets LABEL_INSETS = new Insets(2, 28, 6, 0);

	private final Insets FIELD_INSETS = new Insets(2, 8, 6, 28);

	
	private IWikiTableConfigurationFactory wikiTableConfigFactory = WikiTableConfigurationFactory.getInstance();
	
	
	private IApplication application;
	
	
	// Components of the detail panel
	
	private JLabel nameLable = null;
	
	private JTextField name = null;
	
	private JLabel tableStartTagLabel = null;
	
	private JTextField tableStartTag = null;
	
	private JLabel headerStartTagLabel = null;
	
	private JTextField headerStartTag = null;
	
	private JLabel headerCellTagLabel = null;
	
	private JTextField headerCellTag = null;
	
	private JLabel headerEndTagLabel = null;
	
	private JTextField headerEndTag = null;
	
	private JLabel rowStartTagLabel = null;
	
	private JTextField rowStartTag = null;
	
	private JLabel cellTagLabel = null;
	
	private JTextField cellTag = null;
	
	private JLabel rowEndTagLabel = null;
	
	private JTextField rowEndTag = null;
	
	private JLabel tableEndTagLabel = null;
	
	private JTextField tableEndTag = null;
	
	private JLabel noWikiTagLabel = null;
	
	private JTextField noWikiTag = null;
	
	private DefaultListModel wikiConfigListModel = null;

	private JList wikiConfigList;

	private JButton newButton;

	private JButton copyButton;

	private JButton deleteButton;

	private JTable exampleTable;

	private JTextArea exampleText;

	private IWikiTableConfiguration currentConfigurationInView;

	private JCheckBox enabled;

	
	
	public WikiTablePreferencesPanel()
	{
		super(new GridBagLayout());
		createUserInterface();
	}
	
	
	/**
	 * Creates the user Interface
	 */
	private void createUserInterface()
	{
		JPanel jp = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		jp.add(createOverviewPanel(), gbc);
		
		gbc.gridy = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		jp.add(createNotePanel(), gbc);
		
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridheight=2;
		gbc.gridy = 0;
		gbc.gridx =1;
		gbc.fill = GridBagConstraints.BOTH;
		
		jp.add(createDetailPanel(), gbc);

		
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridheight=2;
		gbc.gridy = 0;
		gbc.gridx =2;
		gbc.fill = GridBagConstraints.BOTH;

		jp.add(createExamplePanel(), gbc);

		JScrollPane sp = new JScrollPane(jp);
		gbc = new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=1;
		add(sp,gbc);

	}
	
	/**
	 * Create the component, which shows a example.
	 * The example contains a {@link JTable} and a {@link JTextField}. If the user change a element of the configuration, then the example will be updated.
	 * So we can provide a feedback of the result immediately.
	 */
	private Component createExamplePanel() {
		JPanel jp = new JPanel(new GridBagLayout());
		jp.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("WikiTablePreferencesPanel.titleExample"))); //$NON-NLS-1$

		GridBagConstraints gbc = new GridBagConstraints();
		
		
		String[][] rowData = new String[][]{
				{s_stringMgr.getString("WikiTablePreferencesPanel.austria"), s_stringMgr.getString("WikiTablePreferencesPanel.vienna")}, //$NON-NLS-1$ //$NON-NLS-2$
				{s_stringMgr.getString("WikiTablePreferencesPanel.italy"), s_stringMgr.getString("WikiTablePreferencesPanel.rome")} //$NON-NLS-1$ //$NON-NLS-2$
		};
		
		String[] columnNames = new String[]{s_stringMgr.getString("WikiTablePreferencesPanel.country"), s_stringMgr.getString("WikiTablePreferencesPanel.captial")}; //$NON-NLS-1$ //$NON-NLS-2$
		
		exampleTable = new JTable(rowData, columnNames);
		exampleTable.setMinimumSize(new Dimension(10,10));

		JScrollPane sp = new JScrollPane(exampleTable);
		sp.setPreferredSize(new Dimension(100,50));
		
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weighty=1;
		gbc.weighty=0.1;
		jp.add(sp, gbc);
		
	
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weighty=0;
		gbc.weightx=0;
		jp.add(new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.titleResultExample")), gbc); //$NON-NLS-1$
		
		
		exampleText = new JTextArea(15,20);
		exampleText.setWrapStyleWord(true);
		exampleText.setEditable(false);
		exampleText.setLineWrap(false);
		
		
		gbc.gridy=2;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weighty=1;
		gbc.weightx=1;
		
		sp = new JScrollPane(exampleText);
		jp.add(sp, gbc);
		
		return jp;
	}


	/**
	 * Create the panel with a small help text for the user.
	 * This panel indicates, which variables are allowed.
	 */
	private Component createNotePanel() {
		JPanel jp = new JPanel();
		jp.setBorder(BorderFactory.createTitledBorder("Note")); //$NON-NLS-1$
		
		
		String text = "<html><body>"+ //$NON-NLS-1$
		s_stringMgr.getString("WikiTablePreferencesPanel.hintValueVariable") + //$NON-NLS-1$
		"<br />" + //$NON-NLS-1$
		s_stringMgr.getString("WikiTablePreferencesPanel.hintNewLine") + //$NON-NLS-1$
		"</body></html>"; //$NON-NLS-1$
		JLabel label = new JLabel(text);
		
		jp.add(label);
		
		return jp;
	}
	
	
	/**
	 * The overview panel contains a list of all available configurations.
	 * There are some methods provided for creating, copying or deleting configurations.
	 * @return
	 */
	private Component createOverviewPanel() {
		JPanel jp = new JPanel(new BorderLayout());
		jp.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("WikiTablePreferencesPanel.titleOverview"))); //$NON-NLS-1$
		
		wikiConfigListModel = new DefaultListModel();
		
		wikiConfigList = new JList(wikiConfigListModel);
		wikiConfigList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		wikiConfigList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						IWikiTableConfiguration selectedValue = (IWikiTableConfiguration) wikiConfigList.getSelectedValue();
						showDataFor(selectedValue);
						if(selectedValue != null){
							copyButton.setEnabled(true);
						}else{
							copyButton.setEnabled(false);
						}
					}
				});
				
			}
		});
		
		JScrollPane sp = new JScrollPane(wikiConfigList);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		newButton = new JButton(s_stringMgr.getString("WikiTablePreferencesPanel.new")); //$NON-NLS-1$
		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IWikiTableConfiguration newConfig = new GenericWikiTableConfigurationBean();
				addNewConfigToList(newConfig);
			}

			private void addNewConfigToList(IWikiTableConfiguration newConfig) {
				wikiConfigListModel.addElement(newConfig);
				wikiConfigList.setSelectedValue(newConfig, true);
			}
		});
		buttonPanel.add(newButton);
		
		copyButton = new JButton(s_stringMgr.getString("WikiTablePreferencesPanel.copy")); //$NON-NLS-1$
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IWikiTableConfiguration newConfig = ((IWikiTableConfiguration)wikiConfigList.getSelectedValue()).copyAsUserSpecific();

				int suffix = 0;
				
				String originalName = newConfig.getName();
				
				do{
					suffix++;
					newConfig.setName(originalName+"_"+suffix); //$NON-NLS-1$
				}while(!isUniqueName(newConfig));
					
				
				addNewConfigToList(newConfig);
			}

			
		});
		
		buttonPanel.add(copyButton);
		
		deleteButton = new JButton(s_stringMgr.getString("WikiTablePreferencesPanel.delete")); //$NON-NLS-1$
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = wikiConfigList.getSelectedIndex();
				wikiConfigList.setSelectedIndex(0);
				wikiConfigListModel.remove(selectedIndex);
				// we always have at least on item - the build in configuration
//				wikiConfigList.invalidate();
			}
		});
		buttonPanel.add(deleteButton);
		
		jp.add(sp, BorderLayout.CENTER);
		jp.add(buttonPanel, BorderLayout.SOUTH);
		
		return jp;
	}


	/**
	 * The detail panel contains all information of a specific configuration.
	 * The user can edit the configuration in this panel.
	 */
	private JPanel createDetailPanel(){
		JPanel jp = new JPanel(new GridBagLayout());
		jp.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("WikiTablePreferencesPanel.titleDetailPanel"))); //$NON-NLS-1$
		jp.setMinimumSize(new Dimension(400,50));
		
		final GridBagConstraints gbc = new GridBagConstraints();
		
		int row= 0;
		
		setLabelConstraints(gbc, row);
		nameLable = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.name"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(nameLable, gbc);
		
		setFieldConstraints(gbc, row);
		name = new JTextField(30);
		jp.add(name, gbc);
		
		setLabelConstraints(gbc, ++row);
		tableStartTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.tableStartTag"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(tableStartTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		tableStartTag = new JTextField(30);
		jp.add(tableStartTag, gbc);
		
		
		setLabelConstraints(gbc, ++row);
		headerStartTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.headerStartTag"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(headerStartTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		headerStartTag = new JTextField(30);
		jp.add(headerStartTag, gbc);
		
		setLabelConstraints(gbc, ++row);
		headerCellTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.headerCellTag"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(headerCellTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		headerCellTag = new JTextField(30);
		jp.add(headerCellTag, gbc);
		
		setLabelConstraints(gbc, ++row);
		headerEndTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.headerEndTag"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(headerEndTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		headerEndTag = new JTextField(30);
		jp.add(headerEndTag, gbc);
		
		setLabelConstraints(gbc, ++row);
		rowStartTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.rowStartTag"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(rowStartTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		rowStartTag = new JTextField(30);
		jp.add(rowStartTag, gbc);
		
		setLabelConstraints(gbc, ++row);
		cellTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.cellTag"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(cellTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		cellTag = new JTextField(30);
		jp.add(cellTag, gbc);
		
		setLabelConstraints(gbc, ++row);
		rowEndTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.rowEndTag"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(rowEndTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		rowEndTag = new JTextField(30);
		jp.add(rowEndTag, gbc);
		
		setLabelConstraints(gbc, ++row);
		tableEndTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.tableEndTag"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(tableEndTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		tableEndTag = new JTextField(30);
		jp.add(tableEndTag, gbc);
		
		setLabelConstraints(gbc, ++row);
		noWikiTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.noWikiTag"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(noWikiTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		noWikiTag = new JTextField(30);
		jp.add(noWikiTag, gbc);
		
		setLabelConstraints(gbc, ++row);
		noWikiTagLabel = new JLabel(s_stringMgr.getString("WikiTablePreferencesPanel.enabled"), SwingConstants.RIGHT); //$NON-NLS-1$
		jp.add(noWikiTagLabel, gbc);
		
		setFieldConstraints(gbc, row);
		enabled = new JCheckBox();
		jp.add(enabled, gbc);
		
		addFocusLostListeners();

		return jp;
	}
	
	
	/**
	 * Adds a {@link FocusListener} for the focus lost event to all input fields of the detail panel.
	 * This listener updated the configuration and the example.
	 */
	private void addFocusLostListeners() {
		name.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				String newName = name.getText();
				config.setName(newName);
				
				 if(!isUniqueName(config)){
					 throw new IllegalArgumentException(s_stringMgr.getString("WikiTablePreferencesPanel.errorConfigNotUnique")); //$NON-NLS-1$
				 }
			}
		});
		
		
		tableStartTag.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				config.setTableStartTag(tableStartTag.getText());
			}
		});
		
		headerStartTag.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				config.setHeaderStartTag(headerStartTag.getText());
			}
		});
		
		headerCellTag.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				config.setHeaderCell(headerCellTag.getText());
			}
		});
		
		headerEndTag.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				config.setHeaderEndTag(headerEndTag.getText());
			}
		});
		
		rowStartTag.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				config.setRowStartTag(rowStartTag.getText());
			}
		});
		
		cellTag.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				config.setDataCell(cellTag.getText());
			}
		});
		
		rowEndTag.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				config.setRowEndTag(rowEndTag.getText());
			}
		});
		
		tableEndTag.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				config.setTableEndTag(tableEndTag.getText());
			}
		});
		
		noWikiTag.addFocusListener(new DetailConfigFocusLostListener(){
			@Override
			public void setValue(IWikiTableConfiguration config) throws IllegalArgumentException {
				config.setNoWikiTag(noWikiTag.getText());
			}
		});
		
		enabled.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IWikiTableConfiguration config = currentConfigurationInView;
				config.setEnabled(enabled.isSelected());
			}
		});
		
	}


	private void setLabelConstraints(GridBagConstraints gbc, int gridy)
	{
		gbc.gridx = 0;
		gbc.gridy = gridy;
//		gbc.gridwidth = 0;
		gbc.weightx = 0;
		gbc.insets = LABEL_INSETS;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
	}
	
	private void setFieldConstraints(GridBagConstraints gbc, int gridy)
	{
		gbc.gridx = 1;
		gbc.gridy = gridy;
//		gbc.gridwidth = 0;
		gbc.weightx = 1;
		gbc.insets = FIELD_INSETS;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

	}

	public IApplication getApplication() {
		return application;
	}

	public void setApplication(IApplication app) {
		this.application = app;
	}

	/**
	 * Adds all available configurations to this page
	 * @param squirrelPreferences
	 */
	public void loadData(SquirrelPreferences squirrelPreferences) {
		
		List<IWikiTableConfiguration> configurations = wikiTableConfigFactory.getConfigurations();
		
		for (IWikiTableConfiguration configuration : configurations) {
			wikiConfigListModel.addElement(configuration);
		}
		
		wikiConfigList.setSelectedIndex(0);
		
		IWikiTableConfiguration selectedValue = (IWikiTableConfiguration)wikiConfigList.getSelectedValue();
		showDataFor(selectedValue);	
		if(selectedValue == null){
			this.copyButton.setEnabled(false);
		}
		
	}


	/**
	 * Shows the data of a specific configuration in the detail panel
	 * @param selectedValue
	 */
	private void showDataFor(IWikiTableConfiguration selectedValue) {
		if(selectedValue != null){
			this.currentConfigurationInView = selectedValue;
			fillDetailPane(selectedValue);
			showExample(selectedValue);
		}else{
			this.currentConfigurationInView = null;
			enableInputFields(false);
		}
	}
	
	/**
	 * Disable all input fields
	 */
	private void enableInputFields(boolean enabled) {
		this.name.setEnabled(enabled);
		this.tableStartTag.setEnabled(enabled);
		this.headerStartTag.setEnabled(enabled);
		this.headerCellTag.setEnabled(enabled);
		this.headerEndTag.setEnabled(enabled);
		this.rowStartTag.setEnabled(enabled);
		this.cellTag.setEnabled(enabled);
		this.rowEndTag.setEnabled(enabled);
		this.tableEndTag.setEnabled(enabled);
		this.noWikiTag.setEnabled(enabled);
		this.deleteButton.setEnabled(enabled);
		
		if(currentConfigurationInView != null){
			this.enabled.setEnabled(true);
		}else{
			this.enabled.setEnabled(false);
		}
	}


	/**
	 * Shows the example for a specific configuration
	 */
	private void showExample(IWikiTableConfiguration selectedValue) {
		// select all rows
		exampleTable.changeSelection(0, 0, false, false);
		exampleTable.changeSelection(exampleTable.getRowCount()-1, exampleTable.getColumnCount()-1, true, true);
		
		
		String example = selectedValue.createTransformer().transform(this.exampleTable);
		this.exampleText.setText(example);
	}


	/**
	 * Fills the detail pane with the values of the selected configuration
	 * @param selectedValue
	 */
	private void fillDetailPane(IWikiTableConfiguration config) {
		
		this.name.setText(config.getName());
		
		this.tableStartTag.setText(config.getTableStartTag());
		
		this.headerStartTag.setText(config.getHeaderStartTag());
		
		this.headerCellTag.setText(config.getHeaderCell());
		
		this.headerEndTag.setText(config.getHeaderEndTag());
		
		this.rowStartTag.setText(config.getRowStartTag());
		
		this.cellTag.setText(config.getDataCell());
		
		this.rowEndTag.setText(config.getRowEndTag());
		
		this.tableEndTag.setText(config.getTableEndTag());
		
		this.noWikiTag.setText(config.getNoWikiTag());
		
		this.enabled.setSelected(config.isEnabled());
		
		enableInputFields(!config.isReadOnly());
	}

	/**
	 * Adds a new configuration to the list.
	 */
	private void addNewConfigToList(IWikiTableConfiguration newConfig) {
		wikiConfigListModel.addElement(newConfig);
		wikiConfigList.setSelectedValue(newConfig, true);
	}
	
	/**
	 * checks, if the name of a configuration is unique in the system.
	 */
	private boolean isUniqueName(IWikiTableConfiguration config) {
		boolean unique;
		Object[] configArray =  wikiConfigListModel.toArray();
		unique = true;
		for (Object conf : configArray) {
			// This must be a check, if the references are identical.
			if(conf != conf){
				if(StringUtils.equalsIgnoreCase(config.getName(), ((IWikiTableConfiguration) conf).getName())){
					unique = false;
				}
			}
		}
		return unique;
	}
	
	private abstract class DetailConfigFocusLostListener implements FocusListener{

		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		@Override
		public void focusGained(FocusEvent envent) {
			// nothing
			
		}

		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
		 */
		@Override
		public void focusLost(final FocusEvent event) {
			if(!event.isTemporary()){
				IWikiTableConfiguration config = currentConfigurationInView;
				try{
					setValue(config);
				}catch (IllegalArgumentException e) {
					application.showErrorDialog(e.getMessage());
					JTextField textField = (JTextField) event.getComponent();
					String text = textField.getText();
					System.out.println(text);
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							event.getComponent().requestFocus();
						}
					});
				}
				showExample(config);
			}
			
		}
		
		public abstract void setValue(IWikiTableConfiguration config) throws IllegalArgumentException;
		
	}

	/**
	 * Puts the configurations back into the {@link IWikiTableConfigurationFactory}.
	 * @see IWikiTableConfigurationFactory#replaceUserSpecificConfigurations(List)
	 * @param squirrelPreferences
	 */
	public void applyChanges() {
		
		Object[] array = wikiConfigListModel.toArray();
		
		List<IWikiTableConfiguration> userSpecific = new ArrayList<IWikiTableConfiguration>();
		List<IWikiTableConfiguration> buildIn = new ArrayList<IWikiTableConfiguration>();
		for (Object object : array) {
			IWikiTableConfiguration config = (IWikiTableConfiguration) object;
			if(config.isReadOnly() == false){
				userSpecific.add(config);
			}else{
				buildIn.add(config);
			}
		}
		
		wikiTableConfigFactory.replaceUserSpecificConfigurations(userSpecific);
		wikiTableConfigFactory.replaceBuilInConfiguration(buildIn);
	}


	public IWikiTableConfigurationFactory getWikiTableConfigFactory() {
		return wikiTableConfigFactory;
	}


	public void setWikiTableConfigFactory(IWikiTableConfigurationFactory wikiTableConfigFactory) {
		this.wikiTableConfigFactory = wikiTableConfigFactory;
	}

}
