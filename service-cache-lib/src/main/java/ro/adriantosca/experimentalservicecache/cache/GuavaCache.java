package ro.adriantosca.experimentalservicecache.cache;

import java.util.concurrent.ExecutionException;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.common.cache.LoadingCache;

public class GuavaCache<TIdentifier, TValue> implements Cache<TIdentifier, TValue> {

    @NonNull
    private final LoadingCache<TIdentifier, TValue> loadingCache;

    public GuavaCache(@NonNull LoadingCache<TIdentifier, TValue> loadingCache) {
        this.loadingCache = loadingCache;
    }

    @Override
    public @NonNull TValue get(@NonNull TIdentifier id, @NonNull ExternalGet<TValue, TIdentifier> externalGet) {
        try {
            return loadingCache.get(id, () -> externalGet.call(id));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(
            @NonNull TIdentifier id,
            @NonNull TValue value,
            @NonNull ExternalPut<TValue, TIdentifier> externalPut
    ) {
        try {
            loadingCache.get(id, () -> {
                externalPut.call(id, value);
                return value;
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
