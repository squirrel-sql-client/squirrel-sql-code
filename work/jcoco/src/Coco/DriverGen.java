package Coco;

import java.io.*;

class DriverGen {

	static final char CR  = '\r';
	static final char LF  = '\n';
	static final int EOF  = -1;
    static File inDir;              // input directory
    static File outDir;             // output directory

	static Reader fram;                 // driver frame file            Java 1.1
	static PrintWriter gen;             // generated driver source file Java 1.1

	private static void CopyFramePart(String stop) {
		int last = 0;
		int startCh = stop.charAt(0);
		int endOfStopString = stop.length() - 1;
		try {
			int ch = fram.read();
			while (ch!=EOF)
				if (ch==startCh) {
					int i = 0;
					do {
						if (i==endOfStopString) return; // stop[0..i] found
						ch = fram.read(); i++;
					} while (ch==stop.charAt(i));
					// stop[0..i-1] found; continue with last read character
					gen.print(stop.substring(0, i));
				} else if (ch==LF) { if (last!=CR)gen.println(); last = ch; ch = fram.read();
				} else if (ch==CR) { gen.println(); last = ch; ch = fram.read();
				} else {
					gen.print((char)ch); last = ch; ch = fram.read();
				}
		} catch (IOException e) {
			Scanner.err.Exception("-- error reading Driver.frame");
		}
		Scanner.err.Exception("-- incomplete or corrupt Driver.frame");
	}

	static void WriteDriver() {
		Symbol root = Tab.Sym(Tab.gramSy);
  		File f = new File(inDir, root.name + ".frame");
  		if (!f.exists()) {
    		System.out.print(" (cannot find driver frame file " + f.getPath() + ")");
    		f = new File("Driver.frame");
    		if (!f.exists()) 
    			Scanner.err.Exception(" -- cannot locate driver frame " + f.getPath());
  		}
		try {
            fram = new BufferedReader(new FileReader(f));
        }
		catch (IOException e) {
			Scanner.err.Exception("-- cannot open " + f.getPath());
		}
		try {
            File packageDir = Tab.getPackageDir(outDir);
			gen = new PrintWriter(new BufferedWriter(new FileWriter(new File(packageDir, "Comp.java"))));
        }
		catch (IOException e) {
			Scanner.err.Exception("-- cannot create driver file");
		}
		CopyFramePart("-->package"); gen.print(Tab.getPackageName());
		CopyFramePart("$$$");
		gen.close();
	}

	static void Init(File _inDir, File _outDir)
    {
        inDir = _inDir;
		outDir = _outDir;
	}
}
