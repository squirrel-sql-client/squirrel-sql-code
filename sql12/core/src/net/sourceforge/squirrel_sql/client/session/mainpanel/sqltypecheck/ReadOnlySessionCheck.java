package net.sourceforge.squirrel_sql.client.session.mainpanel.sqltypecheck;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.EditableSqlCheck;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JLabel;

public class ReadOnlySessionCheck
{
   public static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ReadOnlySessionCheck.class);

   public static boolean checkSqlExecutionAllowed(ISession session, QueryHolder querySql)
   {
      if(false == session.getAlias().isReadOnly())
      {
         return true;
      }

      boolean selectStatement = SQLTypeCheck.isSelectStatement(querySql.getQuery());

      if(false == selectStatement)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("DataChangesAllowedCheck.sql.execution.not.allowed"));
         return false;
      }

      return true;

   }

   public static boolean checkMakeEditableAllowed(ISession session)
   {
      if(session.getAlias().isReadOnly())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("DataChangesAllowedCheck.make.editable.not.allowed"));
         return false;
      }

      return true;
   }

   public static boolean checkDeleteRows(ISession session)
   {
      if(session.getAlias().isReadOnly())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("DataChangesAllowedCheck.delete.all.rows.not.allowed"));
         return false;
      }

      return true;
   }

   public static boolean checkDbCopyPaste(ISession destSession)
   {
      if(destSession.getAlias().isReadOnly())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("DataChangesAllowedCheck.paste.table.not.allowed"));
         return false;
      }

      return true;
   }

   public static boolean checkRefactoringAllowed(ISession session)
   {
      return !session.getAlias().isReadOnly();
   }

   public static void issueRefactoringAllowedMessage()
   {
      Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("DataChangesAllowedCheck.refactoring.not.allowed"));
   }

   /**
    * See also {@link EditableSqlCheck#EditableSqlCheck(SQLExecutionInfo, ISession)}
    */
   public static boolean checkMakeEditableToolbarButton(ISession session)
   {
      if(session.getAlias().isReadOnly())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("DataChangesAllowedCheck.make.editable.toolbar.button.not.allowed"));
         return false;
      }

      return true;
   }

   public static boolean isSessionReadOnly(ISession session)
   {
      return session.getAlias().isReadOnly();
   }

   public static JLabel createReadStatusBarLabel()
   {
      JLabel lblReadOnly = new JLabel(s_stringMgr.getString("ReadOnlySessionCheck.read.only.label"));
      lblReadOnly.setToolTipText(s_stringMgr.getString("ReadOnlySessionCheck.read.only.label.tooltip"));
      return lblReadOnly;
   }
}
