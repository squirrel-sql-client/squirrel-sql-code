/*
 * This file is part of a syntax highlighting package
 * Copyright (C) 1999-2002 Stephen Ostermiller
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

import java.io.*;
import com.Ostermiller.Syntax.Lexer.*;
import com.Ostermiller.bte.*;
import gnu.getopt.*;
import java.util.*;
import java.text.*;
import com.Ostermiller.util.*;
import java.lang.reflect.*;
import java.net.MalformedURLException;

/**
 * ToHTML will take source and convert it into an html document with
 * syntax highlighting.  All the special characters in the html will be 
 * replaced by their escape sequences. 
 * <p>
 * A typical use of this class might be:
 * <pre>
 * try {
 *     ToHTML toHTML = new ToHTML();
 *     toHTML.setInput(new FileReader("Source.java"));
 *     toHTML.setOutput(new FileWriter("Source.java.html"));
 *     toHTML.setMimeType("text/x-java");
 *     toHTML.setFileExt("java") 
 *     toHTML.writeFullHTML();
 * } catch (InvocationTargetException x){
 *     // can only happen if setLexerType(java.lang.String) method is used.
 * } catch (CompileException x){
 *     // can only happen if setTemplate(java.lang.String) method is used.
 * } catch (IOException x){
 *     System.err.println(x.getMessage());
 * }
 * </pre>
 * <p>
 * A ToHTML instance may be reused, however it is not thread safe and should only be
 * used by one thread at a time without external synchronization.
 */
public class ToHTML {

    private static ToHTML defaultToHTML = new ToHTML();
    private PrintWriter out;
    private Reader in;
    private String fileExt;
    private String mimeType;
    private Lexer lexer;
    private String lexerType;
    private String styleSheet;
    private HashSet ignoreStyles = new HashSet();
    private HashMap translateStyles = new HashMap();
    private String bteSuper;
    private String title;
    private String docName;
    private HashMap lexers = new HashMap();    
    private static HashMap registeredMimeTypes = new HashMap();
    private static HashMap registeredFileExtensions = new HashMap();
    static {
        register(
            "com.Ostermiller.Syntax.Lexer.HTMLLexer1", 
            new String[] {
                "text/html",
            },
            new String[] {
                "htm",
                "html",
            } 
        );
        register(
            "com.Ostermiller.Syntax.Lexer.JavaLexer", 
            new String[] {
                "text/x-java",
                "text/java", // for backwards compatibility, but not correct
            },
            new String[] {
                "jav",
                "java",
            } 
        ); 
		register(
            "com.Ostermiller.Syntax.Lexer.SQLLexer", 
            new String[] {
                "text/x-sql",
                "application/x-sql",
            },
            new String[] {
                "sql",
            } 
        ); 
        register(
            "com.Ostermiller.Syntax.Lexer.CLexer", 
            new String[] {
                "text/x-c++hdr",
                "text/x-csrc",
                "text/x-chdr",
                "text/x-csrc",
                "text/c", // for backwards compatibility, but not correct
            },
            new String[] {
                "c", 
                "h", 
                "cc",
                "cpp",
                "cxx",
                "c++",
                "hpp",
                "hxx",
                "hh",
            } 
        ); 
        register(
            "com.Ostermiller.Syntax.Lexer.PropertiesLexer", 
            new String[] {
                "text/x-properties",
            },
            new String[] {
                "props",
                "properties",
            } 
        );
        register(
            "com.Ostermiller.Syntax.Lexer.LatexLexer", 
            new String[] {
                "application/x-latex",
                "text/x-latex",
                "application/x-tex",
                "text/x-tex",
            },
            new String[] {
                "tex",
		"sty",
		"cls",
		"dtx",
		"ins",
                "latex",
            } 
        );        
        register(
            "com.Ostermiller.Syntax.Lexer.PlainLexer", 
            new String[] {
                "text/plain",
                "text",
            },
            new String[] {
                "txt",
                "text",
            } 
        );
    }
    
    /**
     * Register a lexer to handle the given mime types and fileExtensions.
     * <p>
     * If a document has a type "text/plain" it will first match a registered
     * mime type of "text/plain" and then a registered mime type of "text".
     *
     * @param lexer String representing the fully qualified java name of the lexer.
     * @param mimeTypes array of mime types that the lexer can handle.
     * @param fileExtensions array of fileExtensions the lexer can handle. (case insensitive)
     */
    public static void register(String lexer, String[] mimeTypes, String[] fileExtensions){
        for (int i=0; i<mimeTypes.length; i++){
            registeredMimeTypes.put(mimeTypes[i], lexer);
        }
        for (int i=0; i<fileExtensions.length; i++){
            registeredFileExtensions.put(fileExtensions[i].toLowerCase(), lexer);
        }
    }
    
    /**
     * Open a span if needed for the given style.
     * 
     * @param description style description.
     * @param out place to write output.
     */
    private void openSpan(String description, PrintWriter out) throws IOException {
        if (translateStyles.containsKey(description)){
            description = (String)translateStyles.get(description);
        }
        if (!ignoreStyles.contains(description)){  
            out.print("<span class=" + description + ">");
        }
    }
    /**
     * Close a span if needed for the given style.
     * 
     * @param description style description.
     * @param out place to write output.
     */
    private void closeSpan(String description, PrintWriter out) throws IOException {
        if (translateStyles.containsKey(description)){
            description = (String)translateStyles.get(description);
        }
        if (!ignoreStyles.contains(description)){ 
            out.print("</span>");
        }
    }
    
