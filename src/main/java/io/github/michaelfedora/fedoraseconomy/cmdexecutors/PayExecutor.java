package io.github.michaelfedora.fedoraseconomy.cmdexecutors;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Michael on 3/19/2016.
 */
public class PayExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("pay");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Pay another user"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.user(Text.of("user")),
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.catalogedElement(Text.of("currency"), Currency.class))
                .executor(new PayExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player))
            throw new CommandException(Text.of("Your not a player! You can't pay anyone!"));

        User user = args.<User>getOne("user").orElseThrow(() -> new CommandException(Text.of("Bad param [user]!")));

        BigDecimal amount = BigDecimal.valueOf(args.<Double>getOne("amount").orElseThrow(() -> new CommandException(Text.of("Bad param [amount]!"))));

        Currency currency = args.<Currency>getOne("currency").orElseThrow(() -> new CommandException(Text.of("Bad param [currency]!")));

        UniqueAccount myAccount = tryGetUniqueAccount(((User) src).getUniqueId());
        UniqueAccount theirAccount = tryGetUniqueAccount(user.getUniqueId());

        TransferResult result = myAccount.transfer(theirAccount, currency, amount, Cause.of(NamedCause.of(src.getName(), src)));

        if(result.getResult() != ResultType.SUCCESS) {
            src.sendMessage(Text.of("Could not pay ", TextColors.AQUA, user.getName(), TextColors.RESET, " ", currency.format(amount), ": ", result.getResult()));
        } else {
            src.sendMessage(Text.of("Payed ", TextColors.AQUA, user.getName(), TextColors.RESET, " ", currency.format(amount), "!"));
        }

        return CommandResult.success();
    }
}
