package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.FormatXmlBean;

public interface FormatControllerListener
{
   void formatsChanged(FormatXmlBean selectedFormat);
}
