package com.rentoki.umafx.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Cache {
    private static final Map<Object, Object> CACHE = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <K, V> V getOrCompute(K key, Supplier<V> loader) {
        return (V) CACHE.computeIfAbsent(key, o -> loader.get());
    }

    public static void remove(Object key) {
        CACHE.remove(key);
    }

    public static void clear() {
        CACHE.clear();
    }
}
