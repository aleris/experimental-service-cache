package ro.adriantosca.experimentalservicecache.testservice;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

public record ChristmasPackage(@NonNull UUID id, @NonNull String to, @NonNull List<String> gifts) { }
