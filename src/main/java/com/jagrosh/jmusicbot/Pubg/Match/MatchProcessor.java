package com.jagrosh.jmusicbot.Pubg.Match;

import com.github.mautini.pubgjava.model.generic.Entity;
import com.github.mautini.pubgjava.model.participant.Participant;
import com.github.mautini.pubgjava.model.roster.Roster;
import com.jagrosh.jmusicbot.Pubg.EntityTypes;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatchProcessor {
    public static Pair<Roster, List<Participant>> ProcessMatch( List<Entity> matchEntities, String playerId){
        Pair<List<Roster>, List<Participant>> catergorizedItems = categorize(matchEntities);

        List<Roster> rosters = catergorizedItems.getLeft();
        List<Participant> participants = catergorizedItems.getRight();

        String participantId = participants.stream().filter(p -> p.getParticipantAttributes().getParticipantStats().getPlayerId().equals(playerId)).toList().get(0).getId();

        List<Roster> potentialRosters = rosters.stream().filter(
                r -> r.getRosterRelationships().getParticipants().getData().stream().map(l -> l.getId()).toList().contains(participantId)).toList();

        if (potentialRosters.size() == 0){
            return null;
        }

        Roster roster = potentialRosters.get(0);

        List<String> teammateIds = roster.getRosterRelationships().getParticipants().getData().stream().map(p -> p.getId()).toList();
        List<Participant> teammates = participants.stream().filter(p -> teammateIds.contains(p.getId())).toList();

        return new Pair<Roster, List<Participant>>() {
            @Override
            public Roster getLeft() {
                return roster;
            }

            @Override
            public List<Participant> getRight() {
                return teammates;
            }

            @Override
            public List<Participant> setValue(List<Participant> value) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static Pair<List<Roster>, List<Participant>> categorize(List<Entity> entities){
        List<Roster> rosters = new ArrayList<Roster>();
        List<Participant> participants = new ArrayList<Participant>();

        for (Entity entity: entities) {
            switch (entity.getType()){
                case EntityTypes.participant -> {
                    participants.add((Participant) entity);
                    break;
                }
                case EntityTypes.roster -> {
                    rosters.add((Roster) entity);
                    break;
                }
            }
        }
        return new Pair<List<Roster>, List<Participant>>() {
            @Override
            public List<Roster> getLeft() {
                return rosters;
            }

            @Override
            public List<Participant> getRight() {
                return participants;
            }

            @Override
            public List<Participant> setValue(List<Participant> value) {
                return null;
            }
        };
    }
}
