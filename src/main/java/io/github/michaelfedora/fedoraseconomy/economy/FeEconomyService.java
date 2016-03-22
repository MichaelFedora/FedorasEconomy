package io.github.michaelfedora.fedoraseconomy.economy;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeUniqueAccount;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeVirtualAccount;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.context.Contextual;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeEconomyService implements EconomyService {

    private Currency defaultCurrency;

    public FeEconomyService(Currency defaultCurrency) {
        this.setDefaultCurrency(defaultCurrency);
    }

    public void setDefaultCurrency(Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;

        if(!this.getCurrencies().contains(defaultCurrency))
            Sponge.getRegistry().register(Currency.class, defaultCurrency);
    }

    /**
     * Retrieves the default {@link Currency} used by the {@link EconomyService}.
     *
     * @return {@link Currency} default for the EconomyService
     * @see Currency
     */
    @Override
    public Currency getDefaultCurrency() {
        return this.defaultCurrency;
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
        return new HashSet<>(Sponge.getRegistry().getAllOf(Currency.class));
    }

    /**
     * Returns whether a {@link UniqueAccount} exists with the specified {@link UUID}.
     *
     * @param uuid The {@link UUID} of the account to check for
     * @return Whether a {@link UniqueAccount} exists with the specified {@link UUID}
     */
    @Override
    public boolean hasAccount(UUID uuid) {
        return this.hasAccount(uuid.toString());
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

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            ResultSet resultSet = conn.getMetaData().getTables(null, null, identifier, null);

            return resultSet.next();

        } catch(SQLException e) {

            FedorasEconomy.getLogger().error("SQL Error", e);

            return false;
        }
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

        Optional<Account> account = this.getOrCreateAccount(uuid.toString());

        if(!account.isPresent())
            return Optional.empty();

        return FeUniqueAccount.fromAccount(account.get()).map((a) -> (UniqueAccount) a);
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

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            int update = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `" + identifier + "`(currency VARCHAR(255), balance DECIMAL)").executeUpdate();

            return Optional.of(new FeVirtualAccount(identifier)).map((a) -> (Account) a);

        } catch(SQLException e) {

            FedorasEconomy.getLogger().error("SQL Error", e);

            return Optional.empty();
        }
    }

    /**
     * Register a function that calculates {@link Context}s relevant to a
     * {@link Contextual} given at the time the function is called.
     *
     * @param calculator The context calculator to register
     */
    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {
        // do nothing
    }
}
