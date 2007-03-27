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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Runs a bookmark.
 *
 * @author      Joseph Mocker
 **/
public class RunBookmarkCommand implements ICommand {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RunBookmarkCommand.class);

	/** Parent frame. */
	private final Frame frame;

    /** The session that we are saving a script for. */
    private final ISession session;

    /** The current plugin. */
    private SQLBookmarkPlugin plugin;
    
    /** The bookmark to run */
    private Bookmark bookmark;
   private ISQLEntryPanel _sqlEntryPanel;

   /**
     * Ctor.
     *
     * @param   frame   Parent Frame.
     * @param   session The session that we are saving a script for.
     * @param   bookmark The bookmark to run.
     * @param   plugin  The current plugin.
     *
     * @param sqlEntryPanel
     * @throws  IllegalArgumentException
     *          Thrown if a <TT>null</TT> <TT>ISession</TT> or <TT>IPlugin</TT>
     *          passed.
     */
    public RunBookmarkCommand(Frame frame, ISession session,
                              Bookmark bookmark, SQLBookmarkPlugin plugin, ISQLEntryPanel sqlEntryPanel)
        throws IllegalArgumentException {
        super();
      _sqlEntryPanel = sqlEntryPanel;
      if (session == null) {
            throw new IllegalArgumentException("Null ISession passed");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Null IPlugin passed");
        }
        this.frame = frame;
        this.session = session;
        this.plugin = plugin;
	this.bookmark = bookmark;

    }

    /**
     * Load the Bookmark into the SQL Edit buffer.
     */
	 public void execute()
	 {
		 if (session != null)
		 {
			 String sql = parseAndLoadSql(bookmark.getSql());

			 if (null != sql)
			 {

				 int caretPosition = _sqlEntryPanel.getCaretPosition();
				 _sqlEntryPanel.replaceSelection(sql);
				 _sqlEntryPanel.setCaretPosition(caretPosition + sql.length());
			 }
		 }
	 }


	/**
     * Parse the SQL and prompt the user for entry values.
     *
     * Bookmarked SQL strings can have replaceable parameters. At the time 
     * the bookmark is loaded, the user is asked to enter values for any
     * of the parameters. The parameters come in three forms:
     * ${prompt[, tip]}		 - simple anonymous parameter
     * ${id=name, prompt[, tip]} - named parameter, allows it to be reused
     * ${ref=name}		 - use the value of an already named parameter.
     *   where
     * prompt is the string to display in the popup prompt
     * tip is the optional tooltip to display on the popup prompt
     * name is the "variable" name of the parameter.

     * @param       sql The SQL to parse and load.
     * @return      the post-processed SQL.
     **/
    protected String parseAndLoadSql(String sql) {

        // TODO: Make Parameter implement SQLItem interface which has a getString
        // method which can also be implemented by SQLString, or SQLFragment or
        // some such.  We can then eliminate the use of instanceof below and 
        // clean up the code a bit, by making itemsInSql look like:
        //
        //   ArrayList<SQLItem> itemsInSql = new ArrayList<SQLItem>();
        // 
    	ArrayList itemsInSql = new ArrayList();
    	HashMap<String,Parameter> paramsById = new HashMap<String,Parameter>();
    	ArrayList<Parameter> parameters = new ArrayList<Parameter>();

    
        HashMap<String, Parameter> lookup = new HashMap<String, Parameter>();
        
        //
        // First parse the SQL string
        //
        int start = 0;
        int idx = 0;
        while ((idx = sql.indexOf("${", start)) >= 0) {
            int ridx = sql.indexOf("}", idx);
            if (ridx < 0) break;

            String arg = sql.substring(idx + 2, ridx);
            itemsInSql.add(sql.substring(start, idx));
            start = ridx + 1;

            StringTokenizer st = new StringTokenizer(arg, ",");
            Parameter parameter = new Parameter();
            if (arg.startsWith("ref=")) {
                String ref = st.nextToken();
                parameter.reference = ref.substring(4);
            }
            else if (arg.startsWith("id=")) {
                String id = st.nextToken();
                String prompt = st.nextToken();
                parameter.id = id.substring(3);
                parameter.prompt = prompt;
                if (st.countTokens() > 0) {
                    String tip = st.nextToken();
                    parameter.tip = tip;
                }
            }
            else {
                String prompt = st.nextToken();
                parameter.prompt = prompt;
                if (st.countTokens() > 0) {
                    String tip = st.nextToken();
                    parameter.tip = tip;
                }
            }

            if (parameter.reference == null) {

                // 1646886: If we've already seen the parameter, don't create another
                // instance as this will force the user to enter the same value twice.
                // Add the previous instance to itemsInSql though so that the parameters
                // value gets propagated to the right spot(s) in the SQL statement.
                if (lookup.containsKey(parameter.prompt)) {
                    parameter = lookup.get(parameter.prompt);
                } else {
                    lookup.put(parameter.prompt, parameter);
                    parameters.add(parameter);
                }

            }
            if (parameter.id != null) {
                paramsById.put(parameter.id, parameter);
            }
            itemsInSql.add(parameter);
        }
        itemsInSql.add(sql.substring(start));

        DoneAction doneAction = null;
        //
        // If there are parameters in the SQL string, then we need
        // to prompt for some answers.
        //
        if (parameters.size() > 0) {
            // i18n[sqlbookmark.qureyParams=Query Parameters]
            JDialog dialog = new JDialog(frame, s_stringMgr.getString("sqlbookmark.qureyParams"), true);
            Container contentPane = dialog.getContentPane();
            contentPane.setLayout(new BorderLayout());

            PropertyPanel propPane = new PropertyPanel();
            contentPane.add(propPane, BorderLayout.CENTER);

            for (idx = 0; idx < parameters.size(); idx++) {
                Parameter parameter = parameters.get(idx);

                JLabel label = new JLabel(parameter.prompt + ":",
                        SwingConstants.RIGHT);
                if (parameter.tip != null)
                    label.setToolTipText(parameter.tip);

                JTextField value = new JTextField(20);
                propPane.add(label, value);

                parameter.value = value;
            }

            JPanel actionPane = new JPanel();
            contentPane.add(actionPane, BorderLayout.SOUTH);

            // i18n[sqlbookmark.btnOk=OK]
            JButton done = new JButton(s_stringMgr.getString("sqlbookmark.btnOk"));
            actionPane.add(done);
            doneAction = new DoneAction(dialog);
            done.addActionListener(doneAction);
            dialog.getRootPane().setDefaultButton(done);
            dialog.setLocationRelativeTo(frame);
            dialog.pack();
            dialog.setVisible(true);
        }

        if(null == doneAction || doneAction.actionExecuted())
        {
            //
            // No go through the parse SQL and build the final SQL replacing
            // parameters with values is goes.
            //
            StringBuffer sqlbuf = new StringBuffer();
            for (idx = 0; idx < itemsInSql.size(); idx++) {
                Object item = itemsInSql.get(idx);
                if (item instanceof String)
                    sqlbuf.append((String) item);
                if (item instanceof Parameter) {
                    Parameter parameter = (Parameter) item;
                    if (parameter.reference != null)
                        parameter = paramsById.get(parameter.reference);

                    sqlbuf.append(parameter.value.getText());
                }
            }

            return sqlbuf.toString();

        }
        else
        {
            return null;
        }

    }

    /**
     * Internal convenience class for managing attributes of a parameter.
     */
    class Parameter {
	String reference;
	String id;
	String prompt;
	String tip;
	JTextField value;
    }

    /** 
     * Internal action class called when user clicks the "OK" button.
     */
    class DoneAction implements ActionListener {

	JDialog dialog = null;
       private boolean _actionExecuted;

       public DoneAction(JDialog dialog) {
	    super();
	    this.dialog = dialog;
	}
	 
	public void actionPerformed(ActionEvent e) {
	   _actionExecuted = true;
      dialog.dispose();
	}

    public boolean actionExecuted(){
       return _actionExecuted;
    }
   }
	
}
