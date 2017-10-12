package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeUniqueAccount;
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
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 3/19/2016.
 */
public class FeTossExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("toss", "burn");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Toss some money into the void (primarily for testing)"))
                .permission(PluginInfo.DATA_ROOT + ".global." + NAME)
                .arguments(GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.catalogedElement(Text.of("currency"), Currency.class))
                .executor(new FeTossExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player))
            throw new CommandException(Text.of("You're not a player! You can't toss money away!"));

        BigDecimal amount = BigDecimal.valueOf(args.<Double>getOne("amount").orElseThrow(() -> new CommandException(Text.of("Bad param [amount]!"))));

        if(amount.compareTo(BigDecimal.ZERO) < 0) throw new CommandException(Text.of("You cannot throw away a negative amount!"));

        Currency currency = args.<Currency>getOne("currency").orElseThrow(() -> new CommandException(Text.of("Bad param [currency]!")));

        FeUniqueAccount myAccount = tryGetUniqueAccount(((User) src).getUniqueId());

        TransactionResult result = myAccount.withdraw(currency, amount, Cause.of(NamedCause.of(src.getName(), src)), null, true);

        if(result.getResult() != ResultType.SUCCESS) {
            src.sendMessage(Text.of("Could not toss ", currency.format(amount), ": ", result.getResult()));
        } else {
            src.sendMessage(Text.of("Tossed ", currency.format(amount), "!"));
        }

        return CommandResult.success();
    }
}
