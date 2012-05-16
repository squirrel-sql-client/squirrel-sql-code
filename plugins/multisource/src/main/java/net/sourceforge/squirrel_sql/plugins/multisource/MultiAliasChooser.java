package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverClassLoader;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

/**
 * A dialog that allows a user to select an existing alias to add to the
 * virtualization.
 */
public class MultiAliasChooser extends JDialog {
	private static final long serialVersionUID = 1L;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MultiAliasChooser.class);

	private JComboBox _aliasCbx;
	private JTextField _nameTxt;
	private JTextField _schemaTxt;
	private ArrayList<ISQLAlias> _aliasList;
	private ISQLAlias _selectedAlias;
	private String _sourceName;
	private IApplication _app;
	private ISession _session;

	public MultiAliasChooser(IApplication app, ISession session, ArrayList<ISQLAlias> aliasList) {
		super((Frame) null, s_stringMgr.getString("MultiAliasChooser.title"), true);
		_aliasList = aliasList;
		_app = app;
		_session = session;
		createUserInterface();
      pack();
	}

	/**
	 * Show dialog.
	 */
	public ISQLAlias showDialog() {
		setVisible(true);
		return _selectedAlias;
	}

	/**
	 * Builds the components of the dialog.
	 */
	private void createUserInterface() {
		Container contentPane = getContentPane();
      contentPane.setLayout(new BorderLayout());

		JPanel content = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

		// Alias combobox and label
      gbc = new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		content.add(new JLabel(s_stringMgr.getString("MultiAliasChooser.prompt"), JLabel.LEFT), gbc);

      gbc = new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,10,5), 0,0);
      _aliasCbx = new JComboBox(_aliasList.toArray());
		_aliasCbx.setMaximumRowCount(5); // Display up to 5 rows without a scrollbar
		content.add(_aliasCbx, gbc);
		
		// Name textbox and label
      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      content.add(new JLabel(s_stringMgr.getString("MultiAliasChooser.name"), JLabel.LEFT), gbc);
		_nameTxt = new JTextField(15);
		_nameTxt.setText(_aliasList.get(0).toString());

      gbc = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      content.add(_nameTxt, gbc);

		// Schema textbox and label
      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      content.add(new JLabel(s_stringMgr.getString("MultiAliasChooser.schema"), JLabel.LEFT), gbc);
		_schemaTxt = new JTextField(15);

      gbc = new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      content.add(_schemaTxt, gbc);

      contentPane.add(content, BorderLayout.CENTER);

		// Buttons
		contentPane.add(createButtonsPanel(), BorderLayout.SOUTH);

		_aliasCbx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				_sourceName = StringUtilities.javaNormalize(((ISQLAlias) _aliasCbx.getSelectedItem()).getName());
				_nameTxt.setText(_sourceName);
			}
		});

		GUIUtils.centerWithinParent(this);
		setResizable(false);
	}

	/**
	 * Creates OK and Cancel buttons with action events.
	 * @return
	 */
	private JPanel createButtonsPanel() {
		JPanel pnl = new JPanel();

		// OK button
		JButton okBtn = new JButton(s_stringMgr.getString("MultiAliasChooser.ok"));
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!executeAddSource())
					return;
				dispose();
			}
		});
		
		// Cancel button
		JButton cancelBtn = new JButton(s_stringMgr.getString("MultiAliasChooser.cancel"));
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MultiAliasChooser.this._selectedAlias = null;
				dispose();
			}
		});

		pnl.add(okBtn);
		pnl.add(cancelBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, cancelBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}
	
	/**
	 * Adds the selected source to the virtualization.
	 * Uses reflection to call methods of the UnityJDBC driver.
	 */
	private boolean executeAddSource() {
		ISQLAlias alias = (ISQLAlias) _aliasCbx.getSelectedItem();
		MultiAliasChooser.this._selectedAlias = alias;
		
		// Verify correct format of source name (no spaces, etc.)
		_sourceName = _nameTxt.getText();
		if (_sourceName.contains(" ")) {
			Dialogs.showOk(_nameTxt.getParent().getParent(), "Source name cannot contain spaces.");
			return false;
		}
		
		// Retrieve schema name if it exists
		String schemaName = _schemaTxt.getText();
		if (schemaName.trim().equals(""))
			schemaName = null;

		try {
			// Verify that source name is not a duplicate
			Object schema = MultiSourcePlugin.getSchema(_session.getSQLConnection().getConnection());
			Method getDBMethod = schema.getClass().getMethod("getDB", new Class[] { java.lang.String.class });
			Object result = getDBMethod.invoke(schema, new Object[] { _sourceName });

			if (result != null) {
				Dialogs.showOk(_nameTxt.getParent().getParent(), "Source name already exists in virtualization.  Select a different name.");
				return false;
			}

			// Now actually do extract with this information
			ClassLoader loader = _session.getSQLConnection().getConnection().getClass().getClassLoader();
			Object extractor = Class.forName("com.unityjdbc.sourcebuilder.AnnotatedExtractor", true, loader).newInstance();

			// Create connection
			SQLDriverManager dm = _app.getSQLDriverManager();
			IIdentifier driverID = alias.getDriverIdentifier();
			ISQLDriver driver = _app.getDataCache().getDriver(driverID);
			ISQLConnection con = dm.getConnection(driver, alias, alias.getUserName(), alias.getPassword(), alias.getDriverPropertiesClone());

			ClassLoader newSourceLoader = new SQLDriverClassLoader(driver);

			Method meth = extractor.getClass().getMethod("extract", new Class[] { java.lang.String.class, java.lang.String.class,
							java.lang.String.class, java.lang.String.class,
							java.util.Properties.class, java.lang.String.class,
							java.lang.String.class, java.sql.Connection.class,
							java.lang.ClassLoader.class });

			Connection c = con.getConnection();
			Object asd = meth.invoke(extractor, new Object[] { driver.getDriverClassName(), alias.getUrl(), alias.getUserName(), alias.getPassword(), null, 
																_sourceName, schemaName, c, newSourceLoader });

			// Add new database to schema
			Method addSourceMethod = schema.getClass().getMethod("addDatabase", new Class[] { asd.getClass() });
			addSourceMethod.invoke(schema, new Object[] { asd });

			MultiSourcePlugin.refreshTree(_session);
		} catch (Exception e) {
			Dialogs.showOk(_nameTxt.getParent().getParent(), "Error during extraction: "+ e);
			return false;
		}
		return true;
	}
}
