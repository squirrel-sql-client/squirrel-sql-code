package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EscapeAction extends AbstractAction
{
   private JButton _btnUnhighlightResult;
   private JButton _btnHideFindPanel;
   private Timer _timer;
   private boolean _hitLately;

   public EscapeAction(JButton btnUnhighlightResult, JButton btnHideFindPanel)
   {
      super("DataSetFind.EscapeAction");
      _btnUnhighlightResult = btnUnhighlightResult;
      _btnHideFindPanel = btnHideFindPanel;

      _timer = new Timer(1000, new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _hitLately = false;
         }
      });

      _timer.setRepeats(false);
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      if(_hitLately)
      {
         _btnHideFindPanel.doClick();
         _timer.stop();
         _hitLately = false;
      }
      else
      {
         _btnUnhighlightResult.doClick();
         _timer.restart();
         _hitLately = true;
      }
   }
}