    /**
     * Write a highlighted document html fragment.
     * 
     * @param lexer Lexer from which to get input.
     * @param out place to write output.
     * @throws IOException if an I/O error occurs.
     */
    private void writeHTMLFragment(Lexer lexer, PrintWriter out) throws IOException {
        String currentDescription = null;
        Token token;
        out.println();
        out.print("<pre>");
		while ((token = lexer.getNextToken()) != null){
            // optimization implemented here:
            // ignored white space can be put in the same span as the stuff
            // around it.  This saves space because spans don't have to be
            // opened and closed.            
            if ((token.isWhiteSpace() && ignoreStyles.contains("whitespace")) ||
                   (currentDescription != null && token.getDescription().equals(currentDescription))){
                writeEscapedHTML(token.getContents(), out);
            } else {
                if (currentDescription != null) closeSpan(currentDescription, out);
                currentDescription = token.getDescription();
                openSpan(currentDescription, out);
                writeEscapedHTML(token.getContents(), out);
            }         
        }
        if (currentDescription != null) closeSpan(currentDescription, out);
        out.println("</pre>");
    }
    
    /**
     * Write a HTML fragment to the output location that has been set for this class.
     * <p>
     * The type of syntax highlighting done will be determined first by the class name for the lexer,
     * then by the mime type, then by the file extension.
     * <p>
     * To save space in the resulting output, it is often a good idea to ignore whitespace.  If 
     * whitespace is ignored, it will be merged with surrounding tags.
     *
     * @throws IOException if an I/O error occurs.
     * @throws InvocationTargetException if a lexer class is specified which can't be instantiated.
     */     
    public void writeHTMLFragment() throws IOException, InvocationTargetException {
        Lexer lexer = this.lexer;
        if (lexer == null) lexer = getLexerFromClass(lexerType);    
        if (lexer == null) lexer = getLexerFromMime();
        if (lexer == null) lexer = getLexerFromExt();
        writeHTMLFragment(lexer, out);      
        out.flush(); 
    }
    
    /**
     * Format for date.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd yyyy 'at' HH:mm");
    
    /**
     * Cache of a potentially large buffer
     */
    private CircularCharBuffer circularBuffer= null;
    
    /**
     * Cache of BTE files.
     */    
   private com.Ostermiller.bte.Compiler compiler = null;
    
    /**
     * Write a full HTML document to the output location that has been set for this class.
     * If a template URL is specified, and <a href="http://ostermiller.org/bte/">template libraries</a>
     * are installed, templates will be used.
     * <p>
     * The type of syntax highlighting done will be determined first by the class name for the lexer,
     * then by the mime type, then by the file extension.
     * <p>
     * To save space in the resulting output, it is often a good idea to ignore whitespace.  If 
     * whitespace is ignored, it will be merged with surrounding tags.
     * <p>
     * If templates are used, the following sections are available to the template:<br>
     * date<br>
     * highlightedDocument<br>
     * fileExtension<br>
     * fileName (minus the extension)<br>
     * styleSheet (if specified)<br>
     * title (if specified)<br>
     * The <a href="http://ostermiller.org/syntax/page.bte">default template</a> may be used as a guide.
     *
     * @throws IOException if an I/O error occurs.
     * @throws InvocationTargetException if a lexer class is specified which can't be instantiated.
     * @throws CompileException if a bte template is specified which can't be compiled.
     */     
    public void writeFullHTML() throws IOException, InvocationTargetException, CompileException {
        Lexer lexer = this.lexer;
        if (lexer == null) lexer = getLexerFromClass(lexerType);    
        if (lexer == null) lexer = getLexerFromMime();
        if (lexer == null) lexer = getLexerFromExt();
        boolean noBTE = false;
        try {
            if (bteSuper == null){
                noBTE = true;
            } else {
                if (compiler == null){
                    compiler = new com.Ostermiller.bte.Compiler();
                }
                if (circularBuffer == null){
                    circularBuffer = new CircularCharBuffer(CircularCharBuffer.INFINITE_SIZE);
                } else {
                    circularBuffer.clear();
                }
                PrintWriter bteOut = new PrintWriter(circularBuffer.getWriter()); 
                bteOut.print("<%bte.doc super='" + bteSuper + "' %>\n");
                bteOut.print("<%bte.tpl name=date %>" + dateFormat.format(new Date()) + "<%/bte.tpl%>\n");
                if (title != null){
                    bteOut.print("<%bte.tpl name=title %>" + title + "<%/bte.tpl%>\n");
                }
                if (fileExt != null){
                    bteOut.print("<%bte.tpl name=fileExtension %>" + fileExt + "<%/bte.tpl%>\n");
                }
                if (docName != null){
                    bteOut.print("<%bte.tpl name=fileName %>" + docName + "<%/bte.tpl%>\n");
                }
                if (styleSheet != null){
                    bteOut.print("<%bte.tpl name=styleSheet %>" + styleSheet + "<%/bte.tpl%>\n");
                }
                bteOut.print("<%bte.tpl name=highlightedDocument %>");
                writeHTMLFragment(lexer, bteOut);
                bteOut.print("<%/bte.tpl%>\n");
                bteOut.print("<%/bte.doc%>\n");
                bteOut.close();
                Reader bteIn = circularBuffer.getReader();
                compiler.compile(bteIn, out);
            } 
        } catch (NoClassDefFoundError x){ 
            noBTE = true;
        }
        if (noBTE){          
            PrintWriter out = this.out;
            out.println("<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN'>");
            out.println("<html>");
            out.println("<head>");
            if (title != null){
                out.println("<title>" + title + "</title>");
            }
            out.println("<meta http-equiv='content-type' content='text/html;charset=ISO-8859-1'>");            
            if (styleSheet != null){
                out.println("<link rel='stylesheet' title='Syntax Highlighting' href='" + styleSheet + "' type='text/css'>");
            }
            out.println("</head>");
            out.println("<body>");
            writeHTMLFragment(lexer, out);
            out.println("<div style='border: thin black ridge;padding:1cm;'>");
            out.println("<!--");
            out.println("To customize the appearance of the html that is emitted you must install");
            out.println("the BTE template system from:");
            out.println("http://ostermiller.org/bte/");
            out.println("-->");
            out.println("Syntax Highlighting created using the ");
            out.println("<a href='http://ostermiller.org/syntax/'>com.Ostermiller.Syntax</a> package.<br>");
            out.println(dateFormat.format(new Date()));
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }         
        out.flush(); 
    }
    
