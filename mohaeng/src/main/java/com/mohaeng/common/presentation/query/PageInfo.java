package com.mohaeng.common.presentation.query;

public record PageInfo(
        int currentPage,
        int lastPage,
        int pageSize,
        int totalElements
) {
    public static PageInfo from(int currentPage, int lastPage, int pageSize, long totalElements) {
        return new PageInfo(currentPage + 1, lastPage, pageSize, (int) totalElements);
    }
}