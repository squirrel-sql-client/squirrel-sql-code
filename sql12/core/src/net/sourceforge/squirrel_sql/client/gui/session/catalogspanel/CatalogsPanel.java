package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class CatalogsPanel extends JPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CatalogsPanel.class);
	JLabel lblCatalogs;

	JComboBox catalogsCmb;
	JButton btnConfiCatalogLoading;

	public CatalogsPanel()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
		lblCatalogs = new JLabel(s_stringMgr.getString("CatalogsPanel.catalog"));
		add(lblCatalogs, gbc);


		catalogsCmb = new JComboBox();
		gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,2),0,0);
		add(catalogsCmb, gbc);

		gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,10),0,0);
		btnConfiCatalogLoading = GUIUtils.styleAsToolbarButton(new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.THREE_DOTS)));
		Dimension configButtonSize = new Dimension(catalogsCmb.getPreferredSize().height + 1, catalogsCmb.getPreferredSize().height + 1);
		btnConfiCatalogLoading.setPreferredSize(configButtonSize);
		btnConfiCatalogLoading.setToolTipText(s_stringMgr.getString("CatalogsPanel.configure.catalogs.to.load"));
		add(btnConfiCatalogLoading, gbc);
	}

	public void initSizeAndBackgroundAfterCatalogsComboFilled()
	{
		int preferredWidth = lblCatalogs.getPreferredSize().width + catalogsCmb.getPreferredSize().width + btnConfiCatalogLoading.getPreferredSize().width + 20;
		GUIUtils.setPreferredWidth(this, preferredWidth);
		GUIUtils.setMinimumWidth(this, preferredWidth);
		GUIUtils.setMaximumWidth(this, preferredWidth);
		GUIUtils.inheritBackground(this);
	}

}
