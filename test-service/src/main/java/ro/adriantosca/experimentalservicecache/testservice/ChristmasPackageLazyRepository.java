package ro.adriantosca.experimentalservicecache.testservice;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public class ChristmasPackageLazyRepository {
    private final Logger logger = LoggerFactory.getLogger(ChristmasPackageLazyRepository.class);

    @NonNull
    public ChristmasPackage get(@NonNull UUID id) {
        logger.info("Getting for {}", id);

        try {
            Thread.sleep(251);
        } catch (InterruptedException e) {
            throw new RuntimeException("Sorry no presents this year.");
        }
        var christmasPackage = new ChristmasPackage(
                id,
                "My daughter",
                List.of(
                        "%s LEGOs".formatted(ThreadLocalRandom.current().nextInt(1, 100)),
                        "and", "too", "many", "others"
                )
        );
        logger.info("Got {}", christmasPackage);
        return christmasPackage;
    }

    public void put(@NonNull UUID id, @NonNull ChristmasPackage christmasPackage) {
        logger.info("Putting for {}", id);
        try {
            Thread.sleep(251);
        } catch (InterruptedException e) {
            throw new RuntimeException("Sorry no presents this year.");
        }
        logger.info("Put {}", christmasPackage);
    }
}
