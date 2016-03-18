/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
package io.github.michaelfedora.fedoraseconomy;

import com.google.inject.Inject;
import me.flibio.updatifier.Updatifier;
import org.slf4j.Logger;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

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

        // register listeners
        // register api
        // register registry
    }

    @Listener
    public void onInit(GameInitializationEvent gie) {

        registerCommands();
    }

    private void registerCommands() {

    }

    @Listener
    public void onLoadComplete(GameLoadCompleteEvent glce) {
        // database stuff
    }
}
