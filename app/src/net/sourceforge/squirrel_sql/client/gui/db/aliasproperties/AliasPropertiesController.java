package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.DriverPropertiesController;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class AliasPropertiesController
{
   private static AliasPropertiesController _currentlyOpenInstance;

   private AliasPropertiesInternalFrame _frame;
   private ArrayList _iAliasPropertiesPanelController = new ArrayList();
   private IApplication _app;
   private SQLAlias _alias;

   public static void showAliasProperties(IApplication app, SQLAlias selectedAlias)
   {
      if(null == _currentlyOpenInstance)
      {
         _currentlyOpenInstance = new AliasPropertiesController(app, selectedAlias);
      }
      else
      {
         GUIUtils.moveToFront(_currentlyOpenInstance._frame);
      }

   }

   private AliasPropertiesController(IApplication app, SQLAlias selectedAlias)
   {
      _app = app;
      _alias = selectedAlias;
      _frame = new AliasPropertiesInternalFrame();

      _app.getMainFrame().addInternalFrame(_frame, false);

      GUIUtils.centerWithinDesktop(_frame);

      _frame.setVisible(true);


      _frame.btnOk.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      _frame.btnClose.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onClose();
         }

      });


      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            performClose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      _frame.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      _frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      _frame.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      _frame.getRootPane().getActionMap().put("CloseAction", closeAction);

      loadTabs();

   }

   private void loadTabs()
   {
      _iAliasPropertiesPanelController.add(new SchemaPropertiesController());
      _iAliasPropertiesPanelController.add(new DriverPropertiesController());


      for (int i = 0; i < _iAliasPropertiesPanelController.size(); i++)
      {
         IAliasPropertiesPanelController aliasPropertiesController = (IAliasPropertiesPanelController) _iAliasPropertiesPanelController.get(i);
         aliasPropertiesController.initialize(_alias, _app);

         int index = _frame.tabPane.getTabCount();
         _frame.tabPane.add(aliasPropertiesController.getTitle(), aliasPropertiesController.getPanelComponent());
         _frame.tabPane.setToolTipTextAt(index, aliasPropertiesController.getHint());

      }


   }

   private void performClose()
   {
      _currentlyOpenInstance = null;
      _frame.dispose();
   }

   private void onOK()
   {
      for (int i = 0; i < _iAliasPropertiesPanelController.size(); i++)
      {
         IAliasPropertiesPanelController aliasPropertiesController = (IAliasPropertiesPanelController) _iAliasPropertiesPanelController.get(i);
         aliasPropertiesController.applyChanges();
      }
      performClose();
   }

   private void onClose()
   {
      performClose();
   }

}
