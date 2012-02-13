package net.sf.squirrel_sql.util;

import java.io.File;
import java.io.IOException;

public interface PomFileUtilsFactory
{

	public abstract PomFileUtils create(File pomFile) throws IOException;

}