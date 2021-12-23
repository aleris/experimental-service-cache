package ro.adriantosca.experimentalservicecache.cache;

import org.checkerframework.checker.nullness.qual.NonNull;

record CachedItem<TIdentifier, TValue>(
        @NonNull TIdentifier id,
        @NonNull TValue value
) implements Identifiable<TIdentifier> { }
