package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.print.Printable;

public interface GraphPrintable extends Printable
{
   void initPrint(double formatWidth, double formatHeight, double formatScale);
}
