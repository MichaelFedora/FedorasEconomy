package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.unique;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
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
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Michael on 3/23/2016.
 */
public class FeUniqueAddExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("add");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Add to an unique account's balance"))
                .permission(PluginInfo.DATA_ROOT + ".unique." + NAME)
                .arguments(GenericArguments.user(Text.of("user")),
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.catalogedElement(Text.of("currency"), Currency.class))
                .executor(new FeUniqueAddExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        User user = args.<User>getOne("user").orElseThrow(() -> new CommandException(Text.of("Bad param [user]!")));

        UniqueAccount account = tryGetUniqueAccount(user.getUniqueId());

        BigDecimal amount = BigDecimal.valueOf(args.<Double>getOne("amount").orElseThrow(() -> new CommandException(Text.of("Bad param [amount]!"))));

        Currency currency = args.<Currency>getOne("currency").orElseThrow(() -> new CommandException(Text.of("Bad param [currency]!")));

        TransactionResult result;
        if(amount.compareTo(BigDecimal.ZERO) < 0)
            result = account.withdraw(currency, amount.abs(), Cause.of(NamedCause.of(src.getName(), src)));
        else
            result = account.deposit(currency, amount, Cause.of(NamedCause.of(src.getName(), src)));

        if(result.getResult() != ResultType.SUCCESS) {
            src.sendMessage(Text.of("Could not add ", currency.format(amount), " to ", user.getName(), "'s account: ", result.getResult()));
        } else {
            src.sendMessage(Text.of("Added ", currency.format(amount), " to ", user.getName(), "'s account!"));
        }

        return CommandResult.success();
    }
}
