package net.sourceforge.squirrel_sql.plugins.editextras.searchandreplace;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

public class SearchAndReplaceKernel
{
	private ISQLPanelAPI _textArea;
	private int _nextFindStart = 0;
	private FindConfig _lastFindConfig;

	public SearchAndReplaceKernel(ISQLPanelAPI textArea)
	{
		_textArea = textArea;
	}

	void reset()
	{
		_nextFindStart = 0;
	}

	boolean performFind(FindConfig findConfig)
	{
		_lastFindConfig = findConfig;

		_textArea.setSQLScriptSelectionStart(0);
		_textArea.setSQLScriptSelectionEnd(0);
		String script = _textArea.getEntireSQLScript();

		Pattern pat = Pattern.compile(getRegExp());
      Matcher mat = pat.matcher(script);
		if(mat.find(_nextFindStart))
		{
			int selStart = mat.start();
			if(Character.isWhitespace(script.charAt(selStart)))
			{
				++selStart;
			}
			_textArea.setSQLScriptSelectionStart(selStart);
			_textArea.setSQLScriptSelectionEnd(selStart + findConfig.toSearch.length());
			_nextFindStart = mat.start() + 1;
			return true;
		}
		else
		{
			_nextFindStart = 0;
			return false;
		}

	}

	private String getRegExp()
	{
		StringBuffer ret = new StringBuffer();

		if(_lastFindConfig.wholeWord)
		{
			ret.append("(\\s|\\A)");
		}

		for (int i = 0; i < _lastFindConfig.toSearch.length(); i++)
		{
			if('.' == _lastFindConfig.toSearch.charAt(i))
			{
				ret.append("(\\.)");
			}
			else if('^' == _lastFindConfig.toSearch.charAt(i))
			{
				ret.append("(\\^)");
			}
         else if('?' == _lastFindConfig.toSearch.charAt(i))
         {
            ret.append("(\\?)");
         }
			else if('$' == _lastFindConfig.toSearch.charAt(i))
			{
				ret.append("(\\$)");
			}
//			else if('\\' == _lastFindConfig.toSearch.charAt(i) && i+1 < _lastFindConfig.toSearch.length() && 'n' == _lastFindConfig.toSearch.charAt(i+1))
//			{
//				ret.append("(\\n)");
//				++i;
//			}
			else if('(' == _lastFindConfig.toSearch.charAt(i))
			{
				ret.append("(\\()");
			}
         else if('*' == _lastFindConfig.toSearch.charAt(i))
         {
            ret.append("(\\*)");
         }
			else if(')' == _lastFindConfig.toSearch.charAt(i))
			{
				ret.append("(\\))");
			}
			else if('\\' == _lastFindConfig.toSearch.charAt(i))
			{
				ret.append("(\\\\)");
			}
			else if(!_lastFindConfig.matchCase)
			{
				char lc = Character.toLowerCase(_lastFindConfig.toSearch.charAt(i));
				char uc = Character.toUpperCase(_lastFindConfig.toSearch.charAt(i));
				ret.append('(').append(lc).append('|').append(uc).append(')');
			}
			else
			{
				ret.append(_lastFindConfig.toSearch.charAt(i));
			}
		}

		if(_lastFindConfig.wholeWord)
		{
			ret.append("(\\s|\\z)");
		}


		return ret.toString();
	}

	void repeatLastFind()
	{
	   if(null != _lastFindConfig)
		{
			performFind(_lastFindConfig);
		}
	}

	void beginFromStart()
	{
		_nextFindStart = 0;
	}

	void findSelected()
	{
		beginFromStart();
		String toSearch = _textArea.getSelectedSQLScript();

		if(null == toSearch || "".equals(toSearch))
		{
			return;
		}

		FindConfig fc = new FindConfig(toSearch, false, false);
		performFind(fc);
	}

	String getSelectedText()
	{
		return _textArea.getSelectedSQLScript();
	}

	void replaceSelectionBy(String toReplaceBy)
	{
      _nextFindStart = _textArea.getSQLScriptSelectionStart() + toReplaceBy.length();
		_textArea.replaceSelectedSQLScript(toReplaceBy, true);

		if(_nextFindStart >= _textArea.getEntireSQLScript().length())
		{
			_nextFindStart = 0;
		}
	}

	int getNextFindStart()
	{
		return _nextFindStart;
	}

	FindConfig getLastFindConfig()
	{
		return _lastFindConfig;
	}
}
