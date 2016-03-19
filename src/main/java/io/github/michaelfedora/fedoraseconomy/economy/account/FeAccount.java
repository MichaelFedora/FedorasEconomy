package io.github.michaelfedora.fedoraseconomy.economy.account;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.account.Account;

import java.util.Set;

/**
 * Created by Michael on 3/18/2016.
 */
public abstract class FeAccount implements Account {
    /**
     * Returns the identifier associated with this Contextual. Not guaranteed to
     * be human-readable.
     *
     * @return The unique identifier for this subject
     */
    @Override
    public String getIdentifier() {
        return null;
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
        return null;
    }
}
