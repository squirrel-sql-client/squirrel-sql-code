package net.sourceforge.squirrel_sql.client.update.gui;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * The dialog that presents the UpdateManager
 * 
 */
public class UpdateManagerDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    /** Logger for this class. */
    private final static ILogger s_log = LoggerController
            .createLogger(UpdateManagerDialog.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr = StringManagerFactory
            .getStringManager(UpdateManagerDialog.class);

    /** The controller we send commands to based on the user's interaction */
    private UpdateController _controller = null; 
    
    
    /* GUI Widgets */
    
    /** Check button for dialog */
    private JButton _checkBtn = null;
    
    /** Close button for dialog. */
    private JButton _closeBtn = null;

    /** The parent frame for this dialog */
    private JFrame _parent = null;
        
    /** 
     * The combobox that allows the user to select a channel - such as stable 
     * or snapshot.
     */
    private JComboBox channelSelector = null; 
    
    static interface i18n {
        //i18n[UpdateManagerDialog.title=Update Manager]
        String TITLE = s_stringMgr.getString("UpdateManagerDialog.title");
        
        //i18n[UpdateManagerDialog.closeLabel=Close]
        String CLOSE_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.closeLabel");
        
        //i18n[UpdateManagerDialog.repositoryTabLabel=Repository]
        String LOCATION_TAB_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.repositoryTabLabel");

        //i18n[UpdateManagerDialog.channelLabel=Channel:]
        String CHANNEL_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.channelLabel");        
        
        //i18n[UpdateManagerDialog.hostLabel=Host:]
        String HOST_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.hostLabel");

        //i18n[UpdateManagerDialog.pathLabel=Path:]
        String PATH_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.pathLabel");
        
        //i18n[UpdateManagerDialog.portLabel=Port:]
        String PORT_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.portLabel");

        //i18n[UpdateManagerDialog.checkButtonLabel=Check]
        String CHECK_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.checkButtonLabel");
    }
    
    /** The value indicating that the user wants only stable releases */
    public static String STABLE_CHANNEL_VALUE = "stable"; 

    /** The value indicating that the user wants only snapshot releases */
    public static String SNAPSHOT_CHANNEL_VALUE = "snapshot"; 
    
    public UpdateManagerDialog(JFrame parent, UpdateController controller) {
        super(parent, i18n.TITLE, true);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this._parent = parent; 
        init();
    }

    public void setHost(String host) {
        
    }
    
    private void init() {
        EmptyBorder border = new EmptyBorder(new Insets(5,5,5,5));
        Dimension mediumField = new Dimension(200, 20);
        Dimension portField = new Dimension(50, 20);

        
        this.setLayout(new BorderLayout());
                
        
        JPanel locationPanel = new JPanel();
        locationPanel.setBorder(new EmptyBorder(0,0,0,10));
        locationPanel.setLayout(new GridBagLayout());
        
        int x = 0;
        int y = -1;
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.anchor = GridBagConstraints.NORTH;
        
        
        JLabel hostLabel = getBorderedLabel(i18n.HOST_LABEL, border);
        JTextField hostTF = getSizedTextField(mediumField);

        locationPanel.add(hostLabel, getLabelConstraints(c));
        locationPanel.add(hostTF, getFieldFillHorizontalConstaints(c));
        
        JLabel portLabel = getBorderedLabel(i18n.PORT_LABEL, border);
        JTextField portTF = getSizedTextField(portField);

        locationPanel.add(portLabel, getLabelConstraints(c));
        locationPanel.add(portTF, getFieldConstraints(c));        

        JLabel pathLabel = getBorderedLabel(i18n.PATH_LABEL, border);
        JTextField pathTF = getSizedTextField(mediumField);

        locationPanel.add(pathLabel, getLabelConstraints(c));
        locationPanel.add(pathTF, getFieldFillHorizontalConstaints(c));                
        
        JLabel channelLabel = getBorderedLabel(i18n.CHANNEL_LABEL, border);
        channelSelector = 
            new JComboBox(new Object[] {STABLE_CHANNEL_VALUE, 
                                        SNAPSHOT_CHANNEL_VALUE});
        
        locationPanel.add(channelLabel, getLabelConstraints(c));
        locationPanel.add(channelSelector, getFieldConstraints(c));
        locationPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
                
        this.add(locationPanel, BorderLayout.CENTER);
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        this.setSize(300,200);
    }
    
    private JPanel getButtonPanel() {
        JPanel result = new JPanel();

        _checkBtn = new JButton(i18n.CHECK_LABEL);
        _closeBtn = new JButton(i18n.CLOSE_LABEL);
        
        _closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UpdateManagerDialog.this.setVisible(false);
            }
        });
        
        result.add(_checkBtn);
        result.add(_closeBtn);
        return result;
    }
    
    private GridBagConstraints getLabelConstraints(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy++;                
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        return c;
    }
    
    private GridBagConstraints getFieldFillHorizontalConstaints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;   
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,0,5);
        return c;        
    }
    
    private GridBagConstraints getFieldConstraints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;   
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.NONE;
        return c;
    }

    private JLabel getBorderedLabel(String text, Border border) {
        JLabel result = new JLabel(text);
        result.setBorder(border);
        result.setPreferredSize(new Dimension(115, 20));
        result.setHorizontalAlignment(SwingConstants.RIGHT);
        return result;
    }

    private JTextField getSizedTextField(Dimension preferredSize) {
        JTextField result = new JTextField();
        result.setPreferredSize(preferredSize);
        result.setMinimumSize(preferredSize);
        return result;
    }
    
}
