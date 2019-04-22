package com.gikee.ethereum.utils.tuple;

import lombok.Data;
import reactor.util.annotation.NonNull;

import java.io.Serializable;
import java.util.*;

@Data
public class TwoTuple<A, B> implements Iterable<Object>, Serializable {

    private static final long serialVersionUID = 1L;

    @NonNull
    final A first;

    @NonNull
    final B second;

    public TwoTuple(@NonNull A first, @NonNull B second) {
        this.first = Objects.requireNonNull(first, "first");
        this.second = Objects.requireNonNull(second, "second");
    }

    public List<Object> toList() {
        return Arrays.asList(this.toArray());
    }

    public Object[] toArray() {
        return new Object[]{this.first, this.second};
    }

    @Override
    public Iterator<Object> iterator() {
        return Collections.unmodifiableList(this.toList()).iterator();
    }
}