    /**
     * Set the URL of the BTE template file which should used.
     * For more information on BTE templates please 
     * see the <a href="http://ostermiller.org/bte/">BTE website</a>.
     *
     * @param bteSuper url of the bte template file.
     */
    public void setTemplate(String bteSuper){
        this.bteSuper = bteSuper;
    }
    
    /**
     * Set the file extension to be used for the document.
     * <p>
     * The type of syntax highlighting to use will depend on
     * the lexerType that is set, the mime type that is given,
     * and the file extension that is given.
     * If a lexer is explicitly given, it is used, otherwise
     * if the mime type is recognized, an appropriate style 
     * for that mime type is used, otherwise if the file extension
     * is recognized, an appropriate style for that extension is
     * used, otherwise, no style is given.
     * 
     * @param fileExt the file extension of the document or null to clear.
     */
    public void setFileExt(String fileExt){
        this.fileExt = fileExt;
    }
    
    /**
     * Set the name of this document (minus any file extension).
     * <p>
     * This name will be reported to html template.
     * 
     * @param docName Name of the document.
     */
    public void setDocName(String docName){
        this.docName = docName;
    }
    
    /**
     * Set the lexer to be used for the document.
     * <p>
     * The type of syntax highlighting to use will depend on
     * the lexerType that is set, the mime type that is given,
     * and the file extension that is given.
     * If a lexer is explicitly given, it is used, otherwise
     * if the mime type is recognized, an appropriate style 
     * for that mime type is used, otherwise if the file extension
     * is recognized, an appropriate style for that extension is
     * used, otherwise, no style is given.
     * 
     * @param lexerType full java name of the lexer to use.
     */
    public void setLexerType(String lexerType){
        this.lexerType = lexerType;
    }
    
    /**
     * Set the lexer to be used for the document.
     */
    private void setLexer(Lexer lexer){
        saveLexer(lexer);
        this.lexer = lexer;
    }
    
    /**
     * Set the mime type of the document.
     * <p>
     * The type of syntax highlighting to use will depend on
     * the lexerType that is set, the mime type that is given,
     * and the file extension that is given.
     * If a lexer is explicitly given, it is used, otherwise
     * if the mime type is recognized, an appropriate style 
     * for that mime type is used, otherwise if the file extension
     * is recognized, an appropriate style for that extension is
     * used, otherwise, no style is given.
     * 
     * @param mimeType the mimeType of the document or null to clear.
     */
    public void setMimeType(String mimeType){
        this.mimeType = mimeType;
    }
    
    /**
     * Get the mime type of the document.
     * 
     * @return the mimeType of the document or null if none.
     */
    public String getMimeType(){
        return mimeType;
    }
    
    /**
     * Set the output stream.
     * 
     * @param out new output stream.
     */
    public void setOutput(Writer out){         
        this.out = new PrintWriter(out);
    }
    
    /**
     * Set the output stream.
     * 
     * @param out new output stream.
     */
    public void setOutput(PrintWriter out){
        this.out = out;
    }
    
    /**
     * Set the output to the given file.
     * 
     * @param f file to which to write.
     * @throws IOException if the file cannot be opened.
     */
    public void setOutput(File f) throws IOException {
        setOutput(new FileWriter(f));
    }
    
    /**
     * Set the input to the given file.
     * 
     * @param f file from which to read.
     * @throws IOException if the file cannot be opened.
     */
    public void setInput(File f) throws IOException {
        setInput(new FileReader(f));
    }
    
    /**
     * Set the input stream.
     * 
     * @param in new input stream.
     */
    public void setInput(Reader in){
        this.in = in;
    }
    
    /**
     * Link the html document to the style sheet at the
     * given URL.  
     *
     * @param styleSheet link stylesheet URL to use, or null to none.
     */
    public void setStyleSheet(String styleSheet){
        this.styleSheet = styleSheet;
    } 
    
