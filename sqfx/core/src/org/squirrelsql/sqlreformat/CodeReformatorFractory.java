package org.squirrelsql.sqlreformat;

import org.squirrelsql.AppState;
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.SQLUtil;
import org.squirrelsql.settings.SQLFormatSettings;

public class CodeReformatorFractory
{
   public static CodeReformator createCodeReformator(SQLFormatSettings sqlFormatSettings)
   {
      CodeReformatorConfig codeReformatorConfig = new CodeReformatorConfig(AppState.get().getSettingsManager().getSettings().getStatementSeparator(), new CommentSpec[]{new CommentSpec(SQLUtil.LINE_COMMENT_BEGIN, "\n")}, sqlFormatSettings);
      return new CodeReformator(codeReformatorConfig);
   }

   public static CodeReformator createCodeReformator()
   {
      return createCodeReformator(Dao.loadSQLFormatSeetings());
   }
}
