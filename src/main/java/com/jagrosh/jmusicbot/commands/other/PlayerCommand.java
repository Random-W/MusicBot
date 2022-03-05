package com.jagrosh.jmusicbot.commands.other;

import com.github.mautini.pubgjava.api.PubgClient;
import com.github.mautini.pubgjava.api.PubgClientAsync;
import com.github.mautini.pubgjava.api.ResponseCallback;
import com.github.mautini.pubgjava.exception.PubgClientException;
import com.github.mautini.pubgjava.model.generic.Entity;
import com.github.mautini.pubgjava.model.match.Match;
import com.github.mautini.pubgjava.model.match.MatchResponse;
import com.github.mautini.pubgjava.model.participant.Participant;
import com.github.mautini.pubgjava.model.player.Player;
import com.github.mautini.pubgjava.model.roster.Roster;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.Pubg.EntityTypes;
import com.jagrosh.jmusicbot.Pubg.Match.MatchProcessor;
import com.jagrosh.jmusicbot.Pubg.Match.MatchVisualizer;
import com.jagrosh.jmusicbot.commands.OtherCommand;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PlayerCommand extends OtherCommand {
    final PubgClient pubgClient;

    final PubgClientAsync pubgClientAsync;

    final Paginator.Builder builder;

    final static int defaultNumberOfMatches = 20;

    final static int maximumNumberOfMatches = 30;

    public PlayerCommand(Bot bot, Paginator.Builder builder, PubgClient pubgClient, PubgClientAsync pubgClientAsync)
    {
        super(bot);
        this.name = "player";
        this.help = "Checks telemetry of a match";
        this.arguments = "<matchId>";
        this.guildOnly = false;
        this.builder = builder;
        this.pubgClient = pubgClient;
        this.pubgClientAsync = pubgClientAsync;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        String[] args = event.getArgs().split("\\s+");

        if (args.length == 0){
            event.reply("Please provide player name");
            return;
        }
        final String playerName = args[0];

        int nMatches = PlayerCommand.defaultNumberOfMatches;

        if (args.length > 1){
            try{
                nMatches = Integer.parseInt(args[1]);
            }catch (NumberFormatException e){
                event.reply("Cannot parse provided integer. Using default number " + nMatches);
            }
        }

        if (nMatches > maximumNumberOfMatches){
            event.reply("Provided number '" + nMatches + "' is greater than maximum allowed number. Using maximum allowed number '" + maximumNumberOfMatches + "'");
            nMatches = maximumNumberOfMatches;
        }

        Player player = null;

        try {
            player = this.pubgClient.getPlayersByNames(PubgCmd.platform, playerName).getData().get(0);
        } catch (PubgClientException e) {
            event.reply("Cannot get player");
            return;
        }

        final String playerId = player.getId();

        List<Match> matchInfo = player.getPlayerRelationships().getMatches().getData().stream().limit(nMatches).collect(Collectors.toList());

        final List<String> matchViews = new ArrayList<>();
        for (int i = 0; i < nMatches; i++) {
            matchViews.add(null);
        }


        int i = 0;
        for (Match m: matchInfo) {
            String matchId = m.getId();
            this.pubgClientAsync.getMatch(PlayerCommand.getMatchCallback(i, playerId, matchViews), PubgCmd.platformRegion, matchId);
            i++;
        }

        while (true){

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (matchViews.size() < nMatches){
                continue;
            }

            if (matchViews.stream().allMatch(item -> item != null)){
                break;
            }
        }

        builder.setText((i1,i2) -> "Matches for player " + playerName + " " + playerId)
                .setItems(matchViews.toArray(new String[0]))
                .setUsers(event.getAuthor())
                .setColor(event.getSelfMember().getColor())
                .setItemsPerPage(5);;
        builder.build().paginate(event.getChannel(), 1);
    }

    private static ResponseCallback<MatchResponse> getMatchCallback(int i, String playerId, List<String> output){
        return new ResponseCallback<MatchResponse>() {

            @Override
            public void onResponse(MatchResponse matchResponse) {

                Pair<Roster, List<Participant>> items = MatchProcessor.ProcessMatch(matchResponse.getIncluded(), playerId);
                Match matchData = matchResponse.getData();
                String matchStr = MatchVisualizer.VisualizeMatch(
                        matchData.getId(),
                        matchData.getMatchAttributes().getCreatedAt(),
                        matchData.getMatchAttributes().getGameMode(),
                        matchData.getMatchAttributes().getMapName(),
                        items.getLeft(),
                        items.getRight());

                output.set(i, matchStr);
            }

            @Override
            public void onError(PubgClientException e) {
                output.set(i, "Cannot get information for this match");
            }
        };
    }
}
