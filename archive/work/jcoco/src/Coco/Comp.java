/* PDT 17 Septermber 2002 */
/*--------------------------------------------
Trace output and options
  0: prints the states of the scanner automaton
  1: prints the First and Follow sets of all nonterminals
  2: prints the syntax graph of the productions
  3: traces the computation of the First sets
  4: prints the sets associated with ANYs and synchronisation sets                  // PDT g
  5: test grammar only                                                              // PDT q
  6: prints the symbol table (terminals, nonterminals, pragmas, statistics)         // PDT q
  7: prints a cross reference list of all syntax symbols
  8: prints statistics about the Coco run                                           // PDT q
  9: prints error messages in "merged" mode                                         // PDT q

Trace output can be switched on by the pragma
     $ { digit | letter }                                                           // PDT q
in the attributed grammar, or by command line switches
--------------------------------------------*/

package Coco;

import java.io.*;

class SemErrors extends ErrorStream
{
	void SemErr(int n, int line, int col) {
		String s;
		count++;
		switch (n) {
			case  1: s = "character set may not be empty"; break;
			case  2: s = "unacceptable constant value"; break;
			case  3: s = "a literal must not have attributes"; break;
			case  4: s = "this symbol kind is not allowed in a production"; break;
			case  5: s = "attribute mismatch between declaration and use of this symbol"; break;
			case  6: s = "undefined string in production"; break;
			case  7: s = "name declared twice"; break;
			case  8: s = "this symbol kind not allowed on left side of production"; break;
			case  9: s = "may not ignore CHR(0)"; break;
			case 11: s = "missing production found for grammar name"; break;
			case 12: s = "grammar symbol must not have attributes"; break;
			case 13: s = "a literal must not be declared with a structure"; break;
			case 14: s = "semantic action not allowed here"; break;
			case 15: s = "undefined name"; break;
			case 17: s = "name does not match grammar name"; break;
			case 18: s = "string literal may not extend over line end"; break;
			case 19: s = "missing end of previous semantic action"; break;
			case 20: s = "token might be empty"; break;
			case 21: s = "token must not start with an iteration"; break;
			case 22: s = "comment delimiters may not be structured"; break;
			case 23: s = "only terminals may be weak"; break;
			case 24: s = "literal tokens may not contain white space"; break;
			case 25: s = "comment delimiters must be 1 or 2 characters long"; break;
			case 26: s = "character set contains more than 1 character"; break;
			case 28: s = "bad escape sequence in string or character"; break;
			case 29: s = "literal tokens may not be empty"; break;
			case 30: s = "IGNORE CASE must appear earlier"; break;
			default: s = "Semantic error " + n; break;
		}
		StoreError(n, line, col, s);
	}
}

public class Comp {

	static ErrorStream ErrorHandler;

	public static void main (String[] args) {
		System.out.println("Coco/R for Java V1.14-1");
		System.out.println("based on Coco/R for Java V1.14-PDT (17 September 2002)");
		System.out.println("original by Hanspeter Moessenboeck (moessenboeck@ssw.uni-linz.ac.at)");
		System.out.println("modifications Pat Terry (p.terry@ru.ac.za)");

		String ATGName = null;                                                             // PDT q
        String outPathName = null;
        String inPathName = null;
        boolean dynamic = false;

		for (int i = 0; i < args.length; i++) {
            if("-o".equals(args[i])) {
                if(++i < args.length)
                    outPathName = args[i];
            }
            else if("-f".equals(args[i])) {
                if(++i < args.length)
                    inPathName = args[i];
            }
            else if("-p".equals(args[i])) {
                if(++i < args.length)
                    Tab.setPackageName(args[i]);
            }
            else if("-i".equals(args[i])) {
                dynamic = true;
            }
			else if (args[i].charAt(0) == '-')
                Tab.SetDDT(args[i]);
            else ATGName = args[i];
		}
		if (args.length == 0 || ATGName == null) {
			System.out.println("Usage: CocoR [-ACFGIMNSTXi] [-of <dir>] Grammar.ATG");   // PDT q
			System.out.println("Options:");
			System.out.println(" -A  trace automaton          -C  generate compiler driver");
			System.out.println(" -F  list first/follow sets   -G  print syntax graph");
			System.out.println(" -I  trace first sets         -J  print ANY and SYNC sets");
			System.out.println(" -M  merge errors with source -N  generate symbol names");
			System.out.println(" -P  print statistics         -S  list symbol table");
			System.out.println(" -T  test grammar only        -X  list cross reference table");
            System.out.println(" -i generate instance parser (vs. static)");
            System.out.println(" -f  directory the frame files are read from (default: atg file directory)");
            System.out.println(" -o  directory the generated files are written to (default: atg file directory)");
            System.out.println(" -p  package to generate into (default: name of \"COMPILER\" element)");
			System.out.println("With -C option Grammar.frame or Driver.frame needed in frame directory");
			
		} else {
            File atgFile = new File(ATGName);
            File outDir = outPathName != null ? new File(outPathName) : atgFile.getParentFile();
            File inDir = inPathName != null ? new File(inPathName) : atgFile.getParentFile();

            if (Tab.ddt[9]) ErrorHandler = new MergeErrors(); // Merge error messages in listing
            else ErrorHandler = new SemErrors();              // Error messages on StdOut

            Scanner.Init(atgFile, ErrorHandler);
            Tab.Init();
            DFA.Init(inDir, outDir, dynamic);                   // Scanner
            ParserGen.Init(atgFile, inDir, outDir, dynamic);    // Parser
            DriverGen.Init(inDir, outDir);                      // Driver
            Trace.Init(outDir);

			Parser.Parse();
			ErrorHandler.Summarize();
			Trace.out.flush();
		}
	}
}

