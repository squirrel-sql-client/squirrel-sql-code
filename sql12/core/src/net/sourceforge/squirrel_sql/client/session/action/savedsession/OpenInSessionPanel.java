package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;

public class OpenInSessionPanel extends JPanel
{
   private static final String PREF_OPEN_IN_NEW_SESSION = "SavedSession.OpenInSessionDlg.openInNewSession";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OpenInSessionPanel.class);

   private JRadioButton _radOpenInNewSession;
   private JRadioButton _radOpenInCurrentSession;
   private JLabel _lblDecide;
   private MultipleLineLabel _multiLblDecide;

   public OpenInSessionPanel(String savedSessionName, boolean warnDiscardExistingSqlEditors)
   {
      setLayout(new GridBagLayout());
      layoutUI(savedSessionName, warnDiscardExistingSqlEditors);

      ButtonGroup bg = new ButtonGroup();
      bg.add(_radOpenInNewSession);
      bg.add(_radOpenInCurrentSession);

      _radOpenInNewSession.setSelected(Props.getBoolean(PREF_OPEN_IN_NEW_SESSION, true));
      _radOpenInCurrentSession.setSelected(!Props.getBoolean(PREF_OPEN_IN_NEW_SESSION, true));

      _radOpenInNewSession.addActionListener(e -> savePref());
      _radOpenInCurrentSession.addActionListener(e -> savePref());
   }


   private void savePref()
   {
      Props.putBoolean(PREF_OPEN_IN_NEW_SESSION, _radOpenInNewSession.isSelected());
   }

   private void layoutUI(String savedSessionName, boolean warnDiscardExistingSqlEditors)
   {
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
      if(StringUtilities.isEmpty(savedSessionName, true))
      {
         _lblDecide = new JLabel(s_stringMgr.getString("OpenInSessionPanel.decide.label.exc.name"));
         add(_lblDecide, gbc);
      }
      else
      {
         _multiLblDecide = new MultipleLineLabel(s_stringMgr.getString("OpenInSessionPanel.decide.label.inc.name", savedSessionName));
         add(_multiLblDecide, gbc);
      }

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0);
      _radOpenInNewSession = new JRadioButton(s_stringMgr.getString("OpenInSessionPanel.open.in.new.session"));
      add(_radOpenInNewSession, gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3, 5, 0, 5), 0, 0);
      if( warnDiscardExistingSqlEditors )
      {
         _radOpenInCurrentSession = new JRadioButton(s_stringMgr.getString("OpenInSessionPanel.open.in.existing.session.warn.discard"));
      }
      else
      {
         _radOpenInCurrentSession = new JRadioButton(s_stringMgr.getString("OpenInSessionPanel.open.in.existing.session"));
      }
      add(_radOpenInCurrentSession, gbc);
   }

   public boolean isOpenInNewSession()
   {
      return _radOpenInNewSession.isSelected();
   }

   public void setEnabledOpenInSessionPanel(boolean b)
   {
      if(null != _lblDecide)
      {
         _lblDecide.setEnabled(b);
      }
      if(null != _multiLblDecide)
      {
         _multiLblDecide.setEnabled(b);
      }

      _radOpenInNewSession.setEnabled(b);
      _radOpenInCurrentSession.setEnabled(b);
   }
}
