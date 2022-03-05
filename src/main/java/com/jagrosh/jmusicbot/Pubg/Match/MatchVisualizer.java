package com.jagrosh.jmusicbot.Pubg.Match;

import com.github.mautini.pubgjava.model.GameMode;
import com.github.mautini.pubgjava.model.Map;
import com.github.mautini.pubgjava.model.participant.Participant;
import com.github.mautini.pubgjava.model.participant.ParticipantStats;
import com.github.mautini.pubgjava.model.roster.Roster;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class MatchVisualizer {

    private static int minimumPadding = 1;

    public static String VisualizeMatch(String id, ZonedDateTime createdAt, GameMode gameMode, Map mapName, Roster roster, List<Participant> participants) {
        StringBuilder sb = new StringBuilder();
        sb.append("Match Id: " + id + "\r\n");
        sb.append(" Time: " + createdAt + "\r\n");
        sb.append(" GameMode: " + gameMode + "\r\n");
        sb.append(" Map: " + mapName + "\r\n");

        sb.append("Team Ranking: " + roster.getRosterAttributes().getRosterStats().getRank() + "\r\n");

        List<List<String>> table = new ArrayList<>();
        table.add(new ArrayList<String>(){
            {
                add("");
                add("Kills");
                add("Assists");
                add("DBNO");
                add("Damage");
                add("TeamKills");
            }
        });

        for (Participant p : participants){
            ParticipantStats stats = p.getParticipantAttributes().getParticipantStats();
            List<String> row = new ArrayList<String>(){
                {
                    add(stats.getName());
                    add(stats.getKills().toString());
                    add(stats.getAssists().toString());
                    add(stats.getDbnos().toString());
                    add(stats.getDamageDealt().toString());
                    add(stats.getTeamKills().toString());
                }
            };
            table.add(row);
            //sb.append(" Kills: " + stats.getKills());
            //sb.append(" Assists: " + stats.getAssists());
            //sb.append(" DBNO: " + stats.getDbnos());
            //sb.append(" Damage: " + stats.getDamageDealt());
            //sb.append(" TeamKills: " + stats.getTeamKills());
            //sb.append("\r\n");
        }

        sb.append(visualizeTable(table));

        return sb.toString();
    }

    private static String visualizeTable(List<List<String>> table){
        StringBuilder sb = new StringBuilder("`");

        int nRow = table.size();
        int nCol = table.get(0).size();

        // Get maximum width of all columns
        List<Integer> widths = new ArrayList<Integer>();
        for (int i = 0; i < nCol; i++){
            int maxWidth = 0;
            for (int j = 0; j < nRow; j++){
                if (maxWidth < table.get(j).get(i).length()){
                    maxWidth = table.get(j).get(i).length();
                }
            }
            widths.add(maxWidth + 2 * minimumPadding);
        }

        // Generate horizontal line (it is the same for all rows)
        // StringBuilder horizontalLineBuilder = new StringBuilder();
        // for (int i = 0; i < nCol; i++){
        //     horizontalLineBuilder.append("+" + "-".repeat(widths.get(i)));
        // }
        // horizontalLineBuilder.append("+\r\n");
        // String horizontalLine = horizontalLineBuilder.toString();

        // Start generating the table
        //sb.append(horizontalLine);
        for (int i = 0; i < nRow; i++){
            sb.append(generateContentLine(table.get(i), widths));
            //sb.append(horizontalLine);
        }

        sb.append("`");

        return sb.toString();
    }

    private static String generateContentLine(List<String> content, List<Integer> widths){
        StringBuilder sb = new StringBuilder();

        sb.append("|");

        int nCol = content.size();
        for (int i = 0; i < nCol; i++){
            int nPadding = widths.get(i) - content.get(i).length();
            int nLeftPadding = nPadding / 2;
            int nRightPadding = nPadding - nLeftPadding;

            sb.append(" ".repeat(nLeftPadding));
            sb.append(content.get(i));
            sb.append(" ".repeat(nRightPadding));
            sb.append("|");
        }
        sb.append("\r\n");

        return sb.toString();
    }
}
