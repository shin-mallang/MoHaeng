package com.mohaeng.common.presentation.query;

public record PageInfo(
        int currentPage,
        int lastPage,
        int countPerPage,
        int totalSize
) {
    public static PageInfo from(int currentPage, int lastPage, int countPerPage, long totalSize) {
        return new PageInfo(currentPage + 1, lastPage, countPerPage, (int) totalSize);
    }
}