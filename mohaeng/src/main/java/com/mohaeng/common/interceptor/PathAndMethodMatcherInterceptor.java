package com.mohaeng.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

public class PathAndMethodMatcherInterceptor implements HandlerInterceptor {

    private static final Set<HttpMethod> HTTP_METHODS = Set.of(HttpMethod.values());

    private final HandlerInterceptor handlerInterceptor;
    private final PathAndMethodMapping pathAndMethodMapping;

    public PathAndMethodMatcherInterceptor(final HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
        this.pathAndMethodMapping = new PathAndMethodMapping();
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        if (pathAndMethodMapping.match(request.getRequestURI(), request.getMethod())) {
            return handlerInterceptor.preHandle(request, response, handler);
        }
        return true;
    }

    public PathAndMethodMatcherInterceptor addPathPatterns(final Set<String> pathPatterns, final Set<HttpMethod> httpMethods) {
        pathAndMethodMapping.includePathPattern(pathPatterns, httpMethods);
        return this;
    }

    /* 모든 Http Method 에 대해 허용 */
    public PathAndMethodMatcherInterceptor addPathPatterns(final Set<String> pathPatterns) {
        pathAndMethodMapping.includePathPattern(pathPatterns, HTTP_METHODS);
        return this;
    }

    public PathAndMethodMatcherInterceptor excludePathPattern(final Set<String> pathPatterns, final Set<HttpMethod> httpMethods) {
        pathAndMethodMapping.excludePathPattern(pathPatterns, httpMethods);
        return this;
    }

    /* 모든 Http Method 에 대해 허용 */
    public PathAndMethodMatcherInterceptor excludePathPattern(final Set<String> pathPatterns) {
        pathAndMethodMapping.excludePathPattern(pathPatterns, HTTP_METHODS);
        return this;
    }
}
