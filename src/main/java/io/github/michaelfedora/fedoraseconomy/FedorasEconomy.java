/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
package io.github.michaelfedora.fedoraseconomy;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.*;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.*;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.currency.*;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.user.*;
import io.github.michaelfedora.fedoraseconomy.config.FeConfig;
import io.github.michaelfedora.fedoraseconomy.config.FeCurrencySerializer;
import io.github.michaelfedora.fedoraseconomy.economy.FeCurrency;
import io.github.michaelfedora.fedoraseconomy.economy.FeEconomyService;
import io.github.michaelfedora.fedoraseconomy.listeners.EconomyTransactionListener;
import io.github.michaelfedora.fedoraseconomy.registry.CurrencyRegistry;
import me.flibio.updatifier.Updatifier;
import net.minecrell.mcstats.SpongeStatsLite;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * The main class yo!
 */
@Updatifier(repoName = "FedorasEconomy", repoOwner = "MichaelFedora", version = PluginInfo.VERSION)
@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION, description = PluginInfo.DESCRIPTION, authors = PluginInfo.AUTHORS)
public class FedorasEconomy {

    private static FedorasEconomy instance;

    @Inject
    private Logger logger;
    public static Logger getLogger() { return instance.logger; }

    private ConsoleSource console;
    public static ConsoleSource getConsole() { return instance.console; }

    @Inject
    private SpongeStatsLite stats;

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path sharedConfigDir;
    public static Path getSharedConfigDir() { return instance.sharedConfigDir; }

    public static Path getCurrenciesConfigDir() { return instance.sharedConfigDir.resolve("currencies"); }

    private static SqlService SQL;
    public static javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
        if(SQL == null)
            SQL = Sponge.getServiceManager().provide(SqlService.class).orElseThrow(() -> new SQLException("Could not get SqlService!"));

