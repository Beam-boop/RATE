package utils.sg.smu.securecom.utils;

public class Pair<T1, T2, T3> {
	
    private final T1 key;
    private final T2 value1;
    private final T3 value2;

    public Pair(T1 first, T2 value1, T3 value2) {
        this.key = first;
        this.value1 = value1;
        this.value2 = value2;
    }

	public T1 getKey() {
        return key;
    }

    public T2 getValue1() {
        return value1;
   }
    public T3 getValue2() {
        return value2;
   }
}
