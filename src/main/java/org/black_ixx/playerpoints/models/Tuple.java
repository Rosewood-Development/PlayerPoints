package org.black_ixx.playerpoints.models;

public class Tuple<T, S> {

    private final T first;
    private final S second;

    public Tuple(T first, S second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return this.first;
    }

    public S getSecond() {
        return this.second;
    }

}
