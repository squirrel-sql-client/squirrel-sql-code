package net.sourceforge.squirrel_sql.fw.gui;

@FunctionalInterface
public interface StatusBarHrefListener
{
   void hrefClicked(String linkDescription, Object hrefReferenceObject);
}
