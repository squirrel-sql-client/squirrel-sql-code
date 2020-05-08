package net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.toolbarbuttonchooser.ToolbarButtonChooserUtil;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ButtonChooser;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

public class SQLWorksheetTypeChooser
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLWorksheetTypeChooser.class);

   private NewSQLWorksheetAction _action;
   private final ButtonChooser _buttonChooser = new ButtonChooser(true);

   public SQLWorksheetTypeChooser(NewSQLWorksheetAction action)
   {
      _action = action;

      initActionAndListeners();

      setEnabled(_action.isEnabled());

      _action.setEnabledListener(b -> setEnabled(b));
   }

   private void setEnabled(boolean enabled)
   {
      _buttonChooser.setChooserEnabled(enabled);
   }

   public JComponent getComponent()
   {
      return _buttonChooser.getComponent();
   }

   private void initActionAndListeners()
   {
      SquirrelResources rsrc = Main.getApplication().getResources();
      ImageIcon iconAddSqlTab = rsrc.getIcon(SquirrelResources.IImageNames.ADD_TAB);
      ImageIcon iconNewSqlWorkSheet = rsrc.getIcon(SquirrelResources.IImageNames.NEW_SQL_WORKSHEET);

      String textNewSqlWorkSheet = s_stringMgr.getString("SQLWorksheetTypeChooser.newSqlWorkSheet") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);
      String textAddSqlTab = s_stringMgr.getString("SQLWorksheetTypeChooser.newSqlTab") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);


      JButton btnNewSqlWorkSheet = new JButton(textNewSqlWorkSheet, iconNewSqlWorkSheet);
      btnNewSqlWorkSheet.addActionListener(e -> _action.actionPerformed(e));
      _buttonChooser.addButton(btnNewSqlWorkSheet);

      JButton btnAddSqlTab = new JButton(textAddSqlTab, iconAddSqlTab);
      btnAddSqlTab.addActionListener(e -> _action.actionPerformed(e));
      _buttonChooser.addButton(btnAddSqlTab);

      switch (SQLWorksheetTypeEnum.getSelectedType())
      {
         case SQL_WORKSHEET:
            _buttonChooser.setSelectedButton(btnNewSqlWorkSheet);
            break;
         case SQL_TAB:
            _buttonChooser.setSelectedButton(btnAddSqlTab);
            break;
         default:
            throw new IllegalStateException("Unknown Type " + SQLWorksheetTypeEnum.getSelectedType());
      }

      _buttonChooser.setButtonSelectedListener((button, formerSelectedButton) -> onButtonSelected(button, btnNewSqlWorkSheet, btnAddSqlTab));
   }

   private void onButtonSelected(AbstractButton button, JButton btnNewSqlWorkSheet, JButton btnAddSqlTab)
   {
      if(button == btnNewSqlWorkSheet)
      {
         SQLWorksheetTypeEnum.SQL_WORKSHEET.saveSelected();
      }
      else if(button == btnAddSqlTab)
      {
         SQLWorksheetTypeEnum.SQL_TAB.saveSelected();
      }
   }
}
