package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class AdditionalCatalogsDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AdditionalCatalogsDlg.class);

   final JList<CatalogChecked> chkLstCatalogs;
   final JButton btnOk;

   public AdditionalCatalogsDlg(Window owner)
   {
      super(owner);
      setModal(true);
      setTitle(s_stringMgr.getString("AdditionalCatalogsDlg.title"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5),0,0 );
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("AdditionalCatalogsDlg.explain")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5),0,0 );
      chkLstCatalogs = new JList<>();
      getContentPane().add(new JScrollPane(chkLstCatalogs), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0 );
      btnOk = new JButton(s_stringMgr.getString("AdditionalCatalogsDlg.ok"));
      getContentPane().add(btnOk, gbc);
   }
}
