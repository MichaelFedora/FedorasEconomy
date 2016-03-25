package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.user;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.FeExecutor;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeUserCleanExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("clean");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Cleans a users balances (i.e. removes bad references)"))
                .extendedDescription(Text.of("Cleans a users balances (i.e. removes bad references). WARNING: This is irreversible. USE AT YOUR OWN RISK."))
                .permission(PluginInfo.DATA_ROOT + ".user." + NAME)
                .arguments(GenericArguments.user(Text.of("user")))
                .executor(new FeUserCleanExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        User user = args.<User>getOne("user").orElseThrow(() -> new CommandException(Text.of("Bad param [user]!")));
        final String identifier = user.getUniqueId().toString().toLowerCase();

        Set<String> goodIds = new HashSet<>();
        tryGetUniqueAccount(user.getUniqueId()).getBalances().keySet().forEach((c) -> goodIds.add(c.getId()));

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM `" + identifier + "`").executeQuery();

            Text.Builder tb = Text.builder();
            Set<String> allIds = new HashSet<>();
            while(resultSet.next())
                allIds.add(resultSet.getString("currency"));

            for(String id : allIds) {
                if(!goodIds.contains(id)) {
                    PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM `" + identifier + "` WHERE currency=?");
                    preparedStatement.setString(1, id);
                    preparedStatement.execute();
                }
            }

        } catch(SQLException e) {
            throwSqlException(e);
        }

        return CommandResult.success();
    }
}
