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
package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.Scanner.Buffer;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.Scanner.SBuffer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * This test class is used, to make clear, how the scanner works :-)
 * @author Stefan Willinger
 *
 */
public class ScannerTest {
	private Scanner classUnderTest;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * Ensure, that a comment can contain non ASCII characters.
	 * The previous behavior was to add an "invalid character in source file" error for each character greater than the ASCII range ('\u007f').
	 */
	@Test
	public void testCommentWithNonASCII() {
		SBuffer mockBuffer = new SBuffer("/*Ä*/ select");
		
		ErrorStream mockErrorStream = Mockito.mock(ErrorStream.class);
		classUnderTest = new Scanner(mockBuffer, mockErrorStream);
		Token token = classUnderTest.Scan();
		
		assertEquals("select", token.str);

		verifyZeroInteractions(mockErrorStream);
	}
	
	
	/**
	 * Ensure, that a statement can contain a non ASCII character
	 */
	@Test
	public void testStatementWithNonASCII() {
		SBuffer mockBuffer = new SBuffer("select sysdate as Ä from dual");
		
		ErrorStream mockErrorStream = Mockito.mock(ErrorStream.class);
		classUnderTest = new Scanner(mockBuffer, mockErrorStream);
		Token token;

		token = classUnderTest.Scan();
		assertEquals("select", token.str);
		assertEquals(ParsingConstants.KW_SELECT, token.kind);
		
		token = classUnderTest.Scan();
		assertEquals("sysdate", token.str);
		assertEquals(1, token.kind);
		
		token = classUnderTest.Scan();
		assertEquals("as", token.str);
		assertEquals(ParsingConstants.KW_AS, token.kind);
		
		token = classUnderTest.Scan();
		assertEquals("Ä", token.str);
		assertEquals(1, token.kind);
		
		token = classUnderTest.Scan();
		assertEquals("from", token.str);
		assertEquals(ParsingConstants.KW_FROM, token.kind);
		
		token = classUnderTest.Scan();
		assertEquals("dual", token.str);
		assertEquals(1, token.kind);
		
		verifyZeroInteractions(mockErrorStream);
	}
	
	
	@Test
	public void testTokenKind_Select() {
		SBuffer mockBuffer = new SBuffer("select");
		
		ErrorStream mockErrorStream = Mockito.mock(ErrorStream.class);
		classUnderTest = new Scanner(mockBuffer, mockErrorStream);
		Token token = classUnderTest.Scan();
		
		assertEquals("select", token.str);
		assertEquals(ParsingConstants.KW_SELECT, token.kind);

		verifyZeroInteractions(mockErrorStream);
	}
	
	/**
	 * Just for playing with the Scanner to understand it.
	 */
	@Test
	public void testTokenKind_WithNumbers() {
		SBuffer mockBuffer = new SBuffer("select8");
		
		ErrorStream mockErrorStream = Mockito.mock(ErrorStream.class);
		classUnderTest = new Scanner(mockBuffer, mockErrorStream);
		Token token = classUnderTest.Scan();
		
		assertEquals("select8", token.str);
		// Maybe, 1 means "No Keyword"
		assertEquals(1, token.kind);

		verifyZeroInteractions(mockErrorStream);
	}
	
	/**
	 * Ensure, that the open bracket is read correctly.
	 */
	@Test
	public void testOpenBracket() {
		SBuffer mockBuffer = new SBuffer("(");
		
		ErrorStream mockErrorStream = Mockito.mock(ErrorStream.class);
		classUnderTest = new Scanner(mockBuffer, mockErrorStream);
		Token token = classUnderTest.Scan();
		
		assertEquals("(", token.str);
		assertEquals(ParsingConstants.KIND_OPENING_BRAKET, token.kind);

		verifyZeroInteractions(mockErrorStream);
	}
	
	@Test
	public void testSemikolon() {
		SBuffer mockBuffer = new SBuffer(";");
		
		ErrorStream mockErrorStream = Mockito.mock(ErrorStream.class);
		classUnderTest = new Scanner(mockBuffer, mockErrorStream);
		Token token = classUnderTest.Scan();
		
		assertEquals(";", token.str);
		assertEquals(6, token.kind);

		verifyZeroInteractions(mockErrorStream);
	}

	
	
	/**
	 * For printing out the characters corresponding to the start array of the scanner.
	 * Just for understanding this array.
	 */
	public static void main(String[] args) {
		// This is a copy of the field at the scanner.
		int[] start = {
		    ParsingConstants.KW_AS,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		     0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		     0,  1,  4,  1,  1,  0,  0,  ParsingConstants.KIND_OPENING_BRAKET,  ParsingConstants.KW_UNION, 22, ParsingConstants.KW_ALL, ParsingConstants.KW_INSERT, ParsingConstants.KW_DISTINCT, ParsingConstants.KW_UPDATE,  2, ParsingConstants.KIND_EQUALS,
		     ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT,  ParsingConstants.KW_EXCEPT, ParsingConstants.KW_SET,  ParsingConstants.KW_INTERSECT, ParsingConstants.KW_INTO, ParsingConstants.KW_MINUS, ParsingConstants.KW_FROM,  0,
		     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
		     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
		     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
		     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  1,  1,  1,
		     0};
		 
		 for (int i = 0; i < start.length; i++) {
			System.out.println(((char) i) + "=" + start[i]);
			 
		}
	}
	

}
