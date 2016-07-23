package io.github.michaelfedora.fedoraseconomy.cmdexecutors.fedoraseconomy.user;

import io.github.michaelfedora.fedoraseconomy.PluginInfo;
import io.github.michaelfedora.fedoraseconomy.cmdexecutors.FeExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Michael on 3/23/2016.
 */
public class FeUserGetExecutor extends FeExecutorBase {
    public static final List<String> ALIASES = Arrays.asList("get", "balance", "bal");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Gets the UniqueAccount's balances"))
                .permission(PluginInfo.DATA_ROOT + ".user." + NAME)
                .arguments(GenericArguments.user(Text.of("user")))
                .executor(new FeUserGetExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        User user = args.<User>getOne("user").orElseThrow(() -> new CommandException(Text.of("Bad param [user]!")));

        UniqueAccount uniqueAccount = tryGetUniqueAccount(user.getUniqueId());

        Map<Currency, BigDecimal> balances = uniqueAccount.getBalances();

        int count = 0;

        Text.Builder tb = Text.builder().append(Text.of(TextColors.AQUA, user.getName(), TextColors.GOLD, "'s Balances: "));
        for(Map.Entry<Currency, BigDecimal> entry : balances.entrySet()) {
            tb.append(entry.getKey().format(entry.getValue()));
            if(++count < balances.size())
                tb.append(Text.of(", "));
        }

        src.sendMessage(tb.build());

        return CommandResult.success();
    }
}
