package net.sourceforge.squirrel_sql.plugins.editextras.searchandreplace;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SearchAndReplaceDlg extends JDialog
{
	static final int MODUS_FIND = 0;
	static final int MODUS_REPLACE = 1;

	JComboBox cboToFind = new JComboBox();
	JComboBox cboToReplace = new JComboBox();
   JButton btnSearch = new JButton("Find");
	JButton btnReplace = new JButton("Replace");
	JButton btnReplaceAll = new JButton("Replace All");
   JCheckBox chkMatchCase = new JCheckBox("Match Case");
	JCheckBox chkWholeWord = new JCheckBox("Whole Word");
	JButton btnClose = new JButton("Close");
	JLabel lblMessage = new JLabel();

	private JLabel lblFindWhat = new JLabel("Find what:");
	private JLabel lblRelaceWith = new JLabel("Replace with:");


	public SearchAndReplaceDlg(Frame owner, String title, int modus)
	{
		super(owner, title, false);

		GridLayout mainLayout = new GridLayout(3,1);
		if(MODUS_REPLACE == modus)
		{
			mainLayout = new GridLayout(5,1);
		}
		else
		{
			mainLayout = new GridLayout(3,1);
		}
		mainLayout.setVgap(5);
		getContentPane().setLayout(mainLayout);

		BorderLayout upperLayout = new BorderLayout();
		upperLayout.setHgap(10);
		JPanel upperPnl = new JPanel(upperLayout);
		lblFindWhat.setPreferredSize(lblRelaceWith.getPreferredSize());
		upperPnl.add(lblFindWhat, BorderLayout.WEST);
		upperPnl.add(cboToFind, BorderLayout.CENTER);
		upperPnl.add(btnSearch, BorderLayout.EAST);
		btnSearch.setPreferredSize(btnReplaceAll.getPreferredSize());
		cboToFind.setEditable(true);
		getContentPane().add(upperPnl);

		BorderLayout middleLayout = new BorderLayout();
		JPanel middlePnl = new JPanel(middleLayout);
		JPanel middleCenterPnl = new JPanel(new GridLayout(1,2));
		middlePnl.add(new JPanel(), BorderLayout.CENTER);
		middleCenterPnl.add(chkMatchCase);
		middleCenterPnl.add(chkWholeWord);
		middlePnl.add(middleCenterPnl, BorderLayout.WEST);
		getContentPane().add(middlePnl);

		if(MODUS_REPLACE == modus)
		{
			BorderLayout upperReplaceLayout = new BorderLayout();
			upperReplaceLayout.setHgap(10);
			JPanel upperReplacePnl = new JPanel(upperReplaceLayout);
			upperReplacePnl.add(lblRelaceWith, BorderLayout.WEST);
			upperReplacePnl.add(cboToReplace, BorderLayout.CENTER);
			upperReplacePnl.add(btnReplace, BorderLayout.EAST);
			btnReplace.setPreferredSize(btnReplaceAll.getPreferredSize());
			cboToReplace.setEditable(true);
			getContentPane().add(upperReplacePnl);

			BorderLayout lowerReplaceLayout = new BorderLayout();
			lowerReplaceLayout.setHgap(10);
			JPanel lowerReplacePnl = new JPanel(lowerReplaceLayout);
			lowerReplacePnl.add(new JPanel(), BorderLayout.CENTER);
			lowerReplacePnl.add(btnReplaceAll, BorderLayout.EAST);
			getContentPane().add(lowerReplacePnl);

		}

		BorderLayout lowerLayout = new BorderLayout();
		lowerLayout.setHgap(10);
		JPanel lowerPnl = new JPanel(lowerLayout);
		lowerPnl.add(lblMessage, BorderLayout.CENTER);
		lowerPnl.add(btnClose, BorderLayout.EAST);
		btnClose.setPreferredSize(btnReplaceAll.getPreferredSize());
		getContentPane().add(lowerPnl);



		if(MODUS_REPLACE == modus)
		{
			setSize(500, 200);
		}
		else
		{
			setSize(500, 120);
		}
	}
}
