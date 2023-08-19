package net.sourceforge.squirrel_sql.client.gui.session.catalogscombo;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

public class CatalogsPanel extends JPanel 
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CatalogsPanel.class);

	private static final ILogger s_log = LoggerController.createLogger(CatalogsPanel.class);

	private ISession _session;
	private JComponent _parent;
	private SQLCatalogsComboBox _catalogsCmb;
	private PropertyChangeListener _connectionPropetryListener;
	private JButton _btnConfiCataloLoading;

	public CatalogsPanel(ISession session, JComponent parent)
	{
		_session = session;
		_parent = parent;

		_connectionPropetryListener = evt -> onConnectionPropertyChanged(evt);

		setVisible(false);

		init();
	}

	private void onConnectionPropertyChanged(PropertyChangeEvent evt)
	{
		try
		{
			final String propName = evt.getPropertyName();
			if (propName == null || propName.equals(ISQLConnection.IPropertyNames.CATALOG))
			{
				if (_catalogsCmb != null)
				{
					final ISQLConnection conn = _session.getSQLConnection();
					if (!StringUtils.equals(conn.getCatalog(), _catalogsCmb.getSelectedCatalog()))
					{
						_catalogsCmb.setSelectedCatalog(conn.getCatalog());
					}
				}
			}
		}
		catch (SQLException e)
		{
			s_log.error("Error processing Property ChangeEvent", e);
		}
	}


	private void init()
	{
		try
		{
			if(false == _session.getSQLConnection().getSQLMetaData().supportsCatalogs())
			{
				return;
			}

			final String[] catalogs = _session.getSQLConnection().getSQLMetaData().getCatalogs();

			if(null == catalogs || 0 == catalogs.length)
			{
				return;
			}

			final String selected = _session.getSQLConnection().getCatalog();

			_session.getSQLConnection().removePropertyChangeListener(_connectionPropetryListener);
			_session.getSQLConnection().addPropertyChangeListener(_connectionPropetryListener);

			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					initGuiInForeground(catalogs, selected);
				}
			});


		}
		catch (SQLException e)
		{
			s_log.error(s_stringMgr.getString("CatalogsPanel.error.retrievecatalog"), e);
		}
	}

	private void initGuiInForeground(String[] catalogs, String selected)
	{
		setLayout(new GridBagLayout());
		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
		JLabel lblCatalogs = new JLabel(s_stringMgr.getString("CatalogsPanel.catalog"));
		add(lblCatalogs, gbc);


		_catalogsCmb = new SQLCatalogsComboBox();
		gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,2),0,0);
		add(_catalogsCmb, gbc);
		_catalogsCmb.setCatalogs(catalogs, selected);

		gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,10),0,0);
		_btnConfiCataloLoading = GUIUtils.styleAsToolbarButton(new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.THREE_DOTS)));
		Dimension configButtonSize = new Dimension(_catalogsCmb.getPreferredSize().height + 1, _catalogsCmb.getPreferredSize().height + 1);
		_btnConfiCataloLoading.setPreferredSize(configButtonSize);
		_btnConfiCataloLoading.setToolTipText(s_stringMgr.getString("CatalogsPanel.configure.catalogs.to.load"));
		add(_btnConfiCataloLoading, gbc);

		int preferredWidth = lblCatalogs.getPreferredSize().width + _catalogsCmb.getPreferredSize().width + _btnConfiCataloLoading.getPreferredSize().width + 20;
		GUIUtils.setPreferredWidth(this, preferredWidth);
		GUIUtils.setMinimumWidth(this, preferredWidth);
		GUIUtils.setMaximumWidth(this, preferredWidth);

		GUIUtils.inheritBackground(this);

		setVisible(true);

		_parent.validate();

		_catalogsCmb.addActionListener(new CatalogsComboListener(_session, this));
	}

	public void refreshCatalogs()
	{
		removeAll();

		_session.getApplication().getThreadPool().addTask(() -> init());
	}

	public String getSelectedCatalog()
	{
		if (   null == _catalogsCmb
			 || false == _catalogsCmb.getSelectedItem() instanceof String) // Happens when instanceof SQLCatalogsComboBox.NoCatalogPlaceHolder. Perhaps NoCatalogPlaceHolder should be removed when it causes more trouble.
		{
			return null;
		}
		else
		{
			return (String) _catalogsCmb.getSelectedItem();
		}
	}

}
