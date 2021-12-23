package ro.adriantosca.experimentalservicecache.testservice;

import java.util.UUID;

import org.springframework.lang.NonNull;

public interface ChristmasPackageService {
    ChristmasPackage get(@NonNull UUID id);
    void put(@NonNull UUID id, @NonNull ChristmasPackage christmasPackage);
}
