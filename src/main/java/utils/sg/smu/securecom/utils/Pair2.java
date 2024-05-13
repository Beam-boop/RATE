package utils.sg.smu.securecom.utils;

public class Pair2<T1, T2> {

    private final T1 key;
    private final T2 value;

    public Pair2(T1 first, T2 value1) {
        this.key = first;
        this.value = value1;
    }

    public T1 getKey() {
        return key;
    }

    public T2 getValue() {
        return value;
    }
}
