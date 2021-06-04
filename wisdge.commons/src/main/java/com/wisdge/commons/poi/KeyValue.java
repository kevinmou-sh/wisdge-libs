package com.wisdge.commons.poi;

/**
 * 键值对象
 * @param <K>
 * @param <V>
 */
public class KeyValue<K,V> {
    private K k;
    private V v;
    public K getK () {
        return k;
    }
    public void setK (K k) {
        this.k = k;
    }

    public V getV () {
        return v;
    }

    public void setV (V v) {
        this.v = v;
    }
}