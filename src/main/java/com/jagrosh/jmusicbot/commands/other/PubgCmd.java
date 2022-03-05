package com.jagrosh.jmusicbot.commands.other;

import com.github.mautini.pubgjava.api.PubgClient;
import com.github.mautini.pubgjava.api.PubgClientAsync;
import com.github.mautini.pubgjava.model.Platform;
import com.github.mautini.pubgjava.model.PlatformRegion;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OtherCommand;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.concurrent.TimeUnit;

public class PubgCmd extends OtherCommand {
    private final Bot bot;

    private final Paginator.Builder builder;

    private final PubgClient pubgClient;

    private final PubgClientAsync pubgClientAsync;

    protected static PlatformRegion platformRegion = PlatformRegion.PC_NA;

    protected static Platform platform = Platform.STEAM;

    public PubgCmd(Bot bot)
    {
        super(bot);
        this.bot = bot;
        this.guildOnly = false;
        this.name = "pubg";
        this.arguments = "<telemetry>";
        this.help = "pubg management";
        this.builder = new Paginator.Builder()
                .setColumns(1)
                .setFinalAction(m -> {try{m.clearReactions().queue();}catch(PermissionException ignore){}})
                .waitOnSinglePage(false)
                .showPageNumbers(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
        this.pubgClient = new PubgClient();
        this.pubgClientAsync = new PubgClientAsync();
        this.children = new OtherCommand[]{
                new TelemetryCommand(bot, this.builder, this.pubgClient),
                new PlayerCommand(bot, this.builder, this.pubgClient, this.pubgClientAsync)
        };
    }

    @Override
    public void doCommand(CommandEvent event) {
        event.reply("help");
    }
}
