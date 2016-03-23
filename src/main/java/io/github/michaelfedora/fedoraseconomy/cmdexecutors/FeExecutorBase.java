package io.github.michaelfedora.fedoraseconomy.cmdexecutors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import java.util.UUID;

/**
 * Created by Michael on 3/19/2016.
 */
public abstract class FeExecutorBase implements CommandExecutor {

    public EconomyService tryGetEconomyService() throws CommandException {
        try {
            return Sponge.getServiceManager().provide(EconomyService.class).orElseThrow(Exception::new);
        } catch(Exception e) {
            throw new CommandException(Text.of("Could not get EconomyService!"));
        }
    }

    public Account tryGetAccount(String key) throws CommandException {
        EconomyService economyService = this.tryGetEconomyService();

        try {
            return economyService.getOrCreateAccount(key).orElseThrow(Exception::new);
        } catch(Exception e) {
            throw new CommandException(Text.of("Could not get Account!"));
        }
    }

    public UniqueAccount tryGetUniqueAccount(UUID uuid) throws CommandException {
        EconomyService economyService = this.tryGetEconomyService();

        try {
            return economyService.getOrCreateAccount(uuid).orElseThrow(Exception::new);
        } catch(Exception e) {
            throw new CommandException(Text.of("Could not get Account!"));
        }
    }

}
