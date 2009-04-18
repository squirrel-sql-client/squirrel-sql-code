package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class TableExportCsvDlgTestUI extends BaseSQuirreLJUnit4TestCase
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		TableExportCsvDlg dialog = new TableExportCsvDlg();
		dialog.setSize(500, 500);
		GUIUtils.centerWithinScreen(dialog);

		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}

		});

		dialog.setVisible(true);

	}

}
