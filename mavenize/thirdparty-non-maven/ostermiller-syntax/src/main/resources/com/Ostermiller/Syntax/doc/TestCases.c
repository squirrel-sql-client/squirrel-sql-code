#define first thing
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
if/* *??/*/if
if// the next line should not be a comment \
if

/* c and c++ keywords */
auto
break
case
const
continue
default
do
else
enum
extern
for
goto
if
register
return
sizeof
static
struct
switch
typedef
union
volatile
while

catch
class
const_cast
delete
dynamic_cast
friend
inline
mutable
namespace
new
operator
overload
private
protected
public
reinterpret_cast
static_cast
template
this
try
virtual

bool
char
double
float
int
long
short
signed
unsigned
void

/* pp test (no if should be in a pp or comment) */
#include
#include_next
#define
#undef
#if
#ifdef
#ifndef
#else
#elif
#endif
#line
#pragma
#error
if
#define 1 //comment
if
#define//comment
if
#define/*comment*/
if
#define /*comment*/ 1
if
#define/*comment*/1
if
# define 1// a space after the hash 
if
 #define 1 // a space before the hash
if
??=define // using a trigraph to represent the hash
	 ??= define 1  // really nasty
if
#define something /*multiple line */ \
     4
if
#define aString "A string with escape sequences\n\r\t\\\""
if
#defineNo space after keyword
if
#define stuff /*how about using a trigraph to get to the next line?*/ ??/
	1 // should be part of define stuff.
if
/* Lets try some weird stuff to try and confuse it with trigraphs */
#define line \\
1 // still in define (somebody correct me if the pp does something different here)
if
#define a ??\
line // still in define
if
#define\
 many ??/
 multiple \
 lines 
if

/* Characters */
'a','b','c','d','x','y','z','1','2','9','0','A','B','C','D','X','Y','Z',' ','	',
'!','@','#','$','%','^','&','*','(',')','{','}','?','=','+','|','~','`','"',
// escape sequences
'\a','\b','\f','\n','\r','\t','\v','\'','\"','\?','\\','\0',
'\1','\2','\03','\12','\012','\111','\222','\333',
'\xFF','\x12','\x00','\xDB','\X00','\XFF','\XDB'
// trigraphs including escape sequence trigraphs
'??-','??=','??(','??)','??/0','??/xDB','??/XDB','??/012','??/'','??'','??<','??>','??!','??-'

/* Strings */
"hello","","abcxyz123890ABCXYZ, 	!@#$%^&*(){}?+=|~`'",
"\a\b\f\n\r\t\v\'\"\?\\\0",
// if you are wondering about why some of the following are valid, consider
// "\xFFF" = '\xFF' + 'F'
// "\29" = '\2' + '9'
"\1\2\03\12\012\111\222\333\x000\xFFF\08\29\444",
"??-??=??(??)??'??<??>??!??-??a??b??{??}??","?","??","???","??A","??/xDB??/0??/012??/"",
"string that \
spans ??/
multiple \
lines",

/* Identifiers */
/**/identifier/**/
/**/a0b1c2d3e4f5g/**/
/**/h6i7j8k9l0mno/**/
/**/pqrstuvwxyz/**/
/**/ABCDEFGHIJK/**/
/**/LMNOPQRSTUVWXYZ/**/
/**/dollar$ign/**/
/**/under_score/**/
/**/_begin_under_score/**/

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
expression1&=expression2;
expression1^=expression2;
expression1??'=expression2;
expression1|=expression2;
expression1??!=expression2;
!operand
~operand
??-operand
expression1+expression2
expression1-expression2
expression1*expression2
expression1/expression2
expression1%expression2
expression1&expression2
expression1|expression2
expression1??!expression2
expression1^expression2
expression1??'expression2
expression1>>shift_value
expression1<<shift_value
expression1&&expression2
expression1||expression2
expression1??!|expression2
expression1|??!expression2
expression1??!??!expression2
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
expression1 &= expression2;
expression1 ^= expression2;
expression1 ??'= expression2;
expression1 |= expression2;
expression1 ??!= expression2;
! operand
~ operand
??-operand
expression1 + expression2
expression1 - expression2
expression1 * expression2
expression1 / expression2
expression1 % expression2
expression1 & expression2
expression1 | expression2
expression1 ??! expression2
expression1 ^ expression2
expression1 ??' expression2
expression1 >> shift_value
expression1 << shift_value
expression1 && expression2
expression1 || expression2
expression1 ??!| expression2
expression1 |??! expression2
expression1 ??!??! expression2
expression1 < expression2 
expression1 > expression2 
expression1 <= expression2 
expression1 >= expression2 
expression1 == expression2 
expression1 != expression2 
expression ? expression1 : expression2

