package ro.adriantosca.experimentalservicecache.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import ro.adriantosca.experimentalservicecache.cache.ExternalGet;
import ro.adriantosca.experimentalservicecache.cache.GuavaCache;

class GuavaCacheTest {

    @Test
    void getOrCompute() {
        ExternalGet<String, String> externalGet = mock(ExternalGet.class);
        when(externalGet.call("id1")).thenReturn("value1");
        var loadingCache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .expireAfterAccess(Duration.ofSeconds(3))
                .build(
                        new CacheLoader<String, String>() {
                            public @NonNull String load(String id) {
                                return externalGet.call(id);
                            }
                        }
                );
        var cache = new GuavaCache<>(loadingCache);
        var res1 = cache.get("id1", externalGet);
        assertThat(res1).isEqualTo("value1");
        var res1again = cache.get("id1", externalGet);
        assertThat(res1again).isEqualTo("value1");
        verify(externalGet, times(1)).call("id1");
    }
}
