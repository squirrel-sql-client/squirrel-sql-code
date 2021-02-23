package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import java.util.ArrayList;

@FunctionalInterface
public interface DislplayResultsCallback
{
   void displayResult(ArrayList<FindColumnsResultBean> searchResults, int numberOfTablesDone, int totalNumberOfTables);
}
