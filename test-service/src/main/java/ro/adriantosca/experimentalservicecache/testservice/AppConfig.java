package ro.adriantosca.experimentalservicecache.testservice;

import java.time.Duration;
import java.util.UUID;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import ro.adriantosca.experimentalservicecache.cache.Adi2MemoryCache;
import ro.adriantosca.experimentalservicecache.cache.AdiMemoryCache;
import ro.adriantosca.experimentalservicecache.cache.Cache;

@Configuration
@EnableCaching
public class AppConfig {
//    @Bean
//    @Primary
//    @NonNull
//    public ChristmasPackageService christmasPackageService(
//            @NonNull ChristmasPackageLazyRepository repository,
//            @NonNull Cache<UUID, ChristmasPackage> cache
//    ) {
//        return new ChristmasPackageAdiService(repository, cache);
//    }

// Comment above and uncomment this to use the implementation with spring cache
    @Bean
    @Primary
    @NonNull
    public ChristmasPackageService christmasPackageService(@NonNull ChristmasPackageLazyRepository repository) {
        return new ChristmasPackageSpringService(repository);
    }

    @Bean
    @NonNull
    public CacheManager cacheManager() {
        var manager = new CaffeineCacheManager("christmas-packages");
        var caffeine = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(Duration.ofSeconds(10));
        manager.setCaffeine(caffeine);
        return manager;
    }

    @Bean
    @NonNull
    public Cache<UUID, ChristmasPackage> serviceCache() {
        return new Adi2MemoryCache.Builder<UUID, ChristmasPackage>()
                .withMaximumSize(100)
                .withExpireAfterAccess(Duration.ofSeconds(10))
                .build();
    }
}
