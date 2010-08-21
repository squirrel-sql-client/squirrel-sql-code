package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public class AliasPropertiesController
{
   private static HashMap<IIdentifier, AliasPropertiesController> _currentlyOpenInstancesByAliasID = 
       new HashMap<IIdentifier, AliasPropertiesController>();

   private AliasPropertiesInternalFrame _frame;
   private ArrayList<IAliasPropertiesPanelController> _iAliasPropertiesPanelControllers = new ArrayList<IAliasPropertiesPanelController>();
   private IApplication _app;
   private SQLAlias _alias;

   public static void showAliasProperties(IApplication app, SQLAlias selectedAlias)
   {
      AliasPropertiesController openProps = _currentlyOpenInstancesByAliasID.get(selectedAlias.getIdentifier());
      if(null == openProps)
      {
         _currentlyOpenInstancesByAliasID.put(selectedAlias.getIdentifier(), new AliasPropertiesController(app, selectedAlias));
      }
      else
      {
         openProps._frame.moveToFront();
      }

   }

   private AliasPropertiesController(IApplication app, SQLAlias selectedAlias)
   {
      _app = app;
      _alias = selectedAlias;
      _frame = new AliasPropertiesInternalFrame(_alias.getName(), app);

      _app.getMainFrame().addWidget(_frame);

      DialogWidget.centerWithinDesktop(_frame);

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
        private static final long serialVersionUID = 1L;

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
      _iAliasPropertiesPanelControllers.add(new SchemaPropertiesController(_alias, _app));
      _iAliasPropertiesPanelControllers.add(new DriverPropertiesController(_alias, _app));
      _iAliasPropertiesPanelControllers.add(new ColorPropertiesController(_alias, _app));
      _iAliasPropertiesPanelControllers.add(new ConnectionPropertiesController(_alias, _app));
      
      IAliasPropertiesPanelController[] pluginAliasPropertiesPanelControllers =
         _app.getPluginManager().getAliasPropertiesPanelControllers(_alias);

      _iAliasPropertiesPanelControllers.addAll(Arrays.asList(pluginAliasPropertiesPanelControllers));


      for (int i = 0; i < _iAliasPropertiesPanelControllers.size(); i++)
      {
         IAliasPropertiesPanelController aliasPropertiesController = _iAliasPropertiesPanelControllers.get(i);

         int index = _frame.tabPane.getTabCount();
         _frame.tabPane.add(aliasPropertiesController.getTitle(), aliasPropertiesController.getPanelComponent());
         _frame.tabPane.setToolTipTextAt(index, aliasPropertiesController.getHint());

      }
   }

   private void performClose()
   {
      _currentlyOpenInstancesByAliasID.remove(_alias.getIdentifier());
      _frame.dispose();
   }

   private void onOK()
   {
      for (int i = 0; i < _iAliasPropertiesPanelControllers.size(); i++)
      {
         IAliasPropertiesPanelController aliasPropertiesController = _iAliasPropertiesPanelControllers.get(i);
         aliasPropertiesController.applyChanges();
      }
      performClose();
   }

   private void onClose()
   {
      performClose();
   }

}
