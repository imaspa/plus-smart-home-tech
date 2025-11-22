package ru.yandex.practicum.telemetry.analyzer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.service.HubEventProcessor;
import ru.yandex.practicum.telemetry.analyzer.service.SnapshotProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerStarter implements ApplicationRunner {

    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable ->
            new Thread(runnable, "HubEventHandlerThread")
    );

    @Override
    public void run(org.springframework.boot.ApplicationArguments args){
        log.info("[Analyzer Hub] Starting in background...");
        executor.execute(hubEventProcessor);

        log.info("[Analyzer snapshot] Running processor...");
        snapshotProcessor.processorSnapshot();
    }

    @PreDestroy
    public void shutdown() {
        log.info("[Analyzer Hub] Shutting down HubEventProcessor executor...");
        executor.shutdownNow();
    }
}
