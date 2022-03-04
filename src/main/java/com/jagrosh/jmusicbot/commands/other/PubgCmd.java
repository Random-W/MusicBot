package com.jagrosh.jmusicbot.commands.other;

import com.github.mautini.pubgjava.api.PubgClient;
import com.github.mautini.pubgjava.exception.PubgClientException;
import com.github.mautini.pubgjava.model.PlatformRegion;
import com.github.mautini.pubgjava.model.asset.Asset;
import com.github.mautini.pubgjava.model.generic.Entity;
import com.github.mautini.pubgjava.model.telemetry.Telemetry;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.Pubg.TelemetryEventGroup;
import com.jagrosh.jmusicbot.Pubg.TelmetryProcessor.TelemetryProcessor;
import com.jagrosh.jmusicbot.Pubg.TelmetryVisualizer.TelemetryVisualizer;
import com.jagrosh.jmusicbot.commands.OtherCommand;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PubgCmd extends OtherCommand {
    private final Bot bot;

    private final Paginator.Builder builder;

    private final PubgClient pubgClient;

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
                .setItemsPerPage(15)
                .waitOnSinglePage(false)
                .showPageNumbers(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
        this.pubgClient = new PubgClient();
        this.children = new OtherCommand[]{
                new TelemetryCommand(bot, this.builder, this.pubgClient)
        };
    }

    @Override
    public void doCommand(CommandEvent event) {
        event.reply("help");
    }
}
