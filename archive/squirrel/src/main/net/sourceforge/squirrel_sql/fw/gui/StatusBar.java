package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

public class StatusBar extends JPanel {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
    }

    private boolean _showClock;
    private String _msgWhenEmpty = " ";

    private JLabel _textLbl = new MyLabel();
    private JLabel _clockLbl;

    //private Thread _clockThread;
    private ClockTask _clockTask;

    public StatusBar() {
        this(false);
    }

    public StatusBar(boolean showClock) {
        super();
        createUserInterface(showClock);
    }

    public void showClock(boolean value) {
        if (value != _showClock) {
            if (value) {
                _clockLbl = new MyLabel();
                add(_clockLbl, new ClockConstraints());
                startClockThread();
            } else {
                stopClockThread();
            }
            _showClock = value;
        }
    }

    public boolean isClockShowing() {
        return _showClock;
    }

    public void setText(String text) {
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

    public void clearText() {
        _textLbl.setText(_msgWhenEmpty);
    }

    public void setTextWhenEmpty(String value) {
        final boolean wasEmpty = _textLbl.getText().equals(_msgWhenEmpty);
        if (value != null && value.length() > 0) {
            _msgWhenEmpty = value;
        } else {
            _msgWhenEmpty = " ";
        }
        if (wasEmpty) {
            clearText();
        }
    }

    private void createUserInterface(boolean showClock) {
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
        private StatusBar _bar;

        ClockTask(StatusBar bar) {
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
                break;
            }
        }
    }

    private static class MyLabel extends JLabel {
        MyLabel() {
            super();
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
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
