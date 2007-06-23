package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;


public class GraphSelectionDialog extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GraphSelectionDialog.class);


	JList lstControllers;
   JButton btnCreateNewGraph;
   JButton btnOK;
   JButton btnCancel;

   public GraphSelectionDialog(JFrame parent)
   {
      super(parent, true);
		// i18n[graph.selGraph=Select graph]
		setTitle(s_stringMgr.getString("graph.selGraph"));

      getContentPane().setLayout(new GridBagLayout());


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[graph.selGraphToAdd=Select the graph to add the selected tables to]
		getContentPane().add(new JLabel(s_stringMgr.getString("graph.selGraphToAdd")), gbc);

      lstControllers = new JList();
      lstControllers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);
      getContentPane().add(new JScrollPane(lstControllers), gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      getContentPane().add(createButtonPanel(), gbc);

      setSize(400, 300);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      // i18n[graph.btnOk=OK]
		btnOK = new JButton(s_stringMgr.getString("graph.btnOk"));
      ret.add(btnOK, gbc);
      getRootPane().setDefaultButton(btnOK);


      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      // i18n[graph.btnCreateNewGraph=Create new graph]
		btnCreateNewGraph = new JButton(s_stringMgr.getString("graph.btnCreateNewGraph"));
      ret.add(btnCreateNewGraph, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      // i18n[graph.btnCancel=Cancel]
		btnCancel = new JButton(s_stringMgr.getString("graph.btnCancel"));
      ret.add(btnCancel, gbc);


      return ret;
   }
}
