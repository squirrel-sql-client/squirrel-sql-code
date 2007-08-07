package net.sourceforge.squirrel_sql.plugins.i18n;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DevelopersController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DevelopersController.class);


	private DevelopersPanel _panel;
	private IApplication _app;
	private static final String PREF_KEY_SOURCE_DIR = "SquirrelSQL.i18n.sourceDir";
    private static final String PREF_KEY_INCLUDE_TIMESTAMP = "SquirrelSQL.i18n.includeTimestamp";

    private static Preferences prefs = null;

	public DevelopersController(DevelopersPanel pnlDevelopers)
	{
		_panel = pnlDevelopers;

		_panel.btnChooseSourceDir.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onChooseSourceDir();
			}

		});

		_panel.btnAppendI18nInCode.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onAppendI18nInCode();
			}
		});

		prefs = Preferences.userRoot();
		String sourceDir = prefs.get(PREF_KEY_SOURCE_DIR, null);
		_panel.txtSourceDir.setText(sourceDir);
	}


	private void onChooseSourceDir()
	{
        String startDir = System.getProperties().getProperty("user.home");
        if (_panel.txtSourceDir.getText() != null
                && !"".equals(_panel.txtSourceDir.getText()))
        {
            startDir = _panel.txtSourceDir.getText();
        }
		JFileChooser chooser = new JFileChooser(startDir);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(_app.getMainFrame());

		if(null != chooser.getSelectedFile())
		{
			_panel.txtSourceDir.setText(chooser.getSelectedFile().getPath());
		}
	}

	private void onAppendI18nInCode()
	{
		File sourceDir = getSourceDir();

		if(null == sourceDir)
		{
			return;
		}

		appendProps(sourceDir);
		// i18n[i18n.ParsingFinish=Parsing finished]
		_app.getMessageHandler().showMessage(s_stringMgr.getString("i18n.ParsingFinish"));
	}

	private void appendProps(File sourceDir)
	{

		try
		{
			File[] files = sourceDir.listFiles();

			ArrayList<String> newProps = new ArrayList<String>();
			ArrayList<String> replaceProps = new ArrayList<String>();


			File i18nStringFile = new File(sourceDir, "I18NStrings.properties");
			Properties curProps = new Properties();
			FileInputStream fis;

			if(i18nStringFile.exists())
			{
				fis = new FileInputStream(i18nStringFile);
				curProps.load(fis);
				fis.close();
			}

			for (int i = 0; i < files.length; i++)
			{

				if(files[i].isDirectory() && false == "CVS".equals(files[i].getName()))
				{
					appendProps(files[i]);
				}
				else if(files[i].getName().endsWith(".java"))
				{
                    int occurrences = 0;
					StringBuffer code = new StringBuffer();
					fis = new FileInputStream(files[i]);

					int buf = fis.read();
					while(-1 != buf)
					{
						code.append((char)buf);
						buf = fis.read();
					}

					fis.close();


					try
					{
						occurrences = parseProps(code.toString(),curProps, newProps, replaceProps);
                        if (occurrences > 0) {
                            int occurrencesFound = fixSourceFile(files[i].getAbsolutePath());
                            if (occurrences != occurrencesFound) {
                                Object[] params =
                                    new Object[]{
                                        Integer.valueOf(occurrences),
										Integer.valueOf(occurrencesFound),
										files[i].getPath() 
                                    };

							    // i18n[i18n.unequalOccurrences=Found {0} i18n comments but only {1} places
							    // to convert to s_stringMgr.getString() in file {2}]
							    String msg = s_stringMgr.getString("i18n.unequalOccurrences", params);
                                _app.getMessageHandler().showErrorMessage(msg);
                            }
                        }
					}
					catch (Exception e)
					{
						Object[] params = new Object[]{files[i].getPath(), e.toString()};
						_app.getMessageHandler().showErrorMessage(s_stringMgr.getString("i18n.failedToParse", params));
						// i18n[i18n.failedToParse=Failed to parse {0}\n{1}]
						continue;
					}
				}
			}

			if(0 < newProps.size() || 0 <replaceProps.size())
			{
				FileOutputStream fos = new FileOutputStream(i18nStringFile, true);
				PrintWriter ps = new PrintWriter(fos);
                
                String includeTimestamp = prefs.get(PREF_KEY_INCLUDE_TIMESTAMP, "true");
                if (includeTimestamp.equals("true")) {
                    // No i18n, developers should write English props.
                    ps.println("\n#\n#Missing/changed properties generated by I18n Plugin on " + 
                               new java.util.Date() + "\n#");
                }

                Collections.sort(newProps);
				for (int j = 0; j < newProps.size(); j++)
				{
					ps.println(newProps.get(j));
				}

				for (int j = 0; j < replaceProps.size(); j++)
				{
					// No i18n, developers should write English props.
					ps.println();
					ps.println("# A T T E N T I O N: REPLACES SAME KEY ABOVE");
					ps.println(replaceProps.get(j));
				}

				ps.flush();
				fos.flush();
				ps.close();
				fos.close();

				Object[] params = new Object[] {
                        Integer.valueOf(newProps.size()), 
                        Integer.valueOf(replaceProps.size()), 
                        i18nStringFile.getPath()
                };

				_app.getMessageHandler().showMessage(s_stringMgr.getString("i18n.parseSuccess", params));
				// i18n[i18n.parseSuccess=Added {0} new and {1} replaced properties to {2}]
			}

		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

	}

	private int parseProps(String code, 
                           Properties curProps, 
                           ArrayList<String> newProps, 
                           ArrayList<String> replaceProps) 
        throws I18nParseException
	{
        int occurrences = 0;
		code = code.replace('\r', ' ');

		Pattern pat = Pattern.compile("//\\x20*i18n\\[(.*)");
		Matcher m = pat.matcher(code);

		int[] propBounds = new int[]{0,0};

		while(m.find(propBounds[1]))
		{
            occurrences++;
			propBounds[0] = m.start(m.groupCount());
			String prop = getProp(code, propBounds);

			int equalsPos = prop.indexOf('=');
			if(0 > equalsPos)
			{
				throw new I18nParseException("Property " + prop + " has no key.");
			}
			String key = prop.substring(0, equalsPos);
			String val = prop.substring(equalsPos + 1).trim();

			if(curProps.containsKey(key) && false == val.equals(I18nUtils.normalizePropVal((String) curProps.get(key))))
			{
				replaceProps.add(prop);
			}
			else if(false == curProps.containsKey(key))
			{
				boolean found = false;
				for (int i = 0; i < newProps.size(); i++)
				{
					if(newProps.get(i).split("=")[0].startsWith(key.split("=")[0]))
					{
						found = true;
						replaceProps.add(prop);
						break;
					}

				}

				if(false == found)
				{
					newProps.add(prop);
				}

			}
		}
		return occurrences;
	}

	private String getProp(String code, int[] propBounds) throws I18nParseException
	{
		boolean isInComment = true;
		boolean isABracket = false;
		boolean isASlash = false;
		boolean isInCommentBegin = false;

		StringBuffer ret = new StringBuffer();


		for(int i=propBounds[0]; i < code.length(); ++i)
		{
			if(isInComment && isABracket && ']' != code.charAt(i))
			{
				if(isABracket)
				{
					propBounds[1] = i;
					return I18nUtils.normalizePropVal(ret.toString());
				}
			}
			else if(isInComment && ']' == code.charAt(i))
			{
				isABracket = !isABracket;
				isASlash = false;
			}
			else if(isInComment && '\n' == code.charAt(i))
			{
				isInComment = false;
				isABracket = false;
				isASlash = false;
			}
			else if(false == isInComment && '\n' == code.charAt(i))
			{
				throw new I18nParseException("Property " + ret.toString() + " does not end with ]");
			}
			else if(false == isInComment && false == isASlash && '/' == code.charAt(i))
			{
				isASlash = true;
				isABracket = false;
			}
			else if(false == isInComment && isASlash && '/' == code.charAt(i))
			{
				isInComment = true;
				isInCommentBegin = true;
				isABracket = false;
				isASlash = false;
			}

			if(isInComment && false == isInCommentBegin && false == isABracket)
			{
				ret.append(code.charAt(i));
			}

			isInCommentBegin = false;

		}

		if(ret.toString().length() > 50)
		{
			ret.setLength(50);
		}


		throw new I18nParseException("Property " + ret.toString() + " does not end with ]");
	}


	private File getSourceDir()
	{
		String buf = _panel.txtSourceDir.getText();
		if(null == buf || 0 == buf.trim().length())
		{
				String msg = s_stringMgr.getString("I18n.NoSourceDir");
				// i18n[I18n.NoSourceDir=Please choose a source directory.]
				JOptionPane.showMessageDialog(_app.getMainFrame(), msg);
			return null;

		}


		File sourceDir = new File(buf);
		if(false == sourceDir.isDirectory())
		{
				String msg = s_stringMgr.getString("I18n.SourceDirIsNotADirectory", sourceDir.getPath());
				// i18n[I18n.SourceDirIsNotADirectory=Source directory {0} is not a directory.]
				JOptionPane.showMessageDialog(_app.getMainFrame(), msg);
		}

		if(false == sourceDir.exists())
		{
			String msg = s_stringMgr.getString("I18n.SourceDirDoesNotExist", sourceDir.getPath());
			// i18n[I18n.SourceDirDoesNotExist=Source directory {0} does not exist.]
			JOptionPane.showMessageDialog(_app.getMainFrame(), msg);
			return null;
		}

		return sourceDir;
	}

	private int fixSourceFile(String filename) throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String nextLine = in.readLine();
		String lineToPrint = nextLine;
		int occurrencesReplaced = 0;
		boolean writeFixFile =false;

		ArrayList<String> linesToPrint = new ArrayList<String>();

		Pattern pat = Pattern.compile("\\s*//\\s*i18n\\[(.*)");
		Pattern commentLinePattern = Pattern.compile("\\s*//");
		while (nextLine != null)
		{
			Matcher m = pat.matcher(nextLine);
			if (m.matches())
			{
				String[] parts = nextLine.split("\\[");
				if (1 < parts.length)
				{
					parts = parts[1].split("\\]");
					if (0 < parts.length)
					{
						parts = parts[0].split("=");
						if (1 < parts.length)
						{
							String key = parts[0];
							String val = parts[1];

							// print the i18n comment
							linesToPrint.add(nextLine);

							nextLine = in.readLine();
							Matcher commentMatch = commentLinePattern.matcher(nextLine);

							if (!commentMatch.matches())
							{
								String quotedVal = "\"" + val + "\"";
								int indexOfQuotedVal = nextLine.indexOf(quotedVal);

								lineToPrint = nextLine;
								if(-1 < indexOfQuotedVal)
								{
									// Rob: Removed replacement via RegExp because it needed several RegExp escapes in val.

									String stringManager = "s_stringMgr.getString(\"" + key + "\")";
									lineToPrint =
										nextLine.substring(0, indexOfQuotedVal) +
									   stringManager +
									   nextLine.substring(indexOfQuotedVal + quotedVal.length());

									writeFixFile = true;
									occurrencesReplaced++;
								}
								else
								{
									String stringManagerBegin = "s_stringMgr.getString(\"" + key + "\""; // No end bracket, params might follow 
									if(-1 < nextLine.indexOf(stringManagerBegin))
									{
										// We see that the replacement was already done before so we can count this as replaced
										occurrencesReplaced++;
									}
								}
							}
							else
							{
								lineToPrint = nextLine;
								// here we've hit the second line of a multi-line i18n stanza
								// Just skip it, we're not that sophisticated.
							}
						}
					}
				}

			}
			linesToPrint.add(lineToPrint);
			nextLine = in.readLine();
			lineToPrint = nextLine;
		}
		in.close();

		if(writeFixFile)
		{
			String outFileName = filename + ".fixed";
			PrintWriter out = new PrintWriter(new FileOutputStream(outFileName));
			for (int i = 0; i < linesToPrint.size(); i++)
			{
				out.println(linesToPrint.get(i));
			}
			out.flush();
			out.close();
			// i18n[i18n.wroteFixedFile=Wrote file {0}]
			_app.getMessageHandler().showMessage(s_stringMgr.getString("i18n.wroteFixedFile", outFileName));
		}

		return occurrencesReplaced;
	}

	public void initialize(IApplication app)
	{
		_app = app;
	}

	public void uninitialize()
	{
		Preferences.userRoot().put(PREF_KEY_SOURCE_DIR, _panel.txtSourceDir.getText());
	}



}
