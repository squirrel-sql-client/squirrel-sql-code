package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class CancelPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CancelPanel.class);

   JLabel sqlLbl = new JLabel();
   JLabel currentStatusLbl = new JLabel();
   JButton cancelBtn;
   JButton closeBtn;
   JTextField txtExecTimeCounter;
   JTextField txtNumberOfRowsRead;
   JCheckBox chkPlaySoundWhenFinished;
   JButton btnConfigureFinishedSound;
   JButton btnCopySqlToClip;
   JButton btnShowExecutingSql;


   public CancelPanel(ISession session)
   {
      super(new BorderLayout());

      add(createNorthPanel(session), BorderLayout.NORTH);
      add(createCenterPanel(), BorderLayout.CENTER);
   }

   private JPanel createNorthPanel(ISession session)
   {
      JPanel ret = new JPanel(new BorderLayout());

      ImageIcon icon = session.getApplication().getResources().getIcon(SquirrelResources.IImageNames.CLOSE);
      closeBtn = new JButton(icon);
      closeBtn.setBorderPainted(false);
      closeBtn.setMargin(new Insets(0, 0, 0, 0));



      ret.add(closeBtn, BorderLayout.EAST);
      ret.add(new JPanel(), BorderLayout.CENTER);
      return ret;
   }

   private JPanel createCenterPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      cancelBtn = new JButton(s_stringMgr.getString("SQLResultExecuterPanel.cancelButtonLabel"));

      GridBagConstraints gbc;

      gbc =  new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 10, 5, 10),0,0);
      ret.add(createSQLLabelWithButtons(), gbc);

      gbc =  new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 10, 0, 10),0,0);
      ret.add(sqlLbl, gbc);


      gbc =  new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10),0,0);
      ret.add(new JLabel(s_stringMgr.getString("SQLResultExecuterPanel.statusLabel")), gbc);

      gbc =  new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10),0,0);
      ret.add(currentStatusLbl, gbc);



      gbc =  new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10),0,0);
      ret.add(cancelBtn, gbc);


      gbc =  new GridBagConstraints(0,3,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(30, 10, 0, 10),0,0);
      ret.add(createExecutionTimePanel(), gbc);


      gbc =  new GridBagConstraints(0,4,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(30, 10, 0, 10),0,0);
      ret.add(createNotificationPanel(), gbc);

      // dist
      gbc =  new GridBagConstraints(0,5,2,1,0,1, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0),0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   private JPanel createSQLLabelWithButtons()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 1, 0),0,0);
      ret.add(new JLabel(s_stringMgr.getString("SQLResultExecuterPanel.sqlLabel")), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0);
      btnCopySqlToClip = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.COPY_SQL));
      btnCopySqlToClip.setToolTipText(s_stringMgr.getString("SQLResultExecuterPanel.copy.executing.sql.to.clipboard"));

      ret.add(GUIUtils.styleAsToolbarButton(btnCopySqlToClip), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 0),0,0);
      btnShowExecutingSql = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SQL));
      btnShowExecutingSql.setToolTipText(s_stringMgr.getString("SQLResultExecuterPanel.show.executing.sql"));
      ret.add(GUIUtils.styleAsToolbarButton(btnShowExecutingSql), gbc);


      return ret;
   }

   private JPanel createNotificationPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      chkPlaySoundWhenFinished = new JCheckBox(s_stringMgr.getString("SQLResultExecuterPanel.playWhenFinishedNotificationSound"));
      ret.add(chkPlaySoundWhenFinished, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0,0);
      btnConfigureFinishedSound = new JButton(s_stringMgr.getString("SQLResultExecuterPanel.configureWhenFinishedNotificationSound"));
      ret.add(btnConfigureFinishedSound, gbc);

      return ret;
   }

   private JPanel createExecutionTimePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("SQLResultExecuterPanel.executingFor")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      txtExecTimeCounter = new JTextField();
      txtExecTimeCounter.setEditable(false);
      txtExecTimeCounter.setColumns(10);
      GUIUtils.forceWidth(txtExecTimeCounter, 120);
      ret.add(txtExecTimeCounter, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("SQLResultExecuterPanel.execMillis")), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("SQLResultExecuterPanel.numberOfRowsRead")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      txtNumberOfRowsRead = new JTextField();
      txtNumberOfRowsRead.setEditable(false);
      txtNumberOfRowsRead.setColumns(10);
      GUIUtils.forceWidth(txtNumberOfRowsRead, 120);
      ret.add(txtNumberOfRowsRead, gbc);

      // Removed on account of Stanimir Stamenkov's analysis in bug #1421 in comment on 2020-07-12.
      //GUIUtils.setMinimumHeight(ret, 50);

      return ret;
   }


}
