package io.github.michaelfedora.fedoraseconomy.economy.account;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.economy.event.FeEconomyTransactionEvent;
import io.github.michaelfedora.fedoraseconomy.economy.event.FeEconomyTransferEvent;
import io.github.michaelfedora.fedoraseconomy.economy.transaction.FeTransactionResult;
import io.github.michaelfedora.fedoraseconomy.economy.transaction.FeTransferResult;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;
import org.spongepowered.api.service.economy.transaction.*;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

/**
 * Created by Michael on 3/18/2016.
 */
public abstract class FeAccount implements Account {

    private final String identifier;
    private final Text displayName;

    FeAccount(Account account, Text displayName) {

        this.identifier = account.getIdentifier();

        if(FeAccount.class.isAssignableFrom(account.getClass()))
            this.displayName = account.getDisplayName();
        else
            this.displayName = displayName;
    }

    FeAccount(String identifier, Text displayName) {

        if(!identifier.toLowerCase().startsWith("account:"))
            identifier = "account:" + identifier;

        this.identifier = identifier;
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this account.
     * <p>
     * <p>This should be used by plugins to get a human-readable name for an
     * account, regardless of the specific type ({@link UniqueAccount} or
     * {@link VirtualAccount}).</p>
     * <p>
     * <p>Its contents are dependent on the provider of {@link EconomyService}.
     * For example, an economy plugin could allow players to configure the
     * display name of their account</p>.
     *
     * @return the display name for this account.
     */
    @Override
    public Text getDisplayName() {
        return this.displayName;
    }

    /**
     * Gets the default balance of this account for the specified
     * {@link Currency}.
     * <p>
     * <p>The default balance is used when the balance is retrieved for the
     * first time for a given {@link Currency} on this account, or if no
     * balance is available for the {@link Context}s used when retrieving
     * a balance.</p>
     *
     * @param currency the currency to get the default balance for.
     * @return The default balance for the specified {@link Currency}.
     */
    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return BigDecimal.ZERO;
    }

