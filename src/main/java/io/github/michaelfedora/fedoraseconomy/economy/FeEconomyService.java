package io.github.michaelfedora.fedoraseconomy.economy;

import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeEconomyService implements EconomyService {


    /**
     * Retrieves the default {@link Currency} used by the {@link EconomyService}.
     *
     * @return {@link Currency} default for the EconomyService
     * @see Currency
     */
    @Override
    public Currency getDefaultCurrency() {
        return null;
    }

    /**
     * Returns the {@link Set} of supported {@link Currency} objects that are
     * implemented by this EconomyService.
     * <p>
     * <p>The economy service provider may only support one currency, in which
     * case {@link #getDefaultCurrency()} will be the only member of the set.</p>
     * <p>
     * <p>The set returned is a read-only a view of all currencies available in
     * the EconomyService.</p>
     *
     * @return The {@link Set} of all {@link Currency}s
     */
    @Override
    public Set<Currency> getCurrencies() {
        return null;
    }

    /**
     * Returns whether a {@link UniqueAccount} exists with the specified {@link UUID}.
     *
     * @param uuid The {@link UUID} of the account to check for
     * @return Whether a {@link UniqueAccount} exists with the specified {@link UUID}
     */
    @Override
    public boolean hasAccount(UUID uuid) {
        return false;
    }

    /**
     * Returns whether an {@link Account} with the specified identifier exists.
     * <p>
     * <p>Depending on the implementation, the {@link Account} may be a {@link UniqueAccount} or
     * a {@link VirtualAccount}.
     *
     * @param identifier The identifier of the account to check for
     * @return Whether an {@link Account} with the specified identifier exists
     */
    @Override
    public boolean hasAccount(String identifier) {
        return false;
    }

    /**
     * Gets the {@link UniqueAccount} for the user with the specified {@link UUID}.
     * <p>
     * <p>If an account does not already exists with the specified {@link UUID}, it will be
     * created.</p>
     * <p>
     * <p>Creation might fail if the provided {@link UUID} does not correspond to an actual
     * player, or for an implementation-defined reason.</p>
     *
     * @param uuid The {@link UUID} of the account to get.
     * @return The {@link UniqueAccount}, if available.
     */
    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
        return null;
    }

    /**
     * Gets the {@link VirtualAccount} with the specified identifier
     * <p>
     * <p>Depending on the implementation, the {@link Account} may be a {@link UniqueAccount} or
     * a {@link VirtualAccount}.
     * <p>
     * <p>If an account does not already exists with the specified identifier, it will be
     * created.</p>
     * <p>
     * <p>Creation may fail for an implementation-defined reason.</p>
     *
     * @param identifier The identifier of the account to get.
     * @return The {@link Account}, if available.
     */
    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {
        return null;
    }

    /**
     * Register a function that calculates {@link Context}s relevant to a
     * {@link Contextual} given at the time the function is called.
     *
     * @param calculator The context calculator to register
     */
    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {

    }
}
