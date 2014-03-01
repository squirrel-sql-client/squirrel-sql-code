/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JScrollBar;

import net.sourceforge.squirrel_sql.client.gui.IAbortEventHandler;
import net.sourceforge.squirrel_sql.client.gui.ProgressAbortDialog;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.gui.TaskDescriptionComponent;

import org.apache.commons.lang.StringUtils;

/**
 * A special extension for a ProgressAbortDialog, that customize the general information area about the operation.
 * This class adds readable information about the SQL and the target file.
 * @author Stefan Willinger
 *
 */
public class SQL2FileProgressAbortDialog extends ProgressAbortDialog {

	
	private String targetFile;
	
	private String sql;
	
	private TaskDescriptionComponent taskDescription;
	
	/**
	 * @see ProgressAbortDialog
	 * @wbp.parser.constructor
	 */
	public SQL2FileProgressAbortDialog(Dialog owner, String title, String targetFile, String sql, IAbortEventHandler abortHandler) {
		super(owner, title, "", 0, true, abortHandler);
		setTargetFile(targetFile);
		setSql(sql);
		setLabelValues();
	}

	
	/**
	 * 
	 */
	private void setLabelValues() {
		taskDescription.setTargetFile(getTargetFile());
		taskDescription.setSql(getSql());
	}


	/**
	 * @see ProgressAbortDialog
	 */
	public SQL2FileProgressAbortDialog(Frame owner, String title,  String targetFile, String sql, IAbortEventHandler abortHandler) {
		super(owner, title, "", 0, true, abortHandler);
		setTargetFile(targetFile);
		setSql(sql);
		setLabelValues();
	}


	/**
	 * Create a task description for this progress dialog.
	 * The task description is embedded in a {@link JScrollBar} and contains the following items:
	 * <ul>
	 * <li>The path of the target file</li>
	 * <li>The running SQL statement as a formated String.</li>
	 * </ul>
	 * @see net.sourceforge.squirrel_sql.client.gui.ProgressAbortDialog#createTaskDescripion()
	 * @see TaskDescriptionComponent
	 */
	@Override
	protected JComponent createTaskDescripion() {
		this.taskDescription = new TaskDescriptionComponent(getTargetFile(), getSql());
		return taskDescription;
	}


	/**
	 * @return the targetFile
	 */
	public String getTargetFile() {
		return targetFile;
	}


	/**
	 * @param targetFile the targetFile to set
	 */
	public void setTargetFile(String targetFile) {
		if(StringUtils.isBlank(targetFile)){
			throw new IllegalArgumentException("targetFile must not be blank.");
		}
		this.targetFile = targetFile;
	}


	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}


	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		if(StringUtils.isEmpty(sql)){
			throw new IllegalArgumentException("sql must not be blank");
		}
		this.sql = sql;
	}

	/**
	 * Just for playing...
	 */
	public static void main(String[] args) throws Exception {
		IAbortEventHandler handler = new IAbortEventHandler() {
			
			@Override
			public void cancel() {
				System.out.println("echo");
			}
		};
		
		SQL2FileProgressAbortDialog dialog = new SQL2FileProgressAbortDialog((Frame)null, "myTitle", "file", "myDescription",  handler);
		Thread.sleep(3000);
		dialog.currentlyLoading("Running query");
		Thread.sleep(3000);
		dialog.setTaskStatus("1 Row(s) exported");
		Thread.sleep(3000);
		dialog.setTaskStatus("100 Row(s) exported");
		Thread.sleep(3000);
		dialog.setTaskStatus("1000 Row(s) exported");
		dialog.currentlyLoading("Finished");
		
	}

}
