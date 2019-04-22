package com.gikee.ethereum.utils.tuple;

import lombok.Data;
import reactor.util.annotation.NonNull;

import java.util.Objects;

@Data
public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {

    private static final long serialVersionUID = 1L;

    @NonNull
    public final C third;

    public ThreeTuple(A first, B second, C third) {
        super(first, second);
        this.third = Objects.requireNonNull(third, "third");
    }

}
