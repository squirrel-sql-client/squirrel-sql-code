/*
 * This file is part of a syntax highlighting package
 * Copyright (C) 2003 Stephen Ostermiller 
 * http://ostermiller.org/contact.pl?regarding=Syntax+Highlighting
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * See COPYING.TXT for details.
 */
package com.Ostermiller.Syntax;

import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.util.*;
import org.apache.tools.ant.types.*;

import java.io.*;
import java.net.*;

public class ToHTMLAntTask extends MatchingTask {

	/** 
	 * The compiler that does to work for this task.
	 */
	private ToHTML toHTML = new ToHTML();
	
	public ToHTMLAntTask(){		
		toHTML.setStyleSheet("syntax.css");
	}
	
	private File srcDir;
	/**
	 * Set the source dir to find the source files.
	 */
	public void setSrcdir(File srcDir) {
		this.srcDir = srcDir;
	}
	
	private File destDir = null;
	/**
	 * Set the destination where the fixed files should be placed.
	 * Default is to replace the original file.
	 */
	public void setDestdir(File destDir) {
		this.destDir = destDir;
	}

	/**
	 * Set the mime-type for files to be highlighted.
	 * text/html, text/x-java...
	 */
	public void setMime(String mime) {
		toHTML.setMimeType(mime);
	}
	
	/**
	 * Java class name of the lexer to use.
	 */
	public void setLexer(String lexer) {
		toHTML.setLexerType(lexer);
	}
	
	/**
	 * Use the given title in the html page.
	 */
	public void setTitle(String title) {
		toHTML.setTitle(title);
	}	
	
	/**
	 * BTE template to use.  see: ostermiller.org/bte
	 */
	public void setTemplate(String template) throws MalformedURLException {
		toHTML.setTemplate(new URL(getProject().getBaseDir().toURL(), template).toString());
	}
	
	/**
	 * Cascading Style Sheet to which html should be linked.
	 */
	public void setCSS(String css) {
		toHTML.setStyleSheet(css);
	}
	
	public void addConfiguredParameter(Parameter param) throws BuildException {
		if (param.getType().equals("ignore")){
			if (param.getName() == null){
				throw new BuildException("ignore parameter must have a name!");
			}			
			if (param.getValue() != null){
				throw new BuildException("ignore parameter does not take a value!");
			}
			toHTML.addIgnoreStyle(param.getName());
		} else if (param.getType().equals("translate")){
			if (param.getName() == null || param.getValue() == null){
				throw new BuildException("translate parameter must have a name and value!");
			}
			toHTML.translateStyle(param.getName(), param.getValue());
		} else {			
			throw new BuildException("unknown parameter type: " + param.getType());
		}
	}
	
	/**
	 * Executes the task.
	 */
	public void execute() throws BuildException {
		if (srcDir == null) {
			srcDir = getProject().getBaseDir();
		}
		if (!srcDir.exists()) {
			throw new BuildException("srcdir does not exist!");
		}
		if (!srcDir.isDirectory()) {
			throw new BuildException("srcdir is not a directory!");
		}
		if (destDir == null) {
			destDir = srcDir;
		}
		if (!destDir.exists()) {
			throw new BuildException("destdir does not exist!");
		}
		if (!destDir.isDirectory()) {
			throw new BuildException("destdir is not a directory!");
		}
		
		DirectoryScanner ds = super.getDirectoryScanner(srcDir);
		String[] files = ds.getIncludedFiles();
		SourceFileScanner sfs = new SourceFileScanner(this);
		String[] newFiles = sfs.restrict(
			files, 
			srcDir, 
			destDir, 
			new FileNameMapper(){
				public void setFrom(String from){} 
				public void setTo(String to){}
				public String[] mapFileName(String f){
					return new String[]{f+".html"};
				}
			}
		);
		
		try {
			if (newFiles.length > 0){
				System.out.println("Applying syntax highlighting to " + newFiles.length + " files.");
			}
			for (int i = 0; i < newFiles.length; i++) {
				File destFile = new File(destDir, newFiles[i]+".html");
				File destParent = destFile.getParentFile();
				if (destParent != null && !destParent.exists()){
					if (!destParent.mkdirs()){
						throw new BuildException("could not create directory: " + destParent);
					}
				}			
				toHTML.setInput(new FileReader(new File(srcDir, newFiles[i])));
	 			toHTML.setOutput(new FileWriter(destFile));
				toHTML.setDocNameFromFileName(newFiles[i]);
		  		toHTML.setExtFromFileName(newFiles[i]);				
	 			toHTML.writeFullHTML();
			}
		} catch (Exception x){
			throw new BuildException(x);
		}
	}
}
