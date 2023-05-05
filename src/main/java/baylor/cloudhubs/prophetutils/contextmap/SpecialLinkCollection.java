package baylor.cloudhubs.prophetutils.contextmap;

import java.util.HashMap;
import java.util.Iterator;

public class SpecialLinkCollection<T> implements Iterable<T>{
    private HashMap<T, T> map;

    public SpecialLinkCollection() {
        map = new HashMap<>();
    }

    public boolean add(T obj) {
        if (map.containsKey(obj)) {
            return false;
        }
        map.put(obj, obj);
        return true;
    }

    public T get(T obj) {
        return map.get(obj);
    }

    public boolean contains(T obj) {
        return map.containsKey(obj);
    }

    public boolean remove(T obj) {
        return map.remove(obj) != null;
    }

    public int size() {
        return map.size();
    }

    @Override
    public Iterator<T> iterator() {
        return map.values().iterator();
    }
}