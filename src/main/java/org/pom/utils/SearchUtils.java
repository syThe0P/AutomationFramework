package org.pom.utils;

import java.util.List;

public class SearchUtils {
    private static SearchUtils searchUtils;

    public static SearchUtils getInstance() {
        if (searchUtils == null) {
            searchUtils = new SearchUtils();
        }
        return searchUtils;
    }

    public boolean validateSearchResultsInList(List<String> arrayList, String searchText, boolean isExactMatch) {
        boolean isMatching = true;
        for (String arrayListItem : arrayList) {
            isMatching = (isExactMatch) ? arrayListItem.equalsIgnoreCase(searchText) : arrayListItem.toLowerCase().contains(searchText.toLowerCase());
            if (!isMatching) {
                isMatching = false;
                break;
            }
        }
        return isMatching;
    }

    public boolean validateSearchResultsInList(List<String> arrayList, String searchText) {
        return validateSearchResultsInList(arrayList, searchText, false);
    }
}