package Taste;

import java.awt.*;
import java.io.*;

class TasteErrors extends ErrorStream {

	void SemErr(int n, int line, int col) {
		String s;
		count++;
		switch (n) {
			case 0: {s = "invalid character"; break;}
			case 1: {s = "Identifier redeclared"; break;}
			case 2: {s = "Undeclared identifier"; break;}
			case 3: {s = "Block identifier mismatch"; break;}
			case 4: {s = "Integer type expected"; break;}
			case 5: {s = "Incompatible types"; break;}
			case 6: {s = "Boolean type expected"; break;}
			case 7: {s = "Variable expected"; break;}
			case 8: {s = "Invalid procedure call"; break;}
			case 9: {s = "Program too long"; break;}
			default: {s = "Semantic error " + n; break;}
		}
		StoreError(n, line, col, s);
	}

}

class Comp {

	static ErrorStream ErrorHandler;

// Portability - use the following for Java 1.0
//	private static DataInputStream getInput() { // Java 1.0
// Portability - use the following for Java 1.0
//	private static BufferedReader getInput() { // Java 1.1

	private static BufferedReader getInput() { // Java 1.1
		FileDialog d = new FileDialog(new Frame("Taste"), "Select data file");
		d.show();
		String data = d.getFile();
		if (data == null) return null;
		data = d.getDirectory() + data;
		try {

// Portability - use the following for Java 1.0
//			return new DataInputStream(new FileInputStream(data)); // Java 1.0
// Portability - use the following for Java 1.1
//			return new BufferedReader(new FileReader(data)); // Java 1.1

			return new BufferedReader(new FileReader(data)); // Java 1.1

		} catch (IOException e) {
		System.out.println("--- error accessing file " + data);
		return null;
		}
	}

	public static void main (String args[]) {
		String file, dir;

// Portability - use the following for Java 1.0
//		DataInputStream data; // Java 1.0
// Portability - use the following for Java 1.1
//		BufferedReader data; // Java 1.1

		BufferedReader data; // Java 1.1

		if (args.length == 0) {
			FileDialog d = new FileDialog(new Frame("Taste"), "Select source file");
			d.show();
			file = d.getFile(); dir = d.getDirectory();
		} else {
			file = args[0]; dir = "";
		}
		if (file != null) {
			// ErrorHandler = new MergeErrors(); // Merge error messages in listing
			// ErrorHandler = new ErrorStream(); // Very rudimentary
			ErrorHandler = new TasteErrors(); // Error messages reported to StdOut
			file = dir + file;
			Scanner.Init(file, ErrorHandler);
			Parser.Parse();
			if (Parser.Successful()) {
				if (args.length == 0) data = getInput();

// Portability - use the following for Java 1.0
//				else data = new DataInputStream(System.in); // Java 1.0
// Portability - use the following for Java 1.1
//				else data = new BufferedReader(new InputStreamReader(System.in)); // Java 1.1

				else data = new BufferedReader(new InputStreamReader(System.in)); // Java 1.1

				if (data != null) TC.Interpret(data);
			} else ErrorHandler.Summarize(dir);
		}
		System.exit(0);
	} 

}
