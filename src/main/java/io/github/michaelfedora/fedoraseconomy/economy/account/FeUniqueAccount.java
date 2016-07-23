package io.github.michaelfedora.fedoraseconomy.economy.account;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeUniqueAccount extends FeAccount implements UniqueAccount {

    private final UUID uuid;

    private static Text makeDisplayName(UUID uuid) {

        Optional<UserStorageService> opt_uss = Sponge.getServiceManager().provide(UserStorageService.class);

        if(opt_uss.isPresent()) {
            Optional<User> opt_user = opt_uss.get().get(uuid);
            if(opt_user.isPresent())
                return Text.of("UniqueAccount[", TextStyles.ITALIC, opt_user.get().getName(), TextStyles.RESET, "]");
        }

        return Text.of("UniqueAccount[" + uuid + "]");
    }

    public FeUniqueAccount(UUID uuid) {
        super(uuid.toString(), makeDisplayName(uuid));
        this.uuid = uuid;
    }

    public FeUniqueAccount(UniqueAccount account) {
        super(account, makeDisplayName(account.getUniqueId()));
        this.uuid = account.getUniqueId();
    }

    public static Optional<FeUniqueAccount> fromAccount(Account account) {
        final String uuid_string = account.getIdentifier().substring("account:".length());
        if(!uuid_string.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"))
            return Optional.empty();

        return Optional.of(new FeUniqueAccount(UUID.fromString(uuid_string)));
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
