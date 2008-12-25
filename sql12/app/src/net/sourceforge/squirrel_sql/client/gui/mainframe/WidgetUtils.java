package net.sourceforge.squirrel_sql.client.gui.mainframe;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.util.List;
import java.util.ArrayList;

public class WidgetUtils extends GUIUtils
{
   /**
	 * Return an array containing all <TT>JInternalFrame</TT> objects
    * that were passed in <TT>frames</TT> that are tool windows.
    *
    * @param	frames	<TT>JInternalFrame</TT> objects to be checked.
    */
   public static IWidget[] getOpenToolWindows(IWidget[] frames)
   {
      if (frames == null)
      {
         throw new IllegalArgumentException("null JInternalFrame[] passed");
      }
      List<IWidget> framesList = new ArrayList<IWidget>();
      for (int i = 0; i < frames.length; ++i)
      {
         IWidget fr = frames[i];
         if (fr.isToolWindow() && !fr.isClosed())
         {
            framesList.add(frames[i]);
         }
      }
      return framesList.toArray(new IWidget[framesList.size()]);
   }

   /**
	 * Return an array containing all <TT>JInternalFrame</TT> objects
    * that were passed in <TT>frames</TT> that are <EM>not</EM> tool windows.
    *
    * @param	frames	<TT>JInternalFrame</TT> objects to be checked.
    */
   public static IWidget[] getOpenNonToolWindows(IWidget[] frames)
   {
      if (frames == null)
      {
         throw new IllegalArgumentException("null JInternalFrame[] passed");
      }
      List<IWidget> framesList = new ArrayList<IWidget>();
      for (int i = 0; i < frames.length; ++i)
      {
         if (!frames[i].isToolWindow() && !frames[i].isClosed())
         {
            framesList.add(frames[i]);
         }
      }
      return framesList.toArray(new IWidget[framesList.size()]);
   }

   /**
	 * Return an array containing all <TT>JInternalFrame</TT> objects
    * that were passed in <TT>frames</TT> that are <EM>not</EM> tool windows.
    * and are not minimized.
    *
    * @param	frames	<TT>JInternalFrame</TT> objects to be checked.
    */
   public static IWidget[] getNonMinimizedNonToolWindows(IWidget[] frames)
   {
      if (frames == null)
      {
         throw new IllegalArgumentException("null JInternalFrame[] passed");
      }
      List<IWidget> framesList = new ArrayList<IWidget>();
      for (int i = 0; i < frames.length; ++i)
      {
         IWidget fr = frames[i];
         if (!fr.isToolWindow() && !fr.isClosed() && !fr.isIcon())
         {
            framesList.add(frames[i]);
         }
      }
      return framesList.toArray(new IWidget[framesList.size()]);
   }
}
