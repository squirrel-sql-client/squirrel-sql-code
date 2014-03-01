package net.sourceforge.squirrel_sql.client.session.parser.kernel;

public interface ParsingConstants
{
   //
   //
   // DO NOT CHANGE THE VALUES OF THIS CONSTANTS.
   //
   // These constants where introduced to make the parser
   // code more readable. Care was taken. Still it can
   // not be guaranteed that changing a value will
   // not be harmful.
   //


   static final int KW_ACTION = 90;
   static final int KW_ADD = 97;
   static final int KW_ALTER = 98;
   static final int KW_AND = 57;
   static final int KW_ASC = 51;
   static final int KW_AVG = 48;
   static final int KW_BETWEEN = 67;
   static final int KW_BY = 36;
   static final int KW_CASCADE = 88;
   static final int KW_CHAR = 72;
   static final int KW_CHARACTER = 73;
   static final int KW_CHECK = 92;
   static final int KW_COMMIT = 69;
   static final int KW_CONSTRAINT = 99;
   static final int KW_COUNT = 44;
   static final int KW_CREATE = 93;
   static final int KW_DATE = 79;
   static final int KW_DEFAULT = 81;
   static final int KW_DESC = 50;
   static final int KW_DROP = 96;
   static final int KW_ESCAPE = 60;
   static final int KW_FOREIGN = 84;
   static final int KW_GROUP = 35;
   static final int KW_HAVING = 37;
   static final int KW_IN = 68;
   static final int KW_INDEX = 100;
   static final int KW_INT = 76;
   static final int KW_INTEGER = 75;
   static final int KW_IS = 61;
   static final int KW_KEY = 83;
   static final int KW_LIKE = 59;
   static final int KW_MATCH = 86;
   static final int KW_MAX = 46;
   static final int KW_MIN = 47;
   static final int KW_MONTH = 42;
   static final int KW_NO = 89;
   static final int KW_NOT = 54;
   static final int KW_NULL = 49;
   static final int KW_NUMERIC = 78;
   static final int KW_OR = 58;
   static final int KW_ORDER = 38;
   static final int KW_PARTIAL = 87;
   static final int KW_PRIMARY = 82;
   static final int KW_REFERENCES = 85;
   static final int KW_RESTRICT = 95;
   static final int KW_ROLLBACK = 70;
   static final int KW_SMALLINT = 77;
   static final int KW_SUM = 45;
   static final int KW_TABLE = 94;
   static final int KW_TIME = 80;
   static final int KW_TIMESTAMP = 40;
   static final int KW_UNIQUE = 91;
   static final int KW_UPPER = 41;
   static final int KW_USING = 33;
   static final int KW_VARCHAR = 74;
   static final int KW_WHERE = 34;
   static final int KW_WORK = 71;
   static final int KW_YEAR = 43;

   static final int KW_ALL = 11;
   static final int KW_AS = 23;
   static final int KW_DISTINCT = 21;
   static final int KW_EXCEPT = 8;
   static final int KW_FROM = 19;
   static final int KW_INSERT = 15;
   static final int KW_INTERSECT = 9;
   static final int KW_INTO = 16;
   static final int KW_MINUS = 10;
   static final int KW_SET = 13;
   static final int KW_UNION = 7;
   static final int KW_UPDATE = 12;
   static final int KW_CROSS = 25;
   static final int KW_DELETE = 18;
   static final int KW_FULL = 28;
   static final int KW_INNER = 27;
   static final int KW_JOIN = 24;
   static final int KW_LEFT = 29;
   static final int KW_NATURAL = 26;
   static final int KW_ON = 32;
   static final int KW_OUTER = 31;
   static final int KW_RIGHT = 30;
   static final int KW_SELECT = 20;
   static final int KW_VALUES = 17;

   static final int KW_CASE = -1000;
   static final int KW_WHEN = -1001;
   static final int KW_THEN = -1002;
   static final int KW_ELSE = -1003;
   static final int KW_END = -1004;
   static final int KW_VIEW = -1005;


   static final int KIND_CLOSING_BRAKET = 102;
   static final int KIND_OPENING_BRAKET = 5;
   static final int KIND_COMMA = 101;
   static final int KIND_ASTERISK = 39;
   static final int KIND_EQUALS = 14;

   static final int KW_INSET = 115;
   static final int KW_MINUS_SIGN = 52;
}
