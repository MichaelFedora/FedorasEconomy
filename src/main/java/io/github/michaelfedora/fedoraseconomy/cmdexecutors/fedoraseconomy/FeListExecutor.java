package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeListExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("list", "l");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("List all accounts"))
                .permission(PluginInfo.DATA_ROOT + ".user." + NAME)
                .executor(new FeListExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        EconomyService eco = tryGetEconomyService();

        Set<Account> accounts = new HashSet<>();

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES").executeQuery();
            while(resultSet.next()) {
                final String table_type = resultSet.getString("TABLE_TYPE");
                final String name = resultSet.getString("TABLE_NAME");

                if(!table_type.equals("TABLE") || !name.toLowerCase().startsWith("account:"))
                    continue;

                eco.getOrCreateAccount(name).ifPresent(accounts::add);
            }

        } catch(SQLException e) {
            throwSqlException(e);
        }

        Text.Builder tb = Text.builder().append(Text.of(TextColors.GOLD, TextStyles.BOLD, "Accounts: "));

        int count = 0;
        for(Account a : accounts) {
            tb.append(Text.builder().onHover(TextActions.showText(Text.of(a.getIdentifier()))).append(Text.of(TextColors.AQUA, a.getDisplayName())).build());
            if(++count < accounts.size())
                tb.append(Text.of(TextColors.GRAY, ", "));
        }

        src.sendMessage(tb.build());

        return CommandResult.success();
    }
}
