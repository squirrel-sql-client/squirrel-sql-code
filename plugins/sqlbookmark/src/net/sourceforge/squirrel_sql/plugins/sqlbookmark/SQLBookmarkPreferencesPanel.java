/*
 * Copyright (C) 2003 Joseph Mocker
 * mock-sf@misfit.dhs.org
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

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.IApplication;
import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;

/**
 * Manage the bookmarks.
 *
 * The interface to allow a user to manages his/her bookmarks is through
 * this class. The user can add, edit, remove and shift the order of the
 * bookmarks with this user interface. The interface shows up in as
 * a new tab in the Global Preferences dialog.
 *
 *
 * @author      Joseph Mocker
 **/
public class SQLBookmarkPreferencesPanel implements IGlobalPreferencesPanel {
    private interface IPrefKeys {
	String BM_TITLE = "prefs.title";
	String BM_HINT = "prefs.hint";
	String BM_UP = "button.up.title";
	String BM_DOWN = "button.down.title";
	String BM_ADD = "button.add.title";
	String BM_EDIT = "button.edit.title";
	String BM_DEL = "button.del.title";
    }

    /** The main panel for preference administration */
    protected JPanel main;

    /** Handle to the main application */
    protected IApplication app;

    /** Handle to the plugin */
    protected SQLBookmarkPlugin plugin;

    /** The original state of the bookmarks */
    protected BookmarkManager originalMarks;

    /** The working set of the updated bookmarks */
    protected ArrayList updatedMarks;

    /** The list representing the bookmark updates */
    protected JList markList;

    /** The data model of the markList */
    protected DefaultListModel markModel;

    /** Create the preferences */
    public SQLBookmarkPreferencesPanel(SQLBookmarkPlugin plugin) {
	this.plugin = plugin;
	this.originalMarks = plugin.getBookmarkManager();
	updatedMarks = new ArrayList();
    }

    /**
     * Initialize the user interface 
     *
     * @param       app Handle to the main application.
     **/
    public void initialize(IApplication app) {
	// this gets called after getPanelComponent()
	this.app = app;

	buildPanelComponent(main);
    }

    /** 
     * Return the title for the tab name 
     *
     * @return      The tab title.
     */
    public String getTitle() {
	return plugin.getResourceString(IPrefKeys.BM_TITLE);
    }

    /** 
     *Return the tool tip for the tab 
     *
     * @return      The tab hint
     */
    public String getHint() {
	return plugin.getResourceString(IPrefKeys.BM_HINT);
    }

    /** 
     * Make the changes active to the rest of the application 
     */
    public void applyChanges() {
	// create a new bookmark manager
	BookmarkManager bookmarks = plugin.getBookmarkManager();
//	    new BookmarkManager(plugin.userSettingsFolder);

       bookmarks.removeAll();
	// load all the new changed bookmarks into it
	for (Iterator i = updatedMarks.iterator(); i.hasNext(); ) {
	    Bookmark mark = (Bookmark) i.next();
	    bookmarks.add(mark);
	}
	
	// set it as the new bookmark manager.
	//plugin.setBookmarkManager(bookmarks);
	
	// rebuild the bookmark menu.
	plugin.rebuildMenu();
	try {
	    // save the updates.
	    bookmarks.save();
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Return the panel that will contain the prefernces ui.
     *
     * @return      Panel containing preferences.
     **/
    public Component getPanelComponent() {
	// this gets called before initialize()
	main = new JPanel(new BorderLayout());
	
	return main;
    }

    /**
     * Builds everything in the prefernces ui. 
     *
     * Note that this is called from the initialize() method because
     * it needs some information contained in the app handle.
     *
     * @param       main Handle to the main panel component
     **/
    protected void buildPanelComponent(JPanel main) {
	
	markModel = new DefaultListModel();
	for (Iterator i = originalMarks.iterator(); i.hasNext(); ) {
	    Bookmark mark = (Bookmark) i.next();
	    markModel.addElement(mark.getName());
	    updatedMarks.add((Bookmark) mark.clone());
	}
	
	markList = new JList(markModel);
	markList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	JScrollPane viewport = new JScrollPane(markList);
	main.add(viewport, BorderLayout.CENTER);

   JLabel lblAccesshint = new JLabel(plugin.getResourceString(AddBookmarkDialog.BM_ACCESS_HINT));
   lblAccesshint.setForeground(Color.red);
   main.add(lblAccesshint, BorderLayout.SOUTH);


	JPanel buttonPane = new JPanel(new GridLayout(0,1));
	JButton up = new JButton(plugin.getResourceString(IPrefKeys.BM_UP));
	buttonPane.add(up);
	JButton down= new JButton(plugin.getResourceString(IPrefKeys.BM_DOWN));
	buttonPane.add(down);
	JButton add = new JButton(plugin.getResourceString(IPrefKeys.BM_ADD));
	buttonPane.add(add);
	JButton edit= new JButton(plugin.getResourceString(IPrefKeys.BM_EDIT));
	buttonPane.add(edit);
	JButton del = new JButton(plugin.getResourceString(IPrefKeys.BM_DEL));
	buttonPane.add(del);
	
	Box buttonBox = new Box(BoxLayout.Y_AXIS);
	buttonBox.add(Box.createGlue());
	buttonBox.add(buttonPane);
	buttonBox.add(Box.createGlue());
	main.add(buttonBox, BorderLayout.EAST);

	up.addActionListener(new UpAction(this));
	down.addActionListener(new DownAction(this));
	add.addActionListener(new AddAction(this));
	edit.addActionListener(new EditAction(this));
	del.addActionListener(new DeleteAction(this));
    }


    /**
     * Internal class to build the bookmark entry/update component
     *
     * @author      Joseph Mocker
     **/
    class EntryDialog extends JDialog {

	JTextField name;
	JTextField description;
	JTextArea sql;
	JButton action;
	JButton cancel;
	boolean cancelled = false;
       private JScrollPane sqlScroll;

       /**
	 * Create the entry dialog 
	 *
	 * @param    owner The frame the dialog will be centered in
	 */
	public EntryDialog(Frame owner) {
	    super(owner, "Edit bookmark", true);

	    Container contentPane = getContentPane();
	    contentPane.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      contentPane.add(new JLabel("Name:"), gbc);

      name = new JTextField(30);
      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5),0,0);
      contentPane.add(name, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      contentPane.add(new JLabel("Description:"), gbc);

      description = new JTextField();
      gbc = new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5),0,0);
      contentPane.add(description, gbc);

      gbc = new GridBagConstraints(0,2,2,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5),0,0);
      contentPane.add(new JLabel("Script:"), gbc);

