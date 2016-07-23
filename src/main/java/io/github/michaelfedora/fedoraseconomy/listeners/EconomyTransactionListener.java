package io.github.michaelfedora.fedoraseconomy.listeners;

import io.github.michaelfedora.fedoraseconomy.config.FeConfig;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeUniqueAccount;
import io.github.michaelfedora.fedoraseconomy.economy.event.FeEconomyTransferEvent;
import io.github.michaelfedora.fedoraseconomy.economy.transaction.FeTransactionResult;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * Created by Michael on 3/25/2016.
 */
public final class EconomyTransactionListener {

    @Listener
    public void onTransaction(EconomyTransactionEvent event) {

        TransactionResult result = event.getTransactionResult();

        if(result.getType() != TransactionTypes.DEPOSIT
                && result.getType() != TransactionTypes.WITHDRAW)
            return;

        FeTransactionResult feResult = new FeTransactionResult(result);

        if(! (FeConfig.instance.getVerboseLogging() || feResult.isInternal()) )
            return;

        Account account = feResult.getAccount();
        Optional<FeUniqueAccount> opt_uniqueAccount = FeUniqueAccount.fromAccount(account);

        if(opt_uniqueAccount.isPresent()) {

            FeUniqueAccount uniqueAccount = opt_uniqueAccount.get();

            Optional<Player> opt_player = Sponge.getServer().getPlayer(uniqueAccount.getUniqueId());

            if(opt_player.isPresent()) {

                Player player = opt_player.get();

                Text.Builder tb = Text.builder();

                String action;
                if(feResult.getType() == TransactionTypes.DEPOSIT) action = "Deposited ";
                else action = "Withdrew ";

                tb.append(Text.of(action, feResult.getCurrency().format(feResult.getAmount()), "!"));

                player.sendMessage(tb.build());
            }

        } else {

            // notify all owners of the VirtualAccount of the transaction
        }
    }

    @Listener
    public void onTransfer(FeEconomyTransferEvent event) {

    }
}
