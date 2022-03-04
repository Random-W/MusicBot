package com.jagrosh.jmusicbot.Pubg.TelmetryVisualizer;

import com.github.mautini.pubgjava.model.telemetry.event.LogPlayerRevive;
import com.github.mautini.pubgjava.model.telemetry.event.LogPlayerTakeDamage;
import com.github.mautini.pubgjava.model.telemetry.event.TelemetryEvent;
import com.jagrosh.jmusicbot.Pubg.TelemetryEventGroup;
import com.jagrosh.jmusicbot.Pubg.TelemetryEventNames;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TelemetryVisualizer {
    public static List<String> visualizeTelemetry(List<TelemetryEventGroup> eventGroups){
        List<String> result = new ArrayList<String>();
        int i = 0;
        ZonedDateTime startTime = null;
        for (TelemetryEventGroup eventGroup: eventGroups) {
            if (i == 0){
                startTime = eventGroup.events.get(0).getTimestamp();
                i++;
                continue;
            }
            result.addAll(TelemetryVisualizer.visualizeEventGroup(eventGroup, startTime));
        }
        return result;
    }

    private static List<String> visualizeEventGroup(TelemetryEventGroup eventGroup, ZonedDateTime startTime){
        List<String> result = new ArrayList<String>();
        for (TelemetryEvent event: eventGroup.events) {
            result.add(TelemetryVisualizer.visualizeEvent(event, startTime));
        }
        return result;
    }

    private static String visualizeEvent(TelemetryEvent event, ZonedDateTime startTime){
        String eventType = event.getType();
        long duration = Duration.between(startTime, event.getTimestamp()).toMillis();
        long minutes = duration / (1000 * 60);
        long seconds = (duration % (1000 * 60)) / 1000;
        long milliSeconds = duration % (1000);

        String timeStr = (minutes < 10 ? "0" : "") + minutes + ":"
                            + (seconds < 10 ? "0" : "") + seconds + ":"
                            + (milliSeconds < 10 ? "0" : "") + (milliSeconds < 100 ? "0" : "") + milliSeconds;

        if (eventType.equalsIgnoreCase(TelemetryEventNames.LogPlayerReviveEventName)){
            LogPlayerRevive reviveEvent = (LogPlayerRevive) event;
            return "`" + timeStr + " " + reviveEvent.getCharacter().getName() + " revived " + reviveEvent.getVictim().getName() + "`";
        } else if (eventType.equalsIgnoreCase(TelemetryEventNames.LogPlayerTakeDamageEventName)){
            LogPlayerTakeDamage takeDamageEvent = (LogPlayerTakeDamage) event;
            return "`" + timeStr + " " + takeDamageEvent.getAttacker().getName() + " attacked " + takeDamageEvent.getVictim().getName()
                            + ". " + takeDamageEvent.getDamage() + " " + takeDamageEvent.getDamageTypeCategory() + "`";
        }else{
            return "";
        }
    }
}
