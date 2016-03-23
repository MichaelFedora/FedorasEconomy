package io.github.michaelfedora.fedoraseconomy.economy.account;

import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeUniqueAccount extends FeAccount implements UniqueAccount {

    private final UUID uuid;

    public FeUniqueAccount(UUID uuid) {
        super(uuid.toString(), Text.of("UniqueAccount[" + uuid + "]"));
        this.uuid = uuid;
    }

    public static Optional<FeUniqueAccount> fromAccount(Account account) {
        String identifier = account.getIdentifier();
        if(!identifier.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"))
            return Optional.empty();

        return Optional.of(new FeUniqueAccount(UUID.fromString(identifier)));
    }

    /**
     * Gets the unique ID for this object.
     *
     * @return The {@link UUID}
     */
    @Override
    public UUID getUniqueId() {
        return uuid;
    }
}
