package Decl;

import java.awt.*;

public class Comp {

	static ErrorStream ErrorHandler;

	public static void main (String[] args) {
		if (args.length == 0) {
			System.out.println("no input file specified");
		} else {
			String file = args[0];
			System.out.println("C Declarations");
			ErrorHandler = new ErrorStream();
			Scanner.Init(file, ErrorHandler);
			Parser.Parse();
			ErrorHandler.Summarize(file);
		}
	}

}
