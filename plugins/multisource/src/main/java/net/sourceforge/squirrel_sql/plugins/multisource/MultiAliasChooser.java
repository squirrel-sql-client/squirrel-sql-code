package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
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
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeCommand;
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

/**
 * A dialog that allows a user to select an existing alias to add to the virtualization.
 */
public class MultiAliasChooser extends JDialog
{
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

	public MultiAliasChooser(IApplication app, ISession session, ArrayList<ISQLAlias> aliasList)
	{
		super((Frame) null, s_stringMgr.getString("MultiAliasChooser.title"), true);
		setSize(300, 200);
		_aliasList = aliasList;
		_app = app;
		_session = session;
		createUserInterface();
	}

	/**
	 * Show dialog.
	 */
	public ISQLAlias showDialog()
	{
		setVisible(true);
		return _selectedAlias;
	}

	/**
	 * Builds the components of the dialog.
	 */
	private void createUserInterface()
	{
		Container contentPane = getContentPane();

		JPanel content = new JPanel(new GridLayout(4,1));

		content.add(new JLabel(s_stringMgr.getString("MultiAliasChooser.prompt"), JLabel.LEFT));

		_aliasCbx = new JComboBox(_aliasList.toArray());
		_aliasCbx.setMaximumRowCount(5);			// Display up to 5 rows without a scrollbar

		content.add(_aliasCbx);
		JPanel namePanel = new JPanel();
		namePanel.add(new JLabel(s_stringMgr.getString("MultiAliasChooser.name"), JLabel.LEFT));
		_nameTxt = new JTextField(15);
		_nameTxt.setText(_aliasList.get(0).toString());
		namePanel.add(_nameTxt);
		content.add(namePanel);

		JPanel schemaPanel = new JPanel();
		schemaPanel.add(new JLabel(s_stringMgr.getString("MultiAliasChooser.schema"), JLabel.LEFT));
		_schemaTxt = new JTextField(15);
		schemaPanel.add(_schemaTxt);
		content.add(schemaPanel);

		contentPane.add(content, "Center");

		contentPane.add(createButtonsPanel(), "South");

		_aliasCbx.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				_sourceName = ((ISQLAlias) _aliasCbx.getSelectedItem()).getName();
				_nameTxt.setText(_sourceName);
			}
		});

		GUIUtils.centerWithinParent(this);
		setResizable(false);
	}

	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton(s_stringMgr.getString("MultiAliasChooser.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				// Do positive action
				ISQLAlias alias = (ISQLAlias) _aliasCbx.getSelectedItem();
				MultiAliasChooser.this._selectedAlias = alias;
				_sourceName = _nameTxt.getText();
				if (_sourceName.contains(" "))
				{	Dialogs.showOk(_nameTxt.getParent().getParent(), "Source name cannot contain spaces.");
					return;
				}
				String schemaName = _schemaTxt.getText();
				if (schemaName.trim().equals(""))
					schemaName = null;

				try
				{
					// Verify that source name is not a duplicate
					Object schema = MultiSourcePlugin.getSchema(_session.getSQLConnection().getConnection());
					Method getDBMethod = schema.getClass().getMethod("getDB", new Class[]{java.lang.String.class});
					Object result = getDBMethod.invoke(schema,  new Object[]{_sourceName});

					if (result != null)
					{	Dialogs.showOk(_nameTxt.getParent().getParent(), "Source name already exists in virtualization.  Select a different name.");
						return;
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

					Method meth = extractor.getClass().getMethod("extract", new Class[]{java.lang.String.class, java.lang.String.class, java.lang.String.class,
							java.lang.String.class, java.util.Properties.class, java.lang.String.class, java.lang.String.class, java.sql.Connection.class, java.lang.ClassLoader.class});

					Connection c = con.getConnection();
					Object asd = meth.invoke(extractor, new Object[]{driver.getDriverClassName(), alias.getUrl(), alias.getUserName(), alias.getPassword(), null,
								_sourceName, schemaName, c, newSourceLoader});
					
					// Add new database to schema
					Method addSourceMethod = schema.getClass().getMethod("addDatabase", new Class[]{asd.getClass()});					
					addSourceMethod.invoke(schema,  new Object[]{asd});					
					
					MultiSourcePlugin.refreshTree(_session);					
				}
				catch (Exception e)
				{
					Dialogs.showOk(_nameTxt.getParent().getParent(), "Error during extraction: "+e);
					return;
				}
				dispose();
			}
		});
		JButton cancelBtn = new JButton(s_stringMgr.getString("MultiAliasChooser.cancel"));
		cancelBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
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
}
