package net.sourceforge.squirrel_sql.client.gui.db;
/*
 * Copyright (C) 2001-2004 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
 * jcompagner@j-com.nl
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
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetListener;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesCommand;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
/**
 * This internal frame allows the user to connect to an alias.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectionInternalFrame extends DialogWidget
{
	/** Handler called for internal frame actions. */
	public interface IHandler
	{
		/**
		 * User has clicked the OK button to connect to the alias.
		 *
		 * @param	connSheet	The connection internal frame.
		 * @param	user		The user name entered.
		 * @param	password	The password entered.
		 * @param	props		SQLDriverPropertyCollection to connect with.
		 */
		public void performOK(ConnectionInternalFrame connSheet, String user,
								String password, SQLDriverPropertyCollection props);

		/**
		 * User has clicked the Close button. They don't want to
		 * connect to the alias.
		 *
		 * @param	connSheet	The connection internal frame.
		 */
		public void performClose(ConnectionInternalFrame connSheet);

		/**
		 * User has clicked the Cancel button. They want to cancel
		 * the curently active attempt to connect to the database.
		 *
		 * @param	connSheet	The connection internal frame.
		 */
		public void performCancelConnect(ConnectionInternalFrame connSheet);
	}

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ConnectionInternalFrame.class);

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(ConnectionInternalFrame.class);

	/** Application API. */
	private IApplication _app;

	/** Alias we are going to connect to. */
	private ISQLAlias _alias;

	/** JDBC driver for <TT>_alias</TT>. */
	private ISQLDriver _sqlDriver;

	/** <TT>true</TT> means that an attempt is being made to connect to the alias.*/
	private boolean _connecting;

//	private SQLDriverPropertyCollection _props = new SQLDriverPropertyCollection();

	private IHandler _handler;

	private JLabel _aliasName = new JLabel();
	private JLabel _driverName = new JLabel();
	private JLabel _url = new JLabel();
	private JTextField _user = new JTextField();
	private JTextField _password = new JPasswordField();
	private OkClosePanel _btnsPnl = new OkClosePanel(s_stringMgr.getString("ConnectionInternalFrame.connect"));

	private boolean _driverPropertiesLoaded = false;

