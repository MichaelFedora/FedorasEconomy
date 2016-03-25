package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Created by Michael on 3/25/2016.
 */
public class FeTransferExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Collections.singletonList("transfer");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Transfer an amount from one account to another"))
                .permission(PluginInfo.DATA_ROOT + ".user." + NAME)
                .arguments(GenericArguments.string(Text.of("accountFrom")),
                        GenericArguments.string(Text.of("accountTo")),
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.catalogedElement(Text.of("currency"), Currency.class))
                .executor(new FeTransferExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        String accountFrom = args.<String>getOne("accountFrom").orElseThrow(() -> new CommandException(Text.of("Bad param [accountFrom]!")));
        String accountTo = args.<String>getOne("accountTo").orElseThrow(() -> new CommandException(Text.of("Bad param [accountTo]!")));

        BigDecimal amount = BigDecimal.valueOf(args.<Double>getOne("amount").orElseThrow(() -> new CommandException(Text.of("Bad param [amount]!"))));

        Currency currency = args.<Currency>getOne("currency").orElseThrow(() -> new CommandException(Text.of("Bad param [currency]!")));

        Account myAccount = tryGetAccount(accountFrom);
        Account theirAccount = tryGetAccount(accountTo);

        TransferResult result = myAccount.transfer(theirAccount, currency, amount, Cause.of(NamedCause.of(src.getName(), src)));

        if (result.getResult() != ResultType.SUCCESS) {
            src.sendMessage(Text.of("Could not transfer ", currency.format(amount), " to ", TextColors.AQUA, theirAccount.getDisplayName(), TextColors.RESET, ": ", result.getResult()));
        } else {
            src.sendMessage(Text.of("Transferred ", currency.format(amount), " to ", TextColors.AQUA, theirAccount.getDisplayName(), TextColors.RESET, "!"));
        }

        return CommandResult.success();
    }
}
