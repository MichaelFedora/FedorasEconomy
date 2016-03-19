package io.github.michaelfedora.fedoraseconomy.economy.event;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeEconomyTransferEvent implements EconomyTransactionEvent {

    /**
     * Get the cause for the event.
     *
     * @return The last cause
     */
    @Override
    public Cause getCause() {
        return null;
    }

    /**
     * Gets the {@link TransactionResult} for the transaction that occured.
     *
     * @return The {@link TransactionResult}
     */
    @Override
    public TransactionResult getTransactionResult() {
        return null;
    }
}
