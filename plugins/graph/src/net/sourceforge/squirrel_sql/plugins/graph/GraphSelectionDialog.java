package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;


public class GraphSelectionDialog extends JDialog
{
   public JList lstControllers;
   public JButton btnOK;

   public GraphSelectionDialog(JFrame parent)
   {
      super(parent, true);
      setTitle("Select graph");

      getContentPane().setLayout(new BorderLayout());

      getContentPane().add(new JLabel("Select the graph to add the selected tables to"), BorderLayout.NORTH);

      lstControllers = new JList();
      lstControllers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      getContentPane().add(lstControllers, BorderLayout.CENTER);

      btnOK = new JButton("OK");
      getContentPane().add(btnOK, BorderLayout.SOUTH);

      setSize(300, 300);
   }
}
