package org.springframework.springfaces.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class MapEntryValueComparator<K, V> implements Comparator<Map.Entry<K, V>> {

	private Comparator<? super V> valueComparator;

	public MapEntryValueComparator(Comparator<? super V> valueComparator) {
		this.valueComparator = valueComparator;
	}

	public int compare(Entry<K, V> o1, Entry<K, V> o2) {
		return valueComparator.compare(o1.getValue(), o2.getValue());
	}

	public static <K, V> Set<Map.Entry<K, V>> entrySet(Map<K, V> map, Comparator<? super V> valueComparator) {
		MapEntryValueComparator<K, V> mapEntryValueComparator = new MapEntryValueComparator<K, V>(valueComparator);
		TreeSet<Map.Entry<K, V>> rtn = new TreeSet<Map.Entry<K, V>>(mapEntryValueComparator);
		rtn.addAll(map.entrySet());
		return Collections.unmodifiableSet(rtn);
	}

}
