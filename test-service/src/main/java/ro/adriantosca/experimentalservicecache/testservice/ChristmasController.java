package ro.adriantosca.experimentalservicecache.testservice;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("presents")
public class ChristmasController {
    private final ChristmasPackageService service;

    public ChristmasController(@NonNull ChristmasPackageService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @NonNull
    public ChristmasPackage get(@PathVariable @NonNull UUID id) {
        return service.get(id);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void put(@PathVariable @NonNull UUID id, @RequestBody @NonNull ChristmasPackage christmasPackage) {
        service.put(id, christmasPackage);
    }
}
