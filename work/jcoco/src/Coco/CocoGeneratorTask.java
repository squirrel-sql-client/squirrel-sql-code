/*
 * Coco.CocoGeneratorTask
 * 
 * created by cse, 23.09.2002 16:01:52
 */
package Coco;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * an Ant task which invokes the COCO/R Java generator.
 */
public class CocoGeneratorTask extends Task
{
    ErrorStream ErrorHandler;

    private String outDirName;
    private String frameDirName;
    private String atgName;
    private String packageName;
    private String switches;

    /**
     * execute the task
     * @throws BuildException
     */
    public void execute() throws BuildException
    {
        if(outDirName == null)
            throw new BuildException("attribute outdir must be set");
        if(frameDirName == null)
            throw new BuildException("attribute framedir must be set");
        if(atgName == null)
            throw new BuildException("attribute atg must be set");

        boolean instance = false;
        if(switches != null) {
            instance = switches.indexOf('i') >= 0;
            Tab.SetDDT(switches);
        }
        if(packageName != null)
            Tab.setPackageName(packageName);

        File basedir = getProject().getBaseDir();
        File atgFile = new File(basedir, atgName);
        File outDir = outDirName != null ? new File(basedir, outDirName) : atgFile.getParentFile();
        File inDir = frameDirName != null ? new File(basedir, frameDirName) : atgFile.getParentFile();

        if (Tab.ddt[9]) ErrorHandler = new MergeErrors(); // Merge error messages in listing
        else ErrorHandler = new SemErrors();              // Error messages on StdOut

        try {
            Scanner.Init(atgFile, ErrorHandler);
            Tab.Init();
            DFA.Init(inDir, outDir, instance);                   // Scanner
            ParserGen.Init(atgFile, inDir, outDir, instance);    // Parser
            DriverGen.Init(inDir, outDir);                      // Driver
            Trace.Init(outDir);

            Parser.Parse();
            ErrorHandler.Summarize();
            Trace.out.flush();
        }
        catch(RuntimeException r) {
            throw new BuildException(r.getMessage());
        }
    }

    /**
     * set the ATG file to be processed
     * @param atgPath the full path to the ATG file
     */
    public void setAtg(String atgPath)
    {
        atgName = atgPath;
    }

    /**
     * set the directory generated output should go to. The directory will be created
     * if it does not exist
     * @param dir the directory path
     */
    public void setOutdir(String dir)
    {
        outDirName = dir;
    }

    /**
     * set the directory frame files are rad from. The directory will be created
     * if it does not exist
     * @param dir the directory path
     */
    public void setFramedir(String dir)
    {
        frameDirName = dir;
    }

    /**
     * set the package for the generated classes. The package directory hierarchy
     * is assumed under {@link #setOutdir outdir}. It will be created if needed.<br>
     * Defaults to the name of the <em>COMPILER</em> element
     * @param packageName the full package name, separated by dots
     */
    public void setPackage(String packageName)
    {
        this.packageName = packageName;
    }

    /**
     * set generator switches. Recognized switches are:
     * <ul>
     * <li>-A  trace automaton</li>
     * <li>-C  generate compiler driver</li>
     * <li>-F  list first/follow sets</li>
     * <li>-G  print syntax graph</li>
     * <li>-I  trace first sets</li>
     * <li>-J  print ANY and SYNC sets</li>
     * <li>-M  merge errors with source</li>
     * <li>-N  generate symbol names</li>
     * <li>-P  print statistics</li>
     * <li>-S  list symbol table</li>
     * <li>-T  test grammar only</li>
     * <li>-X  list cross reference table</li>
     * <li>-i generate instance parser (vs. static)</li>
     */
    public void setSwitches(String switches)
    {
        this.switches = switches;
    }
}
