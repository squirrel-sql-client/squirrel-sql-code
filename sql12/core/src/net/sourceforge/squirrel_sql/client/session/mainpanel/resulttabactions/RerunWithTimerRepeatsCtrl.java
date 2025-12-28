package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions;

import java.awt.Frame;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;

public class RerunWithTimerRepeatsCtrl
{
   private static final String PREFS_KEY_REPEAT_INTERVAL = "RerunWithTimerRepeatsCtrl.repeat.interval";

   private final RerunWithTimerRepeatsDlg _dlg;
   private boolean _ok;

   public RerunWithTimerRepeatsCtrl(Frame parentWindow)
   {
      _dlg = new RerunWithTimerRepeatsDlg(parentWindow);

      _dlg.txtSeconds.setInt(Props.getInt(PREFS_KEY_REPEAT_INTERVAL, 5));

      _dlg.btnOK.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());

      GUIUtils.initLocation(_dlg, 400, 130);
      GUIUtils.enableCloseByEscape(_dlg);

      GUIUtils.forceFocus(_dlg.txtSeconds);
      _dlg.setVisible(true);
   }

   private void onOk()
   {
      _ok = true;
      close();
   }

   private void close()
   {
      Props.putInt(PREFS_KEY_REPEAT_INTERVAL, _dlg.txtSeconds.getInt());
      _dlg.setVisible(true);
      _dlg.dispose();
   }

   public boolean isOk()
   {
      return _ok;
   }

   public int getRepeatSeconds()
   {
      return _dlg.txtSeconds.getInt();
   }
}
