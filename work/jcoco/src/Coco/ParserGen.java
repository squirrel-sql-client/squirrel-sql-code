package Coco;

import java.io.*;
import java.util.*;

class ParserGen {

	static final int maxSymSets = 128;  // max. nr. of symbol sets
	static final int maxTerm = 3;       // sets of size < maxTerm are enumerated
	static final int maxAlter = 5;      // more than MaxAlter alternatives handled with case
	static final char CR  = '\r';
	static final char LF  = '\n';
	static final char TAB = '\t';
	static final int EOF  = -1;

	static final int tErr = 0;          // error codes
	static final int altErr = 1;
	static final int syncErr = 2;

	static int maxSS;                   // number of symbol sets
	static int errorNr;                 // highest parser error number
	static int curSy;                   // symbol whose production is currently generated
	static StringBuffer err;            // generated parser error messages
    static File atgFile;                // attribute grammar file
    static File inDir;                  // directory the frame files are read from
    static File outDir;                 // directory generated files are written to
    static boolean dynamic;             // generate a static or dynamic (instance methods) parser
	static BitSet[] symSet;

	static Reader fram;                 // parser frame file            Java 1.1
	static PrintWriter gen;             // generated parser source file Java 1.1

	static private String Int(int n, int len) {
		String s = String.valueOf(n);
		int i = s.length(); if (len < i) len = i;
		int j = 0, d = len - s.length();
		char[] a = new char[len];
		for (i=0; i<d; i++) a[i] = ' ';
		for (j=0; i<len; i++) {a[i] = s.charAt(j); j++;}
		return new String(a, 0, len);
	}

	private static void Indent(int n) {
		for (int i=1; i<=n; i++) gen.print('\t');
	}

	private static int Alternatives (int p) {
		int i = 0;
		while (p > 0) {
			i++; p = Tab.Node(p).p2;
		}
		return i;
	}

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
			Scanner.err.Exception("-- error reading Parser.frame");
		}
		Scanner.err.Exception("-- incomplete or corrupt Parser.frame");
	}

	private static void CopySourcePart(Position pos, int indent) {
		// Copy text described by pos from atg to gen
		int ch, lastCh, nChars, i, col;
		if (pos != null) {
			Buffer.Set(pos.beg);
			ch = ' ';
			nChars = pos.len;
			col = pos.col - 1;
			while (nChars > 0 && (ch == ' ' || ch == '\t') ) {
			// skip over leading white space
				ch = Buffer.read(); nChars--; col++;
			}
			Indent(indent);
			loop:
			for (;;) {
				while (ch == CR || ch == LF) {
					// blank lines with correct number of leading blanks
					gen.println();
					lastCh = ch;
					if (nChars > 0) { ch = Buffer.read(); nChars--;  } else break loop;
					if ((ch == '\n') && (lastCh == '\r')) { // must be MS-DOS
						if (nChars > 0) { ch = Buffer.read(); nChars--; } else break loop;
					}
					if (ch != CR && ch != LF) { // there must be something on this line
						Indent(indent);
						i = col - 1;
						while ((ch == ' ' || ch == '\t') && i > 0) {
						// skip at most "col - 1" white space at line start
							if (nChars > 0) { ch = Buffer.read(); nChars--; } else break loop;
							i--;
						}
					}
				}
				i = 0;
				while (ch == ' ') {
					if (nChars > 0) { ch = Buffer.read(); nChars--; } else break loop;
					i++;
				}
				if (ch != CR && ch != LF && ch != EOF)  {
					for (int j = 1; j <= i; j++) gen.print(' ');
					gen.print((char) ch);
					if (nChars > 0) { ch = Buffer.read(); nChars--; } else break loop;
				}
			}
			if (indent > 0) gen.println();
		}
	}

