package ro.adriantosca.experimentalservicecache.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class Adi2MemoryCacheTest {
    @Test
    void get__with_concurrency_keeps_data_correct() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(5);
        ExternalGet<String, String> externalGet = mock(ExternalGet.class);
        when(externalGet.call(any())).thenAnswer(answer -> answer.getArgument(0));
        try {
            var size = 10;
            var cache = new Adi2MemoryCache.Builder<String, String>().withMaximumSize(size).build();
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
