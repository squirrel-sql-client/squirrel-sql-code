package net.sourceforge.squirrel_sql.fw.gui;


import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class MemoryPanel extends JLabel implements ActionListener
{
	private JProgressBar _bar;
	private JButton _btnGarbage;
	private StringBuffer _buffy = new StringBuffer();
	public MemoryPanel()
	{
		this.setPreferredSize(new Dimension(125, 18));


		final Color selectionBackground = (Color)UIManager.get("ProgressBar.selectionBackground");
		final Color selectionForeground = (Color)UIManager.get("ProgressBar.selectionForeground");
		UIManager.put("ProgressBar.selectionBackground",Color.black);
		UIManager.put("ProgressBar.selectionForeground",Color.black);

		_bar = new JProgressBar();
		_bar.setBorderPainted(false);
		_bar.setOpaque(true);
		_bar.setBackground(new Color(244,244,244));
		_bar.setForeground(new Color(153, 204, 255));
		_bar.setBorder(null);

		UIManager.put("ProgressBar.selectionBackground",selectionBackground);
		UIManager.put("ProgressBar.selectionForeground",selectionForeground);

		_bar.setStringPainted(true);

		_btnGarbage = new JButton();
		_btnGarbage.setToolTipText("Run garbage collection");
		_btnGarbage.setFocusable(false);
		_btnGarbage.setFocusPainted(false);
		_btnGarbage.setBorder(null);
		_btnGarbage.setIcon(new LibraryResources().getIcon(LibraryResources.IImageNames.TRASH));
		_btnGarbage.setPreferredSize(new Dimension(20,10));
		_btnGarbage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.gc();
			}
		});

		this.setLayout(new BorderLayout());
		this.add(_bar, BorderLayout.CENTER);
		this.add(_btnGarbage, BorderLayout.EAST);

		this.setBorder(null);

		Timer t = new Timer(500, this);
		t.start();
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
}