    /**
     * Use the given title for the html document.
     *
     * @param title document title, or null to use none.
     */
    public void setTitle(String title){
        this.title = title;
    } 
    
    /**
     * Get the URL of the linked style sheet.  
     *
     * @return stylesheet URL to used, or null if none.
     */
    public String getStyleSheet(){
        return styleSheet;
    } 
    
    /**
     * Get the title used for the html document.
     *
     * @return document title, or null if none.
     */
    public String getTitle(){
        return title;
    } 
    
    /**
     * Adds the specified style to be ignored if it
     * is not already present.
     * Ignored styles will not have a span associated with
     * them in the output.  This can be used to make the
     * resulting files smaller.  It is suggested to
     * ignore "whitespace" as it probably won't look any different
     * with an associated style.
     * <p>
     * If styles have been translated, then the translated style 
     * should be the one to be ignored.
     *
     * @param style the name of the style to be ignored.
     * @return true if the style was not already being ignored.
     */
    public boolean addIgnoreStyle(String style){
        return ignoreStyles.add(style);
    }
    
    /**
     * No longer ignore the given style.
     *
     * @param style the name of the style to no longer be ignored.
     * @return true if the style was being ignored.
     */
    public boolean removeIgnoreStyle(String style){
        return ignoreStyles.remove(style);
    }
    
    /**
     * Rename a style.  Useful for working with style sheets that
     * expect certain names or for saving space by providing shorter
     * names.  The name of the style will only be changed in the html
     * document.  You must edit the CSS stylesheet or provide a custom
     * CSS stylesheet with this option as the default stylesheet will
     * use canonical style names.
     *
     * @param canonicalStyle the canonical (default) name of the style.
     * @param style the new name of the style.
     * @return previous translation of the canonicalStyle.
     */
    public String translateStyle(String canonicalStyle, String style){
        if (canonicalStyle == null || style == null) throw new NullPointerException();
        String value = (String)translateStyles.put(canonicalStyle, style);
        if (value == null) return canonicalStyle;
        return value;
    }
    
    /**
     * No longer ignore the given style.
     *
     * @param canonicalStyle the canonical (default) name of the style.
     * @return previous translation of the canonicalStyle.
     */
    public String removeTranslation(String canonicalStyle){
        String value = (String)translateStyles.remove(canonicalStyle);
        if (value == null) return canonicalStyle;
        return value;
    }

    private static String version = "1.1.1";

    /**
     * Locale specific strings displayed to the user.
     */
 	protected static ResourceBundle labels = ResourceBundle.getBundle("com.Ostermiller.Syntax.ToHTML",  Locale.getDefault());

