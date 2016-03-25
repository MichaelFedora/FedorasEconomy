package io.github.michaelfedora.fedoraseconomy.economy.transaction;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeTransferResult extends FeTransactionResult implements TransferResult {

    private final Account accountTo;

    public FeTransferResult(Account accountFrom, Account accountTo, Currency currency, BigDecimal amount, Set<Context> contexts, ResultType result) {
        super(accountFrom, currency, amount, contexts, result, TransactionTypes.TRANSFER);
        this.accountTo = accountTo;
    }

    public FeTransferResult(TransferResult transferResult) {
        super(transferResult);
        this.accountTo = transferResult.getAccountTo();
    }

    /**
     * Gets the {@link Account} that an amount of a {@link Currency} is being transferred to.
     * <p>
     * <p>{@link #getAccount()} can be used to get the {@link Account} that the currency
     * is being transferred from.</p>
     *
     * @return The {@link Account}
     */
    @Override
    public Account getAccountTo() {
        return this.accountTo;
    }
}
