package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.*;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.net.URLClassLoader;
import java.net.URL;

public class I18nProps extends Object
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(I18nProps.class);


	private File _file;
	private File _zipFile;
	private String _entryName;
	private String _name;

	public I18nProps(File file, URL[] sourceUrls)
	{
		_file = file;
		initName(sourceUrls);
	}

	public I18nProps(File zipFile, String entryName, URL[] sourceUrls)
	{
		_zipFile = zipFile;
		_entryName = entryName;
		initName(sourceUrls);
	}

	private void initName(URL[] sourceUrls)
	{

		for (int i = 0; i < sourceUrls.length; i++)
		{
			String classPathEntry = (String) sourceUrls[i].getPath();
			if(getPath().startsWith(classPathEntry))
			{
				_name = getPath().substring(classPathEntry.length());
				return;
			}

		}
		_name = getPath();
	}

	public String getPath()
	{
		if(null != _file)
		{
			return _file.toString();
		}
		else
		{
			return _zipFile.toString() + File.separator + _entryName;
		}
	}


	public String getName()
	{
		return _name;
	}


	Properties getProperties()
	{
		try
		{
			Properties ret = new Properties();
			InputStream is = getInputStream();
			ret.load(is);
			is.close();
			return ret;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private InputStream getInputStream()
	{
		try
		{
			if(null != _file)
			{
				return new FileInputStream(_file);
			}
			else
			{
				ZipFile zf = new ZipFile(_zipFile);

				ZipEntry entry = zf.getEntry(_entryName);

				return zf.getInputStream(entry);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	public void removeProps(Properties toRemoveFrom)
	{
		Properties toRemove = getProperties();

		for(Enumeration e=toRemove.keys(); e.hasMoreElements(); )
		{
			toRemoveFrom.remove(e.nextElement());
		}
	}

	public void copyTo(File toCopyTo)
	{
		try
		{
			InputStream is = getInputStream();

			FileOutputStream fos = new FileOutputStream(toCopyTo);

			int buf = is.read();
			while(-1 != buf)
			{
				fos.write(buf);
				buf = is.read();

			}

			fos.flush();
			fos.close();
			is.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public String getLocalizedFileName(Locale loc)
	{
		String name = getPath().substring(getPath().lastIndexOf(File.separator) + 1);
		return name.substring(0, name.lastIndexOf(".properties")) + "_" + loc + ".properties";
	}

	public String getUnlocalizedPath(Locale loc)
	{
		String path = getPath().substring(0, getPath().lastIndexOf(File.separator));
		String name = getPath().substring(getPath().lastIndexOf(File.separator));

		return path + name.replaceAll("_"+ loc.toString(), "");
	}

	public static Locale parseLocaleFromPropsFileName(String propsFileName)
	{
		if(false == propsFileName.endsWith(".properties"))
		{
			return null;
		}

		String buf = propsFileName.substring(0, propsFileName.length() - ".properties".length());

		Locale[] availableLocales = Locale.getAvailableLocales();

		for (int i = 0; i < availableLocales.length; i++)
		{
			if(buf.endsWith("_" + availableLocales[i].toString()))
			{
				return availableLocales[i];
			}
		}

		return null;




	}

	public Properties getTranslateableProperties()
	{
		String name;

		if(null == _entryName)
		{
			name = _file.getName();
		}
		else
		{
			if(-1 == _entryName.lastIndexOf(File.separator))
			{
				name = _entryName;
			}
			else
			{
				name = _entryName.substring(_entryName.lastIndexOf(File.separator));
			}
		}

		if(name.startsWith("I18nStrings"))
		{
			return getProperties();
		}
		else
		{
			// These files contain images etc. We try to filter out these props.
			Properties ret = getProperties();
			for(Enumeration e=ret.keys(); e.hasMoreElements();)
			{
				String key = (String) e.nextElement();

				if(key.endsWith(".image") ||
					key.endsWith(".image") ||
					key.endsWith(".rolloverimage") ||
					key.endsWith(".disabledimage") ||
					key.endsWith(".frameIcon") ||
					key.endsWith(".file") ||
					key.endsWith(".images") ||
					key.endsWith(".accelerator") ||
					key.equals("path.defaults")
					)
				{
					ret.remove(key);
				}
			}

			return ret;






		}



	}
}
