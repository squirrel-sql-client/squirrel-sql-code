package net.sourceforge.squirrel_sql.fw.gui.texteditdlg;

import java.util.List;

public interface PreviousTextsProvider
{
   String getInitialEditorContent();

   List<String> getPreviousTexts();

   void setLastEditorContent(String text);

   void save();
}
