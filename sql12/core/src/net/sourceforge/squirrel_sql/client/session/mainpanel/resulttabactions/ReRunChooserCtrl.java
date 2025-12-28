package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import javax.swing.Action;
import javax.swing.JComponent;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.TabButton;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutUtil;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ButtonChooser;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ReRunChooserCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ReRunChooserCtrl.class);

   private final ISession _session;
   private final RerunCurrentSQLResultTabAction _actionDefault;
   private final RerunCurrentSQLResultTabAction _actionTimerRepeats;
   private ButtonChooser _btnChooser;


   public ReRunChooserCtrl(ISession session)
   {
      _btnChooser = new ButtonChooser();
      _session = session;
      _actionDefault = createDefaultReRunAction();
      _actionTimerRepeats = createTimerRepeatsReRunAction();

      TabButton btnDefault = new TabButton(_actionDefault);
      TabButton btnTimerRepeats = new TabButton(_actionTimerRepeats);

      _btnChooser.addButton(btnDefault);
      _btnChooser.addButton(btnTimerRepeats);

      if(RerunResultTabMode.getCurrentMode() == RerunResultTabMode.DEFAULT)
      {
         _btnChooser.setSelectedButton(btnDefault);
      }
      else
      {
         _btnChooser.setSelectedButton(btnTimerRepeats);
      }

      _btnChooser.setButtonSelectedListener(
            (btnNew, btnOld) -> RerunResultTabMode.setCurrentMode(btnNew == btnDefault ? RerunResultTabMode.DEFAULT : RerunResultTabMode.TIMER_REPEATS));
   }

   private RerunCurrentSQLResultTabAction createTimerRepeatsReRunAction()
   {
      RerunCurrentSQLResultTabAction ret = createDefaultReRunAction();

      ret.putValue(Action.SMALL_ICON, Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.RUN_TIMER));
      String description = s_stringMgr.getString("ReRunChooserCtrl.rerun.timered", ShortcutUtil.getKeystrokeString(ret.getKeyStroke()));
      ret.putValue(Action.SHORT_DESCRIPTION, description);
      ret.putValue(Action.LONG_DESCRIPTION, description);

      return ret;
   }

   private RerunCurrentSQLResultTabAction createDefaultReRunAction()
   {
      RerunCurrentSQLResultTabAction ret = new RerunCurrentSQLResultTabAction();
      ret.setSQLPanel(_session.getSQLPanelAPIOfActiveSessionWindow());
      return ret;
   }

   public JComponent getComponent()
   {
      return _btnChooser.getComponent();
   }

   public void setResultTab(ResultTab resultTab)
   {
      _actionDefault.setResultTab(resultTab);
      _actionTimerRepeats.setResultTab(resultTab);
   }
}
