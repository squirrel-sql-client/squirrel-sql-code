package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
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

         if(parenthesedSelect.getSelect() instanceof PlainSelect)
         {
            List<TableColumnInfo> parenthesedSelectColumns = createParenthesedSelectColumns(parenthesedSelect.getPlainSelect(), session);
            ret.add(new ParenthesedSelectInfo(statementBounds, errorInfosBuffer, parenthesedSelectsAliasName, parenthesedSelectColumns));
         }
         else if(parenthesedSelect.getSelect() instanceof SetOperationList setOperationList) // UNION
         {
            for( Select select : setOperationList.getSelects() )
            {
               if(select instanceof PlainSelect plainSelect)
               {
                  List<TableColumnInfo> parenthesedSelectColumns = createParenthesedSelectColumns(plainSelect, session);
                  ret.add(new ParenthesedSelectInfo(statementBounds, errorInfosBuffer, parenthesedSelectsAliasName, parenthesedSelectColumns));

                  // First only because we only need the columns of one of the unions
                  break;
               }
            }
         }


         parseTerminateRequestCheck.check();
      }

      if(parsingResult.getParenthesedSelects().isEmpty() && false == parsingResult.getParseErrors().isEmpty())
      {
         ret = HeuristicParenthesedSelectsParser.parse(statementBounds, session, errorInfosBuffer);
      }

      return ret;
   }

   private static List<TableColumnInfo> createParenthesedSelectColumns(PlainSelect plainSelect, ISession session)
   {
      List<SelectItem<?>> selectItems = plainSelect.getSelectItems();

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