	/**
	 * Program to add syntax highlighting to source files.
     * Execute <b>java&nbsp;com.Ostermiller.Syntax.ToHTML&nbsp;--help</b>
     * for more details.
	 */ 
	public static void main(String[] args){          
        // create the command line options that we are looking for
        LongOpt[] longopts = {
            new LongOpt(labels.getString("help.option"), LongOpt.NO_ARGUMENT, null, 1),
            new LongOpt(labels.getString("version.option"), LongOpt.NO_ARGUMENT, null, 2),
            new LongOpt(labels.getString("about.option"), LongOpt.NO_ARGUMENT, null, 3),
            new LongOpt(labels.getString("m.option"), LongOpt.REQUIRED_ARGUMENT, null, 'm'),            
            new LongOpt(labels.getString("l.option"), LongOpt.REQUIRED_ARGUMENT, null, 'l'),            
            new LongOpt(labels.getString("T.option"), LongOpt.REQUIRED_ARGUMENT, null, 'T'),            
            new LongOpt(labels.getString("i.option"), LongOpt.REQUIRED_ARGUMENT, null, 'i'),            
            new LongOpt(labels.getString("t.option"), LongOpt.REQUIRED_ARGUMENT, null, 't'),              
            new LongOpt(labels.getString("s.option"), LongOpt.REQUIRED_ARGUMENT, null, 's'),            
            new LongOpt(labels.getString("o.option"), LongOpt.REQUIRED_ARGUMENT, null, 'o'),           
            new LongOpt(labels.getString("f.option"), LongOpt.NO_ARGUMENT, null, 'f'),            
            new LongOpt(labels.getString("r.option"), LongOpt.REQUIRED_ARGUMENT, null, 'r'), 
        };        
        String oneLetterOptions = "m:l:T:i:t:s:o:fr:";
        Getopt opts = new Getopt(labels.getString("tohtml"), args, oneLetterOptions, longopts);        
        ToHTML toHTML = new ToHTML();
        toHTML.setTemplate(ClassLoader.getSystemResource("com/Ostermiller/Syntax/page.bte").toString()); 
        toHTML.setStyleSheet("syntax.css"); 
        String output = null;
        String title = null;
        boolean force = false;
        int c;
        while ((c = opts.getopt()) != -1){
            switch(c){
          		case 1:{
                    // print out the help message
                    String[] helpFlags = new String[]{
                        "--" + labels.getString("help.option"),
                        "--" + labels.getString("version.option"),
                        "--" + labels.getString("about.option"),
                        "-m --" + labels.getString("m.option") + " <" + labels.getString("type") + ">",
                        "-l --" + labels.getString("l.option") + " <" + labels.getString("class") + ">",
                        "-T --" + labels.getString("T.option") + " <" + labels.getString("title") + ">",
                        "-i --" + labels.getString("i.option") + " <" + labels.getString("class") + ">",
                        "-t --" + labels.getString("t.option") + " <" + labels.getString("url") + "|" + labels.getString("file") + ">",
                        "-s --" + labels.getString("s.option") + " <" + labels.getString("url") + ">",
                        "-o --" + labels.getString("o.option") + " <" + labels.getString("file") + ">",
                        "-f --" + labels.getString("f.option"),
                        "-r --" + labels.getString("r.option") + " <" + labels.getString("translate") + ">",
                    };
                    int maxLength = 0;
                    for (int i=0; i<helpFlags.length; i++){
                        maxLength = Math.max(maxLength, helpFlags[i].length());
                    }
                    maxLength += 1;
                	System.out.println(
                        labels.getString("tohtml") + " [-" + StringHelper.replace(oneLetterOptions, ":", "") + "] <" + labels.getString("files") + ">\n" +
                        labels.getString("purpose.message") + "\n" +
                        labels.getString("stdin.message") + "\n" +
                        " " + StringHelper.postpad(helpFlags[0] ,maxLength, ' ') + labels.getString("help.message") + "\n" +
                        " " + StringHelper.postpad(helpFlags[1] ,maxLength, ' ') + labels.getString("version.message") + "\n" +
                        " " + StringHelper.postpad(helpFlags[2] ,maxLength, ' ') + labels.getString("about.message") + "\n" +
                        " " + StringHelper.postpad(helpFlags[3] ,maxLength, ' ') + labels.getString("m.message") + "\n" +                        
                        " " + StringHelper.postpad(helpFlags[4] ,maxLength, ' ') + labels.getString("l.message") + "\n" +                        
                        " " + StringHelper.postpad(helpFlags[5] ,maxLength, ' ') + labels.getString("T.message") + "\n" +
   					    " " + StringHelper.postpad(helpFlags[6] ,maxLength, ' ') + labels.getString("i.message") + "\n" +
                        " " + StringHelper.postpad(helpFlags[7] ,maxLength, ' ') + labels.getString("t.message") + "\n" +
   				        " " + StringHelper.postpad(helpFlags[8] ,maxLength, ' ') + labels.getString("s.message") + "\n" +
                        " " + StringHelper.postpad(helpFlags[9] ,maxLength, ' ') + labels.getString("o.message") + "\n" +
                        " " + StringHelper.postpad(helpFlags[10] ,maxLength, ' ') + labels.getString("f.message") + "\n" +
                        " " + StringHelper.postpad(helpFlags[11] ,maxLength, ' ') + labels.getString("r.message") + "\n" 
                    );
                    System.exit(0);
                } break;
                case 2:{
                    // print out the version message
                    System.out.println(MessageFormat.format(labels.getString("version"), (Object[])new String[] {version}));
                    System.exit(0);
                } break;
                case 3:{
                    System.out.println(
                        labels.getString("tohtml") + " -- " + labels.getString("purpose.message") + "\n" +
                        MessageFormat.format(labels.getString("copyright"), (Object[])new String[] {"1999-2002", "Stephen Ostermiller (http://ostermiller.org/contact.pl?regarding=Syntax+Highlighting)"}) + "\n\n" +
                        labels.getString("license")
                    );
                    System.exit(0);
                } break;
                case 'm':{                    
                    toHTML.setMimeType(opts.getOptarg());
                } break;
                case 'l':{
                    toHTML.setLexerType(opts.getOptarg());
                } break;                
                case 'T':{
                    title = opts.getOptarg();
                } break;                              
                case 'i':{
                    toHTML.addIgnoreStyle(opts.getOptarg());
                } break;                            
                case 't':{
                    String template = opts.getOptarg();
                    File f = new File(opts.getOptarg());
                    if (f.exists()){
                        try {
                            toHTML.setTemplate(f.toURL().toString());
                        } catch (MalformedURLException mfue){
                            toHTML.setTemplate(template);
                        }
                    } else {
                        toHTML.setTemplate(template);
                    }
                } break;                           
                case 's':{
                    toHTML.setStyleSheet(opts.getOptarg());
                } break;                          
                case 'o':{
                    output = opts.getOptarg();
                } break;                         
                case 'f':{
                    force = true;
                } break;                        
                case 'r':{
                    java.util.StringTokenizer st = new java.util.StringTokenizer(opts.getOptarg(), "=", false);
                    if (st.countTokens() != 2){
                        System.err.println(labels.getString("unexpectedTranslation"));
                        System.exit(1);
                    }
                    toHTML.translateStyle(st.nextToken().trim(), st.nextToken().trim());
                } break;
                default:{
                    System.err.println(labels.getString("unexpectedArgument"));
                    System.exit(1);
                }
            }
        } 
        try {
		    if (args.length <= opts.getOptind()){              
                try {               
                    if (output == null) output = "--"; 
                    toHTML.setOutput(output, "out.html", force);
                    toHTML.setInput(new InputStreamReader(System.in));
                    toHTML.setTitle((title!=null)?(title):("HTML of System.in" + ((toHTML.getMimeType()==null)?"":(" ("+toHTML.getMimeType()+")"))));
                    toHTML.writeFullHTML();
                } catch (IOException e){
			        System.err.println(e.getMessage());
		        }
		    } else {
			    for (int i=opts.getOptind(); i<args.length; i++){
                    try {
                        String defaultName = args[i] + ".html";
                        String outputFileName = null;
                        outputFileName = (output == null)?defaultName:output;
                        toHTML.setOutput(outputFileName, defaultName, force);
                        toHTML.setExtFromFileName(args[i]);
                        toHTML.setDocNameFromFileName(args[i]);
                        toHTML.setInput(new FileReader(args[i]));
                        toHTML.setTitle((title!=null)?(title):("HTML of " +
                        getDocName(args[i]) + ((toHTML.getMimeType()==null)?"":(" ("+toHTML.getMimeType()+")"))));
                        toHTML.writeFullHTML();      
                    } catch (IOException e){
			            System.err.println(e.getMessage());
		            }
			    }
		    }
        } catch (InvocationTargetException x){
            System.err.println(x.getMessage());
        } catch (CompileException x){
            System.err.println("Compile Exception: " + x.getMessage());
        }            
	}
    
