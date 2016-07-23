package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.user;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import io.github.michaelfedora.fedoraseconomy.economy.account.FeUniqueAccount;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Created by Michael on 3/19/2016.
 */
public class FeUserResetExecutor extends FeExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("reset", "zero");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Resets a user's balance to zero"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.user(Text.of("user")))
                .executor(new FeUserResetExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        User user = args.<User>getOne("user").orElseThrow(() -> new CommandException(Text.of("Bad param [user]!")));

        FeUniqueAccount account = tryGetUniqueAccount(user.getUniqueId());

        Map<Currency, TransactionResult> result = account.resetBalances(Cause.of(NamedCause.of(src.getName(), src)), null, true);

        boolean success = false;
        boolean failure = false;

        for(Map.Entry<Currency, TransactionResult> e : result.entrySet()) {
            if(e.getValue().getResult() == ResultType.SUCCESS) success = true;
            else failure = true;
        }

        if(failure && !success) {
            src.sendMessage(Text.of("Could not reset ", TextColors.AQUA, user.getName(), TextColors.RESET, "'s account"));
        } if(failure && success) {
            src.sendMessage(Text.of("Could not ", TextStyles.ITALIC, "totally", TextStyles.RESET, " reset", TextColors.AQUA, user.getName(), TextColors.RESET, "'s account"));
        } else {
            src.sendMessage(Text.of("Reset ", TextColors.AQUA, user.getName(), TextColors.RESET, "'s account"));
        }

        return CommandResult.success();
    }
}
