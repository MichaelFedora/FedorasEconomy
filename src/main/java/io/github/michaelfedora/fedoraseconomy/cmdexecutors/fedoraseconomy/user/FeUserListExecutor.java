package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.user;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeUniqueAccount;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeVirtualAccount;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 3/23/2016.
 */
public class FeUserListExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("list", "l");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("List all user/unique accounts"))
                .permission(PluginInfo.DATA_ROOT + ".user." + NAME)
                .executor(new FeUserListExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        EconomyService eco = tryGetEconomyService();

        Set<UniqueAccount> uniqueAccounts = new HashSet<>();

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES").executeQuery();
            while(resultSet.next()) {
                final String name = resultSet.getString("TABLE_NAME");

                if(!name.startsWith("account:"))
                    continue;

                eco.getOrCreateAccount(name).ifPresent((a) -> FeUniqueAccount.fromAccount(a).ifPresent(uniqueAccounts::add));
            }

        } catch(SQLException e) {
            throwSqlException(e);
        }

        Text.Builder tb = Text.builder().append(Text.of(TextColors.GOLD, TextStyles.BOLD, "UniqueAccounts: "));

        int count = 0;
        for(UniqueAccount ua : uniqueAccounts) {
            tb.append(Text.builder().onHover(TextActions.showText(Text.of(ua.getIdentifier()))).append(ua.getDisplayName()).build());
            if(++count < uniqueAccounts.size())
                tb.append(Text.of(", "));
        }

        src.sendMessage(tb.build());

        return CommandResult.success();
    }
}
