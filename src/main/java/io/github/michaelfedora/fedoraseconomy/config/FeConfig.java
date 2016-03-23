package io.github.michaelfedora.fedoraseconomy.config;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Michael on 3/22/2016.
 */
public class FeConfig implements FeConfigurable {

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode root;

    public FeConfig() {
        Path path = FedorasEconomy.getSharedConfigDir().resolve(PluginInfo.DATA_ROOT + ".cfg");
        this.loader = HoconConfigurationLoader.builder().setPath(path).build();
    }

    @Override
    public void load() {
        try {
            root = loader.load();
        } catch(IOException e) {
            FedorasEconomy.getLogger().error("Could not load configuration!", e);
        }
    }

    @Override
    public void save() {
        try {
            loader.save(root);
        } catch(IOException e) {
            FedorasEconomy.getLogger().error("Could not save configuration!", e);
        }
    }

    @Override
    public CommentedConfigurationNode root() { return root; }

    @Override
    public CommentedConfigurationNode getNode(Object... path) { return root.getNode(path); }

}
