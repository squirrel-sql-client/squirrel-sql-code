package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import java.awt.*;

public class ScriptEnvironment
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ScriptEnvironment.class);

	private ISQLPanelAPI m_sqlPanelApi;
	private JFrame m_ownerFrame;

	private JDialog m_dlg;
	private JTabbedPane m_tabbedPane;
	private JLabel m_lblStatus;

	private Vector<PrintStream> m_printStreams = new Vector<PrintStream>();
	private int createdPrintStreamsCount = 0;

	ScriptEnvironment(ISQLPanelAPI sqlPanelApi, JFrame ownerFrame)
	{
		m_sqlPanelApi = sqlPanelApi;
		m_ownerFrame = ownerFrame;

		// i18n[userscript.execOutput=Script execution output]
		m_dlg = new JDialog(m_ownerFrame, s_stringMgr.getString("userscript.execOutput"), false);
		m_dlg.getContentPane().setLayout(new BorderLayout());
		m_tabbedPane = new JTabbedPane();
		// i18n[userscript.executing=Executing Script...]
		m_lblStatus = new JLabel(s_stringMgr.getString("userscript.executing"));

		m_dlg.getContentPane().add(m_tabbedPane, BorderLayout.CENTER);
		m_dlg.getContentPane().add(m_lblStatus, BorderLayout.SOUTH);

		GUIUtils.centerWithinParent(m_dlg);

		m_dlg.setSize(400, 400);
	}

	public PrintStream createPrintStream()
	{
		return createPrintStream(null);
	}

	public PrintStream createPrintStream(String tabTitle)
	{
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final JTextArea txtOut = new JTextArea();

		if(null == tabTitle)
		{
			m_tabbedPane.addTab("<" + (++createdPrintStreamsCount) + ">", txtOut);
		}
		else
		{
			m_tabbedPane.addTab(tabTitle, new JScrollPane(txtOut));
		}

		PrintStream ret =
			new PrintStream(bos)
			{
				public void flush()
				{
					super.flush();
					onFlush(bos, txtOut);
				}
			};


		// Dialog is shown only when it is written to.
		m_dlg.setVisible(true);

		m_printStreams.add(ret);

		return ret;
	}

	private void onFlush(ByteArrayOutputStream bos, JTextArea txtOut)
	{
		txtOut.append(bos.toString());
		bos.reset();
	}

	public PrintStream getSQLAreaPrintStream()
	{
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ret =
			new PrintStream(bos)
			{
				public void flush()
				{
					super.flush();
					onFlushToSqlArea(bos);
				}
			};
		m_printStreams.add(ret);
		return ret;
	}

	private void onFlushToSqlArea(ByteArrayOutputStream bos)
	{
		m_sqlPanelApi.appendSQLScript(bos.toString());
		bos.reset();
	}


	void flushAll()
	{
		for (int i = 0; i < m_printStreams.size(); i++)
		{
			PrintStream printStream = m_printStreams.elementAt(i);
			printStream.flush();
		}
	}

	void setExecutionFinished(boolean successful)
	{
		if(successful)
		{
			// i18n[userscript.scriptCompleted=Script completed]
			m_lblStatus.setText(s_stringMgr.getString("userscript.scriptCompleted"));
		}
		else
		{
			// i18n[userscript.scriptCompletedErr=Script completed with errors]
			m_lblStatus.setText(s_stringMgr.getString("userscript.scriptCompletedErr"));
		}
	}
}
