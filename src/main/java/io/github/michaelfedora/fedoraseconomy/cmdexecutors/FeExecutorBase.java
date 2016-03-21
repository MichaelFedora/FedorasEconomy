package io.github.michaelfedora.fedoraseconomy.cmdexecutors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;

/**
 * Created by Michael on 3/19/2016.
 */
public abstract class FeExecutorBase implements CommandExecutor {

    public Account tryGetAccount(User user) throws CommandException {
        EconomyService economyService;

        try {
            economyService = Sponge.getServiceManager().provide(EconomyService.class).orElseThrow(Exception::new);
        } catch(Exception e) {
            throw new CommandException(Text.of("Could not get EconomyService!"));
        }

        try {
            return economyService.getOrCreateAccount(user.getUniqueId()).orElseThrow(Exception::new);
        } catch(Exception e) {
            throw new CommandException(Text.of("Could not get Account!"));
        }
    }

}
