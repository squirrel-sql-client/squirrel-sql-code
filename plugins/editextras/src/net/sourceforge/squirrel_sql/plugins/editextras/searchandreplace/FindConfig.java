package net.sourceforge.squirrel_sql.plugins.editextras.searchandreplace;

public class FindConfig
{
	String toSearch;
	boolean wholeWord;
	boolean matchCase;

	FindConfig(String toSearch, boolean wholeWord, boolean matchCase)
	{
		this.toSearch = toSearch;
		this.wholeWord = wholeWord;
		this.matchCase = matchCase;
	}

	public boolean equals(Object obj)
	{
		if( null == obj || !(obj instanceof FindConfig) )
		{
			return false;
		}

		FindConfig other = (FindConfig) obj;

		return toSearch.equals(other.toSearch) && wholeWord == other.wholeWord && matchCase == other.matchCase;
	}
}
