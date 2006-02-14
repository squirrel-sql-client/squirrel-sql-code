package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import org.netbeans.editor.Syntax;
import org.netbeans.editor.ext.ExtKit;

import javax.swing.text.Document;


public class SQLKit extends ExtKit
{
   private SyntaxFactory _syntaxFactory;
//   public static final String duplicateLineAction = "duplicate-line-action";


   public SQLKit(SyntaxFactory syntaxFactory)
   {
      _syntaxFactory = syntaxFactory;
   }

   /**
    * Create new instance of syntax coloring scanner
    *
    * @param doc document to operate on. It can be null in the cases the syntax
    *            creation is not related to the particular document
    */
   public Syntax createSyntax(Document doc)
   {
      return _syntaxFactory.getSyntax(doc);
   }


//   protected Action[] createActions()
//   {
//      Action[] javaActions = new Action[]
//      {
//         new NetbeansDuplicateLineAction(),
//      };
//      return TextAction.augmentList(super.createActions(), javaActions);
//   }

}
