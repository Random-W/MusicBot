package com.jagrosh.jmusicbot.Pubg.Telemetry;
import com.github.mautini.pubgjava.model.telemetry.event.TelemetryEvent;



import java.util.List;

public class TelemetryEventGroup {
    public List<TelemetryEvent> events;

    public TelemetryEventGroup(List<TelemetryEvent> events){
        this.events = events;
    }
}
