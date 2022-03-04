package com.jagrosh.jmusicbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mautini.pubgjava.model.asset.Asset;
import com.github.mautini.pubgjava.model.generic.Entity;
import com.github.mautini.pubgjava.model.match.Match;
import com.github.mautini.pubgjava.model.participant.Participant;
import com.github.mautini.pubgjava.model.player.Player;
import com.github.mautini.pubgjava.model.roster.Roster;
import com.github.mautini.pubgjava.model.status.Status;
import com.github.mautini.pubgjava.model.telemetry.Telemetry;
import com.github.mautini.pubgjava.model.telemetry.event.LogPlayerTakeDamage;
import com.github.mautini.pubgjava.model.telemetry.event.TelemetryEvent;
import com.github.mautini.pubgjava.model.tournament.Tournament;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jagrosh.jmusicbot.Pubg.TelemetryEventGroup;
import com.jagrosh.jmusicbot.Pubg.TelmetryProcessor.TelemetryProcessor;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TelemetryProcessorTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                @Override
                public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                    out.value(value.toString());
                }

                @Override
                public ZonedDateTime read(JsonReader in) throws IOException {
                    return ZonedDateTime.parse(in.nextString());
                }
            })
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(src)))
            .registerTypeAdapter(Entity.class, (JsonDeserializer<Entity>) (json, typeOfT, context) -> {
                JsonObject jsonObject = json.getAsJsonObject();

                JsonElement jsonType = jsonObject.get("type");
                String type = jsonType.getAsString();

                switch (type) {
                    case "player":
                        return context.deserialize(json, Player.class);
                    case "match":
                        return context.deserialize(json, Match.class);
                    case "status":
                        return context.deserialize(json, Status.class);
                    case "roster":
                        return context.deserialize(json, Roster.class);
                    case "participant":
                        return context.deserialize(json, Participant.class);
                    case "asset":
                        return context.deserialize(json, Asset.class);
                    case "tournament":
                        return context.deserialize(json, Tournament.class);
                    default:
                        return null;
                }
            })
            .registerTypeAdapter(TelemetryEvent.class, (JsonDeserializer<TelemetryEvent>) (json, typeOfT, context) -> {
                JsonObject jsonObject = json.getAsJsonObject();
                JsonElement jsonType = jsonObject.get("_T");
                String type = jsonType.getAsString();
                try {
                    Class c = Class.forName("com.github.mautini.pubgjava.model.telemetry.event." + type);
                    return context.deserialize(json, c);
                } catch (ClassNotFoundException e) {
                    //e.printStackTrace();
                }

                return null;
            })
            .create();

    @Test
    public void processorBasicTest() throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(".\\PlayTakeDamageExample.json")), StandardCharsets.UTF_8);

        LogPlayerTakeDamage event = (LogPlayerTakeDamage) this.gson.fromJson(content, TelemetryEvent.class);

        assertTrue(TelemetryProcessor.isEventOfInterest(event));
    }

    @Test
    public void processorRealData() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(".\\29c5fae0-9874-11ec-abdd-7e9458cb6e9d-telemetry.json")), StandardCharsets.UTF_8);

        List<TelemetryEvent> t = Arrays.stream(this.gson.fromJson(content, TelemetryEvent[].class)).toList();

        List<TelemetryEventGroup> eventGroups = TelemetryProcessor.processTelemetry(new Telemetry(t));
    }
}
