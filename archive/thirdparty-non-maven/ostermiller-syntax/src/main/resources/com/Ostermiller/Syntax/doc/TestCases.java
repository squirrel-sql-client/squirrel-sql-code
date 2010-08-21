if/* First we will test the comments */if
if/* There should be no if that is commented */if
if// End of line comment
if/** Documentation comment */if
if/* multiple
 line
          comment
   */if
if/**
 multiple
 line
    doc comment
   */if
if/* 
 * nice
 * looking
 * comment
 */if
if// /* end of line comment */ more comment
if// /* end of line comment
if// end of line comment */ more comment
if/*back to*//*back*/if
if/*back to*///back
if/**/if
if/***/if
if/****/if
if/*****/if
if/******/if
if/* */if
if/* **/if
if/* ***/if
if/* ****/if
if/** */if
if/** **/if
if/** ***/if
if/** ****/if
if/*** */if
if/**** */if
if/* * */if
if/* ** */if
if/** * */if
if/** ** */if
if/*  * */if
if/*  ** */if
if/**  * */if
if/**  ** */if
if/* * ** *** **** / // /// //// */if
if/** * ** *** **** / // /// //// */if
if/*/*/if
if/*/**/if
if/*/***/if
if/*/****/if
if/*//*/if
if/*//**/if
if/*//***/if
if/*//****/if
if/*\*/if
if/*\\*/if
if// the next line should not be a comment \
if

/* java keywords */
abstract
boolean
break
byte
case
catch
char
class
const
continue
default
do
double
else
extends
final
finally
float
for
goto
if
implements
import
instanceof
int
interface
long
native
new
package
private
protected
public
return
short
static
strictfp
super
switch
synchronized
this
throw
throws
transient
try
void
volatile
while

/* Identifiers */
/**/identifier/**/
/**/a0b1c2d3e4f5g/**/
/**/h6i7j8k9l0mno/**/
/**/pqrstuvwxyz/**/
/**/ABCDEFGHIJK/**/
/**/LMNOPQRSTUVWXYZ/**/
/**/dollar$ign/**/
/**/$tart_with_dollar/**/
/**/under_score/**/
/**/_begin_under_score/**/
/**/unicode\u9877identifier/**/

/* operators and separators */
++operand;
--operand;
expression1=expression2;
expression1*=expression2;
expression1/=expression2;
expression1%=expression2;
expression1+=expression2;
expression1-=expression2;
expression1<<=shift_value;
expression1>>=shift_value;
expression1>>>=shift_value
expression1&=expression2;
expression1^=expression2;
expression1|=expression2;
!operand
~operand
expression1+expression2
expression1-expression2
expression1*expression2
expression1/expression2
expression1%expression2
expression1&expression2
expression1|expression2
expression1^expression2
expression1>>shift_value
expression1<<shift_value
expression1>>>shift_value
expression1&&expression2
expression1||expression2
expression1<expression2
expression1>expression2 
expression1<=expression2 
expression1>=expression2 
expression1==expression2 
expression1!=expression2
expression?expression1:expression2 

++ operand;
-- operand;
expression1 = expression2;
expression1 *= expression2;
expression1 /= expression2;
expression1 %= expression2;
expression1 += expression2;
expression1 -= expression2;
expression1 <<= shift_value;
expression1 >>= shift_value;
expression1 >>>= shift_value
expression1 &= expression2;
expression1 ^= expression2;
expression1 |= expression2;
! operand
~ operand
expression1 + expression2
expression1 - expression2
expression1 * expression2
expression1 / expression2
expression1 % expression2
expression1 & expression2
expression1 | expression2
expression1 ^ expression2
expression1 >> shift_value
expression1 << shift_value
expression1 >>> shift_value
expression1 && expression2
expression1 || expression2
expression1 < expression2 
expression1 > expression2 
expression1 <= expression2 
expression1 >= expression2 
expression1 == expression2 
expression1 != expression2 
expression ? expression1 : expression2

/* separators */
( ) [ ] { } ; , .
// each separator should be its own token
()]{};,.

/* Strings */
"hello","","abcxyz123890ABCXYZ, 	!@#$%^&*(){}?+=|~`'",
"\b\f\n\r\t\'\"\\\0",
// if you are wondering about why some of the following are valid, consider
// "\29" = '\2' + '9'
// "\uFFFFF" = '\uFFFF' + 'F'
"\1\2\03\12\012\111\222\333\08\29\444\u03a9\uFFFF\u03a9a\uFFFFF",
"??-??=??(??)??'??<??>??!??-??a??b??{??}??/8??","?","??","???","??A","??/xDB??/0??/012??/",

/* Characters */
'a','b','c','d','x','y','z','1','2','9','0','A','B','C','D','X','Y','Z',' ','	',
'!','@','#','$','%','^','&','*','(',')','{','}','?','=','+','|','~','`','"',
// escape sequences
'\b','\f','\n','\r','\t','\'','\"','\\','\0',
'\1','\2','\03','\12','\012','\111','\222','\333',
'\023','\u03a9','\uFFFF',

