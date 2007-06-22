package net.sourceforge.squirrel_sql.plugins.oracle.explainplan;
/*
 * Copyright (C) 2004 Jason Height
 * jmheight@users.sourceforge.net
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
import java.awt.Component;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.event.SQLResultExecuterTabEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import com.sun.treetable.AbstractTreeTableModel;
import com.sun.treetable.JTreeTable;
import com.sun.treetable.TreeTableModel;


/**
 * This is the panel where Oracle Explain Plans are executed and results presented.
 *
 */
public class ExplainPlanExecuter extends JPanel implements ISQLResultExecuter {

    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExplainPlanExecuter.class);


  /** Logger for this class. */
  private static final ILogger s_log = LoggerController.createLogger(
		ExplainPlanExecuter.class);

  private ISession _session;
  private boolean checkedPlanTable = false;
  /** Factory for generating unique IDs for the explain plan statement ids*/
  private IntegerIdentifierFactory _idFactory = new IntegerIdentifierFactory();

  private JTreeTable treeTable;

  private String _planTableName = "PLAN_TABLE";
  
  private static String USER_PLAN_TABLE_SQL = 
      "SELECT 1 from USER_TABLES WHERE UPPER(TABLE_NAME) = UPPER(?)";

  private static String ALL_PLAN_TABLE_SQL = 
      "SELECT OWNER, TABLE_NAME " +
      "from ALL_TABLES WHERE UPPER(TABLE_NAME) = UPPER(?)";  
  /**
	* Ctor.
	*
	* @param	session	 Current session.
	*
	* @throws	IllegalArgumentException
	*			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	*/
  public ExplainPlanExecuter(ISession session, ISQLPanelAPI sqlpanel) {
	 super();
	 setSession(session);
	 createGUI();
	 sqlpanel.addExecuterTabListener(new MySqlExecuterTabListener());
  }


  public String getTitle() {
	  // i18n[oracle.explainPlan=Explain Plan]
	 return s_stringMgr.getString("oracle.explainPlan");
  }

  public JComponent getComponent() {
	 return this;
  }

  /**
	* Set the current session.
	*
	* @param	session	 Current session.
	*
	* @throws	IllegalArgumentException
	*			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	*/
  public synchronized void setSession(ISession session) {
	 if (session == null) {
		throw new IllegalArgumentException("Null ISession passed");
	 }
	 sessionClosing();
	 _session = session;
  }

  /** Current session. */
  public ISession getSession() {
      return _session;
  }

  private void expandEntireTree(final JTree tree, final TreePath parentPath) {
	 TreeNode parent = (TreeNode)parentPath.getLastPathComponent();
	 int size = parent.getChildCount();
	 for (int i=0;i<size;i++) {
		TreeNode child = parent.getChildAt(i);
		TreePath p = parentPath.pathByAddingChild(child);
		tree.expandPath(p);
		expandEntireTree(tree, p);
	 }
  }

  protected JTreeTable createTreeTable(TreeTableModel model) {
	 JTreeTable treeTable = new JTreeTable(model);
	 treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	 TableColumnModel columnModel = treeTable.getColumnModel();
	 //Operation column increased
	 columnModel.getColumn(1).setPreferredWidth(300);
	 //Options column increased
	 columnModel.getColumn(2).setPreferredWidth(100);
	 JTree treeTableTree = treeTable.getTree();
	 treeTableTree.setCellRenderer(new PlanTreeCellRenderer());
	 //Expand all of the rows in the tree if the root is non null.
	 Object root = treeTableTree.getModel().getRoot();
	 if (root != null) {
		TreePath p = new TreePath(root);
		expandEntireTree(treeTableTree, p);
	 }
	 return treeTable;
  }

  public void execute(ISQLEntryPanel sqlPanel) {
	 String sqlToBeExecuted = sqlPanel.getSQLToBeExecuted();
	 if (sqlToBeExecuted != null && (sqlToBeExecuted.trim().length() > 0)) {
         sqlToBeExecuted = sqlToBeExecuted.trim();
         if (sqlToBeExecuted.endsWith(";")) {
            sqlToBeExecuted = 
                sqlToBeExecuted.substring(0, sqlToBeExecuted.length()-1);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Executing SQL: "+sqlToBeExecuted);
            }
        }
         
		String statementId = "squirrel_exp_plan"+_idFactory.createIdentifier();
		PreparedStatement deletePlan = null;
		try {
		  //Clear any previous plan
		  deletePlan = getSession().getSQLConnection().prepareStatement(
				"delete from " + getPlanTableName() + " where statement_id = ?");
		  deletePlan.setString(1, statementId);
		  deletePlan.execute();
		  deletePlan.close();

		  String explainSql = "EXPLAIN PLAN SET STATEMENT_ID = '" + statementId +
				"' INTO " + getPlanTableName() + " FOR " + sqlToBeExecuted;
		  Statement explainPlan = null;
		  PreparedStatement returnPlan = null;
		  try {
			 explainPlan = getSession().getSQLConnection().createStatement();
			 explainPlan.execute(explainSql);

			 String extractPlanResults = "select " +
				  "   id," +
				  "   parent_id," +
				  "   LEVEL," +
				  "   STATEMENT_ID," +
				  "   TIMESTAMP," +
				  "   REMARKS," +
				  "   OPERATION," +
				  "   OPTIONS," +
				  "   OBJECT_NODE," +
				  "   OBJECT_OWNER," +
				  "   OBJECT_NAME," +
				  "   OBJECT_INSTANCE," +
				  "   OBJECT_TYPE," +
				  "   OPTIMIZER," +
				  "   SEARCH_COLUMNS," +
				  "   POSITION," +
				  "   COST," +
				  "   CARDINALITY," +
				  "   BYTES," +
				  "   OTHER_TAG," +
				  "   PARTITION_START," +
				  "   PARTITION_STOP," +
				  "   PARTITION_ID," +
				  "   OTHER," +
				  "   DISTRIBUTION " +
				  "from " + getPlanTableName() + " " +
				  "connect by " +
				  "prior id = parent_id and statement_id = ? " +
				  "start with id = 0 and statement_id = ? " +
				  "order by id";

			 //SQLExecuterTask task = new SQLExecuterTask(_session, sql, new DefaultSQLExecuterHandler(_session));
			 //jmh to run async_session.getApplication().getThreadPool().addTask(task);
			 //jmhtask.run();
			 //Set the ? to the statement identifier
			 returnPlan = getSession().getSQLConnection().prepareStatement(
				  extractPlanResults);
			 returnPlan.setString(1, statementId);
			 returnPlan.setString(2, statementId);
			 if (returnPlan.execute()) {
				ResultSet rs = returnPlan.getResultSet();
				int previousLevel = 1;
				ExplainPlanModel.ExplainRow lastRow = null;
				ExplainPlanModel.ExplainRow root = new ExplainPlanModel.ExplainRow(null,
					 -1, null, null, null, sqlToBeExecuted, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
				ExplainPlanModel model = new ExplainPlanModel(root);
				while (rs.next()) {
				  BigDecimal id = rs.getBigDecimal(1);
				  BigDecimal parent_id = rs.getBigDecimal(2);
				  int level = rs.getBigDecimal(3).intValue();
				  String stmntId = rs.getString(4);
				  java.sql.Timestamp timeStamp = rs.getTimestamp(5);
				  String remarks = rs.getString(6);
				  String operation = rs.getString(7);
				  String options = rs.getString(8);
				  String object_node = rs.getString(9);
				  String object_owner = rs.getString(10);
				  String object_name = rs.getString(11);
				  String object_instance = rs.getString(12);
				  String object_type = rs.getString(13);
				  String optimizer = rs.getString(14);
				  BigDecimal searchColumns = rs.getBigDecimal(15);
				  BigDecimal position = rs.getBigDecimal(16);
				  BigDecimal cost = rs.getBigDecimal(17);
				  BigDecimal cardinality = rs.getBigDecimal(18);
				  BigDecimal bytes = rs.getBigDecimal(19);
				  String other_tag = rs.getString(20);
				  String distribution = rs.getString(21);

				  ExplainPlanModel.ExplainRow parent = null;

				  if (level == 1) {
					 parent = (ExplainPlanModel.ExplainRow) model.getRoot();
				  }
				  else if (previousLevel == level) {
					 parent = ((ExplainPlanModel.ExplainRow)lastRow.getParent().getParent()).findChild(parent_id.
						  intValue());
				  }
				  else if (level > previousLevel) {
					 parent = ((ExplainPlanModel.ExplainRow)lastRow.getParent()).findChild(parent_id.intValue());
				  }
				  else if (level < previousLevel) {
					 parent = (ExplainPlanModel.ExplainRow)lastRow.getParent();
					 for (int i=previousLevel-level;i>=0;i--) {
						parent = (ExplainPlanModel.ExplainRow)parent.getParent();
					 }
					 parent = parent.findChild(parent_id.intValue());
				  }

				  if (parent == null)
					 throw new RuntimeException("parent is null. Coding error");

				  ExplainPlanModel.ExplainRow row = new ExplainPlanModel.ExplainRow(
						parent,
						id.intValue(),
						stmntId,
						timeStamp,
						remarks,
						operation,
						options,
						object_node,
						object_owner,
						object_name,
						object_instance,
						object_type,
						optimizer,
						searchColumns,
						position,
						cost,
						cardinality,
						bytes,
						other_tag,
						distribution);
				  parent.addChild(row);
				  lastRow = row;
				  previousLevel = level;
				}

				//Unfortunately we need to remove the exising tree table component and create a
				//new one due to limitations with replacing models in the existing
				//sun implementation. Why on earth they couldnt formalise the tree
				//table example on JFC (which we use) a bit more is anyones guess.
				this.removeAll();
				add(new javax.swing.JScrollPane(createTreeTable(model)), BorderLayout.CENTER);
			 }

		  }
		  catch (SQLException ex) {
			 getSession().showErrorMessage(ex);
             s_log.error(ex);
		  }
		  finally {
			 try {
				if (explainPlan != null)
				  explainPlan.close();
			 }
			 catch (SQLException ex) {}
		  }
		} catch (SQLException ex) {
		  getSession().showErrorMessage(ex);
		} finally {
		  try {
			 if (deletePlan != null)
				deletePlan.close();
		  }
		  catch (SQLException ex) {}
		}
	 }
	 else {
		_session.showErrorMessage(
			// i18n[oracle.noSql=No SQL selected for execution.]
			s_stringMgr.getString("oracle.noSql"));
	 }
  }

  public String getPlanTableName() {
      return _planTableName;
  }

  /**
	* Sesssion is ending.
	* Remove all listeners that this component has setup. Close all
	* torn off result tab windows.
	*/
  void sessionClosing() {
  }

  private void createGUI() {
	 setLayout(new BorderLayout());
	 add(new javax.swing.JScrollPane(createTreeTable( new ExplainPlanModel(null))), BorderLayout.CENTER);
  }

  /** Called when this executer is activated*/
  void createPlanTable() {
      if (!checkedPlanTable) {
          // only check once per session
          checkedPlanTable = true;
          
          if (!userPlanTableExists()) {
              boolean planTableAvailable = true;
              
              //if doesnt exist prompt to create it.
              
              //i18n[explainplanexecuter.createPlanTableMsg=The Oracle Plan 
              //Table '{0}' doesnt exist in the current schema. Do you want to 
              //create it?]
              String msg = 
                  s_stringMgr.getString("explainplanexecuter.createPlanTableMsg",
                                        getPlanTableName());
              
              //i18n[explainplanexecuter.createPlanTableTitle=Create Plan Table]
              String title = 
                  s_stringMgr.getString("explainplanexecuter.createPlanTableTitle");
              
              int result = 
                  JOptionPane.showConfirmDialog(this,
                                                msg, 
                                                title,
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE);
              if (result == JOptionPane.YES_OPTION) {
                  planTableAvailable = createLocalPlanTable();
              } else {
                  planTableAvailable = getAlternatePlanTable(getPlanTableName());
              }
              if (!planTableAvailable) {
                  // Tell the user that they won't be able to show the explain plan
                  
                  JFrame f = _session.getApplication().getMainFrame();                  
                  //i18n[explainplanexecuter.planTableUnavailable=Explain plans 
                  //will be unavailable for this session]
                  msg = 
                      s_stringMgr.getString(
                              "explainplanexecuter.planTableUnavailable");
                  
                  //i18n[explainplanexecuter.planTableUnavailableTitle=
                  //PLAN_TABLE not found]
                  title = 
                      s_stringMgr.getString(
                              "explainplanexecuter.planTableUnavailableTitle");
                      
                  JOptionPane.showMessageDialog(f, 
                                                msg, 
                                                "title", 
                                                JOptionPane.INFORMATION_MESSAGE);
                  
              }
          } 
      }
  }

  private boolean createLocalPlanTable() {
      boolean result = true;
      String createPlanTableSQL = 
          getCreatePlanTableSQL(getPlanTableName()); 
      Statement stmt = null;
      try {
          ISession session = getSession();
          ISQLConnection con = session.getSQLConnection();
          stmt = con.createStatement();
          stmt.execute(createPlanTableSQL);
          SchemaInfo schemaInfo = session.getSchemaInfo();
          schemaInfo.refershCacheForSimpleTableName("PLAN_TABLE");
      } catch (SQLException ex) {
          result = false;
          getSession().showErrorMessage(ex);
          s_log.error(ex);
      } finally {
          if (stmt != null) try {stmt.close();} catch (SQLException e) {}
      }      
      return result;
  }
  
  /**
   * Look for a PLAN_TABLE in another schema that we may be able to access 
   * using the following SQL:
   * 
   *  "SELECT OWNER, TABLE_NAME " +
      "from ALL_TABLES WHERE UPPER(TABLE_NAME) = UPPER(?)";
   * 
   */
  private boolean getAlternatePlanTable(String planTableName) {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      ArrayList<String> planTableList = new ArrayList<String>();
      try {
          ISQLConnection con = _session.getSQLConnection();
          pstmt = con.prepareStatement(ALL_PLAN_TABLE_SQL);
          pstmt.setString(1, planTableName);
          rs = pstmt.executeQuery();
          while (rs.next()) {
              String owner = rs.getString(1);
              String tableName = rs.getString(2);
              StringBuffer tmp = new StringBuffer();
              tmp.append(owner);
              tmp.append(".");
              tmp.append(tableName);
              planTableList.add(tmp.toString());
          }
      } catch (SQLException e) {
          getSession().showErrorMessage(e);
          s_log.error(e);          
      } finally {
          if (rs != null) try {rs.close();} catch (SQLException e) {}
          if (pstmt != null) try {pstmt.close();} catch (SQLException e) {}
      }
      if (planTableList.size() == 0) {
          s_log.info("No PLAN_TABLE table found in view ALL_TABLES");
          return false;
      }
      String[] planTables = 
          planTableList.toArray(new String[planTableList.size()]);
      
      JFrame f = _session.getApplication().getMainFrame();
      
      //i18n[explainplanexecuter.choosePlanTableMsg=Choose a PLAN_TABLE to 
      //store the result in]
      String message = 
          s_stringMgr.getString("explainplanexecuter.choosePlanTableMsg");
      
      //i18n[explainplanexecuter.choosePlanTableTitle=Available PLAN_TABLEs]
      String chooserTitle = 
          s_stringMgr.getString("explainplanexecuter.choosePlanTableMsg");
          
       
      String option = 
          (String)JOptionPane.showInputDialog(f,
                                              message,
                                              chooserTitle,
                                              JOptionPane.INFORMATION_MESSAGE, 
                                              null, 
                                              planTables, 
                                              planTables[0]);
      if (option != null) {
          _planTableName = option;
      } else {
          return false;
      }      
      return true;
  }
  
  /**
   * Determines if the user has a PLAN_TABLE in their schema using SQL:
   * 
   * "SELECT 1 from USER_TABLES WHERE UPPER(TABLE_NAME) = UPPER(?)";
   * 
   * @return true if PLAN_TABLE exists; false otherwise.
   */
  private boolean userPlanTableExists() {
      boolean result = false;
      PreparedStatement stmt = null;
      ResultSet rs = null;
      try {
          ISQLConnection con = getSession().getSQLConnection();
          stmt = con.prepareStatement(USER_PLAN_TABLE_SQL);
          stmt.setString(1, getPlanTableName());
          rs = stmt.executeQuery();
          if (rs.next()) {
              result = true;
          }
      } catch (SQLException e) {
          getSession().showErrorMessage(e);
          s_log.error(e);
      } finally {
          if (rs != null) try { rs.close(); } catch (SQLException e) {}
          if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
      }
      return result;
  }
  
  private String getCreatePlanTableSQL(String tableName) {
      StringBuffer result = new StringBuffer("CREATE TABLE ");
      result.append(tableName);
      result.append(" (");
      result.append("STATEMENT_ID                    VARCHAR2(30),");
      result.append("TIMESTAMP                       DATE,");
      result.append("REMARKS                         VARCHAR2(80),");
      result.append("OPERATION                       VARCHAR2(30),");
      result.append("OPTIONS                         VARCHAR2(30),");
      result.append("OBJECT_NODE                     VARCHAR2(128),");
      result.append("OBJECT_OWNER                    VARCHAR2(30),");
      result.append("OBJECT_NAME                     VARCHAR2(30),");
      result.append("OBJECT_INSTANCE                 NUMBER(38),");
      result.append("OBJECT_TYPE                     VARCHAR2(30),");
      result.append("OPTIMIZER                       VARCHAR2(255),");
      result.append("SEARCH_COLUMNS                  NUMBER,");
      result.append("ID                              NUMBER(38),");
      result.append("PARENT_ID                       NUMBER(38),");
      result.append("POSITION                        NUMBER(38),");
      result.append("COST                            NUMBER(38),");
      result.append("CARDINALITY                     NUMBER(38),");
      result.append("BYTES                           NUMBER(38),");
      result.append("OTHER_TAG                       VARCHAR2(255),");
      result.append("PARTITION_START                 VARCHAR2(255),");
      result.append("PARTITION_STOP                  VARCHAR2(255),");
      result.append("PARTITION_ID                    NUMBER(38),");
      result.append("OTHER                           LONG,");
      result.append("DISTRIBUTION                    VARCHAR2(30)");
      result.append(")");
      return result.toString();
  }
  
  private class MySqlExecuterTabListener implements ISQLResultExecuterTabListener {
	 public void executerTabAdded(SQLResultExecuterTabEvent evt) {}

	 public void executerTabRemoved(SQLResultExecuterTabEvent evt) {}

	 public void executerTabActivated(SQLResultExecuterTabEvent evt) {
		if (evt.getExecuter() == ExplainPlanExecuter.this) {
		  createPlanTable();
		}
	 }
  }

  public static class ExplainPlanModel extends AbstractTreeTableModel {
		// Names of the columns.
		private final String[]  cNames = {
                                        //i18n[explainplanexecuter.enumeration=#]
                                        s_stringMgr.getString("explainplanexecuter.enumeration"),
                                        //i18n[explainplanexecuter.operation=Operation]
                                        s_stringMgr.getString("explainplanexecuter.operation"),
                                        //i18n[explainplanexecuter.options=Options]
                                        s_stringMgr.getString("explainplanexecuter.options"),
                                        //i18n[explainplanexecuter.objectName=Object Name]
                                        s_stringMgr.getString("explainplanexecuter.objectName"),
                                        //i18n[explainplanexecuter.mode=Mode]
                                        s_stringMgr.getString("explainplanexecuter.mode"),
                                        //i18n[explainplanexecuter.cost=Cost]
                                        s_stringMgr.getString("explainplanexecuter.cost"),
                                        //i18n[explainplanexecuter.bytes=Bytes]
                                        s_stringMgr.getString("explainplanexecuter.bytes"),
                                        //i18n[explainplanexecuter.cardinality=Cardinality]
                                        s_stringMgr.getString("explainplanexecuter.cardinality"),
														};

		// Types of the columns.
		private final Class[]  cTypes = { TreeTableModel.class,
														 String.class,
														 String.class,
														 String.class,
														 String.class,
														 String.class,
														 String.class,
														 String.class
													  };
		public static class ExplainRow implements TreeNode {
		  private ExplainRow parent;
		  private List<ExplainRow> children;
		  private int id;
		  private String idObj;
		  private String stmntId;
		  private java.sql.Timestamp timeStamp;
		  private String remarks;
		  private String operation;
		  private String options;
		  private String object_node;
		  private String object_owner;
		  private String object_name;
		  private String object_instance;
		  private String object_type;
		  private String optimizer;
		  private BigDecimal searchColumns;
		  private BigDecimal position;
		  private BigDecimal cost;
		  private BigDecimal cardinality;
		  private BigDecimal bytes;
		  private String other_tag;
		  private String distribution;

		  public ExplainRow(ExplainRow parent,
								  int id,
								  String stmntId,
								  java.sql.Timestamp timeStamp,
								  String remarks,
								  String operation,
								  String options,
								  String object_node,
								  String object_owner,
								  String object_name,
								  String object_instance,
								  String object_type,
								  String optimizer,
								  BigDecimal searchColumns,
								  BigDecimal position,
								  BigDecimal cost,
								  BigDecimal cardinality,
								  BigDecimal bytes,
								  String other_tag,
								  String distribution) {
			 this.parent = parent;
			 this.id = id;
			 if (id == -1)
				this.idObj = "";
			 else this.idObj = Integer.toString(id);
			 this.stmntId = stmntId;
			 this.timeStamp = timeStamp;
			 this.remarks = remarks;
			 this.operation = operation;
			 this.options = options;
			 this.object_node = object_node;
			 this.object_owner = object_owner;
			 this.object_name = object_name;
			 this.object_instance = object_instance;
			 this.object_type = object_type;
			 this.optimizer = optimizer;
			 this.searchColumns = searchColumns;
			 this.position = position;
			 this.cost = cost;
			 this.cardinality = cardinality;
			 this.bytes = bytes;
			 this.other_tag = other_tag;
			 this.distribution = distribution;
		  }

		  public int getID() {
			 return id;
		  }

		  //TreeNode Interface
		  public TreeNode getParent() {
			 return parent;
		  }

		  public Enumeration<ExplainRow> children() {
			 if (children == null) {
				children = new ArrayList<ExplainRow>();
             }
			 return Collections.enumeration(children);
		  }

		  public boolean getAllowsChildren() {
			 return true;
		  }

		  public int getIndex(TreeNode node) {
			 if (children == null)
				return -1;
			 return children.indexOf(node);
		  }

		  public void addChild(ExplainRow row) {
			 if (children == null) {
				children = new ArrayList<ExplainRow>();
             }
			 children.add(row);
		  }

		  public boolean isLeaf() {
			 return  ((children == null)||(children.size() == 0));
		  }

		  public Object getValueAt(int column) {
			 switch (column) {
				case 0: return this.idObj;
				case 1: return this.operation;
				case 2: return this.options;
				case 3: return this.object_name;
				case 4: return this.optimizer;
				case 5: return this.cost;
				case 6: return this.bytes;
				case 7: return this.cardinality;
				default: return null;
			 }
		  }

		  public int getChildCount() {
			 return (children==null) ? 0 : children.size();
		  }

		  public TreeNode getChildAt(int child) {
			 return children.get(child);
		  }

		  public ExplainRow findChild(int id) {
			 for (int i=getChildCount()-1;i>=0;i--) {
				ExplainRow child = (ExplainRow)getChildAt(i);
				if (child.getID() == id) {
				  return child;
				}
			 }
			 return null;
		  }

		  public String toString() {
			 return idObj;
		  }
		}

		public ExplainPlanModel(ExplainRow root) {
		  super(root);
		}

		//
		// The TreeModel interface
		//

		/**
		 * Returns the number of children of <code>node</code>.
		 */
		public int getChildCount(Object node) {
		  ExplainRow er = (ExplainRow)node;
		  return er.getChildCount();
		}

		/**
		 * Returns the child of <code>node</code> at index <code>i</code>.
		 */
		public Object getChild(Object node, int i) {
		  ExplainRow er = (ExplainRow)node;
		  return er.getChildAt(i);
		}

		/**
		 * Returns true if the passed in object represents a leaf, false
		 * otherwise.
		 */
		public boolean isLeaf(Object node) {
			 return ((ExplainRow)node).isLeaf();
		}

		//
		//  The TreeTableNode interface.
		//

		/**
		 * Returns the number of columns.
		 */
		public int getColumnCount() {
			 return cNames.length;
		}

		/**
		 * Returns the name for a particular column.
		 */
		public String getColumnName(int column) {
			 return cNames[column];
		}

		/**
		 * Returns the class for the particular column.
		 */
		public Class getColumnClass(int column) {
			 return cTypes[column];
		}

		/**
		 * Returns the value of the particular column.
		 */
		public Object getValueAt(Object node, int column) {
			 ExplainRow     fn = (ExplainRow)node;
			 return fn.getValueAt(column);
		}
  }

  private class PlanTreeCellRenderer extends DefaultTreeCellRenderer {

      private static final long serialVersionUID = 6829431667964347305L;

      public Component getTreeCellRendererComponent(JTree tree,
                                                    Object value,
                                                    boolean selected,
                                                    boolean expanded,
                                                    boolean leaf,
                                                    int row,
                                                    boolean hasFocus) 
      {
          super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
          this.setIcon(null);
          return this;
      }

  }


  public static void main(String[] args) {
	 javax.swing.JFrame frame = new javax.swing.JFrame("Tree Table test");
	 ExplainPlanModel.ExplainRow root = new ExplainPlanModel.ExplainRow(null, -1, "root", null, null, "JMH Root", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	 ExplainPlanModel.ExplainRow child = new ExplainPlanModel.ExplainRow(root, 0, "child", null, null, "Child 0", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	 ExplainPlanModel.ExplainRow child2 = new ExplainPlanModel.ExplainRow(root, 1, "child 2", null, null, "Child 1", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	 ExplainPlanModel.ExplainRow child3 = new ExplainPlanModel.ExplainRow(child2, 2, "child 3", null, null, "Child 2", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	 ExplainPlanModel.ExplainRow child4 = new ExplainPlanModel.ExplainRow(child3, 4, "child 4", null, null, "Child 4", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	 ExplainPlanModel.ExplainRow child5 = new ExplainPlanModel.ExplainRow(child2, 5, "child 5", null, null, "Child 5", null, null, null, null, null, null, null, null, null, null, null, null, null, null);

	 root.addChild(child);
	 root.addChild(child2);
	 child2.addChild(child3);
	 child2.addChild(child5);
	 child3.addChild(child4);
	 TreeTableModel model = new ExplainPlanModel(root);
	 JTreeTable treeTable = new JTreeTable(model);
	 int rowCount = treeTable.getTree().getRowCount();
	 for (int i=0;i<rowCount;i++) {
		treeTable.getTree().expandRow(i);
	 }
	 frame.getContentPane().add(new javax.swing.JScrollPane(treeTable));
	 frame.setSize(640, 480);
	 frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
	 frame.setVisible(true);

  }

  /**
   * @see net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter#getSelectedResultTab()
   */
  public IResultTab getSelectedResultTab() {
      throw new UnsupportedOperationException("ExplainPlanExecuter has no ResultTabs");
  }


  

}