    private static String getDocName(String fileName){
        String docName = null;
        if (fileName != null) {
            docName = "";
            int dotIndex = fileName.lastIndexOf(".");
            int sepIndex = fileName.lastIndexOf(System.getProperty("file.separator"));
            int start = sepIndex + 1;
            if (sepIndex == -1){
                start = 0;
            } 
            int end = dotIndex;
            if (dotIndex == -1 || dotIndex < sepIndex){
                end = fileName.length();
            }
            docName = fileName.substring(start, end);
        }
        return docName;
    }
    
    /**
     * Sets the document name minus the path and extension
     *
     * @param inputFileName file name from which to extract the document name.
     */
    public void setDocNameFromFileName(String inputFileName){ 
        setDocName(getDocName(inputFileName));
    }

    
    /**
     * Sets the file extension to be anything after the last '.' in the
     * given file name.
     *
     * @param inputFileName file name from which to extract the extension.
     */
    public void setExtFromFileName(String inputFileName){             
        String extension = null;
        if (inputFileName != null) {
            extension = "";
            int dotIndex = inputFileName.lastIndexOf(".");
            int sepIndex = inputFileName.lastIndexOf(System.getProperty("file.separator"));
            if (dotIndex != -1 && dotIndex > sepIndex) {
                extension = inputFileName.substring(dotIndex+1, inputFileName.length());
            } 
        }
        setFileExt(extension);
    }
    
    private void setOutput(String output, String defaultName, boolean force) throws IOException {
        if (output.equals("--")){ 
            setOutput(new PrintWriter(System.out, true));
        } else {
            File outputFile = new File(defaultName);
            if (output != null){
                outputFile = new File(output);
                if (outputFile.isDirectory()){
                    int dirIndex = defaultName.lastIndexOf(System.getProperty("file.separator"));
                    if (dirIndex != -1){
                        defaultName = defaultName.substring(dirIndex+1, defaultName.length());
                    }
                    outputFile = new File (outputFile, defaultName);
                }
            }
            if (!force && outputFile.exists()){
                throw new IOException(
					MessageFormat.format(
						labels.getString("fileExists"),
						new Object[] {
                            outputFile.toString()
                        }
                    )
                );
            }
            setOutput(outputFile);
            if (getStyleSheet().equals("syntax.css")){
                putSyntaxCSS(outputFile.getParentFile());
            }
        }
    }
    
