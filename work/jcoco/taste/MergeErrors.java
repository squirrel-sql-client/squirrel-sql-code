package Taste;

// This demonstrates an ErrorStream subclass that merges error messages
// with source text

import java.io.*;

class ErrorRec {
	int line, col, num;
	String str;
	ErrorRec next;

	ErrorRec(int n, int l, int c, String s) {
		num = n; line = l; col = c; str = s; next = null;
	}

	void Display() {
		System.out.println("-- line " + line + " col " + col + ": " + str);
	}

}

class MergeErrors extends TasteErrors {

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
		System.out.print("**** ");
		for (int c = 1; c < e.col; c++)
			if (s.charAt(c-1) == '\t') System.out.print("\t"); else System.out.print(" ");
		System.out.println("^ " + e.str);
	}

	void Summarize(String dir) {
	// overrides parent method
		if (count == 0) {super.Summarize(dir); return;}
		String s;
		ErrorRec cur = first;
		Buffer.Set(0);
		int lnr = 1; s = GetLine();
		while (!eof) {
			System.out.println(Int(lnr, 4) + " " + s);
			while (cur != null && cur.line == lnr) {
				Display(s, cur); cur = cur.next;
			}
			lnr++; s = GetLine();
		}
		if (cur == null) return;
		System.out.println(Int(lnr, 4));
		while (cur != null) {
			Display(s, cur); cur = cur.next;
		}
	}

}
