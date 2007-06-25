package net.sourceforge.squirrel_sql.plugins.hibernate;

import javax.swing.*;
import java.util.ArrayList;

public interface IHQLTabController
{

   void addToToolbar(AbstractAction action);

   void displaySqls(ArrayList<String> sqls);
}
