/*
 * Copyright (C) 2011 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Class that processes arguments that are specific to the DBCopy command-line.
 */
public class CommandLineArgumentProcessor
{
	public static final String SOURCE_CATALOG = "source-catalog";
	
	public static final String SOURCE_SCHEMA = "source-schema";
	
	public static final String DEST_CATALOG = "dest-catalog";
	
	public static final String DEST_SCHEMA = "dest-schema";

	public static final String TABLE_PATTERN = "table-pattern";

	public static final String TABLE_LIST = "table-list";

	public static final String DEST_SESSION = "dest-alias";

	public static final String SOURCE_SESSION = "source-alias";

	private Options options = new Options();

	private CommandLine cmd = null;

	public CommandLineArgumentProcessor(String[] args) throws ParseException
	{
		createOptions();
		CommandLineParser parser = new GnuParser();
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			System.err.println(e.getMessage());
			formatter.printHelp("DBCopyCLI", options, true);
			throw e;
		}
	}

	@SuppressWarnings("static-access")
	private void createOptions()
	{
		Option sourceOption =
			OptionBuilder.hasArg().isRequired().withLongOpt(SOURCE_SESSION).withDescription(
				"The name of the source alias to copy tables from").create();
		options.addOption(sourceOption);
		
		Option destOption = 
			OptionBuilder.hasArg().isRequired().withLongOpt(DEST_SESSION).withDescription(
				"The name of the destination alias to copy tables to").create();
		options.addOption(destOption);

		Option sourceSchemaOption = 
			OptionBuilder.hasArg().withLongOpt(SOURCE_SCHEMA).withDescription(
				"The source schema to copy tables from").create();
		
		Option sourceCatalogOption = 
			OptionBuilder.hasArg().withLongOpt(SOURCE_CATALOG).withDescription(
				"The source catalog to copy tables from").create();
		
		OptionGroup sourceSchemaGroup = new OptionGroup();
		sourceSchemaGroup.setRequired(true);
		sourceSchemaGroup.addOption(sourceSchemaOption);
		sourceSchemaGroup.addOption(sourceCatalogOption);
		
		options.addOptionGroup(sourceSchemaGroup);
		
		Option listOption = 
			OptionBuilder.hasArg().withLongOpt(TABLE_LIST).withDescription(
				"A comma-delimited list of tables to copy").create();
		Option patternOption = 
			OptionBuilder.hasArg().withLongOpt(TABLE_PATTERN).withDescription(
				"A regexp pattern to match source table names").create();
		
		OptionGroup tableGroup = new OptionGroup();
		tableGroup.setRequired(true);
		tableGroup.addOption(listOption);
		tableGroup.addOption(patternOption);
		
		options.addOptionGroup(tableGroup);
		
		Option destSchemaOption = 
			OptionBuilder.hasArg().withLongOpt(DEST_SCHEMA).withDescription(
				"The destination schema to copy tables into").create();
		
		Option destCatalogOption = 
			OptionBuilder.hasArg().withLongOpt(DEST_CATALOG).withDescription(
				"The destination catalog to copy tables into").create();
		
		OptionGroup destSchemaGroup = new OptionGroup();
		destSchemaGroup.setRequired(true);
		destSchemaGroup.addOption(destSchemaOption);
		destSchemaGroup.addOption(destCatalogOption);
		
		options.addOptionGroup(destSchemaGroup);
		
		
	}

	public String getSourceAliasName() {
		return cmd.getOptionValue(SOURCE_SESSION);
	}
	
	public String getDestAliasName() {
		return cmd.getOptionValue(DEST_SESSION);
	}
	
	public String getDestSchemaName() {
		return cmd.getOptionValue(DEST_SCHEMA);
	}

	public String getDestCatalogName() {
		return cmd.getOptionValue(DEST_CATALOG);
	}

	public String getSourceCatalogName() {
		return cmd.getOptionValue(SOURCE_CATALOG);
	}
	
	public String getSourceSchemaName() {
		return cmd.getOptionValue(SOURCE_SCHEMA);
	}
	
	public List<String> getTableList() {
		List<String> result = new ArrayList<String>();
		if (cmd.hasOption(TABLE_LIST)) {
			String tableListStr = cmd.getOptionValue(TABLE_LIST);
			if (tableListStr.contains(",")) {
				String[] parts = tableListStr.split(",");
				for (String part : parts) {
					result.add(part);
				}
			} else {
				result.add(tableListStr);
			}
		}
		// TODO: add support for regular expression.
		return result;
	}
}
