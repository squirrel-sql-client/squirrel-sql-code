package net.sourceforge.squirrel_sql.client.session.parser.kernel;

public class SqlLiteralHelper
{
   static boolean isInLiteral(int literalDelimsCount)
   {
      return 1 == literalDelimsCount % 2;
   }
}
