package ro.adriantosca.experimentalservicecache.testservice;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ro.adriantosca.experimentalservicecache.cache.Cache;

@Service
public class ChristmasPackageAdiService implements ChristmasPackageService {
    private final Logger logger = LoggerFactory.getLogger(ChristmasPackageAdiService.class);

    private final ChristmasPackageLazyRepository repository;
    private final Cache<UUID, ChristmasPackage> cache;

    public ChristmasPackageAdiService(
            @NonNull ChristmasPackageLazyRepository repository,
            @NonNull Cache<UUID, ChristmasPackage> cache
    ) {
        this.repository = repository;
        this.cache = cache;
    }

    @NonNull
    public ChristmasPackage get(@NonNull UUID id) {
        logger.info("Getting package {}", id);
        var christmasPackage = cache.get(id, repository::get);
        logger.info("Got package {}", christmasPackage);
        return christmasPackage;
    }

    @Override
    public void put(@NonNull UUID id, @NonNull ChristmasPackage christmasPackage) {
        logger.info("Putting package {}", id);
        cache.put(id, christmasPackage, repository::put);
        logger.info("Put package {}", christmasPackage);
    }
}
