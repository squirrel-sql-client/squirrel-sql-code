package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class EscapeDateFrame extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(EscapeDateFrame.class);


	JTextField txtYear = new JTextField();
	JTextField txtMonth = new JTextField();
	JTextField txtDay = new JTextField();
	JTextField txtHour = new JTextField();
	JTextField txtMinute = new JTextField();
	JTextField txtSecond = new JTextField();
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

		pnlEdit.setLayout(new GridLayout(6, 2));

		// i18n[editextras.year=Year]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.year")));
		pnlEdit.add(txtYear);
		// i18n[editextras.month=Month]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.month")));
		pnlEdit.add(txtMonth);
		// i18n[editextras.day=Day]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.day")));
		pnlEdit.add(txtDay);
		// i18n[editextras.hour=Hour]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.hour")));
		pnlEdit.add(txtHour);
		// i18n[editextras.minute=Minute]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.minute")));
		pnlEdit.add(txtMinute);
		// i18n[editextras.second=Second]
		pnlEdit.add(new JLabel(s_stringMgr.getString("editextras.second")));
		pnlEdit.add(txtSecond);

		JPanel pnlButtons = new JPanel(new GridLayout(3, 1));
		pnlButtons.add(btnTimestamp);
		pnlButtons.add(btnDate);
		pnlButtons.add(btnTime);

		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());

		pnlMain.add(pnlEdit, BorderLayout.CENTER);
		pnlMain.add(pnlButtons, BorderLayout.SOUTH);

		getContentPane().add(pnlMain);
		setSize(250, 250);

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
}

