package io.github.michaelfedora.fedoraseconomy.cmdexecutors;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeAccount;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeUniqueAccount;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeVirtualAccount;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Michael on 3/19/2016.
 */
public abstract class FeExecutorBase implements CommandExecutor {

    protected static EconomyService tryGetEconomyService() throws CommandException {
        try {
            return Sponge.getServiceManager().provide(EconomyService.class).orElseThrow(Exception::new);
        } catch(Exception e) {
            throw new CommandException(Text.of("Could not get EconomyService!"));
        }
    }

    protected static FeAccount tryGetAccount(String key) throws CommandException {
        EconomyService economyService = tryGetEconomyService();

        try {
            Account a =  economyService.getOrCreateAccount(key).orElseThrow(Exception::new);
            if(FeAccount.class.isAssignableFrom(a.getClass())) return (FeAccount) a;
            else return new FeVirtualAccount(a);
        } catch(Exception e) {
            throw new CommandException(Text.of("Could not get Account!"));
        }
    }

    protected static FeUniqueAccount tryGetUniqueAccount(UUID uuid) throws CommandException {
        EconomyService economyService = tryGetEconomyService();

        try {
            UniqueAccount ua = economyService.getOrCreateAccount(uuid).orElseThrow(Exception::new);
            if(FeUniqueAccount.class.isAssignableFrom(ua.getClass())) return (FeUniqueAccount) ua;
            else return new FeUniqueAccount(ua);
        } catch(Exception e) {
            throw new CommandException(Text.of("Could not get UniqueAccount!"));
        }
    }

    protected static void throwSqlException(SQLException e) throws CommandException {
        FedorasEconomy.getLogger().error("SQL Error", e);
        throw new CommandException(Text.of("SQL Error"), e);
    }

}
