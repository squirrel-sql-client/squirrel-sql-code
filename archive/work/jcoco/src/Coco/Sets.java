package Coco;

import java.util.BitSet;

class Sets {

	static boolean Empty(BitSet s) {			/** s=={}? */
		int max = s.size();
		for (int i=0; i<=max; i++) {
			if (s.get(i)) return false;
		}
		return true;
	}

	static boolean Different(BitSet s1, BitSet s2) {	/** s1*s2=={}? */
		int max = s1.size();
		for (int i=0; i<=max; i++)
			if (s1.get(i) && s2.get(i)) return false;
		return true;
	}

	static boolean Includes(BitSet s1, BitSet s2) {	/** s1 > s2 ? */
		int max = s2.size();
		for (int i=0; i<=max; i++)
			if (s2.get(i) && !s1.get(i)) return false;
		return true;
	}

	static BitSet FullSet(int max) {			/** return {0..max} */
		BitSet s = new BitSet();
		for (int i=0; i<=max; i++) s.set(i);
		return s;
	}

	static int Size(BitSet s) {					/** return number of elements in s */
		int size = 0, max = s.size();
		for (int i=0; i<=max; i++)
			if (s.get(i)) size++;
		return size;
	}

	static int First(BitSet s) {				/** return first element in s */
		int max = s.size();
		for (int i=0; i<=max; i++)
			if (s.get(i)) return i;
		return -1;
	}

	static void Differ(BitSet s, BitSet s1) {	/** s = s - s1 */
		int max = s.size();
		for (int i=0; i<=max; i++) {
			if (s1.get(i)) s.clear(i);
		}
	}

}
