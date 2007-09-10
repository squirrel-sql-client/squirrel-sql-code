package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;
import java.util.Vector;
import java.util.Arrays;


public class NamespaceCtrl
{
   private NamespaceDlg _dlg;
   private NamespaceCtrlListener _namespaceCtrlListener;
   private ISession _session;

   private String[] _cols = new String[]{"Namespace", "Associated aliases"};

   private static final String PREFS_KEY_ALIAS_NAME_TEMPLATE = "Squirrel.ixdb.aliasNameTemplate";
   private static final String ALIAS_NAME_TEMPLATE_DEFAULT = "Cache %server %namespace";


   public NamespaceCtrl(ISession session, String[][] nameSpacesAndAliases, NamespaceCtrlListener namespaceCtrlListener)
   {
      _session = session;

      _session.getApplication().getSessionManager().addSessionListener(new SessionAdapter()
      {
         public void sessionClosed(SessionEvent evt)
         {
            _dlg.setVisible(false);
            _dlg.dispose();
         }
      });

      _namespaceCtrlListener = namespaceCtrlListener;
      _dlg = new NamespaceDlg(session.getApplication().getMainFrame());

      DefaultTableModel dtm =
         new DefaultTableModel()
         {
            public boolean isCellEditable(int row, int column)
            {
               return false;
            }
         };

      dtm.setDataVector(nameSpacesAndAliases, _cols);

      _dlg.tblNamespaces.setModel(dtm);

      _dlg.tblNamespaces.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            onMouseClicked(e);
         }

      });

      String aliasNameTemplate = Preferences.userRoot().get(PREFS_KEY_ALIAS_NAME_TEMPLATE, ALIAS_NAME_TEMPLATE_DEFAULT);

      _dlg.txtAliasNameTemplate.setText(aliasNameTemplate);

      _dlg.setSize(400, 400);

      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);


   }

   private void onMouseClicked(MouseEvent e)
   {
      if(1 < e.getClickCount())
      {
         int selRow =_dlg.tblNamespaces.getSelectedRow();

         if(-1 == selRow)
         {
            return;
         }

         DefaultTableModel dtm =(DefaultTableModel) _dlg.tblNamespaces.getModel();

         Vector dataVector = dtm.getDataVector();

         Vector rowVector =  (Vector) dataVector.get(selRow);
         String[] selNamespaceAndAlias = (String[]) rowVector.toArray(new String[rowVector.size()]);

         if(0 < selNamespaceAndAlias[1].length())
         {
            String msg =
               "The selected namespace already has one or more aliases.\n" +
               "Do you want to create another alias for the selected namespace?.";

            if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_dlg, msg, "Duplicate alias", JOptionPane.YES_NO_CANCEL_OPTION))
            {
               return;
            }
         }

         String aliasNameTemplate = _dlg.txtAliasNameTemplate.getText();

         if(null == aliasNameTemplate || 0 == aliasNameTemplate.trim().length())
         {
            aliasNameTemplate = ALIAS_NAME_TEMPLATE_DEFAULT;
         }

         Preferences.userRoot().put(PREFS_KEY_ALIAS_NAME_TEMPLATE, aliasNameTemplate);


         String newAliasName = _namespaceCtrlListener.nameSpaceSelected(_session, selNamespaceAndAlias[0], aliasNameTemplate);

         if(0 == selNamespaceAndAlias[1].length())
         {
            selNamespaceAndAlias[1] = newAliasName;
         }
         else
         {
            selNamespaceAndAlias[1] += ";" + newAliasName;
         }

         rowVector.remove(1);
         rowVector.add(selNamespaceAndAlias[1]);

         Vector colsVector = new Vector(Arrays.asList(_cols));
         dtm.setDataVector(dataVector, colsVector);


         _dlg.tblNamespaces.getSelectionModel().setSelectionInterval(selRow, selRow);
      }
   }

}