/* old was
	private static void CopySourcePart(Position pos, int indent) {
		// Copy text described by pos from atg to gen
		int ch, nChars, i;
		if (pos != null) {
			Buffer.Set(pos.beg); ch = Buffer.read(); nChars = pos.len - 1;
			Indent(indent);
			loop:
			while (nChars >= 0) {
				while (ch==CR) {
					gen.println(); Indent(indent);
					ch = Buffer.read(); nChars--;
					for (i=1; i<=pos.col && ch<=' '; i++) { // skip blanks at beginning of line
						ch = Buffer.read(); nChars--;
					}
					if (i <= pos.col) pos.col = i - 1; // heading TABs => not enough blanks
					if (nChars < 0) break loop;
				}
				gen.print((char)ch);
				ch = Buffer.read(); nChars--;
			}
			if (indent > 0) gen.println();
		}
	}
*/

	private static void PrintTermName(int n) {
		if (Tab.sy[n].symName == null) gen.print(n);
		else gen.print("SYM." + Tab.sy[n].symName);
	}

	private static void GenErrorMsg(int errTyp, int errSym) {
		errorNr++;
		String oldname = Tab.Sym(errSym).name; // .replace('"', '\\\"'); //PDT Wed  01-19-00
		StringBuffer name = new StringBuffer();
		for (int i = 0; i < oldname.length(); i++) {
			if (oldname.charAt(i) == '\'' ||
				oldname.charAt(i) == '\"' ||
				oldname.charAt(i) == '\\') name.append('\\');
			name.append(oldname.charAt(i));
		}
		err.append("\t\t\tcase " + errorNr + ": {s = \"");
		switch (errTyp) {
			case tErr: {err.append(name + " expected"); break;}
			case altErr: {err.append("invalid " + name); break;}
			case syncErr: {err.append("this symbol not expected in " + name); break;}
		}
		err.append("\"; break;}\n");
	}

	private static int NewCondSet(BitSet s) {
		for (int i=1; i<=maxSS; i++) // skip symSet[0] (reserved for union of SYNC sets)
			if (s.equals(symSet[i])) return i;
		maxSS++; Tab.Assert(maxSS <= maxSymSets, 5);
		symSet[maxSS] = (BitSet) s.clone();
		return maxSS;
	}

	private static void GenCond(BitSet s) {
		int n = Sets.Size(s);
		if (n==0) gen.print("false"); // should never happen
		else if (n <= maxTerm)
			for (int i=0; i<=Tab.maxT; i++) {
				if (s.get(i)) {
					gen.print("t.kind == "); PrintTermName(i);
					n--; if (n > 0) gen.print(" || ");
				}
			}
		else gen.print("StartOf(" + NewCondSet(s) + ")");
	}

	private static void PutCaseLabels(BitSet s) {
		for (int i=0; i<=Tab.maxT; i++)
			if (s.get(i)) { 
				gen.print("case "); 
				PrintTermName(i);
				gen.print(": ");
			}
	}

	private static void GenCode (int p, int indent, BitSet checked) {
		GraphNode n2;
		int p2;
		BitSet s1, s2;
		while (p > 0) {
			GraphNode n = Tab.Node(p);
			switch (n.typ) {
				case Tab.nt: {
					Indent(indent);
					Symbol sym = Tab.Sym(n.p1);
					if (n.retVar!=null) gen.print(n.retVar + " = ");
					gen.print(sym.name + "(");
					CopySourcePart(n.pos, 0);
					gen.println(");");
					break;
				}
				case Tab.t: {
					Indent(indent);
					if (checked.get(n.p1)) gen.println("Get();");
					else {
						gen.print("Expect("); PrintTermName(n.p1); gen.println(");");
					}
					break;
				}
				case Tab.wt: {
					Indent(indent);
					s1 = Tab.Expected(Math.abs(n.next), curSy);
					s1.or(Tab.Set(0));
					gen.print("ExpectWeak("); PrintTermName(n.p1);
					gen.println(", " + NewCondSet(s1) + ");");
					break;
				}
				case Tab.any: {
					Indent(indent);
					gen.println("Get();");
					break;
				}
				case Tab.eps: break; // nothing
				case Tab.sem: {
					CopySourcePart(n.pos, indent);
					break;
				}
				case Tab.sync: {
					Indent(indent);
					GenErrorMsg(syncErr, curSy);
					s1 = (BitSet) Tab.Set(n.p1).clone();
					gen.print("while (!("); GenCond(s1);
					gen.println(")) {Error(" + errorNr + "); Get();}");
					break;
				}
				case Tab.alt: {
					s1 = Tab.First(p);
					boolean equal = s1.equals(checked);
					int alts = Alternatives(p);
					if (alts > maxAlter)
						{Indent(indent); gen.println("switch (t.kind) {");}
					p2 = p;
					while (p2 != 0) {
						n2 = Tab.Node(p2);
						s1 = Tab.Expected(n2.p1, curSy);
						Indent(indent);
						if (alts > maxAlter) {PutCaseLabels(s1); gen.println("{");}
						else if (p2==p) {gen.print("if ("); GenCond(s1); gen.println(") {");}
						else if (n2.p2==0 && equal) gen.println("} else {");
						else {gen.print("} else if ("); GenCond(s1); gen.println(") {");}
						s1.or(checked);
						GenCode(n2.p1, indent + 1, s1);
						if (alts > maxAlter) {
							Indent(indent); gen.println("\tbreak;");
							Indent(indent); gen.println("}");
						}
						p2 = n2.p2;
					}
					Indent(indent);
					if (equal) gen.println("}");
					else {
						GenErrorMsg(altErr, curSy);
						if (alts > maxAlter) {
							gen.println("default: Error(" + errorNr + ");");
							Indent(indent); gen.println("}");
						} else {
							gen.println("} else Error(" + errorNr + ");");
						}
					}
					break;
				}
				case Tab.iter: {
					Indent(indent);
					n2 = Tab.Node(n.p1);
					gen.print("while (");
					if (n2.typ==Tab.wt) {
						s1 = Tab.Expected(Math.abs(n2.next), curSy);
						s2 = Tab.Expected(Math.abs(n.next), curSy);
						gen.print("WeakSeparator(");
						PrintTermName(n2.p1); gen.print(", " + NewCondSet(s1) + ", "
							+ NewCondSet(s2) + ")");
						s1 = new BitSet(); // for inner structure
						if (n2.next > 0) p2 = n2.next; else p2 = 0;
					} else {
						p2 = n.p1; s1 = Tab.First(p2); GenCond(s1);
					}
					gen.println(") {");
					GenCode(p2, indent + 1, s1);
					Indent(indent); gen.println("}");
					break;
				}
				case Tab.opt:
					s1 = Tab.First(n.p1);
					if (!checked.equals(s1)) {
						Indent(indent);
						gen.print("if ("); GenCond(s1); gen.println(") {");
						GenCode(n.p1, indent+1, s1);
						Indent(indent); gen.println("}");
					} else GenCode(n.p1, indent, checked);
					break;
			}
			if (n.typ!=Tab.eps && n.typ!=Tab.sem && n.typ!=Tab.sync) checked = new BitSet();
			p = n.next;
		}
	}

	private static void GenCodePragmas() {
		for (int i=Tab.maxT+1; i<=Tab.maxP; i++) {
			gen.println("\t\t\tif (t.kind == " + i + ") {");
			CopySourcePart(Tab.Sym(i).semPos, 4);
			gen.println("\t\t\t}");
		}
	}

	private static void GenProductions() {
		for (curSy=Tab.firstNt; curSy<=Tab.lastNt; curSy++) {
			Symbol sym = Tab.Sym(curSy);
			gen.print("\tprivate ");
            if(!dynamic)
                gen.print("static ");
			if (sym.retType==null)
                gen.print("final void ");
            else
                gen.print(sym.retType + " ");
			gen.print(sym.name + "(");
			CopySourcePart(sym.attrPos, 0);
			gen.println(") {");
			if (sym.retVar!=null) gen.println("\t\t" + sym.retType + " " + sym.retVar + ";");
			CopySourcePart(sym.semPos, 2);
			GenCode(sym.struct, 2, new BitSet());
			if (sym.retVar!=null) gen.println("\t\treturn " + sym.retVar + ";");
			gen.println("\t}"); gen.println();
		}
	}

	private static void InitSets() {
		symSet[0] = Tab.Set(0);
		for (int i=0; i<=maxSS; i++) {
			gen.print("\t{");
			BitSet s = symSet[i];
			for (int j=0; j<=Tab.maxT; j++) {
				if (s.get(j)) gen.print("T,"); else gen.print("x,");
				if ((j+1) % 60 == 0) {gen.println(); gen.print("\t ");}
				else if (j%4==3) gen.print(" ");
			}
			if (i < maxSS) gen.println("x},"); else gen.println("x}");
		}
	}

	static String GetString(int beg, int end) {
		StringBuffer s = new StringBuffer();
		int oldPos = Buffer.pos;
		Buffer.Set(beg);
		while (beg < end) {s.append((char)Buffer.read()); beg++;}
		Buffer.Set(oldPos);
		return s.toString();
	}

	static void WriteParser(boolean withNames) {
		//Symbol root = Tab.Sym(Tab.gramSy);

		try {
            fram = new BufferedReader(new FileReader(new File(inDir, "Parser.frame")));
        }
		catch (IOException e) {
			Scanner.err.Exception("-- cannot open Parser.frame");
		}
        File packageDir = null;
		try {
            packageDir = Tab.getPackageDir(outDir);
			gen = new PrintWriter(
                  new BufferedWriter(
                        new FileWriter(new File(packageDir, "Parser.java"))));
        }
		catch (IOException e) {
			Scanner.err.Exception("-- cannot generate parser file");
		}
		if (withNames) Tab.AssignNames();
		err = new StringBuffer(2048);
		for (int i=0; i<=Tab.maxT; i++) GenErrorMsg(tErr, i);

		gen.println("package " + Tab.getPackageName() + ";");
        for(Iterator it=Tab.importList.iterator(); it.hasNext();) {
            gen.print("import "); gen.print((String)it.next()); gen.println(";");
        }
		CopyFramePart("-->constants");
		gen.println("\tprivate static final int maxT = " + Tab.maxT + ";");
		gen.println("\tprivate static final int maxP = " + Tab.maxP + ";");
		CopyFramePart("-->declarations"); CopySourcePart(Tab.semDeclPos, 0);
		CopyFramePart("-->pragmas"); GenCodePragmas();
		CopyFramePart("-->productions"); GenProductions();
		CopyFramePart("-->parseRoot"); gen.println("\t\t" + Tab.Sym(Tab.gramSy).name + "();");
		CopyFramePart("-->initialization"); InitSets();
		CopyFramePart("-->ErrorStream");
		gen.close();

		try {
			gen = new PrintWriter(
                  new BufferedWriter(
                        new FileWriter(new File(packageDir, "ErrorStream.java"))));
        }
		catch (IOException e) {
			Scanner.err.Exception("-- cannot generate error stream file");
		}
		gen.println("package " + Tab.getPackageName() + ";");
		CopyFramePart("-->errors");
		String es = err.toString();

		for (int j = 0; j < es.length(); j++) { // PDT
			if (es.charAt(j)!='\n') gen.print(es.charAt(j)); else gen.println();
		}
		CopyFramePart("$$$");

		if (withNames) { // generate class of terminal names
			gen.println();
			gen.println("class SYM {");
			for (int n = 0; n <= Tab.maxT; n++) {
				gen.println("\tstatic final int " + Tab.sy[n].symName + " = " + n + ";");
			}
			gen.println("}");
		}
		gen.close();
	}

	static void WriteStatistics() {
		Trace.println();
		Trace.println("Statistics:");
		Trace.println("-----------");
		Trace.println(Int(Tab.maxT+1, 4) + " terminals     - Max " + Tab.maxTerminals);
		Trace.println(Int(Tab.lastNt - Tab.firstNt + 1, 4) + " non-terminals - Max " + (Tab.maxSymbols - Tab.maxT - 1));
		Trace.println(Int(Tab.maxSymbols - Tab.firstNt+Tab.maxT + 1, 4) + " symbols       - Max " + Tab.maxSymbols);
		Trace.println(Int(Tab.nNodes, 4) + " graph nodes   - Max " + Tab.maxNodes);
		Trace.println(Int(maxSS - 1, 4) + " symbol sets   - Max " + maxSymSets);
		Trace.println(Int(Tab.maxC, 4) + " char sets     - Max " + Tab.maxClasses);
		Trace.println();
	}

    static void Init(File _atgFile, File _inDir, File _outDir, boolean _dynamic)
    {
        atgFile = _atgFile;
        inDir = _inDir;
        outDir = _outDir;
        dynamic = _dynamic;
		errorNr = -1;
		symSet = new BitSet[maxSymSets];
		maxSS = 0; // symSet[0] reserved for union of all SYNC sets
	}

}
