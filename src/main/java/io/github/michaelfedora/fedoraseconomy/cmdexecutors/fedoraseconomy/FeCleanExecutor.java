package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeCleanExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("clean");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Cleans up an accounts' balances (i.e. removes bad references)"))
                .extendedDescription(Text.of("Cleans an accounts' balances (i.e. removes bad references). WARNING: This is irreversible. USE AT YOUR OWN RISK."))
                .permission(PluginInfo.DATA_ROOT + ".user." + NAME)
                .arguments(GenericArguments.string(Text.of("accountName")))
                .executor(new FeCleanExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        String accountName = args.<String>getOne("accountName").orElseThrow(() -> new CommandException(Text.of("Bad param [accountName]!"))).toLowerCase();

        Set<String> goodIds = new HashSet<>();
        Account account = tryGetAccount(accountName);
        account.getBalances().keySet().forEach((c) -> goodIds.add(c.getId()));

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM `" + account.getIdentifier() + "`").executeQuery();

            Set<String> allIds = new HashSet<>();
            while(resultSet.next())
                allIds.add(resultSet.getString("currency"));

            for(String id : allIds) {
                if(!goodIds.contains(id)) {
                    PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM `" + account.getIdentifier() + "` WHERE currency=?");
                    preparedStatement.setString(1, id);
                    preparedStatement.execute();
                    src.sendMessage(Text.of(TextStyles.BOLD, TextColors.GOLD, "Deleted bad reference in ", TextColors.AQUA, account.getDisplayName(), TextColors.GOLD, "'s account: ", TextStyles.RESET, TextColors.AQUA, id));
                }
            }

        } catch(SQLException e) {
            throwSqlException(e);
        }

        return CommandResult.success();
    }

    public static void cleanAll() {

        CommandSource src = Sponge.getServer().getConsole();

        Set<String> goodIds = new HashSet<>();
        Sponge.getRegistry().getAllOf(Currency.class).forEach((c) -> goodIds.add(c.getId()));

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES").executeQuery();

            Set<String> accountNames = new HashSet<>();
            while(resultSet.next()) {
                final String table_type = resultSet.getString("TABLE_TYPE");
                final String name = resultSet.getString("TABLE_NAME");

                if(!table_type.equals("TABLE") || !name.toLowerCase().startsWith("account:"))
                    continue;

                accountNames.add(name);
            }

            for(String accountName : accountNames) {
                resultSet = conn.prepareStatement("SELECT currency FROM `" + accountName + "`").executeQuery();

                Set<String> allIds = new HashSet<>();
                while(resultSet.next())
                    allIds.add(resultSet.getString("currency"));

                for(String id : allIds) {
                    if(!goodIds.contains(id)) {
                        PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM `" + accountName + "` WHERE currency=?");
                        preparedStatement.setString(1, id);
                        preparedStatement.execute();
                        src.sendMessage(Text.of(TextStyles.BOLD, TextColors.GOLD, "Deleted bad reference: ", TextStyles.RESET, TextColors.AQUA, id));
                    }
                }
            }
        } catch(SQLException e) {
            FedorasEconomy.getLogger().error("SQL Error", e);
        }
    }
}