      sql = new JTextArea(5,30);
      gbc = new GridBagConstraints(0,3,2,1,1,1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);
          sqlScroll = new JScrollPane(sql);
          contentPane.add(sqlScroll, gbc);


      action = new JButton("OK");
      gbc = new GridBagConstraints(0,4,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      contentPane.add(action, gbc);

      cancel = new JButton("Close");
      gbc = new GridBagConstraints(1,4,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      contentPane.add(cancel, gbc);

	    action.addActionListener(new DoneAction(this));
	    cancel.addActionListener(new CancelAction(this));

       getRootPane().setDefaultButton(action);

	    pack();
	}

	/**
	 * Report whether user cancelled the operation or not.
	 *
	 * @return      if true, user cancelled operation.
	 **/
	public boolean isCancelled() {
	    return cancelled;
	}

	/**
	 * Set the cancelled status. Called by the button actions.
	 *
	 * @param       status The cancelled status.
	 **/
	public void setCancelled(boolean status) {
	    cancelled = status;
	}
    }

    /**
     * Internal action class for the EntryDialog. Called when the user
     * clicks the "OK" button.
     *
     * @author      Joseph Mocker
     **/
    class DoneAction implements ActionListener {

	EntryDialog dialog = null;
	
	public DoneAction(EntryDialog dialog) {
	    this.dialog = dialog;
	}
	 
	public void actionPerformed(ActionEvent e) {
	    dialog.setVisible(false);
	    dialog.setCancelled(false);
	}
    }

    /**
     * Internal action class for the EntryDialog. Called when the user
     * clicks the "Cancel" button.
     *
     * @author      Joseph Mocker
     **/
    class CancelAction implements ActionListener {

	EntryDialog dialog = null;
	
	public CancelAction(EntryDialog dialog) {
	    this.dialog = dialog;
	}
	 
	public void actionPerformed(ActionEvent e) {
	    dialog.setVisible(false);
	    dialog.setCancelled(true);
	}
    }

    /**
     * Internal action class for the main preferences tab. 
     *
     * This is a base class for several other action classes
     * providing common functionality needed by the other classes.
     *
     * @author      Joseph Mocker
     **/
    class EntryAction {
	SQLBookmarkPreferencesPanel admin;
	int item;
	Bookmark mark;
	EntryDialog entryDialog;

	public EntryAction(SQLBookmarkPreferencesPanel admin) {
	    this.admin = admin;
	}

