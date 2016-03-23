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
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Michael on 3/23/2016.
 */
public class FeUniqueGetExecutor extends FeExecutorBase {
    public static final List<String> ALIASES = Arrays.asList("balance", "bal");

    public static final String NAME = ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Gets the UniqueAccount's balances"))
                .permission(PluginInfo.DATA_ROOT + ".unique." + NAME)
                .arguments(GenericArguments.optional(GenericArguments.user(Text.of("user"))))
                .executor(new FeUniqueGetExecutor())
                .build();
    }

    public static void run(MessageReceiver receiver, UniqueAccount account) throws CommandException {

        Map<Currency, BigDecimal> balances = account.getBalances();

        int count = 0;

        Text.Builder tb = Text.builder().append(Text.of(TextColors.GOLD, TextStyles.BOLD, "Balances: "));
        for(Map.Entry<Currency, BigDecimal> entry : balances.entrySet()) {
            tb.append(entry.getKey().format(entry.getValue()));
            if(++count < balances.size())
                tb.append(Text.of(", "));
        }

        receiver.sendMessage(tb.build());

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<User> user = args.getOne("user");
        if(!user.isPresent())
            if(src instanceof Player)
                user = Optional.of((User) src);

        if(!user.isPresent())
            throw new CommandException(Text.of("Bad param [user]!"));


        UniqueAccount account = tryGetUniqueAccount(user.get().getUniqueId());

        FeUniqueGetExecutor.run(src, account);

        return CommandResult.success();
    }
}
