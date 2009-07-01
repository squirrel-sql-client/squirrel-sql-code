/*
 Copyright (C) 2009  Jos� David Moreno Ju�rez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.ui;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.EventListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.control.GenerateControlFileActionListener;

/**
 * This frame allows the user to specify settings for the generation of the
 * SQL*Loader control file as well as launch the generation.
 * 
 * @author Jos� David Moreno Ju�rez
 */
public class ControlFileGenerationFrame extends DialogWidget implements
		EventListener {

	final class CloseDialogActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			dispose();
		}
	}

	private static final int SMALL_TEXTFIELDS_WIDTH = 23;

	private static final int CONTROL_FILE_TEXTFIELD_WIDTH = 122;

	private static final String DEFAULT_FIELD_SEPARATOR = ",";

	private static final String DEFAULT_STRING_DELIMITATOR = "\"";

	private ISession session;

	/**
	 * Creates a new instance of the dialog {@link ControlFileGenerationFrame}
	 * with the specified title and session.
	 * 
	 * @param title		title of this dialog
	 * @param session	database session
	 */
	public ControlFileGenerationFrame(String title, ISession session) {
		super(title, true, true, true, true, session.getApplication());
		this.session = session;
		initComponents();
	}

	/**
	 * Initializes the UI components.
	 */
	private void initComponents() {
		
		CloseDialogActionListener closeDialogActionListener = new CloseDialogActionListener();

		/* "Load mode" appendRadioButton group */
        JPanel loadModePanel = new JPanel();
        ButtonGroup loadModeButtonGroup = new ButtonGroup();
        final JRadioButton appendRadioButton = new JRadioButton("Append");
        JRadioButton replaceRadioButton = new JRadioButton("Replace");
        loadModePanel.setBorder(BorderFactory.createTitledBorder("Load mode"));
        loadModeButtonGroup.add(appendRadioButton);
        loadModeButtonGroup.add(replaceRadioButton);
        replaceRadioButton.setSelected(true);
        GroupLayout loadModePanelLayout = new GroupLayout(loadModePanel);
        loadModePanel.setLayout(loadModePanelLayout);
        loadModePanelLayout.setHorizontalGroup(
            loadModePanelLayout.createParallelGroup(LEADING)
            .addGroup(loadModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loadModePanelLayout.createParallelGroup(LEADING)
                    .addComponent(appendRadioButton)
                    .addComponent(replaceRadioButton))
                .addContainerGap())
        );
        loadModePanelLayout.setVerticalGroup(
            loadModePanelLayout.createParallelGroup(LEADING)
            .addGroup(loadModePanelLayout.createSequentialGroup()
                .addComponent(replaceRadioButton)
                .addPreferredGap(RELATED)
                .addComponent(appendRadioButton))
        );
        
        /* "Field separator" text field */
        final JTextField fieldSeparatorTextfield = new JTextField();
        JLabel fieldSeparatorLabel = new JLabel("Field separator: ");
        fieldSeparatorTextfield.setText(DEFAULT_FIELD_SEPARATOR);

        /* "String delimitator" text field */
        final JTextField stringDelimitatorTextfield = new JTextField();
        JLabel stringDelimitatorLabel = new JLabel("String delimitator: ");
        stringDelimitatorTextfield.setText(DEFAULT_STRING_DELIMITATOR);

        /* "Directory for control files" file chooser */
        final JFileChooser controlFileChooser = new JFileChooser();
        final JPanel controlFilePanel = new JPanel();
        final JTextField controlFileTextfield = new JTextField();
        JButton controlFileButton = new JButton("Choose...");
        controlFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		controlFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (controlFileChooser.showOpenDialog(controlFilePanel)==JFileChooser.APPROVE_OPTION) {
					controlFileTextfield.setText(controlFileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
        controlFilePanel.setBorder(BorderFactory.createTitledBorder("Directory for control files"));
        GroupLayout controlFilePanelLayout = new GroupLayout(controlFilePanel);
        controlFilePanel.setLayout(controlFilePanelLayout);
        controlFilePanelLayout.setHorizontalGroup(
            controlFilePanelLayout.createParallelGroup(LEADING)
            .addGroup(controlFilePanelLayout.createSequentialGroup()
                .addComponent(controlFileTextfield, PREFERRED_SIZE, CONTROL_FILE_TEXTFIELD_WIDTH, PREFERRED_SIZE)
                .addPreferredGap(RELATED)
                .addComponent(controlFileButton))
        );
        controlFilePanelLayout.setVerticalGroup(
            controlFilePanelLayout.createParallelGroup(LEADING)
            .addGroup(controlFilePanelLayout.createParallelGroup(BASELINE)
                .addComponent(controlFileTextfield, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                .addComponent(controlFileButton))
        );

        /* Dialog buttons */
        JPanel buttonPanel = new JPanel();
        JButton generateButton = new JButton("Generate");
        JButton closeButton = new JButton("Close");
        generateButton.addActionListener(new GenerateControlFileActionListener(stringDelimitatorTextfield, fieldSeparatorTextfield, appendRadioButton, controlFileTextfield, session));
		closeButton.addActionListener(closeDialogActionListener);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generateButton)
                .addPreferredGap(RELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(LEADING)
            .addGroup(TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonPanelLayout.createParallelGroup(BASELINE)
                    .addComponent(closeButton)
                    .addComponent(generateButton)))
        );

        /* Dialog layout */
        final Container contentPane = getContentPane();
		GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(LEADING)
                    .addComponent(loadModePanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(LEADING)
                            .addComponent(fieldSeparatorLabel)
                            .addComponent(stringDelimitatorLabel))
                        .addPreferredGap(RELATED)
                        .addGroup(layout.createParallelGroup(LEADING)
                            .addComponent(stringDelimitatorTextfield, PREFERRED_SIZE, SMALL_TEXTFIELDS_WIDTH, PREFERRED_SIZE)
                            .addComponent(fieldSeparatorTextfield, PREFERRED_SIZE, SMALL_TEXTFIELDS_WIDTH, PREFERRED_SIZE)))
                    .addComponent(controlFilePanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                    .addComponent(buttonPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loadModePanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                .addPreferredGap(RELATED)
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(fieldSeparatorLabel)
                    .addComponent(fieldSeparatorTextfield, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                .addPreferredGap(RELATED)
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(stringDelimitatorLabel)
                    .addComponent(stringDelimitatorTextfield, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                .addPreferredGap(RELATED)
                .addComponent(controlFilePanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                .addPreferredGap(RELATED)
                .addComponent(buttonPanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE))
        );
        final JRootPane rootPane = getRootPane();
		/* On escape keystroke, it closes the dialog */
        rootPane.registerKeyboardAction(closeDialogActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        /* Makes the Generate button the default button */
        rootPane.setDefaultButton(generateButton);
        
        pack();
	}
}
