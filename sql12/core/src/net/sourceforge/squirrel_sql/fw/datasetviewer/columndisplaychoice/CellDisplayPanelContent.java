package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import javax.swing.JComponent;

/**
 * Marker interface for JComponents that can be contained in {@link CellDisplayPanel}
 */
public interface CellDisplayPanelContent<T extends JComponent>
{
   default T castToComponent()
   {
      return (T) this;
   }

   /**
    * Memory leak: Allows closed Sessions to be garbage collected after a CellDataDialog was opened.
    */
   void cleanUp();
}