	public boolean entry() {
	    entryDialog = new EntryDialog(admin.app.getMainFrame());
	    if (item >= 0) {
		mark = (Bookmark) admin.updatedMarks.get(item);
		entryDialog.name.setText(mark.getName());
		entryDialog.description.setText(mark.getDescription());
		entryDialog.sql.setText(mark.getSql());
	    }

	    entryDialog.setLocationRelativeTo(admin.app.getMainFrame());
	    entryDialog.setVisible(true);

	    return entryDialog.isCancelled();
	}
    }

    /**
     * Internal action class for the main preferences tab. Called when
     * user clicks the "Add" button.
     *
     * @author      Joseph Mocker
     **/
    class AddAction extends EntryAction implements ActionListener {

	public AddAction(SQLBookmarkPreferencesPanel admin) {
	    super(admin);
	}
	
	public void actionPerformed(ActionEvent e) {
	    item = -1;

	    if (entry()) return;
	    
	    item = admin.markList.getSelectedIndex();
	    
	    mark = new Bookmark();
	    mark.setName(entryDialog.name.getText());
	    mark.setDescription(entryDialog.description.getText());
	    mark.setSql(entryDialog.sql.getText());

	    if (item < 0) {
		admin.markModel.addElement(mark.getName());
		admin.updatedMarks.add(mark);
		admin.markList.setSelectedIndex(admin.updatedMarks.size() - 1);
	    } else {
		admin.markModel.insertElementAt(mark.getName(), item);
		admin.updatedMarks.add(item, mark);
		admin.markList.setSelectedIndex(item);
	    }
	}
    }

    /**
     * Internal action class for the main preferences tab. Called when
     * user clicks the "Edit" button.
     *
     * @author      Joseph Mocker
     **/
    class EditAction extends EntryAction implements ActionListener {

	public EditAction(SQLBookmarkPreferencesPanel admin) {
	    super(admin);
	}
	
	public void actionPerformed(ActionEvent e) {
	    item = admin.markList.getSelectedIndex();
	    if (item < 0) return;
	    
	    if (entry()) return;
	    
	    mark.setName(entryDialog.name.getText());
	    mark.setDescription(entryDialog.description.getText());
	    mark.setSql(entryDialog.sql.getText());
	    admin.markModel.setElementAt(mark.getName(), item);
	}
    }

    /**
     * Internal action class for the main preferences tab. Called when
     * the user clicks the "Delete" button.
     *
     * @author      Joseph Mocker
     **/
    class DeleteAction extends EntryAction implements ActionListener {

	public DeleteAction(SQLBookmarkPreferencesPanel admin) {
	    super(admin);
	}
	
	public void actionPerformed(ActionEvent e) {
	    item = admin.markList.getSelectedIndex();
	    if (item < 0) return;
	    
	    admin.markModel.removeElementAt(item);
	    admin.updatedMarks.remove(item);
	}
    }

    /**
     * Internal action class for the main preferences tab. Called when
     * user clicks the "Move Up" button.
     *
     * @author      Joseph Mocker
     **/
    class UpAction extends EntryAction implements ActionListener {

	public UpAction(SQLBookmarkPreferencesPanel admin) {
	    super(admin);
	}
	
	public void actionPerformed(ActionEvent e) {
	    item = admin.markList.getSelectedIndex();
	    if (item < 1) return;
	    
	    Bookmark mark1 = (Bookmark) admin.updatedMarks.get(item - 1);
	    Bookmark mark2 = (Bookmark) admin.updatedMarks.get(item);
	    admin.updatedMarks.set(item - 1, mark2);
	    admin.updatedMarks.set(item, mark1);

	    admin.markModel.setElementAt(mark2.getName(), item - 1);
	    admin.markModel.setElementAt(mark1.getName(), item);

	    admin.markList.setSelectedIndex(item - 1);
	}
    }

    /**
     * Internal action class for the main preferences tab. Called when
     * use clicks the "Move Down" button.
     *
     * @author      Joseph Mocker
     **/
    class DownAction extends EntryAction implements ActionListener {

	public DownAction(SQLBookmarkPreferencesPanel admin) {
	    super(admin);
	}
	
	public void actionPerformed(ActionEvent e) {
	    item = admin.markList.getSelectedIndex();
	    if (item > (admin.updatedMarks.size() - 2)) return;
	    
	    mark = (Bookmark) admin.updatedMarks.get(item + 1);
	    Bookmark mark2 = (Bookmark) admin.updatedMarks.get(item);
	    admin.updatedMarks.set(item + 1, mark2);
	    admin.updatedMarks.set(item, mark);

	    admin.markModel.setElementAt(mark2.getName(), item + 1);
	    admin.markModel.setElementAt(mark.getName(), item);

	    admin.markList.setSelectedIndex(item + 1);
	}
    }
}
