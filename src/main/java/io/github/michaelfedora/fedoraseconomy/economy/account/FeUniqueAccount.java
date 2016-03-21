package io.github.michaelfedora.fedoraseconomy.economy.account;

import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import java.util.UUID;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeUniqueAccount extends FeAccount implements UniqueAccount {

    private UUID uuid;

    public FeUniqueAccount(UUID uuid) {
        super(uuid.toString(), Text.of("UniqueAccount[" + uuid + "]"));
        this.uuid = uuid;
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
