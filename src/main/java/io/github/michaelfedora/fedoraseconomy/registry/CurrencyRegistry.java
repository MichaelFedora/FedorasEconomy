package io.github.michaelfedora.fedoraseconomy.registry;

import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.service.economy.Currency;

import java.util.*;

/**
 * Created by Michael on 3/21/2016.
 */
public class CurrencyRegistry implements CatalogRegistryModule<Currency> {

    private Set<Currency> currencies = new HashSet<>();

    public void add(Currency currency) {
        this.currencies.add(currency);
    }

    public void remove(Currency currency) {
        this.currencies.remove(currency);
    }

    public boolean contains(Currency currency) {
        return this.currencies.contains(currency);
    }

    @Override
    public Optional<Currency> getById(String id) {
        for(Currency c : this.currencies) {
            if(c.getId().equals(id)) {
                return Optional.of(c);
            }
        }

        return Optional.empty();
    }

    @Override
    public Collection<Currency> getAll() {
        return this.currencies;
    }
}
