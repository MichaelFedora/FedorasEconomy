package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.user;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeUserGetRawExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("getraw", "rawbalance", "rawbal");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Get's the raw balance of a user (i.e. including currencies which aren't available"))
                .permission(PluginInfo.DATA_ROOT + ".user." + NAME)
                .arguments(GenericArguments.user(Text.of("user")))
                .executor(new FeUserGetRawExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        User user = args.<User>getOne("user").orElseThrow(() -> new CommandException(Text.of("Bad param [user]!")));

        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM `account:" + user.getUniqueId() + "`").executeQuery();
            Text.Builder tb = Text.builder();
            tb.append(Text.of(TextColors.GOLD, "===== ", TextColors.AQUA, user.getName(), TextColors.GOLD, "'s Raw Balances", " ====="));
            while(resultSet.next()) {
                tb.append(Text.of('\n', TextColors.GOLD, '[', TextColors.AQUA, resultSet.getString("currency"), TextColors.GRAY, ": ", TextColors.DARK_GRAY, resultSet.getBigDecimal("balance"), TextColors.GOLD, ']'));
            }
            src.sendMessage(tb.build());

        } catch(SQLException e) {
            throwSqlException(e);
        }

        return CommandResult.success();
    }
}
