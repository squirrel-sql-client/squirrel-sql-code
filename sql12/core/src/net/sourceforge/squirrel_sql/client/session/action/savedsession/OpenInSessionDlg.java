package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class OpenInSessionDlg extends JDialog
{
   private static final String PREF_OPEN_IN_NEW_SESSION = "SavedSession.OpenInSessionDlg.openInNewSession";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OpenInSessionDlg.class);

   private JRadioButton _radOpenInNewSession;
   private JRadioButton _radOpenInCurrentSession;
   private JButton _btnOk;
   private JButton _btnCancel;
   private boolean _isOk;

   public OpenInSessionDlg(JFrame owner, String savedSessionName, boolean sqlVirgin)
   {
      super(owner, s_stringMgr.getString("OpenInSessionDlg.title"), true);

      layoutUI(savedSessionName, sqlVirgin);

      ButtonGroup bg = new ButtonGroup();
      bg.add(_radOpenInNewSession);
      bg.add(_radOpenInCurrentSession);

      _radOpenInNewSession.setSelected(Props.getBoolean(PREF_OPEN_IN_NEW_SESSION, true));
      _radOpenInCurrentSession.setSelected(!Props.getBoolean(PREF_OPEN_IN_NEW_SESSION, true));

      _btnOk.addActionListener(e -> onOk());
      _btnCancel.addActionListener(e -> close());

      GUIUtils.initLocation(this, 630, 200);

      GUIUtils.enableCloseByEscape(this, dlg -> savePref());

      setVisible(true);
   }

   private void onOk()
   {
      _isOk = true;
      close();
   }

   private void close()
   {
      savePref();
      setVisible(false);
      dispose();
   }

   private void savePref()
   {
      Props.putBoolean(PREF_OPEN_IN_NEW_SESSION, _radOpenInNewSession.isSelected());
   }

   private void layoutUI(String savedSessionName, boolean sqlVirgin)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("OpenInSessionDlg.decide.label", savedSessionName)), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0);
      _radOpenInNewSession = new JRadioButton(s_stringMgr.getString("OpenInSessionDlg.open.in.new.session"));
      getContentPane().add(_radOpenInNewSession, gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0);
      if( sqlVirgin )
      {
         _radOpenInCurrentSession = new JRadioButton(s_stringMgr.getString("OpenInSessionDlg.open.in.existing.session"));
      }
      else
      {
         _radOpenInCurrentSession = new JRadioButton(s_stringMgr.getString("OpenInSessionDlg.open.in.existing.session.warn.discard"));
      }
      getContentPane().add(_radOpenInCurrentSession, gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      _btnOk = new JButton(s_stringMgr.getString("OpenInSessionDlg.Ok"));
      ret.add(_btnOk, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0);
      _btnCancel = new JButton(s_stringMgr.getString("OpenInSessionDlg.Cancel"));
      ret.add(_btnCancel, gbc);

      return ret;
   }

   public boolean isOk()
   {
      return _isOk;
   }

   public boolean isOpenInNewSession()
   {
      return _radOpenInNewSession.isSelected();
   }
}
