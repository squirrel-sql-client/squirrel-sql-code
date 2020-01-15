package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.matchpatch;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

import java.awt.Color;

public class ChangeRendererStyle
{
   private boolean _deletedBold;
   private boolean _deletedItalic;
   private Color _deletedForgeGround;
   private Color _afterInsertColor;
   private Color _beforeInsertColor;

   public Color getAfterInsertColor()
   {
      return _afterInsertColor;
   }

   public void setAfterInsertColor(Color afterInsertColor)
   {
      _afterInsertColor = afterInsertColor;
   }

   public Color getBeforeInsertColor()
   {
      return _beforeInsertColor;
   }

   public void setBeforeInsertColor(Color beforeInsertColor)
   {
      _beforeInsertColor = beforeInsertColor;
   }

   public boolean isDeletedBold()
   {
      return _deletedBold;
   }

   public void setDeletedBold(boolean deletedBold)
   {
      _deletedBold = deletedBold;
   }

   public boolean isDeletedItalic()
   {
      return _deletedItalic;
   }

   public void setDeletedItalic(boolean deletedItalic)
   {
      _deletedItalic = deletedItalic;
   }

   public Color getDeletedForgeGround()
   {
      return _deletedForgeGround;
   }

   public void setDeletedForgeGround(Color deletedForgeGround)
   {
      _deletedForgeGround = deletedForgeGround;
   }

   public static ChangeRendererStyle createFromPrefs()
   {
      SquirrelPreferences prefs = Main.getApplication().getSquirrelPreferences();

      ChangeRendererStyle ret = new ChangeRendererStyle();
      ret.setDeletedBold(prefs.isDeletedBold());
      ret.setDeletedItalic(prefs.isDeletedItalics());
      ret.setDeletedForgeGround(new Color(prefs.getDeletedForegroundRGB()));
      ret.setAfterInsertColor(new Color(prefs.getInsertEndBackgroundRGB()));
      ret.setBeforeInsertColor(new Color(prefs.getInsertBeginBackgroundRGB()));

      return ret;
   }

}
