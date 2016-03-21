package io.github.michaelfedora.fedoraseconomy.economy.account;

import org.spongepowered.api.service.economy.account.VirtualAccount;
import org.spongepowered.api.text.Text;

/**
 * Created by Michael on 3/18/2016.
 */
public class FeVirtualAccount extends FeAccount implements VirtualAccount {

    public FeVirtualAccount(String identifier) {
        super(identifier, Text.of("VirtualAccount[" + identifier + "]"));
    }
}