    /**
     * Returns whether this account has a set balance for the specified
     * {@link Currency}, with the specified {@link Context}s.
     * <p>
     * <p>If this method returns <code>false</code>, then {@link #getDefaultBalance(Currency)}
     * will be used when retrieving a balance for the specified {@link Currency} with
     * the specified {@link Context}s.</p>
     *
     * @param currency The {@link Currency} to determine if a balance is set for.
     * @param contexts The {@link Context}s to use with the {@link Currency}.
     * @return Whether this account has a set balance for the specified {@link Currency} and {@link Context}s
     */
    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            PreparedStatement statement = conn.prepareStatement("SELECT 1 FROM `" + this.identifier + "` WHERE currency=? LIMIT 1");
            statement.setString(1, currency.getId());

            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return false;

        } catch(SQLException e) {
            FedorasEconomy.getLogger().error("SQL Error", e);
            return false;
        }

        return true;
    }

    /**
     * Returns a {@link BigDecimal} representative of the balance stored within this
     * {@link Account} for the {@link Currency} given and the set of {@link Context}s.
     * <p>
     * <p>The default result when the account does not have a balance of the
     * given {@link Currency} should be {@link BigDecimal#ZERO}.</p>
     * <p>
     * <p>The balance may be unavailable depending on the set of {@link Context}s used.</p>
     *
     * @param currency a {@link Currency} to check the balance of
     * @param contexts a set of contexts to check the balance against
     * @return he value for the specified {@link Currency} with the specified {@link Context}s.
     */
    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            PreparedStatement statement = conn.prepareStatement("SELECT balance FROM `" + this.identifier + "` WHERE currency=? LIMIT 1");
            statement.setString(1, currency.getId());

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getBigDecimal("balance");
            }

        } catch(SQLException e) {
            FedorasEconomy.getLogger().error("SQL Error", e);
        }

        return this.getDefaultBalance(currency);
    }

    /**
     * Returns a {@link Map} of all currently set balances the account holds within
     * the set of {@link Context}s.
     * <p>
     * <p>Amounts may differ depending on the {@link Context}s specified and
     * the implementation. The set of {@link Context}s may be empty.</p>
     * <p>
     * <p>{@link Currency} amounts which are 0 may or may not be included in the
     * returned mapping.</p>
     * <p>
     * <p>Changes to the returned {@link Map} will not be reflected in the underlying
     * {@link Account}.
     * See {@link #setBalance(Currency, BigDecimal, Cause, Set)}  to set values.</p>
     *
     * @param contexts the set of {@link Context}s to use with the specified amounts.
     * @return {@link Map} of {@link Currency} to {@link BigDecimal} amounts that this
     * account holds.
     */
    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {

        Map<Currency, BigDecimal> balances = new HashMap<>();
        Set<Currency> currencies;
        try {

            currencies = Sponge.getServiceManager().provide(EconomyService.class).orElseThrow(Exception::new).getCurrencies();

        } catch(Exception e) {

            FedorasEconomy.getLogger().error("Could not get EconomyService!", e);
            return balances;
        }

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            PreparedStatement statement = conn.prepareStatement("SELECT * FROM `" + this.identifier + "`");

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {

                Optional<Currency> currency = Optional.empty();

                for(Currency c : currencies) {
                    if(c.getId().equals(resultSet.getString("currency"))) {
                        currency = Optional.of(c);
                        break;
                    }
                }

                if(currency.isPresent())
                    balances.put(currency.get(), resultSet.getBigDecimal("balance"));
            }

        } catch(SQLException e) {
            FedorasEconomy.getLogger().error("SQL Error", e);
        }

        return balances;
    }

    /**
     * Sets the balance for this account to the specified amount for
     * the specified {@link Currency}, with the specified set of {@link Context}s.
     * <p>
     * <p>Negative balances may or may not be supported depending on
     * the {@link Currency} specified and the implementation.</p>
     *
     * @param currency The {@link Currency} to set the balance for
     * @param amount   The amount to set for the specified {@link Currency}
     * @param cause    The {@link Cause} for the transaction
     * @param contexts The set of {@link Context}s to use with the specified {@link Currency}
     * @return The result of the transaction
     */
    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return this.setBalance(currency, amount, cause, contexts, false);
    }

    public FeTransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts, boolean internal) {

        FeTransactionResult result;

        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.FLOOR);
        BigDecimal old_bal = BigDecimal.ZERO;
        BigDecimal diff = BigDecimal.ZERO;
        TransactionType trans_type = TransactionTypes.TRANSFER;
        int update = 0;

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            PreparedStatement statement = conn.prepareStatement("SELECT balance FROM `" + this.identifier + "` WHERE currency=? LIMIT 1");
            statement.setString(1, currency.getId());

            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()) {

                if(amount.compareTo(old_bal) > 0) {

                    trans_type = TransactionTypes.DEPOSIT;

                } else if(amount.compareTo(old_bal) < 0) {

                    trans_type = TransactionTypes.WITHDRAW;

                } // else if equal, it will stay the same (TransactionTypes.TRANSFER)

                statement = conn.prepareStatement("INSERT INTO `" + this.identifier + "`(currency, balance) values (?, ?)");
                statement.setString(1, currency.getId());
                statement.setBigDecimal(2, amount);

                update = statement.executeUpdate();

            } else {

                old_bal = resultSet.getBigDecimal("balance");

                diff = amount.subtract(old_bal).abs();

                if(amount.compareTo(old_bal) > 0) {

                    trans_type = TransactionTypes.DEPOSIT;

                } else if(amount.compareTo(old_bal) < 0) {

                    trans_type = TransactionTypes.WITHDRAW;

                } // else if equal, it will stay the same (TransactionTypes.TRANSFER)

                statement = conn.prepareStatement("UPDATE `" + this.identifier + "` SET balance=? WHERE currency=?");
                statement.setBigDecimal(1, amount);
                statement.setString(2, currency.getId());

                update = statement.executeUpdate();
            }

            result = new FeTransactionResult(this, currency, diff, contexts, ResultType.SUCCESS, trans_type, internal);

        } catch(SQLException e) {
            FedorasEconomy.getLogger().error("SQL Error", e);

            result = new FeTransactionResult(this, currency, diff, contexts, ResultType.FAILED, trans_type, internal);
        }

        // Do something with `update`?

        FeEconomyTransactionEvent.of(result, cause).fire();

        return result;
    }

    /**
     * Resets the balances for all {@link Currency}s used on this account to their
     * default values ({@link #getDefaultBalance(Currency)}), using the specified {@link Context}s.
     *
     * @param cause    The {@link Cause} for the transaction
     * @param contexts the {@link Context}s to use when resetting the balances.
     * @return A map of {@link Currency} to {@link TransactionResult}. Each
     * entry represents the result of resetting a particular currency.
     */
    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        return this.resetBalances(cause, contexts, false);
    }

    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts, boolean internal) {

        Map<Currency, TransactionResult> transactions = new HashMap<>();
        Set<Currency> currencies;
        try {
            currencies = Sponge.getServiceManager().provide(EconomyService.class).orElseThrow(Exception::new).getCurrencies();
        } catch(Exception e) {
            return null;
        }

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            PreparedStatement statement = conn.prepareStatement("SELECT currency FROM `" + this.identifier + "`");

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {

                Optional<Currency> currency = Optional.empty();

                for(Currency c : currencies) {
                    if(c.getId().equals(resultSet.getString("currency"))) {
                        currency = Optional.of(c);
                        break;
                    }
                }

                if(currency.isPresent()) {
                    TransactionResult tr = this.resetBalance(currency.get(), cause, contexts, internal);
                    transactions.put(currency.get(), tr);
                }
            }

        } catch(SQLException e) {
            FedorasEconomy.getLogger().error("SQL Error", e);
        }

        return transactions;
    }

    /**
     * Resets the balance for the specified {@link Currency} to its default value
     * ({@link #getDefaultBalance(Currency)}), using the specified {@link Context}s.
     *
     * @param currency The {@link Currency} to reset the balance for
     * @param cause    The {@link Cause} for the transaction
     * @param contexts The {@link Context}s to use when resetting the balance
     * @return The result of the transaction
     */
    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return this.setBalance(currency, getDefaultBalance(currency), cause, contexts, false);
    }

    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts, boolean internal) {
        return this.setBalance(currency, getDefaultBalance(currency), cause, contexts, internal);
    }

    /**
     * Deposits the specified amount of the specified {@link Currency} to this account,
     * using the specified {@link Context}s.
     *
     * @param currency The {@link Currency} to deposit the specified amount for
     * @param amount   The amount to deposit for the specified {@link Currency}.
     * @param cause    The {@link Cause} for the transaction
     * @param contexts the {@link Context}s to use with the specified {@link Currency}
     * @return The result of the transaction
     */
    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return this.deposit(currency, amount, cause, contexts, false);
    }

    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts, boolean internal) {

        FeTransactionResult result;

        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.FLOOR);
        int update = 0;

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            PreparedStatement statement = conn.prepareStatement("SELECT balance FROM `" + this.identifier + "` WHERE currency=? LIMIT 1");
            statement.setString(1, currency.getId());

            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()) {

                statement = conn.prepareStatement("INSERT INTO `" + this.identifier + "`(currency, balance) values (?, ?)");
                statement.setString(1, currency.getId());
                statement.setBigDecimal(2, amount);

                update = statement.executeUpdate();

            } else {

                BigDecimal old_balance = resultSet.getBigDecimal("balance");

                statement = conn.prepareStatement("UPDATE `" + this.identifier + "` SET balance=? WHERE currency=?");
                statement.setBigDecimal(1, old_balance.add(amount));
                statement.setString(2, currency.getId());

                update = statement.executeUpdate();
            }

            result = new FeTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT, internal);

        } catch(SQLException e) {
            FedorasEconomy.getLogger().error("SQL Error", e);

            result = new FeTransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT, internal);
        }

        // Do something with `update`?

        FeEconomyTransactionEvent.of(result, cause).fire();

        return result;
    }

    /**
     * Withdraws the specified amount of the specified {@link Currency} from this account,
     * using the specified {@link Context}s.
     *
     * @param currency The {@link Currency} to deposit the specified amount for
     * @param amount   The amount to deposit for the specified {@link Currency}
     * @param cause    The {@link Cause} for the transaction
     * @param contexts The {@link Context}s to use with the specified {@link Currency}
     * @return The result of the transaction
     */
    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return this.withdraw(currency, amount, cause, contexts, false);
    }

    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts, boolean internal) {

        FeTransactionResult result;

        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.FLOOR);
        int update = 0;

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            PreparedStatement statement = conn.prepareStatement("SELECT balance FROM `" + this.identifier + "` WHERE currency=? LIMIT 1");
            statement.setString(1, currency.getId());

            ResultSet resultSet = statement.executeQuery();

            // take a loan if they don't have enough funds?

            BigDecimal diff;

            if(!resultSet.next()) {

                 diff = this.getDefaultBalance(currency).subtract(amount);

                if(diff.compareTo(BigDecimal.ZERO) >= 0) {

                    statement = conn.prepareStatement("INSERT INTO `" + this.identifier + "`(currency, balance) values (?, ?)");
                    statement.setString(1, currency.getId());
                    statement.setBigDecimal(2, diff);

                    update = statement.executeUpdate();

                } else {

                    return new FeTransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW, internal);
                }

            } else {

                diff = resultSet.getBigDecimal("balance").subtract(amount);

                statement = conn.prepareStatement("UPDATE `" + this.identifier + "` SET balance=? WHERE currency=?");
                statement.setBigDecimal(1, diff);
                statement.setString(2, currency.getId());

                update = statement.executeUpdate();

            }

            result = new FeTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW, internal);

        } catch(SQLException e) {
            FedorasEconomy.getLogger().error("SQL Error", e);

            result = new FeTransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW, internal);
        }

        // Do something with `update`?

        FeEconomyTransactionEvent.of(result, cause).fire();

        return result;
    }

    /**
     * Transfers the specified amount of the specified {@link Currency} from this account
     * the destination account, using the specified {@link Context}s.
     * <p>
     * <p>This operation is a merged {@link #withdraw(Currency, BigDecimal, Cause, Set)}  from this account
     * with a {@link #deposit(Currency, BigDecimal, Cause, Set)}  into the specified account.</p>
     *
     * @param to       the Account to transfer the amounts to.
     * @param currency The {@link Currency} to transfer the specified amount for
     * @param amount   The amount to transfer for the specified {@link Currency}
     * @param cause    The {@link Cause} for the transaction
     * @param contexts The {@link Context}s to use with the specified {@link Currency} and account
     * @return a {@link TransferResult} representative of the effects of the
     * operation
     */
    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return this.transfer(to, currency, amount, cause, contexts, false);
    }

    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts, boolean internal) {

        FeTransferResult transferResult;

        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.FLOOR);

        if(!this.hasBalance(currency, contexts) && this.getDefaultBalance(currency).compareTo(amount) < 0) {

            transferResult = new FeTransferResult(this, to, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, internal);

            FeEconomyTransferEvent.of(transferResult, cause).fire();

            return transferResult;
        }

        if(this.getBalance(currency, contexts).compareTo(amount) < 0) {

            transferResult = new FeTransferResult(this, to, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, internal);

            FeEconomyTransferEvent.of(transferResult, cause).fire();

            return transferResult;
        }

        TransactionResult result = this.withdraw(currency, amount, cause, contexts);

        if(result.getResult() != ResultType.SUCCESS) {

            transferResult = new FeTransferResult(this, to, currency, amount, contexts, result.getResult(), internal);

            FeEconomyTransferEvent.of(transferResult, cause).fire();

            return transferResult;
        }

        result = to.deposit(currency, amount, cause, contexts);

        if(result.getResult() != ResultType.SUCCESS) {

            this.deposit(currency, amount, cause, contexts);

            transferResult = new FeTransferResult(this, to, currency, amount, contexts, result.getResult(), internal);

            FeEconomyTransferEvent.of(transferResult, cause).fire();

            return transferResult;
        }

        transferResult = new FeTransferResult(this, to, currency, amount, contexts, ResultType.SUCCESS, internal);

        FeEconomyTransferEvent.of(transferResult, cause).fire();

        return transferResult;
    }

    /**
     * Returns the identifier associated with this Contextual. Not guaranteed to
     * be human-readable.
     *
     * @return The unique identifier for this subject
     */
    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Calculate active contexts, using the {@link ContextCalculator}s for the
     * service.
     * <p>
     * <p>The result of these calculations may be cached.</p>
     *
     * @return An immutable set of active contexts
     */
    @Override
    public Set<Context> getActiveContexts() {
        return new HashSet<>();
    }
}