/* separators */
( ) [ ??( ] ??) { ??< } ??> ; , . ->
// each separator should be its own token
()[??(]??){??<}??>;,.->


/* Integer Literals */
1 2 3 4 99 1370000
2l 2L 2u 2U 2ul 2Ul 2UL 2uL 2lu 2Lu 2lU 2LU 
0x0 0x1 0x2 0xA 0xa 0xf 0xF 0x100 0xdeadbeef 
0x4l 0x4L 0x4u 0x4U 0x4ul 0x4Ul 0x4UL 0x4uL 0x4lu 0x4Lu 0x4lU 0x4LU
01 02 03 04 0453222 032
07l 07L 07u 07U 07ul 07Ul 07UL 07uL 07lu 07Lu 07lU 07LU

/* Float Literals */
0e0 .0 0. 0.0 0.0e0 0.e0 .0e0 0.0e-0 0.0e88
1e1 .1 1. 1.1 1.1e1 1.e1 .1e1 1.1e-1 3.141592654 6.022e-22
1.79769313486231570e+308 4.94065645841246544e-324
1.0 0.1 1.1 2. .3 4e10 4.e23 8.3e88 2.1e-3 2E2
0e0f 0.0e0F 0.0e-0f 1.1e1f 3.141592654f 6.022e-22f 3.40282347e+38f 1.40239846e-45f
0e0l 0.0e0L 0.0e-0L 1.1e1L 3.141592654L 6.022e-22L 3.40282347e+38L 1.40239846e-45L

if (expression1 and expression2)
result and_eq expression2;
result = expression1 bitand expression2;
result = expression1 bitor expression2;
result = compl expression1;
result = expression1 or expression2;
result or_eq expression2;
result = expression1 xor expression2;
result xor_eq expression2;
if (expression1 not_eq expression2)
asm ("eieio");
class C { explicit C(int i) {} };
export template<class T> void out(const T& t) { std::cerr << t; }
if (x < 0) throw underflow(x);
void f(Shape& r) { typeid(r); }
typename C::value_type s = 0;
using std;
wchar_t ch = 'a';
bool finished = true | false;
if (not finished) { std::cerr << std::endl; }

/* Errors 
 * 
 * Anything after this has some sort of error associated with it.
 * Since there is no standard definition of what an error is or how
 * recovery after errors should be done, there is no standard way to 
 * display errors.  However, if one of the errors gets reported as 
 * valid that is a problem.
 */ 

/* Comment Errors */
if??/*This is not a comment*/if
if??//This is not a comment
if

/* pp errors (no if should be in a pp or comment) */
if
#notdefined This should be an error /* with a */ comment in the middle of it \
      extending to multiple lines. // heck a comment here too.
if
#net
if
#something // comment
if
#frog /* comment */
if
#bugs// comment
if
#logs/* comment */
if
identifier #define A pp statement cannot go here
if

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
/**/'??/'closed'/**/
/**/'??/'unclosed/**/
/**/'??/444'/**/
/**/'\uFFF'/**/
/**/'\uFFFFF'/**/
/**/'\uu'/**/
/**/'\u0'/**/
/**/'\uFFFF'/**/
/**/'\u1234'/**/
/**/'\u0000'/**/

/* String Errors */
/**/"\A"/**/
/**/"\N"/**/
/**/"unclosed/**/
/**/"\"unclosed/**/
/**/"\8"/**/
/**/"\xGG"/**/
/**/"??/"unclosed/**/
/**/"??/8"/**/

/* Identifier Errors */
/**/1cannot_start_with_number/**/
/**/$cannot_start_with_dollar/**/
/**/weird`character/**/
/**/weird#character/**/
/**/weird??=character/**/
/**/weird@character/**/

/* Integer Literal Errors */
/**/08/**/
/**/09/**/
/**/0xg/**/
/**/0xG/**/

/* Float Literal Errors */
/**/0f/**/
/**/0F/**/
/**/1f/**/
/**/0e/**/
/**/88.8e/**/
/**/1.3d/**/
/**/1.3D/**/

/* Comment at end intentionally
 * left open. */
/*/


