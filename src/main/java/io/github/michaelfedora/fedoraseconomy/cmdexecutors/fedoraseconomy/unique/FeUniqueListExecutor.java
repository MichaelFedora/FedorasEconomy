package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.unique;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.economy.FeEconomyService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Michael on 3/23/2016.
 */
public class FeUniqueListExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("list", "l");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("List all unique accounts"))
                .permission(PluginInfo.DATA_ROOT + ".unique." + NAME)
                .executor(new FeUniqueListExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        EconomyService eco = tryGetEconomyService();

        Collection<UniqueAccount> accounts;

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES").executeQuery();
            while(resultSet.next()) {
                eco.getOrCreateAccount(resultSet.getString("TABLE_NAME"));
            }

        } catch(SQLException e) {

        }

        Text.Builder tb = Text.builder().append(Text.of(TextColors.GOLD, TextStyles.BOLD, "Currencies: "));

        int count = 0;
        for(UniqueAccount ua : accounts) {
            tb.append(Text.builder().onHover(TextActions.showText(Text.of(ua.getIdentifier()))).append(ua.getDisplayName()).build());
            if(++count < accounts.size())
                tb.append(Text.of(", "));
        }

        src.sendMessage(tb.build());

        return CommandResult.success();
    }
}
