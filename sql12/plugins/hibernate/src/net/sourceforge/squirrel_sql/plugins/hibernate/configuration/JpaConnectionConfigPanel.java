package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

public class JpaConnectionConfigPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JpaConnectionConfigPanel.class);

   JTextField txtPersistenceUnitName;


   public JpaConnectionConfigPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,3,1,1,1, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH, new Insets(5,5,0,5),0,0);
      // i18n[HibernateConfigPanel.toObtainSessionFactPersUnit=persitence-unit name:]
      add(new JLabel(s_stringMgr.getString("HibernatePanel.information")), gbc);

      gbc = new GridBagConstraints(0,1,3,1,1,1, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      MultipleLineLabel lblDescription = new MultipleLineLabel(s_stringMgr.getString("HibernatePanel.describeJPA"));
      JScrollPane scrollPane = new JScrollPane(lblDescription);
      scrollPane.setMinimumSize(new Dimension(200, 150));
      add(scrollPane, gbc);
      SwingUtilities.invokeLater(() -> lblDescription.scrollRectToVisible(new Rectangle(0, 0)));



      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.toObtainSessionFactPersUnit=persitence-unit name:]
      add(new JLabel(s_stringMgr.getString("HibernatePanel.toObtainSessionFactPersUnit")), gbc);

      gbc = new GridBagConstraints(1,2,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5),0,0);
      txtPersistenceUnitName = new JTextField();
      add(txtPersistenceUnitName, gbc);
   }

}
