package io.github.michaelfedora.fedoraseconomy.registry;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.service.economy.Currency;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Michael on 3/21/2016.
 */
public class CurrencyRegistry implements AdditionalCatalogRegistryModule<Currency> {

    private final Map<String, Currency> currencyMap = new HashMap<>();

    @Override
    public Optional<Currency> getById(String id) {
        return Optional.ofNullable(this.currencyMap.get(checkNotNull(id, "Id cannot be null!")));
    }

    @Override
    public Collection<Currency> getAll() {
        return ImmutableSet.copyOf(this.currencyMap.values());
    }

    @Override
    public void registerAdditionalCatalog(Currency extraCatalog) {
        checkArgument(!this.currencyMap.containsKey(extraCatalog.getId()),
                "Currency with same id is already registered: {}", extraCatalog.getId());
        this.currencyMap.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public void registerDefaults() {

    }
}
