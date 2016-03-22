package io.github.michaelfedora.fedoraseconomy.config;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;

/**
 * Created by Michael on 3/21/2016.
 */
public final class Config {

    private ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(FedorasEconomy.getConfigDir().resolve(PluginInfo.DATA_ROOT + ".cfg")).build();
    private CommentedConfigurationNode root;
    private ConfigurationOptions options;

    public Config(ConfigurationOptions options) {
        this.options = options;
    }

    public Config() {
        this.options = null;
    }

    public void load() {
        try {
            if(options != null)
                root = loader.load(options);
            else
                root = loader.load();
        } catch(IOException e) {
            FedorasEconomy.getLogger().error("Could not load configuration!", e);
        }
    }

    public void save() {
        try {
            loader.save(root);
        } catch(IOException e) {
            FedorasEconomy.getLogger().error("Could not save configuration!", e);
        }
    }

    public CommentedConfigurationNode root() { return root; }

    public CommentedConfigurationNode getNode(Object... path) { return root.getNode(path); }
}
