package emu.grasscutter;

import ch.qos.logback.classic.Logger;
import lombok.Getter;
import org.slf4j.LoggerFactory;

public final class Loggers {

    private static final String mainPackges = "emu.grasscutter";
    private static final String gamePackges = mainPackges+ ".game";

    @Getter
    private static final Logger defaultLogger = (Logger) Grasscutter.getLogger();
    @Getter
    private static final Logger abilitySystem = (Logger) LoggerFactory.getLogger(gamePackges+".ability");
    @Getter
    private static final Logger scriptSystem = (Logger) LoggerFactory.getLogger(mainPackges+".scripts");
    @Getter
    private static final Logger questSystem = (Logger) LoggerFactory.getLogger(gamePackges+".quest");
    @Getter
    private static final Logger resourceSystem = (Logger) LoggerFactory.getLogger(mainPackges+".data");
    @Getter
    private static final Logger clientLogs = (Logger) LoggerFactory.getLogger("gc.client");
}
