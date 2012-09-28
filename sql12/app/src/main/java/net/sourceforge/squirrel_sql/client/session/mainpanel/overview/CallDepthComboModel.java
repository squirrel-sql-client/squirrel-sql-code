package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.ScaleFactory;

import java.util.prefs.Preferences;

public class CallDepthComboModel
{
   public static final String PREF_KEY_OVERVIEW_BAR_CHART_CALL_DEPTH = "Squirrel.overview.bar.calldepth";


   private int _callDepth;

   public CallDepthComboModel(int callDepth)
   {
      _callDepth = callDepth;
   }

   public static CallDepthComboModel[] createModels()
   {
      return new CallDepthComboModel[]
      {
         new CallDepthComboModel(ScaleFactory.DEFAULT_CALL_DEPTH),
         new CallDepthComboModel(ScaleFactory.DEFAULT_CALL_DEPTH + 1),
         new CallDepthComboModel(ScaleFactory.DEFAULT_CALL_DEPTH + 2),
         new CallDepthComboModel(ScaleFactory.DEFAULT_CALL_DEPTH + 3),
         new CallDepthComboModel(ScaleFactory.DEFAULT_CALL_DEPTH + 4),
         new CallDepthComboModel(ScaleFactory.DEFAULT_CALL_DEPTH + 5),
         new CallDepthComboModel(ScaleFactory.DEFAULT_CALL_DEPTH + 6),
      };
   }

   @Override
   public String toString()
   {
      return "" + (int)(Math.pow(2, _callDepth-1) + 0.5d);
   }

   public static CallDepthComboModel getDefaultSelected()
   {
      int callDepth = Preferences.userRoot().getInt(PREF_KEY_OVERVIEW_BAR_CHART_CALL_DEPTH, ScaleFactory.DEFAULT_CALL_DEPTH);

      for (CallDepthComboModel callDepthComboModel : createModels())
      {
         if(callDepthComboModel._callDepth == callDepth)
         {
            return callDepthComboModel;
         }
      }

      return createModels()[0];

   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CallDepthComboModel that = (CallDepthComboModel) o;

      if (_callDepth != that._callDepth) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return _callDepth;
   }

   public static void saveSelection(CallDepthComboModel selectedItem)
   {
      Preferences.userRoot().putInt(PREF_KEY_OVERVIEW_BAR_CHART_CALL_DEPTH, selectedItem._callDepth);
   }

   public int getCallDepth()
   {
      return _callDepth;
   }
}
