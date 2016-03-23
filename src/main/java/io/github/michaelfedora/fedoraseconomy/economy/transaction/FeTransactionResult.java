package io.github.michaelfedora.fedoraseconomy.economy.transaction;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeTransactionResult implements TransactionResult {

    private final Account account;
    private final Currency currency;
    private final BigDecimal amount;
    private final Set<Context> contexts;
    private final ResultType result;
    private final TransactionType type;

    public FeTransactionResult(Account account, Currency currency, BigDecimal amount, Set<Context> contexts, ResultType result, TransactionType type) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.contexts = contexts;
        this.result = result;
        this.type = type;
    }

    /**
     * Gets the {@link Account} involved in the transaction.
     *
     * @return The {@link Account}
     */
    @Override
    public Account getAccount() {
        return this.account;
    }

    /**
     * Gets the {@link Currency} involved in the transaction.
     *
     * @return The {@link Currency}
     */
    @Override
    public Currency getCurrency() {
        return this.currency;
    }

    /**
     * Gets the amount of the {@link Currency} involved in the transaction.
     *
     * @return The amount
     */
    @Override
    public BigDecimal getAmount() {
        return this.amount;
    }

    /**
     * Returns the set of {@link Context}s used to perform the
     * transaction.
     *
     * @return optional set of contexts
     */
    @Override
    public Set<Context> getContexts() {
        return this.contexts;
    }

    /**
     * Get the {@link ResultType} of this transaction.
     *
     * @return resultType
     */
    @Override
    public ResultType getResult() {
        return this.result;
    }

    /**
     * Returns the {@link TransactionType} of this result.
     *
     * @return type of Transaction
     */
    @Override
    public TransactionType getType() {
        return this.type;
    }
}
