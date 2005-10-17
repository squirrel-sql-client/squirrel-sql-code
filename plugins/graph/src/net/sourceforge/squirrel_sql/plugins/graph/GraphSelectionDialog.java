package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;


public class GraphSelectionDialog extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GraphSelectionDialog.class);


	public JList lstControllers;
   public JButton btnOK;

   public GraphSelectionDialog(JFrame parent)
   {
      super(parent, true);
		// i18n[graph.selGraph=Select graph]
		setTitle(s_stringMgr.getString("graph.selGraph"));

      getContentPane().setLayout(new BorderLayout());

		// i18n[graph.selGraphToAdd=Select the graph to add the selected tables to]
		getContentPane().add(new JLabel(s_stringMgr.getString("graph.selGraphToAdd")), BorderLayout.NORTH);

      lstControllers = new JList();
      lstControllers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      getContentPane().add(lstControllers, BorderLayout.CENTER);

		// i18n[graph.btnOk=OK]
		btnOK = new JButton(s_stringMgr.getString("graph.btnOk"));
      getContentPane().add(btnOK, BorderLayout.SOUTH);

      setSize(300, 300);
   }
}