    private static boolean putSyntaxCSS(File directory) throws IOException {
        File f;
        if (directory==null) {
            f = new File("syntax.css");
        } else {
            f = new File(directory, "syntax.css");
        }
        if (f.exists()) return false;
        FileOutputStream out = new FileOutputStream(f);                
		InputStream in = ClassLoader.getSystemResourceAsStream("com/Ostermiller/Syntax/doc/syntax.css");
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer,0,read);
        }
        in.close();
        out.close(); 
        return true;       
    }
        
    /**
	 * Write the html encoded version of a document
	 * using syntax highlighting for Java.
     * <p>
     * The document produced is not a full html document but rather
     * an html fragment that may be inserted into a full document.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param in Data stream that needs to be formatted in html.
	 * @param out html document gets written here.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlifyJava(Reader in, PrintWriter out) throws IOException {        
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setLexer(new JavaLexer(in));
            toHTML.setOutput(out);
            toHTML.writeHTMLFragment();
        } catch (InvocationTargetException x){
            // can't happen
        }   
    } 
    
    /**
	 * Write the html encoded version of a document
	 * using syntax highlighting for C.
     * <p>
     * The document produced is not a full html document but rather
     * an html fragment that may be inserted into a full document.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param in Data stream that needs to be formatted in html.
	 * @param out html document gets written here.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlifyC(Reader in, PrintWriter out) throws IOException {        
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setLexer(new CLexer(in));
            toHTML.setOutput(out);
            toHTML.writeHTMLFragment();
        } catch (InvocationTargetException x){
            // can't happen
        }        
    }
    
    /**
	 * Write the html encoded version of a document
	 * using syntax highlighting for HTML using a simple coloring
     * algorithm.
     * <p>
     * The document produced is not a full html document but rather
     * an html fragment that may be inserted into a full document.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param in Data stream that needs to be formatted in html.
	 * @param out html document gets written here.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlifySimpleHTML(Reader in, PrintWriter out) throws IOException {        
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setLexer(new HTMLLexer(in));
            toHTML.setOutput(out);
            toHTML.writeHTMLFragment();
        } catch (InvocationTargetException x){
            // can't happen
        }     
    }  
    
    /**
	 * Write the html encoded version of a document
	 * using syntax highlighting for HTML using a complex coloring
     * algorithm.
     * <p>
     * The document produced is not a full html document but rather
     * an html fragment that may be inserted into a full document.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param in Data stream that needs to be formatted in html.
	 * @param out html document gets written here.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlifyComplexHTML(Reader in, PrintWriter out) throws IOException {        
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setLexer(new HTMLLexer1(in));
            toHTML.setOutput(out);
            toHTML.writeHTMLFragment();
        } catch (InvocationTargetException x){
            // can't happen
        }       
    }    
    
    /**
	 * Write the html encoded version of a document
	 * using no syntax highlighting.
     * <p>
     * The document produced is not a full html document but rather
     * an html fragment that may be inserted into a full document.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param in Data stream that needs to be formatted in html.
	 * @param out html document gets written here.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlifyPlain(Reader in, PrintWriter out) throws IOException {
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setLexer(new PlainLexer(in));
            toHTML.setOutput(out);
            toHTML.writeHTMLFragment();
        } catch (InvocationTargetException x){
            // can't happen
        }
    }  
	
	/**
	 * Write the html encoded version of a document
	 * using syntax highlighting for the given mime type.
     * <p>
     * Recognized mime-types include:
     * <ul>
     * <li>text/html
     * <li>text/x-java
     * <li>text/x-csrc
     * <li>text/x-chdr
     * <li>text/x-csrc
     * <li>text/x-c++hdr
     * </ul>    
     * If a mime type is not recognized, the html is written 
     * without syntax highlighting.
     * <p>
     * The document produced is not a full html document but rather
     * an html fragment that may be inserted into a full document.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param in Data stream that needs to be formatted in html
	 * @param out html document gets written here
	 * @param mimeType the mime type of the document to be used for syntax highlighting purposes.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlify(Reader in, PrintWriter out, String mimeType) throws IOException {
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setInput(in);
            toHTML.setOutput(out);
            toHTML.setMimeType(mimeType);
            toHTML.writeFullHTML();
        } catch (InvocationTargetException x){
            // can't happen
        } catch (CompileException x){
            // can't happen
        }
	}
    
    private void saveLexer(Lexer lexer){
        if (!lexers.containsKey(lexerType)){
            lexers.put(lexer.getClass().getName(), lexer);
        }
    }
    
    /** 
     * Return a lexer that is an instance of the given
     * class name.
     * If no appropriate lexer is found, return null.
     *
     * @return lexer for the class name.
     * @throws InvocationTargetException if the class could not be instantiated.
     */
    private Lexer getLexerFromClass(String lexerType) throws IOException, InvocationTargetException {
        Lexer lexer = null;
        if (lexerType != null){ 
            if (lexers.containsKey(lexerType)){
                lexer = (Lexer)lexers.get(lexerType);
                lexer.reset(in, 0, 0, 0);
            } else {       
                try {
                    lexer = (Lexer)(
                        Class.forName(lexerType)
                        .getDeclaredConstructor(new Class[] {Class.forName("java.io.Reader")})
                        .newInstance(new Object[] {in})
                    ); 
                    saveLexer(lexer);
                } catch (ClassNotFoundException x){
                    throw new InvocationTargetException(x);
                } catch (NoSuchMethodException x){
                    throw new InvocationTargetException(x);
                } catch (InstantiationException x){
                    throw new InvocationTargetException(x);
                } catch (IllegalAccessException x){
                    throw new InvocationTargetException(x);
                }
            }
        }  
        return lexer;      
    }
        
    /** 
     * Return an appropriate lexer for the current mime type.
     * If no appropriate lexer is found, return null.
     *
     * @return lexer for the current mime type.
     */
    private Lexer getLexerFromMime() throws IOException, InvocationTargetException {
        String lexerType = null;
        if (mimeType != null){
            if (registeredMimeTypes.containsKey(mimeType)){
                lexerType = (String)registeredMimeTypes.get(mimeType);
            } else {
                int slashIndex = mimeType.indexOf("/");
                if (slashIndex > 0){
                    String mainMime = mimeType.substring(0, slashIndex);
                    if (registeredMimeTypes.containsKey(mainMime)){
                        lexerType = (String)registeredMimeTypes.get(mainMime);
                    }               
                }
            }
        }
        return getLexerFromClass(lexerType);
    }
    
    /** 
     * Return an appropriate lexer for the current file extension.
     * If no appropriate lexer is found, return null.
     *
     * @return lexer for the current file extension.
     */
    private Lexer getLexerFromExt() throws IOException, InvocationTargetException { 
        String lexerType = null;
        if (fileExt != null){
            if (registeredFileExtensions.containsKey(fileExt.toLowerCase())){
                lexerType = (String)registeredFileExtensions.get(fileExt.toLowerCase());
            } 
        }
        if (lexerType == null) lexerType = "com.Ostermiller.Syntax.Lexer.PlainLexer";
        return getLexerFromClass(lexerType);
    }
    
    /**
	 * Write the html version of the output from the given lexer as a
	 * stand alone html document with the given title.  The html document
	 * will use the style sheet syntax.css.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param in contains the tokens that need to be html escaped.
	 * @param out html gets written here.
	 * @param title the title to be put on the html document.
     * @throws IOException if an I/O error occurs.
	 */	
	public static void htmlify(Reader in, PrintWriter out, String title, String mimeType) throws IOException {
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setInput(in);
            toHTML.setOutput(out);
            toHTML.setTitle(title);
            toHTML.setMimeType(mimeType);
            toHTML.writeFullHTML();
        } catch (InvocationTargetException x){
            // can't happen
        } catch (CompileException x){
            // can't happen
        }
	}
	
	/**
	 * Write the html version of a document
	 * using syntax highlighting for the given mime type.
     * <P>
     * Conversions between characters and bytes will be done
     * using the default character set for the system.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param in Data stream that needs to be formatted in html.
	 * @param out html document gets written here.
	 * @param mimeType the mime type of the document to be used for syntax highlighting purposes.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlify(InputStream in, PrintStream out, String mimeType) throws IOException {
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setInput(new InputStreamReader(in));
            toHTML.setOutput(new PrintWriter(out));
            toHTML.setMimeType(mimeType);
            toHTML.writeFullHTML(); 
        } catch (InvocationTargetException x){
            // can't happen
        } catch (CompileException x){
            // can't happen
        }       
	}
	
	/**
	 * Write the html version of the output from the given lexer. The 
	 * document is written without an html head or footer so that it 
	 * can be used as part of a larger html document.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param lexer contains the tokens that need to be html escaped.
	 * @param out html gets written here.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlify(Lexer lexer, PrintWriter out) throws IOException {
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setLexer(lexer);
            toHTML.setOutput(new PrintWriter(out));
            toHTML.writeHTMLFragment();
        } catch (InvocationTargetException x){
            // can't happen
        }
	}
	
	/**
	 * Write the html version of the output from the given lexer. The 
	 * document is written without an html head or footer so that it 
	 * can be used as part of a larger html document.
     * <P>
     * Conversions between characters and bytes will be done
     * using the default character set for the system.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param lexer contains the tokens that need to be html escaped.
	 * @param out html gets written here.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlify(Lexer lexer, PrintStream out) throws IOException {
        htmlify(lexer, new PrintWriter(out));
	}
	
	/**
	 * Write the html version of the output from the given lexer as a
	 * stand alone html document with the given title.  The html document
	 * will use the style sheet syntax.css.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param lexer contains the tokens that need to be html escaped.
	 * @param out html gets written here.
	 * @param title the title to be put on the html document.
     * @throws IOException if an I/O error occurs.
	 */	
	public static void htmlify(Lexer lexer, PrintWriter out, String title) throws IOException {
        try {
            ToHTML toHTML = new ToHTML();
            toHTML.setLexer(lexer);
            toHTML.setOutput(out);
            toHTML.setTitle(title);
            toHTML.writeFullHTML();
        } catch (InvocationTargetException x){
            // can't happen
        } catch (CompileException x){
            // can't happen
        }
	}
	
	/**
	 * Write the html version of the output from the given lexer as a
	 * stand alone html document with the given title.  The html document
	 * will use the style sheet syntax.css.
     * <P>
     * Conversions between characters and bytes will be done
     * using the default character set for the system.
     * <p>
     * This is a convenience method that creates an new ToHTML object.
	 * 
	 * @param lexer contains the tokens that need to be html escaped.
	 * @param out html gets written here.
	 * @param title the title to be put on the html document.
     * @throws IOException if an I/O error occurs.
	 */
	public static void htmlify(Lexer lexer, PrintStream out, String title) throws IOException {
		htmlify(lexer, new PrintWriter(out), title);
	}

	/**
	 * Write the string after escaping characters that would hinder 
	 * it from rendering in html.
	 * 
	 * @param text The string to be escaped and written
	 * @param out output gets written here
	 */
	public static void writeEscapedHTML(String text, PrintWriter out){
        boolean lastSpace = false;
		for (int i=0; i < text.length(); i++){
			char ch = text.charAt(i);
            switch(ch){
                case '<': {
                    out.print("&lt;");
                    lastSpace = false;
                    break;
                }
                case '>': {
                    out.print("&gt;");
                    lastSpace = false;
                    break;
                }
                case '&': {
                    out.print("&amp;");
                    lastSpace = false;
                    break;
                }
                case '"': {
                    out.print("&quot;");
                    lastSpace = false;
                    break;
                }
                default: {
					out.print(ch);
                    lastSpace = false;
                    break;
                }
            }
		}
	}
	
	/**
	 * Write the string after escaping characters that would hinder 
	 * it from rendering in html.
     * <P>
     * Conversions between characters and bytes will be done
     * using the default character set for the system.
	 * 
	 * @param text The string to be escaped and written
	 * @param out output gets written here
	 */
	public static void writeEscapedHTML(String text, PrintStream out){
		 writeEscapedHTML(text, new PrintWriter(out));		 
	}
}
