package Taste;

class Obj {
	String name;  // name of the object
	int type;     // type of the object (undef for procs)
	Obj next;     // to next object in same scope
	int kind;
	int adr;      // address in memory or start of proc
	int level;    // nesting level of declaration
	Obj locals;   // scopes: to locally declared objects
	int nextAdr;  // scopes: next free address in this scope
}

class TL {

	// types
	static final int undef = 0;
	static final int integer = 1;
	static final int bool = 2;

	// object kinds
	static final int vars = 0;
	static final int procs = 1;
	static final int scopes = 2;

	private static Obj undefObj;  // object node for erroneous symbols
	static int curLevel;          // nesting level of current scope
	private static Obj topScope;  // topmost procedure scope

	static void EnterScope () {
		Obj scope = new Obj();
		scope.name = ""; scope.type = undef; scope.kind = scopes; 
		scope.locals = null; scope.nextAdr = 3;
		scope.next = topScope; topScope = scope; 
		curLevel++;
	}

	static void LeaveScope () {
		topScope = topScope.next; curLevel--;
	}

	static int DataSpace () {
		return topScope.nextAdr-3;
	}

	static Obj NewObj (String name, int kind) {
		Obj p, obj = new Obj();
		obj.name = new String(name); obj.type = undef; obj.kind = kind;
		obj.level = curLevel;
		p = topScope.locals;
		while (p != null) { 
			if (p.name.equals(name)) Parser.SemError(1);
			p = p.next;
		}
		obj.next = topScope.locals; topScope.locals = obj;
		if (kind == vars) {obj.adr = topScope.nextAdr; topScope.nextAdr++;}
		return obj;
	}

	static Obj This (String name) {
		Obj obj, scope;
		scope = topScope;
		while (scope != null) {
			obj = scope.locals;
			while (obj != null) {
				if (obj.name.equals(name)) return obj;
				obj = obj.next;
			}
			scope = scope.next;
		}
		Parser.SemError(2);
		return undefObj;
	}

	static void Init () {
		topScope = null; curLevel = 0;
		undefObj = new Obj();
		undefObj.name  =  ""; undefObj.type = undef;
		undefObj.kind  = vars; undefObj.adr = 0;
		undefObj.level = 0; undefObj.next = null;
	}

}
