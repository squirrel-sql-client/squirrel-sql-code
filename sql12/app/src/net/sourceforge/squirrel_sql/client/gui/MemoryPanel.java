package net.sourceforge.squirrel_sql.client.gui;


import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.*;

public class MemoryPanel extends JPanel implements ActionListener
{
	private JProgressBar _bar;
	private JButton _btnGarbage;
	private JButton _btnSessionGCStatus;
	private StringBuffer _buffy = new StringBuffer();
	private IApplication _app;
	private HashMap _aliasesBySessionIDsClosed = new HashMap();
	private HashMap _aliasesBySessionIDsConnected = new HashMap();
	private HashMap _aliasesBySessionIDsFinalized = new HashMap();
	private ImageIcon _greenGemIcon;
	private ImageIcon _yellowGemIcon;
	private ImageIcon _redGemIcon;

	public MemoryPanel(IApplication app)
	{
		_app = app;

		_bar = new JProgressBar();

		_bar.setStringPainted(true);

		_btnGarbage = new JButton();
		_btnGarbage.setToolTipText("Run garbage collection");
		_btnGarbage.setBorder(null);

		ImageIcon trashIcon = _app.getResources().getIcon(SquirrelResources.IImageNames.TRASH);
		_btnGarbage.setIcon(trashIcon);

		Dimension prefButtonSize = new Dimension(trashIcon.getIconWidth(), trashIcon.getIconHeight());

		_btnGarbage.setPreferredSize(prefButtonSize);

		_btnGarbage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.gc();
			}
		});


		_greenGemIcon = _app.getResources().getIcon(SquirrelResources.IImageNames.GREEN_GEM);
		_yellowGemIcon = _app.getResources().getIcon(SquirrelResources.IImageNames.YELLOW_GEM);
		_redGemIcon = _app.getResources().getIcon(SquirrelResources.IImageNames.RED_GEM);

		_btnSessionGCStatus = new JButton();

		_btnSessionGCStatus.setBorder(null);

		updateGcStatus();

		_btnSessionGCStatus.setBorder(null);
		_btnSessionGCStatus.setPreferredSize(prefButtonSize);


		JPanel pnlButtons = new JPanel(new GridLayout(1,2,3,0));
		pnlButtons.add(_btnSessionGCStatus);
		pnlButtons.add(_btnGarbage);


		this.setLayout(new BorderLayout(5,0));
		this.add(pnlButtons, BorderLayout.EAST);
		this.add(_bar, BorderLayout.CENTER);

		this.setBorder(null);

		_app.getSessionManager().addSessionListener(new SessionAdapter()
		{
			public void sessionClosed(SessionEvent evt)
			{
				IIdentifier id = evt.getSession().getIdentifier();
				String aliasName = evt.getSession().getAlias().getName();
				_aliasesBySessionIDsClosed.put(id, aliasName);
				updateGcStatus();
			}

			public void sessionConnected(SessionEvent evt)
			{
				IIdentifier id = evt.getSession().getIdentifier();
				String aliasName = evt.getSession().getAlias().getName();
				_aliasesBySessionIDsConnected.put(id, aliasName);
			}

			public void sessionFinalized(IIdentifier sessionId)
			{
				String aliasName = (String) _aliasesBySessionIDsClosed.get(sessionId);
				_aliasesBySessionIDsFinalized.put(sessionId, aliasName);
				updateGcStatus();
			}
		});

		Timer t = new Timer(500, this);
		t.start();
	}

	private void updateGcStatus()
	{
		SessionGCStatus gcStat = getSessionGCStatus();
		_btnSessionGCStatus.setToolTipText(gcStat.tooltip);
		_btnSessionGCStatus.setIcon(gcStat.icon);
	}

	private SessionGCStatus getSessionGCStatus()
	{
		SessionGCStatus ret = new SessionGCStatus();
		int numSessAwaitingGC = _aliasesBySessionIDsClosed.size() - _aliasesBySessionIDsFinalized.size();
		System.out.println("numSessAwaitingGC = " + numSessAwaitingGC);

		ret.tooltip = numSessAwaitingGC + " Session waiting for garbage collection";

		if(numSessAwaitingGC < 2)
		{
			ret.icon = _greenGemIcon;
		}
		else if(numSessAwaitingGC == 2)
		{
			ret.icon = _yellowGemIcon;
		}
		else if(numSessAwaitingGC > 2)
		{
			ret.icon = _redGemIcon;
		}

		return ret;


	}

	public void actionPerformed(ActionEvent e)
	{
		long total = Runtime.getRuntime().totalMemory() >> 10 >> 10;
		long free = Runtime.getRuntime().freeMemory() >> 10 >> 10;
		long just = total-free;

		_bar.setMinimum(0);
		_bar.setMaximum((int)total);
		_bar.setValue((int)just);
		_buffy.setLength(0);
		_buffy.append(just).append(" of ").append(total).append(" MB");
		_bar.setString(_buffy.toString());
	}

	private static class SessionGCStatus
	{
		String tooltip;
		ImageIcon icon;
	}

}