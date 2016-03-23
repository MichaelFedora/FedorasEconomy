package io.github.michaelfedora.fedoraseconomy.economy.event;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeEconomyTransactionEvent implements EconomyTransactionEvent {

    private TransactionResult result;
    private Cause cause;

    public FeEconomyTransactionEvent(TransactionResult result, Cause cause) {
        this.result = result;
        this.cause = cause;
    }

    public static FeEconomyTransactionEvent of(TransactionResult result, Cause cause) {
        return new FeEconomyTransactionEvent(result, cause);
    }

    public boolean fire() {
        return Sponge.getEventManager().post(this);
    }

    /**
     * Get the cause for the event.
     *
     * @return The last cause
     */
    @Override
    public Cause getCause() {
        return this.cause;
    }

    /**
     * Gets the {@link TransactionResult} for the transaction that occurred.
     *
     * @return The {@link TransactionResult}
     */
    @Override
    public TransactionResult getTransactionResult() {
        return this.result;
    }
}
