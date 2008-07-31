package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class EscapeDateFrame extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(EscapeDateFrame.class);


	JTextField txtYear = createTextField();
   JTextField txtMonth = createTextField();
	JTextField txtDay = createTextField();
	JTextField txtHour = createTextField();
	JTextField txtMinute = createTextField();
	JTextField txtSecond = createTextField();
	// i18n[editextras.timeStamp=Time stamp]
	JButton btnTimestamp = new JButton(s_stringMgr.getString("editextras.timeStamp"));

	// i18n[editextras.date=Date]
	JButton btnDate = new JButton(s_stringMgr.getString("editextras.date"));
	// i18n[editextras.time=Time]
	JButton btnTime = new JButton(s_stringMgr.getString("editextras.time"));

	public EscapeDateFrame(Frame owner)
	{
		// i18n[editextras.escapeDate=Escape date]
		super(owner, s_stringMgr.getString("editextras.escapeDate"));

		JPanel pnlEdit = new JPanel();

		pnlEdit.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[editextras.year=Year]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.year")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      pnlEdit.add(txtYear, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[editextras.month=Month]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.month")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnlEdit.add(txtMonth, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[editextras.day=Day]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.day")), gbc);

      gbc = new GridBagConstraints(1,2,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnlEdit.add(txtDay, gbc);


      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[editextras.hour=Hour]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.hour")), gbc);

      gbc = new GridBagConstraints(1,3,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnlEdit.add(txtHour, gbc);


      gbc = new GridBagConstraints(0,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[editextras.minute=Minute]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.minute")), gbc);

      gbc = new GridBagConstraints(1,4,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnlEdit.add(txtMinute, gbc);


      gbc = new GridBagConstraints(0,5,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[editextras.second=Second]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.second")), gbc);

      gbc = new GridBagConstraints(1,5,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnlEdit.add(txtSecond, gbc);

      JPanel pnlButtons = createButtonsPanel();

		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());

		pnlMain.add(pnlEdit, BorderLayout.CENTER);
		pnlMain.add(pnlButtons, BorderLayout.SOUTH);

		getContentPane().add(pnlMain);
		setSize(250, 320);

		getRootPane().setDefaultButton(btnTimestamp);

		AbstractAction closeAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				setVisible(false);
				dispose();
			}
		};
		KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
		getRootPane().getActionMap().put("CloseAction", closeAction);


	}

   private JPanel createButtonsPanel()
   {
      JPanel pnlButtons = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      pnlButtons.add(btnTimestamp, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      pnlButtons.add(btnDate, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      pnlButtons.add(btnTime, gbc);

      return pnlButtons;
   }

   private JTextField createTextField()
   {
      final JTextField ret = new JTextField();
      ret.addFocusListener(new FocusAdapter()
      {
         public void focusGained(FocusEvent e)
         {
            String s = ret.getText();
            if(null != s && 0 < s.length())
            {
               ret.setSelectionStart(0);
               ret.setSelectionEnd(s.length());
            }

         }
      });

      return ret;
   }


}

