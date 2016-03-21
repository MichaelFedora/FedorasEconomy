/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
package io.github.michaelfedora.fedoraseconomy;

import com.google.inject.Inject;
import io.github.michaelfedora.fedoraseconomy.economy.FeCurrency;
import io.github.michaelfedora.fedoraseconomy.economy.FeEconomyService;
import io.github.michaelfedora.fedoraseconomy.registry.CurrencyRegistry;
import me.flibio.updatifier.Updatifier;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;

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

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir; //TODO: Implement config
    public static Path getConfigDir() { return instance.configDir; }

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

    private LinkedHashMap<List<String>, CommandSpec> subCommands;
    public static LinkedHashMap<List<String>, CommandSpec> getSubCommands() { return instance.subCommands; }
    private HashMap<String, LinkedHashMap<List<String>, CommandSpec>> grandChildCommands;
    public static Optional<LinkedHashMap<List<String>, CommandSpec>> getGrandChildCommands(String key) {

        if(instance.grandChildCommands.containsKey(key))
            return Optional.of(instance.grandChildCommands.get(key));

        return Optional.empty();
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent gpie) {
        instance = this;
    }

    public static Currency defaultCurrency = new FeCurrency("fedoraseconomy:default", Text.of(TextColors.GOLD, "Doubloon"), Text.of(TextColors.GOLD, "Doubloons"), Text.of(TextColors.GOLD, "d$"), false, 0, TextColors.GRAY, TextStyles.NONE);

    @Listener
    public void onInit(GameInitializationEvent gie) {

        getLogger().info("===== " + PluginInfo.NAME + " v" + PluginInfo.VERSION + ": Initializing! =====");

        // register listeners - transaction list

        // register registry
        CurrencyRegistry currencyRegistry = new CurrencyRegistry();
        currencyRegistry.add(defaultCurrency);
        Sponge.getRegistry().registerModule(Currency.class, currencyRegistry);

        // register api
        Sponge.getServiceManager().setProvider(this, EconomyService.class, new FeEconomyService(defaultCurrency));

        registerCommands();

        getLogger().info("===== " + PluginInfo.NAME + " v" + PluginInfo.VERSION + ": Done! =====");
    }

    private void registerCommands() {

    }

    @Listener
    public void onLoadComplete(GameLoadCompleteEvent glce) {
        // database stuff
    }
}
