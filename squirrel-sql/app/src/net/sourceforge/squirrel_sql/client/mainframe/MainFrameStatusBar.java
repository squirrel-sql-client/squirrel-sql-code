package net.sourceforge.squirrel_sql.client.mainframe;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
/**
 * Statusbar component for the main frame.
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MainFrameStatusBar extends JPanel {
	/** If <TT>true</TT> the current time should be shown. */
	private boolean _showClock;

	/** Label showing the message in the statusbar. */
	private JLabel _textLbl = new MessageLabel();

	/** Label showing the time. */
	private JLabel _clockLbl;

	/** Font for controls. */
	private Font _font;

	/** This is the task that updates the time every second. */
	private ClockTask _clockTask;

	/** Used to format the displayed date. */
	private DateFormat _fmt = DateFormat.getTimeInstance(DateFormat.LONG);

	/**
	 * Ctor specifying whether time should be displayed.
	 * 
	 * @param	showClock	If <TT>true</TT> time should be displayed.
	 */
	public MainFrameStatusBar(boolean showClock) {
		super();
		createUserInterface(showClock);
	}

	/**
	 * Set the font for controls in this statusbar.
	 * 
	 * @param	font	The font to use.
	 * 
	 * @throws	IllegalArgumentException	if <TT>null</TT> <TT>Font</TT> passed.
	 */
	public synchronized void setFont(Font font) {
		if (font == null) {
			throw new IllegalArgumentException("Font == null");
		}
		super.setFont(font);
		_font = font;
		if (_textLbl != null) {
			_textLbl.setFont(font);
		}
		if (_clockLbl != null) {
			_clockLbl.setFont(font);
		}
	}
 
	/**
	 * Show/hide the time.
	 * 
	 * @param	value	If <TT>true</TT> time should be displayed.
	 */
	public synchronized void showClock(boolean value) {
		if (value != _showClock) {
			if (value) {
				_clockLbl = new MessageLabel();
				_clockLbl.setFont(_font);

				add(_clockLbl, new ClockConstraints());
				startClockThread();
			} else {
				stopClockThread();
			}
			_showClock = value;
		}
	}

	/**
	 * @return	<TT>true</TT> if clock is showing else <TT>false</TT>.
	 */
	public boolean isClockShowing() {
		return _showClock;
	}

	/**
	 * Set the text to display in the message label.
	 * 
	 * @param	text	Text to display in the message label.
	 */
	public synchronized void setText(String text) {
		String myText = null;
		if (text != null) {
			myText = text.trim();
		}
		if (myText != null && myText.length() > 0) {
			_textLbl.setText(myText);
		} else {
			clearText();
		}
	}

	public synchronized void clearText() {
		_textLbl.setText(" ");
	}

	private void createUserInterface(boolean showClock) {
		_textLbl.setFont(_font);
		setLayout(new GridBagLayout());
		add(_textLbl, new TextConstraints());
		showClock(showClock);
		clearText();
	}

	private synchronized void startClockThread() {
		if (_clockTask == null) {
			_clockTask = new ClockTask(this);
			new Thread(_clockTask).start();
		}
	}

	private synchronized void stopClockThread() {
		_clockTask.stop();
		_clockTask = null;
	}

	private synchronized void setTime(Date time) {
		DateFormat fmt = DateFormat.getTimeInstance(DateFormat.LONG);
		_clockLbl.setText(fmt.format(time));
	}

	private static class ClockTask implements Runnable {
		private boolean _stop;
		private MainFrameStatusBar _bar;

		ClockTask(MainFrameStatusBar bar) {
			super();
			_bar = bar;
		}

		synchronized void stop() {
			_stop = true;
		}

		public void run() {
			for(;;) {
				try {
					Thread.currentThread().sleep(1000); // 1 second
				} catch(InterruptedException ex) {
					return;
				}
				if (_stop) {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								_bar.remove(_bar._clockLbl);
								_bar._clockLbl = null;
								_bar.validate();
							}
						});
						Thread.yield();
					} catch(Exception ignore) {
					}
					break;
				}
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							_bar.setTime(Calendar.getInstance().getTime());
						}
					});
					Thread.yield();
				} catch(Exception ignore) {
				}
			}
		}
	}

	private static class MessageLabel extends JLabel {
		MessageLabel() {
			super();
			setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createBevelBorder(BevelBorder.LOWERED),
						BorderFactory.createEmptyBorder(0, 4, 0, 4)));
		}
	}

	private static abstract class BaseConstraints extends GridBagConstraints {
		BaseConstraints() {
			super();
			insets = new Insets(1, 1, 1, 1);
			anchor = GridBagConstraints.WEST;
			gridheight = 1;
			gridwidth = 1;
			gridy = 0;
			weighty = 0.0;
		}
	}

	private static final class TextConstraints extends BaseConstraints {
		TextConstraints() {
			super();
			gridx = 0;
			fill = GridBagConstraints.HORIZONTAL;
			weightx = 1.0;
		}
	}

	private static final class ClockConstraints extends BaseConstraints {
		ClockConstraints() {
			super();
			gridx = 1;
			fill = GridBagConstraints.NONE;
			weightx = 0.0;
		}
	}

}


