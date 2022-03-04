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
        this.children = new OtherCommand[]{
                new TelemetryCommand(bot)
        };
        this.builder = new Paginator.Builder()
                .setColumns(1)
                .setFinalAction(m -> {try{m.clearReactions().queue();}catch(PermissionException ignore){}})
                .setItemsPerPage(15)
                .waitOnSinglePage(false)
                .showPageNumbers(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
        this.pubgClient = new PubgClient();
    }

    @Override
    public void doCommand(CommandEvent event) {
        event.reply("help");
    }

    public class TelemetryCommand extends OtherCommand
    {
        public TelemetryCommand(Bot bot)
        {
            super(bot);
            this.name = "telemetry";
            this.help = "Checks telemetry of a match";
            this.arguments = "<matchId>";
            this.guildOnly = false;
        }

        @Override
        public void doCommand(CommandEvent event)
        {
            String matchId = event.getArgs().replaceAll("\\s+", "_");

            Telemetry telemetry = getTelemetryFromMatchId(matchId, event);

            if (telemetry == null){
                return;
            }

            System.out.println(telemetry.getTelemetryEvents().get(telemetry.getTelemetryEvents().size() - 1).getTimestamp());

            List<TelemetryEventGroup> processedTelemetry = TelemetryProcessor.processTelemetry(telemetry);

            List<String> visualizedTelemetry = TelemetryVisualizer.visualizeTelemetry(processedTelemetry);
            builder.setText((i1,i2) -> "Telemetry for match " + matchId)
                    .setItems(visualizedTelemetry.toArray(new String[0]))
                    .setUsers(event.getAuthor())
                    .setColor(event.getSelfMember().getColor());
            builder.build().paginate(event.getChannel(), 1);
        }

        private Telemetry getTelemetryFromMatchId(String matchId, CommandEvent event){
            String telemetryUrl = null;
            try {
                List<Entity> entities = pubgClient.getMatch(PlatformRegion.PC_NA, matchId).getIncluded();
                for (Entity entity: entities) {
                    if (!entity.getType().equalsIgnoreCase("asset")){
                        continue;
                    }

                    Asset asset = (Asset) entity;
                    telemetryUrl = asset.getAssetAttributes().getUrl();
                }
            } catch (PubgClientException e) {
                e.printStackTrace();
                event.reply("Unable to get match: " + e.getMessage());
                return null;
            }

            if (telemetryUrl == null){
                event.reply("No telemetry is found in match");
                return null;
            }

            try {
                Telemetry telemetry = pubgClient.getTelemetry(telemetryUrl);
                return telemetry;
            } catch (PubgClientException e) {
                e.printStackTrace();
                event.reply("Unable to get telemetry: " + e.getMessage());
                return null;
            }
        }
    }
}
