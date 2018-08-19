package net.sourceforge.squirrel_sql.client.gui.db;

import javax.swing.tree.TreePath;

public class AliasTreePasteState
{
   private TreePath[] _pathsToPaste;
   private AliasTreePasteMode _pasteMode;

   public TreePath[] getPathsToPaste()
   {
      return _pathsToPaste;
   }

   public void setPathsToPaste(TreePath[] pathsToPaste)
   {
      _pathsToPaste = pathsToPaste;
   }

   public AliasTreePasteMode getPasteMode()
   {
      return _pasteMode;
   }

   public void setPasteMode(AliasTreePasteMode pasteMode)
   {
      _pasteMode = pasteMode;
   }
}
