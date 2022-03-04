package com.jagrosh.jmusicbot.Pubg.TelmetryProcessor;

import com.github.mautini.pubgjava.model.telemetry.Telemetry;
import com.github.mautini.pubgjava.model.telemetry.event.LogPlayerRevive;
import com.github.mautini.pubgjava.model.telemetry.event.LogPlayerTakeDamage;
import com.github.mautini.pubgjava.model.telemetry.event.TelemetryEvent;
import com.jagrosh.jmusicbot.Pubg.TelemetryEventGroup;
import com.jagrosh.jmusicbot.Pubg.TelemetryEventNames;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class TelemetryProcessor {

    private static final List<String> playersOfInterest = new ArrayList<String>(){
        {
            add("RandomWander");
            add("idolRegion");
            add("Cherylove");
            add("Chriskn");
        }
    };

    private static final List<String> eventOfInterest = new ArrayList<String>(){
        {
            //add("LogPlayerAttack");
            //add(TelemetryProcessor.LogPlayerKillV2);
            //add("LogPlayerMakeGroggy");
            add(TelemetryEventNames.LogPlayerReviveEventName);
            add(TelemetryEventNames.LogPlayerTakeDamageEventName);
            add(TelemetryEventNames.LogMatchStartEventName);
        }
    };

    public static List<TelemetryEventGroup> processTelemetry(Telemetry telemetry) {
        Stream<TelemetryEvent> eventsOfInterest = TelemetryProcessor.filterTelemetry(telemetry);
        Stream<TelemetryEvent> sortedEvents = eventsOfInterest.sorted(new Comparator<TelemetryEvent>() {
            @Override
            public int compare(TelemetryEvent o1, TelemetryEvent o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        List<TelemetryEventGroup> eventGroups = new ArrayList<TelemetryEventGroup>();
        String lastEventType = null;
        List<TelemetryEvent> eventGroup = new ArrayList<TelemetryEvent>();
        for (TelemetryEvent event: sortedEvents.toList()) {
            if (lastEventType != null && !event.getType().equalsIgnoreCase(lastEventType)){
                eventGroups.add(new TelemetryEventGroup(eventGroup));
                eventGroup = new ArrayList<TelemetryEvent>();
            }
            eventGroup.add(event);
            lastEventType = event.getType();
        }
        eventGroups.add(new TelemetryEventGroup(eventGroup));

        return eventGroups;
    }

    private static Stream<TelemetryEvent> filterTelemetry(Telemetry telemetry){
        return telemetry.getTelemetryEvents().stream().filter(event -> TelemetryProcessor.isEventOfInterest(event));
    }

    public static boolean isEventOfInterest(TelemetryEvent event){
        if (event == null){
            return false;
        }

        if (!TelemetryProcessor.eventOfInterest.contains(event.getType())){
            return false;
        }

        if (event.getType().equalsIgnoreCase(TelemetryEventNames.LogPlayerReviveEventName)){
            LogPlayerRevive reviveEvent = (LogPlayerRevive) event;
            return TelemetryProcessor.playersOfInterest.contains(reviveEvent.getCharacter().getName())
                        || TelemetryProcessor.playersOfInterest.contains(reviveEvent.getVictim().getName());
        } else if (event.getType().equalsIgnoreCase(TelemetryEventNames.LogPlayerTakeDamageEventName)) {
            LogPlayerTakeDamage takeDamageEvent = (LogPlayerTakeDamage) event;

            return takeDamageEvent.getAttacker() != null && (TelemetryProcessor.playersOfInterest.contains(takeDamageEvent.getAttacker().getName())
                    || TelemetryProcessor.playersOfInterest.contains(takeDamageEvent.getVictim().getName()));
        } else if (event.getType().equalsIgnoreCase(TelemetryEventNames.LogMatchStartEventName)){
            return true;
        } else {
            return false;
        }
    }
}