        return SQL.getDataSource(jdbcUrl);
    }

    public static Connection getAccountsConnection() throws SQLException {
        return getDataSource("jdbc:h2:./mods/FedorasData/econAccounts.db").getConnection();
    }

    public static Connection getTransactionListsConnection() throws SQLException {
        return getDataSource("jdbc:h2:./mods/FedorasData/econTransactionLists.db").getConnection();
    }

    //connBal.prepareStatement("CREATE TABLE IF NOT EXISTS " + identifier + "(currency VARCHAR(255), balance DECIMAL)").execute();
    // ex: fedorascurrency:coins | 500.25
    // ex: fedorascurrency:doubloons | 9001

    //connTrans.prepareStatement("CREATE TABLE IF NOT EXISTS " + identifier + "(id IDENTITY, stamp TIMESTAMP, otherParty VARCHAR(255), operation VARCHAR(255), amount DECIMAL, currency VARCHAR(255)");
    // ex: 101 | 2016-03-21 10:43:50 | null | DEPOSIT | 14.25 | fedorascurrency:coins
    // ex: 102 | 2016-03-21 10:45:29 | {uuid} | TRANSFER | 7 | fedorascurrency:doubloons

    private final LinkedHashMap<List<String>, CommandSpec> subCommands = new LinkedHashMap<>();
    public static LinkedHashMap<List<String>, CommandSpec> getSubCommands() {
        return instance.subCommands;
    }

    private final LinkedHashMap<String, LinkedHashMap<List<String>, CommandSpec>> grandChildCommands = new LinkedHashMap<>();
    public static Optional<LinkedHashMap<List<String>, CommandSpec>> getGrandChildCommands(String name) {
        return Optional.ofNullable(instance.grandChildCommands.get(name));
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent gpie) {
        instance = this;
        this.stats.start();
    }

    public static final FeCurrency defaultCurrency = new FeCurrency(
            "currency:fedorian",
            Text.of(TextColors.AQUA, "Fedorian"),
            Text.of(TextColors.AQUA, "Fed", TextColors.DARK_GRAY, TextStyles.OBFUSCATED, "ori", TextColors.AQUA, TextStyles.RESET, "ans"),
            Text.of(TextColors.AQUA, "f$"), true,
            3, Text.of(TextColors.DARK_GRAY, TextStyles.NONE).getFormat(),
            Text.of(TextColors.GRAY, ","));

    @Listener
    public void onInit(GameInitializationEvent gie) {

        this.console = Sponge.getServer().getConsole();

        //getLogger().info("===== " + PluginInfo.NAME + " v" + PluginInfo.VERSION + ": Initializing! =====");
        console.sendMessage(Text.of(TextStyles.BOLD, TextColors.GOLD, "===== ",
                TextStyles.RESET, TextColors.AQUA, PluginInfo.NAME, TextColors.GRAY, " v", TextColors.AQUA, PluginInfo.VERSION, TextColors.RESET, ": Initializing!",
                TextStyles.BOLD, TextColors.GOLD, " ====="));

        // register listeners - transaction list
        Sponge.getEventManager().registerListeners(this, new EconomyTransactionListener());


        // register registry
        Sponge.getRegistry().registerModule(Currency.class, CurrencyRegistry.instance);

        // config stuff

        File currencyConfigDir = getCurrenciesConfigDir().toFile();
        if(!currencyConfigDir.exists()) {
            try {
                currencyConfigDir.mkdir();
            } catch(SecurityException e) {
                logger.error("Could not make private directory!", e);
            }
        }

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(FeCurrency.class), new FeCurrencySerializer());

        FeConfig.initDefaultCurrencyId = defaultCurrency.getId();
        FeConfig.initialize();

        FeCurrencyReloadExecutor.exec(Sponge.getServer().getConsole());

        // register api
        Sponge.getServiceManager().setProvider(this, EconomyService.class, FeEconomyService.instance);
        logger.info("Registered the Economy API!");

        registerCommands();

        //getLogger().info("===== " + PluginInfo.NAME + " v" + PluginInfo.VERSION + ": Done! =====");
        console.sendMessage(Text.of(TextStyles.BOLD, TextColors.GOLD, "===== ",
                TextStyles.RESET, TextColors.AQUA, PluginInfo.NAME, TextColors.GRAY, " v", TextColors.AQUA, PluginInfo.VERSION, TextColors.RESET, ": Done!",
                TextStyles.BOLD, TextColors.GOLD, " ====="));
    }

    @Listener
    public void OnGameLoadComplete(GameLoadCompleteEvent glce) {
        if(FeConfig.instance.getCleanOnStartup()) {
            FeCleanExecutor.cleanAll();
            FePurgeExecutor.run();
        }
    }

    private void registerCommands() {
        CommandManager commandManager = Sponge.getCommandManager();

        commandManager.register(this, BalanceExecutor.create(), BalanceExecutor.ALIASES);
        commandManager.register(this, PayExecutor.create(), PayExecutor.ALIASES);
        commandManager.register(this, BalanceExecutor.createAsMoneyAlias(), BalanceExecutor.MONEY_ALIASES);

        LinkedHashMap<List<String>, CommandSpec> currencyCommands = new LinkedHashMap<>();

        currencyCommands.put(FeCurrencyHelpExecutor.ALIASES, FeCurrencyHelpExecutor.create());
        currencyCommands.put(FeCurrencyListExecutor.ALIASES, FeCurrencyListExecutor.create());
        currencyCommands.put(FeCurrencyDetailsExecutor.ALIASES, FeCurrencyDetailsExecutor.create());
        currencyCommands.put(FeCurrencySetDefaultExecutor.ALIASES, FeCurrencySetDefaultExecutor.create());
        currencyCommands.put(FeCurrencyReloadExecutor.ALIASES, FeCurrencyReloadExecutor.create());

        subCommands.put(FeCurrencyExecutor.ALIASES, FeCurrencyExecutor.create(currencyCommands));

        grandChildCommands.put(FeCurrencyExecutor.NAME, currencyCommands);

        // === ===

        LinkedHashMap<List<String>, CommandSpec> userCommands = new LinkedHashMap<>();

        userCommands.put(FeUserHelpExecutor.ALIASES, FeUserHelpExecutor.create());
        userCommands.put(FeUserListExecutor.ALIASES, FeUserListExecutor.create());
        userCommands.put(FeUserCleanExecutor.ALIASES, FeUserCleanExecutor.create());
        userCommands.put(FeUserGetExecutor.ALIASES, FeUserGetExecutor.create());
        userCommands.put(FeUserGetRawExecutor.ALIASES, FeUserGetRawExecutor.create());
        userCommands.put(FeUserSetExecutor.ALIASES, FeUserSetExecutor.create());
        userCommands.put(FeUserAddExecutor.ALIASES, FeUserAddExecutor.create());
        userCommands.put(FeUserPayExecutor.ALIASES, FeUserPayExecutor.create());
        userCommands.put(FeUserTransferExecutor.ALIASES, FeUserTransferExecutor.create());
        userCommands.put(FeUserResetExecutor.ALIASES, FeUserResetExecutor.create());

        subCommands.put(FeUserExecutor.ALIASES, FeUserExecutor.create(userCommands));

        grandChildCommands.put(FeUserExecutor.NAME, userCommands);

        // === ===

        subCommands.put(FeReloadExecutor.ALIASES, FeReloadExecutor.create());
        subCommands.put(FeConfigExecutor.ALIASES, FeConfigExecutor.create());
        subCommands.put(FeGetConfigExecutor.ALIASES, FeGetConfigExecutor.create());
        subCommands.put(FeHelpExecutor.ALIASES, FeHelpExecutor.create());
        subCommands.put(FeListExecutor.ALIASES, FeListExecutor.create());
        subCommands.put(FeCleanExecutor.ALIASES, FeCleanExecutor.create());
        subCommands.put(FePurgeExecutor.ALIASES, FePurgeExecutor.create());
        subCommands.put(FeGetExecutor.ALIASES, FeGetExecutor.create());
        subCommands.put(FeGetRawExecutor.ALIASES, FeGetRawExecutor.create());
        subCommands.put(FeSetExecutor.ALIASES, FeSetExecutor.create());
        subCommands.put(FeAddExecutor.ALIASES, FeAddExecutor.create());
        subCommands.put(FePayExecutor.ALIASES, FePayExecutor.create());
        subCommands.put(FeTransferExecutor.ALIASES, FeTransferExecutor.create());
        subCommands.put(FeResetExecutor.ALIASES, FeResetExecutor.create());
        subCommands.put(FeTossExecutor.ALIASES, FeTossExecutor.create());

        commandManager.register(this, FeExecutor.create(subCommands), FeExecutor.ALIASES);
    }
}
