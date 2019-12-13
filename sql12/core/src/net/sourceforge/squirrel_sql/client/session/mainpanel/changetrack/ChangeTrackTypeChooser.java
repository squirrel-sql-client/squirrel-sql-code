package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ChangeTrackAction;
import net.sourceforge.squirrel_sql.client.session.action.toolbarbuttonchooser.ToolbarButtonChooserUtil;
import net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice.SQLWorksheetTypeEnum;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ButtonChooser;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChangeTrackTypeChooser
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTrackTypeChooser.class);
   private final SQLPanelApiChangedListener _sqlPanelApiChangedListener;

   private ChangeTrackAction _action;
   private final ButtonChooser _buttonChooser = new ButtonChooser();
   private JButton _btnTrackManual;
   private JButton _btnTrackFile;
   private JButton _btnTrackGit;
   private boolean _dontReactToButtonSelect = false;

   /**
    * For a session window there is one instance of this class, which may server several SQLPanelAPIs when several SQL tabs are open.
    *
    * @param action This is the only instance ChangeTrackAction.
    *               It could also be accessed via {@link IApplication#getActionCollection()}  }
    */
   public ChangeTrackTypeChooser(ChangeTrackAction action, ISession session)
   {
      _action = action;

      initActionAndListeners();

      _buttonChooser.setChooserEnabled(_action.isEnabled());

      _sqlPanelApiChangedListener = newSqlPanelAPIsChangeTrackType -> onSqlPanelApiChanged(newSqlPanelAPIsChangeTrackType);

      _action.addSQLPanelApiChangedListener(_sqlPanelApiChangedListener);

      session.addSimpleSessionListener(() -> _action.removeSQLPanelApiChangedListener(_sqlPanelApiChangedListener));
   }

   /**
    * @param sqlPanelAPI This is the just activated SQLPanelAPI.
    *                    The method must not change the SQLPanelAPI's state only just because it became active.
    *                    I may only adjust this ChangeTrackTypeChooser's state to the SQLPanelAPI's state
    */
   private void onSqlPanelApiChanged(ChangeTrackTypeEnum newSqlPanelAPIsChangeTrackType)
   {
      if(null == newSqlPanelAPIsChangeTrackType)
      {
         _buttonChooser.setChooserEnabled(false);
         return;
      }

      _buttonChooser.setChooserEnabled(true);

      try
      {
         _dontReactToButtonSelect = true;
         adjustChooserToType(newSqlPanelAPIsChangeTrackType);
      }
      finally
      {
         _dontReactToButtonSelect = false;
      }
   }

   public JComponent getComponent()
   {
      return _buttonChooser;
   }

   private void initActionAndListeners()
   {
      SquirrelResources rsrc = Main.getApplication().getResources();
      ImageIcon iconTrackManual = rsrc.getIcon(SquirrelResources.IImageNames.CHANGE_TRACK_MANUAL);
      ImageIcon iconTrackFile = rsrc.getIcon(SquirrelResources.IImageNames.CHANGE_TRACK_FILE);
      ImageIcon iconTrackGit = rsrc.getIcon(SquirrelResources.IImageNames.CHANGE_TRACK_GIT);

      String textTrackManual = s_stringMgr.getString("ChangeTrackTypeChooser.manual") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);
      String textTrackFile = s_stringMgr.getString("ChangeTrackTypeChooser.file") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);
      String textTrackGit = s_stringMgr.getString("ChangeTrackTypeChooser.git") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);

      String toolTipTrackManual = s_stringMgr.getString("ChangeTrackTypeChooser.manual.tooltip") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);
      String toolTipTrackFile = s_stringMgr.getString("ChangeTrackTypeChooser.file.tooltip") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);
      String toolTipTrackGit = s_stringMgr.getString("ChangeTrackTypeChooser.git.tooltip") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);


      _btnTrackManual = new JButton(textTrackManual, iconTrackManual);
      _btnTrackManual.setToolTipText(toolTipTrackManual);
      prolongTooltipDismissTime(_btnTrackManual);
      _btnTrackManual.addActionListener(e -> _action.actionPerformed(e));
      _buttonChooser.addButton(_btnTrackManual);

      _btnTrackFile = new JButton(textTrackFile, iconTrackFile);
      _btnTrackFile.setToolTipText(toolTipTrackFile);
      prolongTooltipDismissTime(_btnTrackFile);
      // TODO: Not clickable
      //btnTrackFile.addActionListener(e -> _action.actionPerformed(e));
      _buttonChooser.addUnclickableButton(_btnTrackFile);

      _btnTrackGit = new JButton(textTrackGit, iconTrackGit);
      _btnTrackGit.setToolTipText(toolTipTrackGit);
      prolongTooltipDismissTime(_btnTrackGit);
      _btnTrackGit.addActionListener(e -> _action.actionPerformed(e));
      _buttonChooser.addButton(_btnTrackGit);


      adjustChooserToType(ChangeTrackTypeEnum.getPreference());

      _buttonChooser.setButtonSelectedListener((button, formerSelectedButton) -> onButtonSelected(button, _btnTrackManual, _btnTrackFile, _btnTrackGit));
   }

   private void prolongTooltipDismissTime(JButton btn)
   {
      final int defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();

      btn.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseEntered(MouseEvent me)
         {
            ToolTipManager.sharedInstance().setDismissDelay(30000);
         }

         @Override
         public void mouseExited(MouseEvent me)
         {
            ToolTipManager.sharedInstance().setDismissDelay(defaultDismissTimeout);
         }
      });
   }

   private void adjustChooserToType(ChangeTrackTypeEnum type)
   {
      switch (type)
      {
         case MANUAL:
            _buttonChooser.setSelectedButton(_btnTrackManual);
            break;
         case FILE:
            _buttonChooser.setSelectedButton(_btnTrackFile);
            break;
         case GIT:
            _buttonChooser.setSelectedButton(_btnTrackGit);
            break;
         default:
            throw new IllegalStateException("Unknown Type " + SQLWorksheetTypeEnum.getSelectedType());
      }
   }

   private void onButtonSelected(AbstractButton button, JButton btnTrackManual, JButton btnTrackFile, JButton btnTrackGit)
   {
      if(_dontReactToButtonSelect)
      {
         return;
      }

      ChangeTrackTypeEnum selectedType;
      if(button == btnTrackManual)
      {
         selectedType = ChangeTrackTypeEnum.MANUAL;
      }
      else if(button == btnTrackFile)
      {
         selectedType = ChangeTrackTypeEnum.FILE;
      }
      else if(button == btnTrackGit)
      {
         selectedType = ChangeTrackTypeEnum.GIT;
      }
      else
      {
         throw new IllegalStateException("How could we get here?");
      }

      selectedType.savePreference();

      _action.changeTrackTypeChangedForCurrentSqlPanel(selectedType);
   }
}