/* Float Literals */
0e0 0d .0 0. 0.0 0.0e0 0.e0 .0e0 0.0e-0 0e0d 0.0e0d 0.0e-0D 0.0e88 0.0e2D
1e1 1d .1 1. 1.1 1.1e1 1.e1 .1e1 1.1e-1 1e0d 1.1e1d 1.1e-1D 3.141592654 6.022e-22
1.79769313486231570e+308 4.94065645841246544e-324
1.0 0.1 1.1 2. .3 4e10 4.e23 8.3e88 2.1e-3 2E2
0f 0e0f 0.0e0F 0.0e-0f 1.1e1f 3.141592654f 6.022e-22f 3.40282347e+38f 1.40239846e-45f

/* Integer Literals */
0 2 1492 2147483647 -2147483648
00 0372 017777777777 020000000000 037777777777 000000000000001
0x0 0xDadaCafe 0x7fffffff 0x80000000 0xffffffff 0x00000000001
0l 0L 76678676L 9223372036854775807L -9223372036854775808L
00L 054325L 0777777777777777777777L 01000000000000000000000L 01777777777777777777777L 0000000000000000000000000001L
0x0L 0xfeeddeadbeefL 0x8000000000000000L 0x7fffffffffffffffL 0xffffffffffffffffL 0x0000ffffffffffffffL

/* Other Literals */
null
true
false

/* Errors 
 * 
 * Anything after this has some sort of error associated with it.
 * Since there is no standard definition of what an error is or how
 * recovery after errors should be done, there is no standard way to 
 * display errors.  However, if one of the errors gets reported as 
 * valid that is a problem.
 */ 

/* Identifier Errors */
/**/1cannot_start_with_number/**/
/**/weird`character/**/
/**/weird#character/**/
/**/weird@character/**/
/**/bad\uFFFunicode/**/

/* String Errors */
/**/"\A"/**/
/**/"\N"/**/
/**/"unclosed/**/
/**/"\"unclosed/**/
/**/"\8"/**/
/**/"\xGG"/**/
/**/"\uFFF"/**/
/**/"\uhhhh"/**/
/**/"\uuc0c0"/**/

/* Character Errors */
/**/'string'/**/
/**/'\A'/**/
/**/'\N'/**/
/**/'unclosed/**/
/**/'''unclosed/**/
/**/'\'closed'/**/
/**/'\'unclosed/**/
/**/'\8'/**/
/**/'\29'/**/
/**/'\444'/**/
/**/'\1111'/**/
/**/'\xFFF'/**/
/**/'\xGG'/**/
/**/'\x000'/**/
/**/'\x111'/**/
/**/'??a'/**/
/**/'??"'/**/
/**/'??}'/**/
/**/'??/'/**/
/**/'??/444'/**/
/**/'\uFFF'/**/
/**/'\uFFFFF'/**/
/**/'\uu'/**/
/**/'\u0'/**/
/**/'\xFF'/**/
/**/'??-'/**/
/**/'??='/**/
/**/'??('/**/
/**/'??)'/**/
/**/'??/0'/**/
/**/'??/xDB'/**/
/**/'??/XDB'/**/
/**/'??/012'/**/
/**/'??/'/**/
/**/'??'/**/
/**/'??<'/**/
/**/'??>'/**/
/**/'??!'/**/
/**/'??-'/**/
/**/'\a'/**/
/**/'\v'/**/
/**/'\?'/**/

/* Float Literal Errors */
/**/0e/**/
/**/88.8e/**/
/**/1.3L/**/
/**/1.3l/**/

/* Integer Literal Errors */
/**/08/**/
/**/09/**/
/**/0xg/**/
/**/0xG/**/
/**/3u/**/
/**/3U/**/
/**/3ul/**/
/**/3Ul/**/
/**/3uL/**/
/**/3UL/**/
/**/3lu/**/
/**/3lU/**/
/**/3Lu/**/
/**/3LU/**/
// the rest are errors on the bounds
// Sun Microsystem has a few bugs in the parse methods
// from strings to numbers,
// so some of these may be incorrectly accepted
/**/5 - 2147483648/**/
/**/214748364/**/
/**/-2147483649/**/
/**/077777777777/**/
/**/010000000000/**/
/**/0x100000000/**/
/**/9223372036854775808L/**/
/**/-9223372036854775809L/**/
/**/5 - 9223372036854775808L/**/
/**/02000000000000000000000L/**/
/**/07777777777777777777777L/**/
/**/0x10000000000000000L/**/
/**/1.79769313486231571e+308/**/
/**/1.79769313486231570e+309/**/
/**/4.94065645841246543e-324/**/
/**/4.94065645841246544e-325/**/
/**/3.40282348e+38f/**/
/**/3.40282347e+39f/**/
/**/1.40239845e-45f/**/
/**/1.40239846e-46f/**/
/**/1.79769313486232570e+308/**/
/**/2.94065645841245544e-324/**/
/**/3.40283347e+38f/**/
/**/1.40220846e-45f/**/

/* Unterminated comment at eof 
