/*
 * Copyright (C) 2008 Dieter Engelhardt
 * dieter@ew6.org
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
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Dieter
 *
 */
public class SQLReplacementPreferencesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Flag indicates changing in textarea
	private boolean hasChanged = false;
	
    String BM_SAVE = "button.save.title";
    String BM_ACCESS_HINT = "dialog.add.accesshint";
    
	JTextArea replacementEditor;
	JButton btnSave;
	
   public SQLReplacementPreferencesPanel(SQLReplacePlugin plugin)
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;
      replacementEditor = new JTextArea();
      // Keylistener to indicate changing of text
      replacementEditor.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent e) {
				if (!hasChanged) {
					hasChanged = true;
				}
			}
		});

      gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      add(new JScrollPane(replacementEditor), gbc);

      JPanel buttonPane = createButtonPane(plugin);
      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,5,5), 0,0);
      add(buttonPane, gbc);

      JPanel southPane = createSouthPane(plugin);
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(southPane, gbc);
   }
   private JPanel createSouthPane(SQLReplacePlugin plugin)
   {
      JPanel pnlSouth = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);

      JLabel lblAccesshint = new JLabel(plugin.getResourceString(BM_ACCESS_HINT));
      lblAccesshint.setForeground(Color.red);
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      pnlSouth.add(lblAccesshint, gbc);

      gbc = new GridBagConstraints(1,0,1,2,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      pnlSouth.add(new JPanel(), gbc);


      return pnlSouth;
   }

   private JPanel createButtonPane(SQLReplacePlugin plugin)
   {
      JPanel buttonPane = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      btnSave = new JButton(plugin.getResourceString(BM_SAVE));
      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,1,1,1), 0, 0);
      buttonPane.add(btnSave, gbc);

      gbc = new GridBagConstraints(0,6,1,1,0,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
      buttonPane.add(new JPanel(), gbc);
      return buttonPane;
   }


   public boolean hasChanged()
   {
	   return hasChanged;
   }
}
