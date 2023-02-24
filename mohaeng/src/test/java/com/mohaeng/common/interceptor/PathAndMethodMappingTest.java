package com.mohaeng.common.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("PathAndMethodMapping(경로와 메서드로 인터셉터 매핑시 사용) 은")
class PathAndMethodMappingTest {

    private final PathAndMethodMapping pathAndMethodMapping = new PathAndMethodMapping();

    @Test
    void 포함항_path_와_httpMethod_를_추가할_수_있다() {
        // given
        final Set<String> path = Set.of("/path/**");
        final Set<HttpMethod> methods = Set.of(HttpMethod.GET, HttpMethod.DELETE);

        // when
        pathAndMethodMapping.includePathPattern(path, methods);

        // then
        assertThat(pathAndMethodMapping.match("/path/", HttpMethod.GET.name())).isTrue();
        assertThat(pathAndMethodMapping.match("/path/**", HttpMethod.GET.name())).isTrue();
        assertThat(pathAndMethodMapping.match("/path/hi", HttpMethod.GET.name())).isTrue();
        assertThat(pathAndMethodMapping.match("/path", HttpMethod.GET.name())).isTrue();
        assertThat(pathAndMethodMapping.match("/pathdwq", HttpMethod.GET.name())).isFalse();
        assertThat(pathAndMethodMapping.match("/path/ddw/1", HttpMethod.POST.name())).isFalse();
    }

    @Test
    void 포함하지_않을_path_와_httpMethod_를_추가할_수_있다() {
        // given
        final Set<String> path = Set.of("/path/**");
        final Set<HttpMethod> includeMethods = Set.of(HttpMethod.GET, HttpMethod.DELETE);
        final Set<HttpMethod> excludeMethods = Set.of(HttpMethod.POST);

        // when
        pathAndMethodMapping.includePathPattern(path, includeMethods);
        pathAndMethodMapping.excludePathPattern(path, excludeMethods);

        // then
        assertThat(pathAndMethodMapping.match("/path/test", HttpMethod.POST.name())).isFalse();
    }

    @Test
    void 포함하며_포함하지_않는_경우_Match_의_결과는_false_이다() {
        // given
        final Set<String> path = Set.of("/path/**");
        final Set<HttpMethod> includeMethods = Set.of(HttpMethod.GET, HttpMethod.DELETE);

        // when
        pathAndMethodMapping.includePathPattern(path, includeMethods);
        pathAndMethodMapping.excludePathPattern(path, includeMethods);

        // then
        assertThat(pathAndMethodMapping.match("/path/test", HttpMethod.POST.name())).isFalse();
    }

    @Test
    void match_는_제외되지_않았으며_포함된_경우에만_true_이다() {
        // given
        final Set<String> path = Set.of("/path/**");
        final Set<HttpMethod> methods = Set.of(HttpMethod.GET, HttpMethod.DELETE);
        final Set<String> excludePath = Set.of("/path/exclude/**");
        final Set<HttpMethod> excludeMethods = Set.of(HttpMethod.GET, HttpMethod.DELETE);

        // when
        pathAndMethodMapping.includePathPattern(path, methods);
        pathAndMethodMapping.excludePathPattern(path, Set.of(HttpMethod.DELETE));
        pathAndMethodMapping.excludePathPattern(excludePath, excludeMethods);

        // then
        assertThat(pathAndMethodMapping.match("/path/", HttpMethod.GET.name())).isTrue();

        /* 제외된 경우이다 */
        assertThat(pathAndMethodMapping.match("/path/exclude", HttpMethod.GET.name())).isFalse();

        /* 포함되었지만 제외된 경우이다. */
        assertThat(pathAndMethodMapping.match("/path/", HttpMethod.DELETE.name())).isFalse();

        /* 제외되지 않았지만 포함되지 않은 경우이다. */
        assertThat(pathAndMethodMapping.match("/pathdqw", HttpMethod.DELETE.name())).isFalse();
    }
}