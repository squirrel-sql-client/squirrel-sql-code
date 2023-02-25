package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class AliasPropertiesInternalFrame extends DialogWidget
{
   private static final String PREF_KEY_ALIAS_PROPS_SHEET_WIDTH = "Squirrel.aliasPropsSheetWidth";
   private static final String PREF_KEY_ALIAS_PROPS_SHEET_HEIGHT = "Squirrel.aliasPropsSheetHeight";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasPropertiesInternalFrame.class);


   JTabbedPane tabPane;
   JButton btnOk;
   JButton btnClose;

   AliasPropertiesInternalFrame(String title)
   {
      super(s_stringMgr.getString("AliasPropertiesInternalFrame.title", title), true, Main.getApplication());

      setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
      makeToolWindow(true);
      setSize(getDimension());

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      // i18n[AliasPropertiesInternalFrame.title=Properties for Alias: {0}]
      getContentPane().add(new JLabel(s_stringMgr.getString("AliasPropertiesInternalFrame.title", title)), gbc);



      tabPane = new JTabbedPane();
      gbc = new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
      getContentPane().add(tabPane, gbc);

      JPanel pnlButtons = new JPanel();
      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
      getContentPane().add(pnlButtons, gbc);

      // i18n[AliasPropertiesInternalFrame.ok=OK]
      btnOk = new JButton(s_stringMgr.getString("AliasPropertiesInternalFrame.ok"));
      pnlButtons.add(btnOk);

      // i18n[AliasPropertiesInternalFrame.close=Close]
      btnClose = new JButton(s_stringMgr.getString("AliasPropertiesInternalFrame.close"));
      pnlButtons.add(btnClose);
   }

   private Dimension getDimension()
   {
      return new Dimension(
         Props.getInt(PREF_KEY_ALIAS_PROPS_SHEET_WIDTH, 700),
         Props.getInt(PREF_KEY_ALIAS_PROPS_SHEET_HEIGHT, 700)
      );
   }


   public void dispose()
   {
      Dimension size = getSize();
      Props.putInt(PREF_KEY_ALIAS_PROPS_SHEET_WIDTH, size.width);
      Props.putInt(PREF_KEY_ALIAS_PROPS_SHEET_HEIGHT, size.height);

      super.dispose();
   }

}
