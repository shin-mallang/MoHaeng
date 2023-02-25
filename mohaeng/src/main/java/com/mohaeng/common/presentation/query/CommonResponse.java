package com.mohaeng.common.presentation.query;

public record CommonResponse<T>(
        T data,
        PageInfo pageInfo
) {
    static <T> CommonResponse<T> from(T data, PageInfo pageInfo) {
        return new CommonResponse<>(data, pageInfo);
    }

    public static <T> CommonResponse<T> from(T data) {
        return new CommonResponse<>(data, null);
    }

    public static CommonResponse<?> ok() {
        return new CommonResponse<>(null, null);
    }
}
