package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewLogsCommand;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.util.log.ILoggerListener;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;


/**
 * This is the Status bar's Log panel.
 * It recieves all logs through the ILoggerListener interface.
 * It updates its status all 200 millis, see _displayLastLogTimer.
 *
 * The traffic lights color of the last log is displayed on the JButton _btnLastLog.
 * If no new log arrives the last log color is displayed for 5000 millis and is the replaced
 * by the "white" icon, see _whiteIconTimer. This allows the user to have an eye an the logs
 * without much disturbance.
 *
 */
public class LogPanel extends JPanel
{
	private SquirrelResources _resources;

	private static final int LOG_TYPE_INFO = 0;
	private static final int LOG_TYPE_WARN = 1;
	private static final int LOG_TYPE_ERROR = 2;


	private JButton _btnLastLog = new JButton();
	private JLabel _lblLogInfo = new JLabel();
	private JButton _btnViewLogs = new JButton();

	private Timer _displayLastLogTimer;
	private Timer _whiteIconTimer;

	private final Vector _logsDuringDisplayDelay = new Vector();
	private LogData _curlogToDisplay;
	private IApplication _app;


	private LogStatistics _statistics = new LogStatistics();

	public LogPanel(IApplication app)
	{
		_app = app;
		_resources = _app.getResources();
		createGui();


		setIconForCurLogType();

		_whiteIconTimer = new Timer(5000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				_btnLastLog.setIcon(_resources.getIcon(SquirrelResources.IImageNames.WHITE_GEM));
			}
		});

		_whiteIconTimer.setRepeats(false);

		int displayDelay = 200;
		_displayLastLogTimer = new Timer(displayDelay, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updatePanel();
			}
		});

		_displayLastLogTimer.setRepeats(false);

		LoggerController.addLoggerListener(new ILoggerListener()
		{
			public void info(Class source, Object message)
			{
				++_statistics.infoCount;
				addLog(LOG_TYPE_INFO, source, message, null);
			}

			public void info(Class source, Object message, Throwable th)
			{
				++_statistics.infoCount;
				addLog(LOG_TYPE_INFO, source, message, th);
			}

			public void warn(Class source, Object message)
			{
				++_statistics.warnCount;
				addLog(LOG_TYPE_WARN, source, message, null);
			}

			public void warn(Class source, Object message, Throwable th)
			{
				++_statistics.warnCount;
				addLog(LOG_TYPE_WARN, source, message, th);
			}

			public void error(Class source, Object message)
			{
				++_statistics.errorCount;
				addLog(LOG_TYPE_ERROR, source, message, null);
			}

			public void error(Class source, Object message, Throwable th)
			{
				++_statistics.errorCount;
				addLog(LOG_TYPE_ERROR, source, message, th);
			}
		});


		_btnLastLog.addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent e)
			{
				setIconForCurLogType();
			}

			public void mouseExited(MouseEvent e)
			{
				if(false == _whiteIconTimer.isRunning())
				{
					_btnLastLog.setIcon(_resources.getIcon(SquirrelResources.IImageNames.WHITE_GEM));
				}
			}
		});

		_btnLastLog.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showLogInDialog();
			}
		});

		_btnViewLogs.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new ViewLogsCommand(_app).execute();
			}
		});

	}

	private void createGui()
	{
		setLayout(new BorderLayout(5,0));

		ImageIcon viewLogsIcon = _resources.getIcon(SquirrelResources.IImageNames.LOGS);
		_btnViewLogs.setIcon(viewLogsIcon);

		Dimension prefButtonSize = new Dimension(viewLogsIcon.getIconWidth(), viewLogsIcon.getIconHeight());
		_btnLastLog.setPreferredSize(prefButtonSize);
		_btnViewLogs.setPreferredSize(prefButtonSize);

		_btnLastLog.setBorder(null);
		_btnViewLogs.setBorder(null);


		add(_btnViewLogs, BorderLayout.WEST);
		add(_lblLogInfo, BorderLayout.CENTER);
		add(_btnLastLog, BorderLayout.EAST);

		_btnLastLog.setToolTipText("Press to view last log entry");

		_btnViewLogs.setToolTipText("Press to open logs");
	}


	private void showLogInDialog()
	{
		if(null != _curlogToDisplay)
		{
			String extMsg = "Logged by " + _curlogToDisplay.source  + ":\n\n" + _curlogToDisplay.message;
			ErrorDialog errorDialog = new ErrorDialog(_app.getMainFrame(), extMsg, _curlogToDisplay.throwable);

			String title;

			switch(_curlogToDisplay.logType)
			{
				case LOG_TYPE_INFO:
					title = "Last log entry (Entry type: Info)";
					break;
				case LOG_TYPE_WARN:
					title = "Last log entry (Entry type: Warning)";
					break;
				case LOG_TYPE_ERROR:
					title = "Last log entry (Entry type: ERROR)";
					break;
				default:
					title = "Last log entry (Entry type: Unknown)";
					break;
			}

			errorDialog.setTitle(title);
			errorDialog.setVisible(true);
		}
	}


	private void addLog(int logType, Class source, Object message, Throwable t)
	{
		LogData log = new LogData();
		log.logType = logType;
		log.source = source;
		log.message = message;
		log.throwable = t;


		synchronized(_logsDuringDisplayDelay)
		{
			_logsDuringDisplayDelay.add(log);
		}

		_displayLastLogTimer.restart();
	}


	private void updatePanel()
	{
		LogData[] logs;
		synchronized(_logsDuringDisplayDelay)
		{
			logs = (LogData[]) _logsDuringDisplayDelay.toArray(new LogData[_logsDuringDisplayDelay.size()]);
			_logsDuringDisplayDelay.clear();
		}

		_curlogToDisplay = null;
		for (int i = 0; i < logs.length; i++)
		{
			if(null == _curlogToDisplay)
			{
				_curlogToDisplay = logs[i];
			}
			else if(_curlogToDisplay.logType <= logs[i].logType)
			{
				_curlogToDisplay = logs[i];
			}
		}


		_lblLogInfo.setText(_statistics.toString());


		setIconForCurLogType();

		_whiteIconTimer.restart();
	}

	private void setIconForCurLogType()
	{
		if(null == _curlogToDisplay)
		{
			_btnLastLog.setIcon(_resources.getIcon(SquirrelResources.IImageNames.WHITE_GEM));
			return;
		}

		switch(_curlogToDisplay.logType)
		{
			case LOG_TYPE_INFO:
				_btnLastLog.setIcon(_resources.getIcon(SquirrelResources.IImageNames.GREEN_GEM));
				break;
			case LOG_TYPE_WARN:
				_btnLastLog.setIcon(_resources.getIcon(SquirrelResources.IImageNames.YELLOW_GEM));
				break;
			case LOG_TYPE_ERROR:
				_btnLastLog.setIcon(_resources.getIcon(SquirrelResources.IImageNames.RED_GEM));
				break;
		}
	}

	private static class LogData
	{
		int logType = -1;
		Object message = null;
		Throwable throwable = null;
		Class source;
	}

	private static class LogStatistics
	{
		int errorCount;
		int warnCount;
		int infoCount;

		private StringBuffer buffy = new StringBuffer();

		public String toString()
		{
			buffy.setLength(0);
			buffy.append("Logs: Errors ").append(errorCount);
			buffy.append(", Warnings ").append(warnCount);
			buffy.append(", Infos ").append(infoCount);

			return buffy.toString();
		}

	}


}
