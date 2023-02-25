package com.mohaeng.common.presentation.interceptor;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathAndMethodMapping {

    private final PathMatcher pathMatcher;
    private final List<PathAndMethod> includePathPattern;
    private final List<PathAndMethod> excludePathPattern;

    public PathAndMethodMapping() {
        this.pathMatcher = new AntPathMatcher();
        this.includePathPattern = new ArrayList<>();
        this.excludePathPattern = new ArrayList<>();
    }

    public boolean match(final String targetPath, final String httpMethod) {
        boolean excludePattern = excludePathPattern.stream()
                .anyMatch(requestPath -> anyMatchPathPattern(targetPath, httpMethod, requestPath));

        boolean includePattern = includePathPattern.stream()
                .anyMatch(requestPath -> anyMatchPathPattern(targetPath, httpMethod, requestPath));

        return !excludePattern && includePattern;
    }

    private boolean anyMatchPathPattern(final String targetPath,
                                        final String httpMethod,
                                        final PathAndMethod requestPathAndMethod) {
        return pathMatcher.match(requestPathAndMethod.path(), targetPath) &&
                requestPathAndMethod.matchMethod(httpMethod);
    }

    public void includePathPattern(final Set<String> pathPatterns, final Set<HttpMethod> httpMethods) {
        this.includePathPattern.addAll(
                pathPatterns.stream()
                        .map(it -> new PathAndMethod(it, httpMethods)).toList()
        );
    }

    public void excludePathPattern(final Set<String> pathPatterns, final Set<HttpMethod> httpMethods) {
        this.excludePathPattern.addAll(pathPatterns.stream()
                .map(it -> new PathAndMethod(it, httpMethods)).toList());
    }

    static class PathAndMethod {
        private final String path;
        private final Set<HttpMethod> methods;

        public PathAndMethod(final String path, final Set<HttpMethod> methods) {
            this.path = path;
            this.methods = new HashSet<>(methods);
        }

        public boolean matchMethod(final String httpMethod) {
            return this.methods.contains(HttpMethod.valueOf(httpMethod));
        }

        public String path() {
            return path;
        }
    }
}
