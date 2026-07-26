package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import org.apache.commons.lang3.StringUtils;

public class ParenthesedSelectInfoCreator
{

   public static List<ParenthesedSelectInfo> createParenthesedSelectInfosForSingleStatement(ISession session,
                                                                                            StatementBounds statementBounds,
                                                                                            ParsingResult parsingResult,
                                                                                            ArrayList<ErrorInfo> errorInfosBuffer,
                                                                                            ParseTerminateRequestCheck parseTerminateRequestCheck)
   {
      List<ParenthesedSelectInfo> ret = new ArrayList<>();
      for( ParenthesedSelect parenthesedSelect : parsingResult.getParenthesedSelects() )
      {
         String parenthesedSelectsAliasName = null;
         if( null != parenthesedSelect.getAlias() && StringUtils.isNotBlank(parenthesedSelect.getAlias().getName()) )
         {
            parenthesedSelectsAliasName = parenthesedSelect.getAlias().getName();
         }

         parseTerminateRequestCheck.check();
         List<TableColumnInfo> parenthesedSelectColumns = createParenthesedSelectColumns(parenthesedSelect, session);

         ret.add(new ParenthesedSelectInfo(statementBounds, errorInfosBuffer, parenthesedSelectsAliasName, parenthesedSelectColumns));

         parseTerminateRequestCheck.check();
      }

      if(parsingResult.getParenthesedSelects().isEmpty() && false == parsingResult.getParseErrors().isEmpty())
      {
         ret = HeuristicParenthesedSelectsParser.parse(statementBounds, session, errorInfosBuffer);
      }

      return ret;
   }

   private static List<TableColumnInfo> createParenthesedSelectColumns(ParenthesedSelect parenthesedSelect, ISession session)
   {
      List<SelectItem<?>> selectItems = parenthesedSelect.getSelect().getPlainSelect().getSelectItems();

      List<TableColumnInfo> columns = new ArrayList<>();
      for( int i = 0; i < selectItems.size(); i++ )
      {
         TableColumnInfo col = null;

         SelectItem<?> selectItem = selectItems.get(i);
         if( null != selectItem.getAlias() && StringUtils.isNotBlank(selectItem.getAlias().getName()) )
         {
            col = ParserUtil.createSubSelectTableColumnInfoFromName(session, selectItem.getAlias().getName(), i);
         }
         else if( selectItem.getExpression() instanceof Column pc && StringUtils.isNotBlank(pc.getColumnName()) )
         {
            col = ParserUtil.createSubSelectTableColumnInfoFromName(session, pc, i);
         }

         if( null != col )
         {
            columns.add(col);
         }
      }

      return columns;
   }

}
