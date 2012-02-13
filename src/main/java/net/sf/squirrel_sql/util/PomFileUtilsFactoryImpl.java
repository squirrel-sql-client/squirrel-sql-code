package net.sf.squirrel_sql.util;

import java.io.File;
import java.io.IOException;

public class PomFileUtilsFactoryImpl implements PomFileUtilsFactory
{
	/**
	 * @see net.sf.squirrel_sql.util.PomFileUtilsFactory#create(java.io.File)
	 */
	@Override
	public PomFileUtils create(File pomFile) throws IOException {
		PomFileUtils result = new PomFileUtilsImpl();
		result.setPomFile(pomFile);
		return result;
	}
}
