package net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class SQLWorksheetTypeChooser
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLWorksheetTypeChooser.class);

   private ImageIcon _iconAddSqlTab;
   private ImageIcon _iconNewSqlWorkSheet;

   private String _textNewSqlWorkSheet;
   private String _textAddSqlTab;


   private ComboButton _btnCombo = new ComboButton();
   private JButton _btnAction;

   private JPopupMenu _comboPopUp;

   private NewSQLWorksheetAction _action;
   private final JPanel _component;

   public SQLWorksheetTypeChooser(NewSQLWorksheetAction action)
   {
      _action = action;

      _component = createPanel(_iconNewSqlWorkSheet);

      initActionAndListeners();

      setEnabled(_action.isEnabled());

      _action.setEnabledListener(b -> setEnabled(b));
   }

   private void setEnabled(boolean enabled)
   {
      _btnCombo.setEnabled(enabled);
      _btnAction.setEnabled(enabled);
   }

   public JComponent getComponent()
   {
      return _component;
   }

   private void initActionAndListeners()
   {
      SquirrelResources rsrc = Main.getApplication().getResources();
      _iconAddSqlTab = rsrc.getIcon(SquirrelResources.IImageNames.ADD_TAB);
      _iconNewSqlWorkSheet = rsrc.getIcon(SquirrelResources.IImageNames.NEW_SQL_WORKSHEET);

      _textNewSqlWorkSheet = s_stringMgr.getString("SQLWorksheetTypeChooser.newSqlWorkSheet") + " (" + rsrc.getAcceleratorString(_action) + ")";
      _textAddSqlTab = s_stringMgr.getString("SQLWorksheetTypeChooser.newSqlTab") + " (" + rsrc.getAcceleratorString(_action) + ")";


      _btnCombo.addActionListener( e -> onShowPopup());

      _comboPopUp = new JPopupMenu();

      JMenuItem mnuNewSqlWorkSheet = new JMenuItem(_textNewSqlWorkSheet, _iconNewSqlWorkSheet);
      mnuNewSqlWorkSheet.addActionListener(e -> onNewSqlWorkSheet() );
      _comboPopUp.add(mnuNewSqlWorkSheet);

      JMenuItem mnuAddSqlTab = new JMenuItem(_textAddSqlTab, _iconAddSqlTab);
      mnuAddSqlTab.addActionListener(e -> onAddSqlTab());
      _comboPopUp.add(mnuAddSqlTab);


      _btnAction.addActionListener(e -> _action.actionPerformed(e));

      switch (SQLWorksheetTypeEnum.getSelecteType())
      {
         case SQL_WORKSHEET:
            onNewSqlWorkSheet();
            break;
         case SQL_TAB:
            onAddSqlTab();
            break;
         default:
            throw new IllegalStateException("Unknown Type " + SQLWorksheetTypeEnum.getSelecteType());
      }
   }

   private void onAddSqlTab()
   {
      _btnAction.setIcon(_iconAddSqlTab);
      _btnAction.setToolTipText(_textAddSqlTab);
      _action.getActionChannel().updateIconAndText(_iconAddSqlTab, _textAddSqlTab);
      SQLWorksheetTypeEnum.SQL_TAB.saveSelected();
   }

   private void onNewSqlWorkSheet()
   {
      _btnAction.setIcon(_iconNewSqlWorkSheet);
      _btnAction.setToolTipText(_textNewSqlWorkSheet);
      _action.getActionChannel().updateIconAndText(_iconNewSqlWorkSheet, _textNewSqlWorkSheet);
      SQLWorksheetTypeEnum.SQL_WORKSHEET.saveSelected();
   }

   private void onShowPopup()
   {
      _comboPopUp.show(_btnAction, 0, _btnAction.getHeight());
   }

   private JPanel createPanel(ImageIcon icon)
   {
      JPanel ret  = new JPanel(new GridBagLayout());

      _btnAction = new JButton(icon);
      GUIUtils.styleAsToolbarButton(_btnAction);

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 1,1);
      ret.add(_btnAction, gbc);

      GUIUtils.styleAsToolbarButton(_btnCombo);
      _btnCombo.setPreferredSize(new Dimension(12, 28));
      _btnCombo.setMinimumSize(new Dimension(12, 28));

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,2), 1,1);
      ret.add(_btnCombo, gbc);

      ret.setPreferredSize(new Dimension(38,28));
      ret.setMaximumSize(new Dimension(38,28));
      return ret;
   }

}
