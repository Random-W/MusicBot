package com.jagrosh.jmusicbot.Pubg.Match;

import com.github.mautini.pubgjava.model.GameMode;
import com.github.mautini.pubgjava.model.Map;
import com.github.mautini.pubgjava.model.participant.Participant;
import com.github.mautini.pubgjava.model.participant.ParticipantStats;
import com.github.mautini.pubgjava.model.roster.Roster;

import java.time.ZonedDateTime;
import java.util.List;

public class MatchVisualizer {

    public static String VisualizeMatch(String id, ZonedDateTime createdAt, GameMode gameMode, Map mapName, Roster roster, List<Participant> participants) {
        StringBuilder sb = new StringBuilder();
        sb.append("Match Id: " + id);
        sb.append(" Time: " + createdAt);
        sb.append(" GameMode: " + gameMode);
        sb.append(" Map: " + mapName);
        sb.append("\r\n");

        sb.append("Team Ranking: " + roster.getRosterAttributes().getRosterStats().getRank());
        sb.append("\r\n");

        for (Participant p : participants){
            ParticipantStats stats = p.getParticipantAttributes().getParticipantStats();
            sb.append(stats.getName());
            sb.append(" Kills: " + stats.getKills());
            sb.append(" Assists: " + stats.getAssists());
            sb.append(" DBNO: " + stats.getDbnos());
            sb.append(" Damage: " + stats.getDamageDealt());
            sb.append(" TeamKills: " + stats.getTeamKills());
            sb.append("\r\n");
        }

        return sb.toString();
    }
}
