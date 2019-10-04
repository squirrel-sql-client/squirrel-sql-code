package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.encryption.AliasPasswordHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

/**
 * A dialog that allows a user to select an existing alias to add to the virtualization.
 */
public class MultiAliasChooser extends JDialog 
{	
	private static final long serialVersionUID = 1L;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MultiAliasChooser.class);

	private ExtractStatusWrapper _statusWrapper;
		
	private JComboBox _aliasCbx;
	private JTextField _nameTxt;
	private JTextField _schemaTxt;
	private ArrayList<ISQLAlias> _aliasList;
	private ISQLAlias _selectedAlias;
	private ISQLAlias _lastAdded;
	private String _sourceName;
	private IApplication _app;
	private ISession _session;
	private JTextField _tablesIncTxt;
	private JTextField _tablesExcTxt;
	private JTextField _catalogIncTxt;
	private Color _progressColor;
	@SuppressWarnings("unused")
	private boolean _extractInProgress = false;
	private Timer _timer;
	
	private JLabel _nameLbl;
	private JLabel _catelogLbl;
	private JLabel _schemaLbl;
	private JLabel _tablesIncLbl;
	private JLabel _tablesExcLbl;
	private JLabel _messagesLbl;
	private JLabel _progressLbl;
	private JPanel _progressPanel;
	private JProgressBar _progressBar;
	private JPanel _progressMessagePanel;
	private JLabel _countLbl;
	private JLabel _lastMessageLbl;
	private JLabel _messageLbl;
	private JComboBox _statsCbx;
	
	private JTextArea txtMessages;
	private JButton btnExit, btnAdd;

	private boolean extractInProgress = false;
	
	private static int fieldSize = 55;


	public MultiAliasChooser(IApplication app, ISession session, ArrayList<ISQLAlias> aliasList) {
		super((Frame) null, s_stringMgr.getString("MultiAliasChooser.title"), true);
		setSize(600, 461); // 785,600
		_aliasList = aliasList;
		_app = app;
		_session = session;
		createUserInterface();    
	}

	/**
	 * Show dialog.
	 */
	public ISQLAlias showDialog() {
		setVisible(true);
		return _lastAdded;
	}

	/**
	 * Builds the components of the dialog.
	 */	
	private void createUserInterface() {
		FlowLayout flowLayout2 = new FlowLayout();
		flowLayout2.setAlignment(java.awt.FlowLayout.LEFT);
		GridLayout gridLayout = new GridLayout(7, 1);
		gridLayout.setHgap(5);
		Font labelFont = new java.awt.Font("DialogInput", java.awt.Font.BOLD, 12); //$NON-NLS-1$
		Font textFont = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 11); //$NON-NLS-1$
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.insets = new java.awt.Insets(2,0,1,0);
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.ipadx = 700;
		gridBagConstraints2.ipady = 250;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.gridheight = 1;
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.CENTER;
		gridBagConstraints2.gridx = 0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.insets = new Insets(2, 0, 5, 0);
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.ipadx = 10;
		gridBagConstraints1.ipady = 0;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridx = 0;
	
		// Main panel for frame
		JPanel mainPanel = new JPanel();
		GridBagLayout gbl_mainPanel = new GridBagLayout();
		gbl_mainPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0};
		gbl_mainPanel.columnWeights = new double[]{1.0};
		gbl_mainPanel.rowHeights = new int[]{0, 0, 48, 0};
		mainPanel.setLayout(gbl_mainPanel);

		// Add data source panel
		JPanel addPanel = new JPanel();
		addPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
		addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.X_AXIS));
		
		JPanel rightAddPanel = new JPanel();
		JPanel tmp = new JPanel();
		tmp.setLayout(null);
		this._nameLbl = new JLabel(s_stringMgr.getString("MultiAliasChooser.name"));
		this._nameLbl.setBounds(12, 6, 100, 17);
		this._nameLbl.setFont(labelFont);
		tmp.add(this._nameLbl);
		rightAddPanel.setLayout(gridLayout);
		this._nameTxt = new JTextField(fieldSize);
		this._nameTxt.setBounds(130, 5, 444, 19);
		this._nameTxt.setFont(textFont);		
		tmp.add(this._nameTxt);
		rightAddPanel.add(tmp);

		tmp = new JPanel();
        tmp.setLayout(null);
        this._catelogLbl = new JLabel(s_stringMgr.getString("MultiAliasChooser.catalog")); 
        this._catelogLbl.setBounds(12, 6, 84, 17);
        this._catelogLbl.setFont(labelFont);
        tmp.add(this._catelogLbl);
        this._catalogIncTxt = new JTextField(fieldSize);
        this._catalogIncTxt.setBounds(130, 5, 444, 19);
        this._catalogIncTxt.setFont(textFont);
        tmp.add(this._catalogIncTxt);
        rightAddPanel.add(tmp);
        
		tmp = new JPanel();
		tmp.setLayout(null);
		this._schemaLbl = new JLabel(s_stringMgr.getString("MultiAliasChooser.schema")); 
		this._schemaLbl.setBounds(12, 6, 84, 17);
		this._schemaLbl.setFont(labelFont);
        tmp.add(this._schemaLbl);
        this._schemaTxt = new JTextField(fieldSize);
        this._schemaTxt.setBounds(130, 5, 444, 19);
        this._schemaTxt.setFont(textFont);
        tmp.add(this._schemaTxt);
        rightAddPanel.add(tmp);
        
		tmp = new JPanel();
		tmp.setLayout(null);
		this._tablesIncLbl = new JLabel(s_stringMgr.getString("MultiAliasChooser.tableInclude"));
		this._tablesIncLbl.setBounds(12, 6, 125, 17);
		this._tablesIncLbl.setFont(labelFont);		
		tmp.add(this._tablesIncLbl);
		this._tablesIncTxt = new JTextField(fieldSize);
		this._tablesIncTxt.setBounds(130, 5, 444, 19);
		this._tablesIncTxt.setFont(textFont);
		this._tablesIncTxt.setText("%");
		tmp.add(this._tablesIncTxt);
		rightAddPanel.add(tmp);
		
		tmp = new JPanel();
		tmp.setLayout(null);
		this._tablesExcLbl = new JLabel(s_stringMgr.getString("MultiAliasChooser.tableExclude"));
		this._tablesExcLbl.setBounds(12, 6, 125, 17);
		this._tablesExcLbl.setFont(labelFont);	
		tmp.add(this._tablesExcLbl);
		this._tablesExcTxt = new JTextField(fieldSize);
		this._tablesExcTxt.setBounds(130, 5, 444, 19);
		this._tablesExcTxt.setFont(textFont);
		this._tablesExcTxt.setText(".*\\$.*");
		tmp.add(this._tablesExcTxt);
		rightAddPanel.add(tmp);
		
		tmp = new JPanel();
		tmp.setLayout(null);
		JLabel tlbl = new JLabel(s_stringMgr.getString("MultiAliasChooser.stats"));
		tlbl.setBounds(12, 6, 125, 17);
		tlbl.setFont(labelFont);
		tmp.add(tlbl);
		
		this._statsCbx = new JComboBox();
		this._statsCbx.setLocation(130, 3);
		this._statsCbx.setSize(100, 19);
		this._statsCbx.setModel(new DefaultComboBoxModel(new String[] {"None", "Row Counts", "All"})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this._statsCbx.setSelectedIndex(1);
		tmp.add(this._statsCbx);
		rightAddPanel.add(tmp);
		
		addPanel.add(rightAddPanel);

		// Messages panel
		JPanel messagePanel = new JPanel();
		messagePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		this.txtMessages = new JTextArea(60, 10);
		this.txtMessages.setText(""); //$NON-NLS-1$
		JScrollPane scrollPane = new JScrollPane(this.txtMessages);
		scrollPane.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		messagePanel.add(scrollPane);
		
		this._messagesLbl = new JLabel(s_stringMgr.getString("MultiAliasChooser.messages"));
		scrollPane.setColumnHeaderView(this._messagesLbl);
		
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 185, 79);
		gridBagConstraints21.ipady = 0;
		gridBagConstraints21.gridwidth = 1;
		gridBagConstraints21.anchor = GridBagConstraints.NORTH;
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
		
		// Source file panel
		JPanel sourcePanel = new JPanel();
		sourcePanel.setLayout(flowLayout);
		sourcePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
		
      	_aliasCbx = new JComboBox(_aliasList.toArray());
		_aliasCbx.setMaximumRowCount(5); // Display up to 5 rows without a scrollbar
		_aliasCbx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				_sourceName = StringUtilities.javaNormalize(((ISQLAlias) _aliasCbx.getSelectedItem()).getName());
				_nameTxt.setText(_sourceName);
			}
		});
		
		_sourceName = StringUtilities.javaNormalize(((ISQLAlias) _aliasCbx.getSelectedItem()).getName());
		_nameTxt.setText(_sourceName);

		sourcePanel.add(new JLabel(s_stringMgr.getString("MultiAliasChooser.prompt")), null);		 //$NON-NLS-1$
		
		sourcePanel.add(_aliasCbx);
		this.btnExit = new JButton(s_stringMgr.getString("MultiAliasChooser.cancel"));
		this.btnExit.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				dispose();
			}
		});
		sourcePanel.add(this.btnExit);		
		mainPanel.add(sourcePanel, gridBagConstraints21);
		mainPanel.add(addPanel, gridBagConstraints1);
		
		this._progressPanel = new JPanel();
		GridBagConstraints gbc__progressPanel = new GridBagConstraints();
		gbc__progressPanel.insets = new Insets(0, 0, 5, 0);
		gbc__progressPanel.fill = GridBagConstraints.BOTH;
		gbc__progressPanel.gridx = 0;
		gbc__progressPanel.gridy = 2;
		mainPanel.add(this._progressPanel, gbc__progressPanel);
		this._progressPanel.setLayout(new GridLayout(2, 1, 0, 0));
		
		this._progressMessagePanel = new JPanel();
		this._progressPanel.add(this._progressMessagePanel);
		GridBagLayout gbl__progressMessagePanel = new GridBagLayout();
		gbl__progressMessagePanel.columnWidths = new int[] {10, 42, 1, 50, 0};
		gbl__progressMessagePanel.rowHeights = new int[] {20, 0};
		gbl__progressMessagePanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl__progressMessagePanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		this._progressMessagePanel.setLayout(gbl__progressMessagePanel);
		
		this._progressLbl = new JLabel(s_stringMgr.getString("MultiAliasChooser.progress"));
		this._progressLbl.setFont(new Font("Tahoma", Font.BOLD, 12)); //$NON-NLS-1$
		GridBagConstraints gbc__progressLbl = new GridBagConstraints();
		gbc__progressLbl.anchor = GridBagConstraints.WEST;
		gbc__progressLbl.insets = new Insets(0, 0, 0, 5);
		gbc__progressLbl.gridx = 0;
		gbc__progressLbl.gridy = 0;
		this._progressMessagePanel.add(this._progressLbl, gbc__progressLbl);
		
		this._countLbl = new JLabel(""); //$NON-NLS-1$
		GridBagConstraints gbc__countLbl = new GridBagConstraints();
		gbc__countLbl.anchor = GridBagConstraints.NORTHWEST;
		gbc__countLbl.insets = new Insets(0, 0, 0, 5);
		gbc__countLbl.gridx = 1;
		gbc__countLbl.gridy = 0;
		this._progressMessagePanel.add(this._countLbl, gbc__countLbl);
		
		this._lastMessageLbl = new JLabel(" "); //$NON-NLS-1$
		this._lastMessageLbl.setFont(new Font("Tahoma", Font.BOLD, 12)); //$NON-NLS-1$
		GridBagConstraints gbc__lastMessageLbl = new GridBagConstraints();
		gbc__lastMessageLbl.anchor = GridBagConstraints.NORTHWEST;
		gbc__lastMessageLbl.insets = new Insets(0, 0, 0, 5);
		gbc__lastMessageLbl.gridx = 2;
		gbc__lastMessageLbl.gridy = 0;
		this._progressMessagePanel.add(this._lastMessageLbl, gbc__lastMessageLbl);
		
		this._messageLbl = new JLabel(""); //$NON-NLS-1$
		this._messageLbl.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc__messageLbl = new GridBagConstraints();
		gbc__messageLbl.anchor = GridBagConstraints.NORTHWEST;
		gbc__messageLbl.gridx = 3;
		gbc__messageLbl.gridy = 0;
		this._progressMessagePanel.add(this._messageLbl, gbc__messageLbl);
		
		this._progressBar = new JProgressBar();
		this._progressBar.setForeground(Color.GREEN);
		this._progressPanel.add(this._progressBar);
		mainPanel.add(messagePanel, gridBagConstraints2);
		addPanel.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 12)); //$NON-NLS-1$
		rightAddPanel.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 12)); //$NON-NLS-1$
		
		this.btnAdd = new JButton(s_stringMgr.getString("MultiAliasChooser.ok"));
		this.btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (_nameTxt.getText().length() == 0)
				{	// Validate before do add source.  Must at least have a source name.				
					return;
				}
				executeAddSource();
				return;
			}
		});

		rightAddPanel.add(this.btnAdd);
		
		this.setContentPane(mainPanel);
		GUIUtils.centerWithinParent(this);
	}

	
	/**
	 * Adds the selected source to the virtualization.
	 * Uses reflection to call methods of the UnityJDBC driver.
	 */
	private boolean executeAddSource() {
		ISQLAlias alias = (ISQLAlias) _aliasCbx.getSelectedItem();
		MultiAliasChooser.this._selectedAlias = alias;
		
		// Verify correct format of source name (no spaces, etc.)
		_sourceName = _nameTxt.getText();
		if (_sourceName.contains(" ")) {
			Dialogs.showOk(_nameTxt.getParent().getParent(), "Source name cannot contain spaces.");
			return false;
		}
		
		// Retrieve schema name if it exists
		String schemaName = _schemaTxt.getText();
		if (schemaName.trim().equals(""))
			schemaName = null;

		try {
			// Verify that source name is not a duplicate
			Object schema = MultiSourcePlugin.getSchema(_session.getSQLConnection().getConnection());
			Method getDBMethod = schema.getClass().getMethod("getDB", new Class[] { java.lang.String.class });
			Object result = getDBMethod.invoke(schema, new Object[] { _sourceName });

			if (result != null) {
				Dialogs.showOk(_nameTxt.getParent().getParent(), "Source name already exists in virtualization.  Select a different name.");
				return false;
			}
			
			ExtractSourceThread th = new ExtractSourceThread("");
			th.start();
		} 
		catch (Exception e) {
			Dialogs.showOk(_nameTxt.getParent().getParent(), "Error during extraction: "+ e);
			return false;
		}
		this._lastAdded = this._selectedAlias;
		return true;
	}
	
	/**
	 * The extraction status is tracked using ExtractStatus class which is created and manipulated using reflection.	 
	 */
	private class ExtractStatusWrapper extends Object
	{
		private Object extractStatus;
		private Class<?> extractStatusClass;
		
		public ExtractStatusWrapper() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
			ClassLoader loader = _session.getSQLConnection().getConnection().getClass().getClassLoader();
			extractStatusClass = Class.forName("com.unityjdbc.sourcebuilder.ExtractStatus", true, loader);
	        extractStatus = extractStatusClass.newInstance();
		}
		
		public Object getStatus() 
		{ 
			return extractStatus; 		
		}
		
		public boolean isComplete() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
		{
			Method meth = extractStatusClass.getMethod("isComplete", new Class[] {});
			return ((Boolean)meth.invoke(extractStatus, new Object[] {})).booleanValue();
		}
		
		public boolean hasError() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Method meth = extractStatusClass.getMethod("hasError", new Class[] {});
			return ((Boolean)meth.invoke(extractStatus, new Object[] {})).booleanValue();
		}
		
		public int getTotalTables() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Method meth = extractStatusClass.getMethod("getTotalTables", new Class[] {});
			return ((Integer)meth.invoke(extractStatus, new Object[] {})).intValue();
		}
		
		public int getProcessedTables() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Method meth = extractStatusClass.getMethod("getProcessedTables", new Class[] {});
			return ((Integer)meth.invoke(extractStatus, new Object[] {})).intValue();
		}
		
		@SuppressWarnings("unchecked")
		public ArrayList<String> getMessages() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Method meth = extractStatusClass.getMethod("getMessages", new Class[] {});
			return (ArrayList<String>)meth.invoke(extractStatus, new Object[] {});
		}
		
		public String getLastMessage() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Method meth = extractStatusClass.getMethod("getLastMessage", new Class[] {});
			return (String)meth.invoke(extractStatus, new Object[] {});
		}
		
		public String getError() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Method meth = extractStatusClass.getMethod("getError", new Class[] {});
			return (String)meth.invoke(extractStatus, new Object[] {});
		}		
	}
	
	private class RefreshAction implements ActionListener 
	{
		private int lastMessageOutput=0;
		
		@Override
		@SuppressWarnings({ "synthetic-access" })
		public void actionPerformed(ActionEvent actionevent) 
		{
			try {
				// Update the progress bar showing the number of tables processed out of the total tables as well as the text label (e.g. 3 of 9)	 	    	  
				MultiAliasChooser.this._progressBar.setMaximum(MultiAliasChooser.this._statusWrapper.getTotalTables());
				MultiAliasChooser.this._progressBar.setValue(MultiAliasChooser.this._statusWrapper.getProcessedTables());
				MultiAliasChooser.this._countLbl.setText(MultiAliasChooser.this._statusWrapper.getProcessedTables()+" of "+MultiAliasChooser.this._statusWrapper.getTotalTables()); //$NON-NLS-1$

				int maxLabelSize = 110;

				// Display the last message
				ArrayList<String> messages = MultiAliasChooser.this._statusWrapper.getMessages();
				if (messages != null && messages.size() > 0)
				{	String st = MultiAliasChooser.this._statusWrapper.getLastMessage(); 
					MultiAliasChooser.this._messageLbl.setText(st.substring(0,Math.min(maxLabelSize, st.length())));	// Only allow a maximum of 50 characters in text display field
				}

				// Print out all the messages to the JTextArea that have not yet been shown
				for (; this.lastMessageOutput < messages.size(); this.lastMessageOutput++)
					MultiAliasChooser.this.txtMessages.append(messages.get(this.lastMessageOutput)+'\n');

				// If there was any error, show the progress bar in red and display the last error message.
				String error = MultiAliasChooser.this._statusWrapper.getError();
				if (error != null)
				{	    
					MultiAliasChooser.this._messageLbl.setText(error.substring(0,Math.min(maxLabelSize, error.length())));	// Only allow a maximum of 50 characters in text display field
					MultiAliasChooser.this._progressBar.setForeground(Color.RED);
				}

				if (MultiAliasChooser.this._statusWrapper.isComplete())
				{			    		
					MultiAliasChooser.this._timer.stop();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ExtractSourceThread extends Thread 
	{
	    /**
	     * Encryption password for schema information
	     */
    	private String password;
    	
    	/**
    	 * Constructs thread for extracting data source and adding into metadata system.
    	 * 
    	 * @param password
    	 *      encryption password
    	 */
    	public ExtractSourceThread(String password)
    	{
    	    this.password = password;
    	}
    	
		@SuppressWarnings("synthetic-access")
		@Override
		public void run()
		{
			if (MultiAliasChooser.this.extractInProgress)
			{	// Extraction going on - do not proceed
				return;
			}
			
			if (MultiAliasChooser.this._progressColor != null) {
				// reset the progress bar
				MultiAliasChooser.this._progressBar.setStringPainted(true);
				MultiAliasChooser.this._progressBar.setForeground(MultiAliasChooser.this._progressColor);
				MultiAliasChooser.this._progressBar.setString("");
			}
			
			MultiAliasChooser.this.extractInProgress = true;
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			MultiAliasChooser.this._progressBar.setForeground(Color.GREEN);
			
			//, alias.getUrl(), alias.getUserName(), alias.getPassword()
			ISQLAlias alias = (ISQLAlias) MultiAliasChooser.this._aliasCbx.getSelectedItem();
			IIdentifier driverID = alias.getDriverIdentifier();
			ISQLDriver driver = _app.getDataCache().getDriver(driverID);
			
			String driverName = driver.getDriverClassName();
			String url = alias.getUrl();
			String userName =  alias.getUserName();
			this.password = AliasPasswordHandler.getPassword(alias);
			String dbName = MultiAliasChooser.this._nameTxt.getText();
	        String schemaName = MultiAliasChooser.this._schemaTxt.getText();
	        String tableInc = MultiAliasChooser.this._tablesIncTxt.getText();
	        String tableExc = MultiAliasChooser.this._tablesExcTxt.getText();
	        String catalogInc = MultiAliasChooser.this._catalogIncTxt.getText();
	        if (catalogInc.trim().equals(""))
	            catalogInc = null;
	        int statsType = MultiAliasChooser.this._statsCbx.getSelectedIndex();
	        
	        if (dbName.equals("")) //$NON-NLS-1$
	         	dbName = null;
	        if (schemaName.equals("")) //$NON-NLS-1$
	         	schemaName = null;
	        
	        // Set up extract status object and start timer to poll status as extraction is proceeding.	        
	        try {
				MultiAliasChooser.this._statusWrapper = new ExtractStatusWrapper();
		        MultiAliasChooser.this._timer = new Timer(100, new RefreshAction());
		        MultiAliasChooser.this._timer.start();
		        
		        ClassLoader loader = _session.getSQLConnection().getConnection().getClass().getClassLoader();
		        Class<?> extractStatusClass = Class.forName("com.unityjdbc.sourcebuilder.ExtractStatus", true, loader);
				Class<?> extractThreadClass = Class.forName("com.unityjdbc.sourcebuilder.ExtractThread", true, loader);
				Constructor<?>[] allConstructors = extractThreadClass.getDeclaredConstructors();
				
				Constructor<?> threadConstructor = null;
				
				for (Constructor<?> ctor : allConstructors) {
					Class<?> [] pType  = ctor.getParameterTypes();
					if (pType.length > 0 && pType[0].equals(extractStatusClass)) {
				    	threadConstructor = ctor;
				    	break;
					}
				}
					
				Object extractThread = null;
				
				if (threadConstructor != null) {
					Method meth = extractThreadClass.getMethod("start", new Class[] {});
					extractThread = threadConstructor.newInstance(MultiAliasChooser.this._statusWrapper.getStatus(), driverName, url, userName, password,  dbName, schemaName, tableInc, tableExc, catalogInc, statsType);
					meth.invoke(extractThread,  new Object[] {});
					
					while (!MultiAliasChooser.this._statusWrapper.isComplete())
					{	
						try 
						{		
							Thread.sleep(10);
						} 
						catch (InterruptedException e) 
						{						
							e.printStackTrace();
						}						
					}
					
					if (!MultiAliasChooser.this._statusWrapper.hasError())
					{			
						Method getDatabase = extractThreadClass.getMethod("getDatabase", new Class[] {});
						Object asd = getDatabase.invoke(extractThread, new Object[] {});
						
						Object schema = MultiSourcePlugin.getSchema(_session.getSQLConnection().getConnection());
						Method addSourceMethod = schema.getClass().getMethod("addDatabase", new Class[] { asd.getClass() });
						addSourceMethod.invoke(schema, new Object[] { asd });

						try {
							MultiSourcePlugin.updateSession(_session);
						} 
						catch (RuntimeException e) {
							MultiAliasChooser.this._progressColor = MultiAliasChooser.this._progressBar.getForeground();							
							MultiAliasChooser.this._progressBar.setStringPainted(true);
							MultiAliasChooser.this._progressBar.setForeground(Color.red);
							MultiAliasChooser.this._progressBar.setString("");
							MultiAliasChooser.this._messageLbl.setText(s_stringMgr.getString("MultiAliasChooser.saveFailed"));
						}
						MultiSourcePlugin.refreshTree(_session);
					}
					MultiAliasChooser.this.extractInProgress = false;
					setCursor(null);					
				}
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
	}
}
