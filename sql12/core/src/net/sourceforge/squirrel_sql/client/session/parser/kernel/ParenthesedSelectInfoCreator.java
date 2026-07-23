package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.sql.Types;
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
         ret = HeuristicParenthesedSelectsParser.parse(statementBounds, session.getSchemaInfo());
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

            col = new TableColumnInfo(null, null, null, selectItem.getAlias().getName(), Types.OTHER, "OTHER", 10, 0, 0, 1, null,
                                      null, 0, i, null, null, session.getMetaData());
         }
         else if( selectItem.getExpression() instanceof Column pc && StringUtils.isNotBlank(pc.getColumnName()) )
         {
            String catalogName = null != pc.getTable() ? pc.getTable().getCatalogName() : null;
            String schemaName = null != pc.getTable() ? pc.getTable().getSchemaName() : null;
            String tableName = null != pc.getTable() ? pc.getTable().getName() : null;
            col = new TableColumnInfo(catalogName, schemaName, tableName, pc.getColumnName(), Types.OTHER, "OTHER", 10, 0, 0, 1, null,
                                      null, 0, i, null, null, session.getMetaData());
         }

         if( null != col )
         {
            columns.add(col);
         }
      }

      return columns;
   }

}