class MergeErrors extends SemErrors
{
    private class ErrorRec {
        int line, col, num;
        String str;
        ErrorRec next;

        ErrorRec(int n, int l, int c, String s) {
            num = n; line = l; col = c; str = s; next = null;
        }

        void Display() {
            Trace.println("-- line " + line + " col " + col + ": " + str);
        }
    }

	ErrorRec first, last;
	boolean eof = false;

	MergeErrors() {
		first = null;
	}

	void StoreError(int n, int line, int col, String s) {
	// overrides parent method
		ErrorRec latest = new ErrorRec(n, line, col, s);
		if (first == null) first = latest; else last.next = latest;
		last = latest;
	}

	private String GetLine() {
		char ch, CR = '\r', LF = '\n';
		int l = 0;
		StringBuffer s = new StringBuffer();
		ch = (char) Buffer.read();
		while (ch != Buffer.eof && ch != CR && ch != LF) {
			s.append(ch); l++; ch = (char) Buffer.read();
		}
		eof = (l == 0 && ch == Buffer.eof);
		if (ch == CR) {  // check for MS-DOS
			ch = (char) Buffer.read();
			if (ch != LF && ch != Buffer.eof) Buffer.pos--;
		}
		return s.toString();
	}

	static private String Int(int n, int len) {
		String s = String.valueOf(n);
		int i = s.length(); if (len < i) len = i;
		int j = 0, d = len - s.length();
		char[] a = new char[len];
		for (i=0; i<d; i++) a[i] = ' ';
		for (j=0; i<len; i++) {a[i] = s.charAt(j); j++;}
		return new String(a, 0, len);
	}

	private void Display(String s, ErrorRec e) {
		Trace.print("**** ");
		for (int c = 1; c < e.col; c++)
			if (s.charAt(c-1) == '\t') Trace.print("\t"); else Trace.print(" ");
		Trace.println("^ " + e.str);
	}

	void Summarize()
    {
		if (count == 0) {
            super.Summarize(); return;
        }
		System.out.println("Errors detected - see listing.txt");
		String s;
		ErrorRec cur = first;
		Buffer.Set(0);
		int lnr = 1; s = GetLine();
		while (!eof) {
			Trace.println(Int(lnr, 4) + " " + s);
			while (cur != null && cur.line == lnr) {
				Display(s, cur); cur = cur.next;
			}
			lnr++; s = GetLine();
		}
		if (cur == null) return;
		Trace.println(Int(lnr, 4));
		while (cur != null) {
			Display(s, cur); cur = cur.next;
		}
	}
}

class Trace
{
	static PrintWriter out;

	static void Init(File dir) {
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(new File(dir, "listing.txt"))));
		}
		catch (IOException e) {
			Scanner.err.Exception("-- could not open listing.txt");
		}
	}

	static void print(String s) {
		out.print(s);
	}

	static void println(String s) {
		out.println(s);
	}

	static void println() {
		out.println();
	}
}
