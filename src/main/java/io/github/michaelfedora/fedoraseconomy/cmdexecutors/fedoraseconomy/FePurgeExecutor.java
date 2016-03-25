package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 3/25/2016.
 */
public class FePurgeExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("purge");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Purges any accounts which lack data"))
                .extendedDescription(Text.of("Cleans an accounts' balances (does not include redundant data / bad references). WARNING: This is irreversible. USE AT YOUR OWN RISK."))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .executor(new FePurgeExecutor())
                .build();
    }

    public static void run() {
        try {
            runWithReceiver(Sponge.getServer().getConsole());
        } catch(CommandException e) {
            // do nothing
        }
    }

    private static void runWithReceiver(MessageReceiver receiver) throws CommandException {
        try(Connection conn = FedorasEconomy.getAccountsConnection()) {

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES").executeQuery();

            Set<String> tableNames = new HashSet<>();
            while(resultSet.next()) {
                final String table_type = resultSet.getString("TABLE_TYPE");
                final String name = resultSet.getString("TABLE_NAME");

                if(!table_type.equals("TABLE") || !name.toLowerCase().startsWith("account:"))
                    continue;

                tableNames.add(name);
            }

            Set<String> tablesToDelete = new HashSet<>();
            for(String tableName : tableNames) {
                resultSet = conn.prepareStatement("SELECT 1 FROM `" + tableName + "`").executeQuery();
                if(!resultSet.next())
                    tablesToDelete.add(tableName);
            }

            for(String tableName : tablesToDelete) {
                receiver.sendMessage(Text.of(TextStyles.BOLD, TextColors.GOLD, "Purged Account: ", TextStyles.RESET, TextColors.AQUA, tableName));
                conn.prepareStatement("DROP TABLE `" + tableName + "`").execute();
            }

        } catch(SQLException e) {
            throwSqlException(e);
        }
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        runWithReceiver(src);

        return CommandResult.success();
    }
}
