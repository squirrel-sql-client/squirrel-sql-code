package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public interface ToolsPopupHandler_I18n
{
   StringManager s_stringMgr = StringManagerFactory.getStringManager(ToolsPopupHandler.class);

   String TO_UPPER_CASE = s_stringMgr.getString("SyntaxPlugin.touppercase");
   String TO_LOWER_CASE = s_stringMgr.getString("SyntaxPlugin.tolowercase");
   String FIND = s_stringMgr.getString("SyntaxPlugin.find");
   String FIND_SELECTED = s_stringMgr.getString("SyntaxPlugin.findselected");
   String REPEAT_LAST_FIND = s_stringMgr.getString("SyntaxPlugin.repeatLastFind");
   String MARK_SELECTED = s_stringMgr.getString("SyntaxPlugin.markSelected");
   String REPLACE = s_stringMgr.getString("SyntaxPlugin.replace");
   String UNMARK = s_stringMgr.getString("SyntaxPlugin.unmark");
   String GO_TO_LINE = s_stringMgr.getString("SyntaxPlugin.gotoline");
   String AUTO_CORR = s_stringMgr.getString("SyntaxPlugin.autocorr");
   String DUP_LINE = s_stringMgr.getString("SyntaxPlugin.duplicateline");
   String COMMENT = s_stringMgr.getString("SyntaxPlugin.comment");
   String UNCOMMENT = s_stringMgr.getString("SyntaxPlugin.uncomment");
   String COPY_AS_RTF = s_stringMgr.getString("SyntaxPlugin.copyasrtf");
   ;
}
