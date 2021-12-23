package ro.adriantosca.experimentalservicecache.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AdiMemoryCacheTest {

    @Nested
    class GetWithMaximumSize {
        @Test
        void get__calls_compute_when_empty() {
            var cache = new AdiMemoryCache.Builder<String, String>().withMaximumSize(3).build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id")).thenReturn("value");
            var res = cache.get("id", externalGet);
            verify(externalGet, times(1)).call("id");
            assertThat(res).isEqualTo("value");
        }

        @Test
        void get__calls_compute_form_multiple_when_empty() {
            var cache = new AdiMemoryCache.Builder<String, String>().withMaximumSize(3).build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id1")).thenReturn("value1");
            when(externalGet.call("id2")).thenReturn("value2");
            when(externalGet.call("id3")).thenReturn("value3");
            var res1 = cache.get("id1", externalGet);
            var res2 = cache.get("id2", externalGet);
            var res3 = cache.get("id3", externalGet);
            verify(externalGet, times(1)).call("id1");
            verify(externalGet, times(1)).call("id2");
            verify(externalGet, times(1)).call("id3");
            assertThat(res1).isEqualTo("value1");
            assertThat(res2).isEqualTo("value2");
            assertThat(res3).isEqualTo("value3");
        }

        @Test
        void get__gets_from_cache_when_exists_and_does_not_call_external_get() {
            var cache = new AdiMemoryCache.Builder<String, String>().withMaximumSize(3).build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id1")).thenReturn("value1");
            var res1 = cache.get("id1", externalGet);
            var res1again = cache.get("id1", externalGet);
            verify(externalGet, times(1)).call("id1");
            assertThat(res1).isEqualTo("value1");
            assertThat(res1again).isEqualTo("value1");
        }

        @Test
        void get__adding_after_max_size_evicts_default_least_used() {
            var cache = new AdiMemoryCache.Builder<String, String>().withMaximumSize(2).build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id1")).thenReturn("value1");
            when(externalGet.call("id2")).thenReturn("value2");
            when(externalGet.call("id3")).thenReturn("value3");
            var res1 = cache.get("id1", externalGet);
            var res2 = cache.get("id2", externalGet);
            var res3 = cache.get("id3", externalGet);
            var res1again = cache.get("id1", externalGet);
            verify(externalGet, times(2)).call("id1");
            verify(externalGet, times(1)).call("id2");
            verify(externalGet, times(1)).call("id3");
            assertThat(res1).isEqualTo("value1");
            assertThat(res2).isEqualTo("value2");
            assertThat(res3).isEqualTo("value3");
            assertThat(res1again).isEqualTo("value1");
        }

        @Test
        void get__adding_after_max_size_evicts_least_used_middle() {
            var cache = new AdiMemoryCache.Builder<String, String>().withMaximumSize(3).build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id1")).thenReturn("value1");
            when(externalGet.call("id2")).thenReturn("value2");
            when(externalGet.call("id3")).thenReturn("value3");
            when(externalGet.call("id4")).thenReturn("value4");
            var res1 = cache.get("id1", externalGet);
            var res2 = cache.get("id2", externalGet);
            var res3 = cache.get("id3", externalGet);
            var res1again = cache.get("id1", externalGet); // makes it most used, makes id2 the least used
            var res4 = cache.get("id4", externalGet); // evicts id2
            var res2again = cache.get("id2", externalGet); // so it will call external get again
            verify(externalGet, times(1)).call("id1");
            verify(externalGet, times(2)).call("id2");
            verify(externalGet, times(1)).call("id3");
            verify(externalGet, times(1)).call("id4");
            assertThat(res1).isEqualTo("value1");
            assertThat(res2).isEqualTo("value2");
            assertThat(res3).isEqualTo("value3");
            assertThat(res4).isEqualTo("value4");
            assertThat(res1again).isEqualTo("value1");
            assertThat(res2again).isEqualTo("value2");
        }

        @Test
        void get__adding_after_max_size_rolls_evicts_least_used() {
            var cache = new AdiMemoryCache.Builder<String, String>().withMaximumSize(3).build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id1")).thenReturn("value1");
            when(externalGet.call("id2")).thenReturn("value2");
            when(externalGet.call("id3")).thenReturn("value3");
            when(externalGet.call("id4")).thenReturn("value4");
            var res1 = cache.get("id1", externalGet);
            var res2 = cache.get("id2", externalGet);
            var res3 = cache.get("id3", externalGet);
            var res1again = cache.get("id1", externalGet); // makes it most used, makes id2 the least used
            var res2again = cache.get("id2", externalGet); // makes it most used, makes id3 the least used
            var res4 = cache.get("id4", externalGet); // evicts id3
            var res3again = cache.get("id3", externalGet); // so it will call external get again
            verify(externalGet, times(1)).call("id1");
            verify(externalGet, times(1)).call("id2");
            verify(externalGet, times(2)).call("id3");
            verify(externalGet, times(1)).call("id4");
            assertThat(res1).isEqualTo("value1");
            assertThat(res2).isEqualTo("value2");
            assertThat(res3).isEqualTo("value3");
            assertThat(res4).isEqualTo("value4");
            assertThat(res1again).isEqualTo("value1");
            assertThat(res2again).isEqualTo("value2");
        }

        @Test
        void get__adding_after_max_size_repeatedly_evicts_all_affected() {
            var cache = new AdiMemoryCache.Builder<String, String>().withMaximumSize(2).build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id1")).thenReturn("value1");
            when(externalGet.call("id2")).thenReturn("value2");
            when(externalGet.call("id3")).thenReturn("value3");
            cache.get("id1", externalGet);
            cache.get("id2", externalGet);
            cache.get("id3", externalGet); // evicts id1
            cache.get("id1", externalGet); // evicts id2
            cache.get("id2", externalGet); // evicts id1 again
            cache.get("id1", externalGet); // evicts id2 again
            verify(externalGet, times(2)).call("id1");
            verify(externalGet, times(2)).call("id2");
            verify(externalGet, times(1)).call("id3");
        }

        @Test
        void get__with_concurrency_keeps_data_correct() throws InterruptedException {
            var executorService = Executors.newFixedThreadPool(5);
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call(any())).thenAnswer(answer -> answer.getArgument(0));
            try {
                var size = 10;
                var cache = new AdiMemoryCache.Builder<String, String>().withMaximumSize(size).build();
                var retrieveCount = 500;
                var countDownLatch = new CountDownLatch(retrieveCount);
                IntStream.range(0, retrieveCount).<Runnable>mapToObj(i -> () -> {
                    var id = "id%s".formatted(i % size);
                    cache.get(id, externalGet);
                    countDownLatch.countDown();
                }).forEach(executorService::submit);
                countDownLatch.await();
                IntStream.range(0, size).forEach(i -> {
                    var id = "id%s".formatted(i);
                    verify(externalGet, times(1)).call(id);
                    assertThat(cache.get(id, externalGet)).isEqualTo(id);
                });
            } finally {
                executorService.shutdown();
            }
        }
    }

    @Nested
    class GetWithExpireAfterAccess {
        @Test
        void get__calls_compute_when_not_present() {
            var cache = new AdiMemoryCache.Builder<String, String>().withExpireAfterAccess(Duration.ofHours(1)).build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id")).thenReturn("value");
            var res = cache.get("id", externalGet);
            verify(externalGet, times(1)).call("id");
            assertThat(res).isEqualTo("value");
        }

        @Test
        void get__does_not_call_compute_when_present_and_not_expired() {
            var timer = new TestTimer();;
            var cache = new AdiMemoryCache.Builder<String, String>()
                    .withExpireAfterAccess(Duration.ofNanos(1))
                    .withTimer(timer)
                    .build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id")).thenReturn("value");
            var res = cache.get("id", externalGet);
            assertThat(res).isEqualTo("value");
            cache.get("id", externalGet);
            verify(externalGet, times(1)).call("id");
        }

        @Test
        void get__calls_compute_when_present_but_expired() {
            var timer = new TestTimer();
            var cache = new AdiMemoryCache.Builder<String, String>()
                    .withExpireAfterAccess(Duration.ofNanos(1))
                    .withTimer(timer)
                    .build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id")).thenReturn("value");
            var res = cache.get("id", externalGet);
            assertThat(res).isEqualTo("value");
            timer.tick();
            cache.get("id", externalGet);
            verify(externalGet, times(2)).call("id");
        }

        @Test
        void get__removes_expired_items_if_eager_removal_enabled() {
            var timer = new TestTimer();
            var cache = new AdiMemoryCache.Builder<String, String>()
                    .withExpireAfterAccess(Duration.ofNanos(1))
                    .withEagerRemoveExpired(true)
                    .withTimer(timer)
                    .build();
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            when(externalGet.call("id1")).thenReturn("value1");
            when(externalGet.call("id2")).thenReturn("value2");
            when(externalGet.call("id3")).thenReturn("value3");
            var res1 = cache.get("id1", externalGet);
            assertThat(res1).isEqualTo("value1");
            timer.tick();
            var res2 = cache.get("id2", externalGet);
            assertThat(res2).isEqualTo("value2");
            timer.tick();
            var res3 = cache.get("id3", externalGet);
            assertThat(res3).isEqualTo("value3");
            timer.tick();
            assertThat(cache.size()).isEqualTo(1);
        }
    }

    @Nested
    class PutWithMaximumSize {

        @Test
        void put__calls_external_and_updates_cache() {
            var cache = new AdiMemoryCache.Builder<String, String>().withMaximumSize(3).build();
            ExternalPut<String, String> externalPut = mock(ExternalPut.class);
            doNothing().when(externalPut).call(any(), any());
            cache.put("id", "value", externalPut);
            verify(externalPut, times(1)).call("id", "value");
            ExternalGet<String, String> externalGet = mock(ExternalGet.class);
            assertThat(cache.get("id", externalGet)).isEqualTo("value");
            verify(externalGet, times(0)).call("id");
        }
    }
}
