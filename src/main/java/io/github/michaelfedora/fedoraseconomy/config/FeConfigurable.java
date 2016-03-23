package io.github.michaelfedora.fedoraseconomy.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

/**
 * Created by Michael on 3/22/2016.
 */
public interface FeConfigurable {

    void load();
    void save();

    CommentedConfigurationNode root();
    CommentedConfigurationNode getNode(Object... path);
}
