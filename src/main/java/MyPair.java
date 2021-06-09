public class MyPair<T, P> {
    private T first;
    private P second;

    public MyPair(T f, P s) {
        first = f;
        second = s;
    }

    public void setFirst(T f) {
        first = f;
    }

    public T getFirst() {
        return first;
    }

    public void setSecond(P s) {
        second = s;
    }

    public P getSecond() {
        return second;
    }
}
