package org.pom.utils;

import java.util.*;

public class SortUtils {
    private static SortUtils sortUtils;

    public static SortUtils getInstance() {
        if (sortUtils == null) {
            sortUtils = new SortUtils();
        }
        return sortUtils;
    }

    public static <T> void sortArrayList(List<T> list, Comparator<? super T> comparator) {
        Collections.sort(list, comparator);
    }

    public <T> boolean validateSortResultsInList(List<T> arrayList, Comparator<? super T> comparator) {
        ArrayList<T> sortedArrayList = new ArrayList<>(List.copyOf(arrayList));
        sortArrayList(sortedArrayList, comparator);
        return arrayList.equals(sortedArrayList);
    }
}