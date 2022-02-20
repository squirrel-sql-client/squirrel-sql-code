package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

public class TemporalScriptGenerationCtrl
{
   private final TemporalScriptGenerationPanel _panel;

   public TemporalScriptGenerationCtrl(String escapeFormatExample, String stringFormatExample, TemporalScriptGenerationFormat generationFormat)
   {
      _panel = new TemporalScriptGenerationPanel(escapeFormatExample, stringFormatExample);

      if(generationFormat == TemporalScriptGenerationFormat.STD_JDBC_FORMAT)
      {
         _panel.radScriptGenerationUseStandardEscapeFormat.setSelected(true);
      }
      else
      {
         _panel.radScriptGenerationUseStringFormat.setSelected(true);
      }
   }

   public TemporalScriptGenerationPanel getPanel()
   {
      return _panel;
   }

   public TemporalScriptGenerationFormat getFormat()
   {
      if(_panel.radScriptGenerationUseStandardEscapeFormat.isSelected())
      {
         return TemporalScriptGenerationFormat.STD_JDBC_FORMAT;
      }
      else
      {
         return TemporalScriptGenerationFormat.STRING_FORMAT;
      }
   }

}