//	/** If checked use the extended driver properties. */
//	private final JCheckBox _useDriverPropsChk = new JCheckBox(s_stringMgr.getString("ConnectionInternalFrame.userdriverprops"));

	/** Button that brings up the driver properties dialog. */
	private final JButton _driverPropsBtn = new JButton(s_stringMgr.getString("ConnectionInternalFrame.props"));

	private StatusBar _statusBar = new StatusBar();

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 * @param	alias	<TT>SQLAlias</TT> that we are going to connect to.
	 * @param	handler	Handler for internal frame actions.
	 *
	 * @throws	IllegalArgumentException
	 * 			If <TT>null</TT> <TT>IApplication</TT>, <TT>ISQLAlias</TT>,
	 * 			or <TT>IConnectionInternalFrameHandler</TT> passed.
	 */
	public ConnectionInternalFrame(IApplication app, ISQLAlias alias,
									IHandler handler)
	{
		super("", true, app);
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (alias == null)
		{
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}
		if (handler == null)
		{
			throw new IllegalArgumentException("Null IConnectionInternalFrameHandler passed");
		}

		_app = app;
		_alias = alias;
		_handler = handler;

		_sqlDriver = _app.getDataCache().getDriver(_alias.getDriverIdentifier());
		if (_sqlDriver == null)
		{
			throw new IllegalStateException(s_stringMgr.getString("ConnectionInternalFrame.error.nodriver",
												_alias.getName()));
		}

		createGUI();
		loadData();
		pack();
	}

	public void executed(final boolean connected)
	{
        _connecting = false;
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                if (connected)
                {
                    dispose();
                }
                else
                {
                    setStatusText(null);
                    _user.setEnabled(true);
                    _password.setEnabled(true);
                    _btnsPnl.setExecuting(false);
                }                
            }
        });
	}

	/**
	 * If the alias specifies autologon then connect after the Dialog is visible.
	 *
	 * @param	visible		If <TT>true</TT> dialog is to be made visible.
	 */
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);

		if (visible && _alias.isAutoLogon())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					connect();
				}
			});
		}
	}

	/**
	 * Set the text in the status bar.
	 *
	 * @param	text	The text to place in the status bar.
	 */
	public void setStatusText(String text)
	{
		_statusBar.setText(text);
	}

	/**
	 * Allow base class to create rootpane and add a couple
	 * of listeners for ENTER and ESCAPE to it.
	 */
	private void initKeyListeners()
	{
		ActionListener escapeListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				ConnectionInternalFrame.this.dispose();
			}
		};

		ActionListener enterListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				ConnectionInternalFrame.this.connect();
			}
		};

		final JRootPane pane = getRootPane();

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		pane.registerKeyboardAction(escapeListener, ks, JComponent.WHEN_IN_FOCUSED_WINDOW);
		ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		pane.registerKeyboardAction(enterListener, ks, JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

   /**
    * Load data about selected alias into the UI.
    */
   private void loadData()
   {
      if (SwingUtilities.isEventDispatchThread())
      {
         _loadData();
      }
      else
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _loadData();
            }
         });
      }
      //loadDriverProperties();

   }

   private void _loadData()
   {
      String userName = _alias.getUserName();
      String password = _alias.getPassword();
      _aliasName.setText(_alias.getName());
      _driverName.setText(_sqlDriver.getName());
      _url.setText(_alias.getUrl());
      _user.setText(userName);
      _password.setText(password);
//      _useDriverPropsChk.setSelected(_alias.getUseDriverProperties());
//      _driverPropsBtn.setEnabled(_useDriverPropsChk.isSelected());
      // This is mainly for long URLs that cannot be fully
      // displayed in the label.
      _aliasName.setToolTipText(_aliasName.getText());
      _driverName.setToolTipText(_driverName.getText());
      _url.setToolTipText(_url.getText());
   }

   private void connect()
	{
		if (!_connecting)
		{
			_connecting = true;
			_btnsPnl.setExecuting(true);
			setStatusText(s_stringMgr.getString("ConnectionInternalFrame.connecting"));
			_user.setEnabled(false);
			_password.setEnabled(false);

         SQLDriverPropertyCollection driverProperties = _alias.getDriverPropertiesClone();
         if (!_alias.getUseDriverProperties())
         {
            driverProperties.clear();
         }
			_handler.performOK(this, _user.getText(), _password.getText(), driverProperties);
		}
	}

	private void cancelConnect()
	{
		if (_connecting)
		{
			// abort first..
			setStatusText(s_stringMgr.getString("ConnectionInternalFrame.cancelling"));
			_btnsPnl.enableCloseButton(false);
			_handler.performCancelConnect(this);
			_connecting = false;
			dispose();
		}
	}

	private void createGUI()
	{
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      makeToolWindow(true);

      final String winTitle =
         s_stringMgr.getString("ConnectionInternalFrame.title", _alias.getName());
      setTitle(winTitle);

      final JPanel content = new JPanel(new BorderLayout());
      content.add(createMainPanel(), BorderLayout.CENTER);
      content.add(_statusBar, BorderLayout.SOUTH);
      setContentPane(content);

      initKeyListeners();

   }

	/**
	 * Create the main panel
	 *
	 * @return	main panel.
	 */
	private Component createMainPanel()
	{
		_user.setColumns(20);
		_password.setColumns(20);

		_driverPropsBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				showDriverPropertiesDialog();
			}
		});

		_btnsPnl.addListener(new MyOkClosePanelListener());

		final FormLayout layout = new FormLayout(
			// Columns
			"right:pref, 8dlu, left:min(100dlu;pref):grow",
			// Rows
			"pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu, "
		+	"pref, 6dlu, pref, 6dlu, pref, 3dlu, pref, 3dlu, pref");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();

		int y = 1;
		builder.addSeparator(getTitle(), cc.xywh(1, y, 3, 1));

		y += 2;
		builder.addLabel(s_stringMgr.getString("ConnectionInternalFrame.alias"), cc.xy(1, y));
		builder.add(_aliasName, cc.xywh(3, y, 1, 1));

		y += 2;
		builder.addLabel(s_stringMgr.getString("ConnectionInternalFrame.driver"), cc.xy(1, y));
		builder.add(_driverName, cc.xywh(3, y, 1, 1));

		y += 2;
		builder.addLabel(s_stringMgr.getString("ConnectionInternalFrame.url"), cc.xy(1, y));
		builder.add(_url, cc.xywh(3, y, 1, 1));

		y += 2;
		builder.addLabel(s_stringMgr.getString("ConnectionInternalFrame.user"), cc.xy(1, y));
		builder.add(_user, cc.xywh(3, y, 1, 1));

		y += 2;
		builder.addLabel(s_stringMgr.getString("ConnectionInternalFrame.password"), cc.xy(1, y));
		builder.add(_password, cc.xywh(3, y, 1, 1));

		y += 2;
      _driverPropsBtn.setIcon(_app.getResources().getIcon(SquirrelResources.IImageNames.ALIAS_PROPERTIES));
      builder.add(_driverPropsBtn, cc.xywh(3, y, 1, 1));

		y += 2;
		builder.addLabel(s_stringMgr.getString("ConnectionInternalFrame.warningcapslock"),
							cc.xywh(1, y, 3, 1));

		y += 2;
		builder.addSeparator("", cc.xywh(1, y, 3, 1));

		y += 2;
		builder.add(_btnsPnl, cc.xywh(1, y, 3, 1));


		// Set focus to password control if default user name has been setup.
		addWidgetListener(new WidgetAdapter()
		{
			private WidgetAdapter _this;
			public void widgetActivated(WidgetEvent evt)
			{
				_this = this;
				final String userName = _user.getText();
				if (userName != null && userName.length() > 0)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							final String pw = _password.getText();
							if (pw != null && pw.length() > 0)
							{
								_btnsPnl.getOKButton().requestFocus();
							}
							else
							{
								_password.requestFocus();
							}
							ConnectionInternalFrame.this.removeWidgetListener(_this);
						}
					});
				}
			}
		});

		return builder.getPanel();
	}

	private void showDriverPropertiesDialog()
	{
      new AliasPropertiesCommand(_alias, _app).execute();
	}


	/**
	 * Listener to handle button events in OK/Close panel.
	 */
	private final class MyOkClosePanelListener implements IOkClosePanelListener
	{
		public void okPressed(OkClosePanelEvent evt)
		{
			ConnectionInternalFrame.this.connect();
		}

		public void closePressed(OkClosePanelEvent evt)
		{
			ConnectionInternalFrame.this.dispose();
		}

		public void cancelPressed(OkClosePanelEvent evt)
		{
			ConnectionInternalFrame.this.cancelConnect();
		}
	}
}
