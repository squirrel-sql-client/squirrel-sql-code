package de.ixdb.squirrel_sql.plugins.cache;

import javax.swing.*;
import java.awt.*;


public class NamespaceDlg extends JDialog
{
   JTextField txtAliasNameTemplate;
   JTable tblNamespaces;

   public NamespaceDlg(JFrame mainFrame)
   {
      super(mainFrame, "Create alias for namespace");

      getContentPane().setLayout(new BorderLayout(0,3));

      JPanel pnlContent = new JPanel(new BorderLayout(0,5));



      pnlContent.add(new JLabel("Double click namespace to create alias"), BorderLayout.NORTH);

      tblNamespaces = new JTable();
      tblNamespaces.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      pnlContent.add(new JScrollPane(tblNamespaces), BorderLayout.CENTER);

      JPanel pnlBottom = new JPanel(new GridLayout(4,1));
      pnlBottom.add(new JLabel("Edit alias name template:"));
      pnlBottom.add(new JLabel("%server will be replaced by server name"));
      pnlBottom.add(new JLabel("%namespace will be replaced by namespace name"));

      txtAliasNameTemplate = new JTextField();
      pnlBottom.add(txtAliasNameTemplate);
      pnlContent.add(pnlBottom, BorderLayout.SOUTH);


      getContentPane().add(new JPanel(), BorderLayout.NORTH);
      getContentPane().add(pnlContent, BorderLayout.CENTER);

   }
}
