package ro.adriantosca.experimentalservicecache.testservice;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "christmas-packages")
public class ChristmasPackageSpringService implements ChristmasPackageService {
    private final Logger logger = LoggerFactory.getLogger(ChristmasPackageSpringService.class);

    private final ChristmasPackageLazyRepository repository;

    public ChristmasPackageSpringService(@NonNull ChristmasPackageLazyRepository repository) {
        this.repository = repository;
    }

    @Cacheable
    @NonNull
    public ChristmasPackage get(@NonNull UUID id) {
        logger.info("Getting the package for {}", id);
        final var christmasPackage = repository.get(id);
        logger.info("Got the package {}", christmasPackage);
        return christmasPackage;
    }

    @Override
    @CachePut(key="#id")
    public void put(@NonNull UUID id, @NonNull ChristmasPackage christmasPackage) {
        logger.info("Putting the package for {}", id);
        repository.put(id, christmasPackage);
        logger.info("Put the package {}", christmasPackage);
    }
}
