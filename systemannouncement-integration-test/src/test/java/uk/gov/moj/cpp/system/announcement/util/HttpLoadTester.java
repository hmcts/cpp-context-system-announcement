package uk.gov.moj.cpp.system.announcement.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * A utility class for performing HTTP load testing.
 * This class sends a large number of HTTP requests to a specified URL using multiple threads
 * and logs the response times for analysis.
 * This can be used to do a quick performance test locally in case of future refactoring
 */
public class HttpLoadTester {

    private static final int TOTAL_REQUESTS = 50000;
    private static final int THREADS = 100;
    private static final int REQUESTS_PER_THREAD = TOTAL_REQUESTS / THREADS;
    private static final int MONITOR_INTERVAL_SECONDS = 5;

    // Target URL for the HTTP requests
    private static final String URL = "https://localhost:8080/systemannouncement-service/rest/systemannouncement/announcements";
    private static final String HEADER_ACCEPT = "application/vnd.systemannouncement.get-banner-announcements+json";
    private static final String HEADER_UID = UUID.randomUUID().toString();

    private static final AtomicBoolean running = new AtomicBoolean(true);

    // Trust manager to bypass SSL certificate validation (for testing purposes only)
    private static final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
    };

    /**
     * Main method to execute the HTTP load test.
     *
     * @param args Command-line arguments (not used).
     * @throws Exception If an error occurs during execution.
     */
    public static void main(String[] args) throws Exception {
        final long startTime = System.currentTimeMillis();

        // Configure SSL context to bypass certificate validation
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(new SSLParameters())
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // Cleanup old logs
        for (int i = 0; i < THREADS; i++) {
            File log = new File("curl_times_" + i + ".log");
            if (log.exists()) {
                log.delete();
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Future<?>> futures = new ArrayList<>();

        // Start monitor thread
        Thread monitorThread = new Thread(HttpLoadTester::monitorLogs);
        monitorThread.start();

        // Launch request threads
        for (int i = 0; i < THREADS; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("curl_times_" + index + ".log", true))) {
                    for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                        long start = System.currentTimeMillis();

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(URL))
                                .header("Accept", HEADER_ACCEPT)
                                .header("CJSCPPUID", HEADER_UID)
                                .GET()
                                .build();

                        try {
                            client.send(request, HttpResponse.BodyHandlers.discarding());
                        } catch (Exception e) {
                            System.err.println("Request failed: " + e.getMessage());
                        }

                        long end = System.currentTimeMillis();
                        writer.write(end / 1000 + " " + (end - start));
                        writer.newLine();
                        writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }

        // Wait for all threads to finish
        for (Future<?> future : futures) {
            future.get();
        }

        running.set(false);
        monitorThread.join();
        executor.shutdown();

        System.out.println("✅ Completed " + TOTAL_REQUESTS + " requests using " + THREADS + " threads.");

        // Calculate and display the total time taken for the load test
        final long endTime = System.currentTimeMillis();
        long totalMillis = endTime - startTime;
        long seconds = totalMillis / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        System.out.println("=======================================");
        System.out.println("✅ Total Time Taken: " + minutes + " minutes " + remainingSeconds + " seconds (" + totalMillis + " ms)");
        System.out.println("=======================================");

    }

    /**
     * Monitors the log files to calculate and display the number of requests
     * completed and their average response time in the last monitoring interval.
     */
    private static void monitorLogs() {
        while (running.get()) {
            try {
                Thread.sleep(MONITOR_INTERVAL_SECONDS * 1000L);

                long now = Instant.now().getEpochSecond();
                long windowStart = now - MONITOR_INTERVAL_SECONDS;
                int totalCount = 0;
                long totalTime = 0;

                // Read and process log files for each thread
                for (int i = 0; i < THREADS; i++) {
                    File log = new File("curl_times_" + i + ".log");
                    if (!log.exists()) continue;

                    try (BufferedReader reader = new BufferedReader(new FileReader(log))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.trim().split("\\s+");
                            if (parts.length != 2) continue;
                            long timestamp = Long.parseLong(parts[0]);
                            long duration = Long.parseLong(parts[1]);
                            if (timestamp >= windowStart) {
                                totalCount++;
                                totalTime += duration;
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading log file: " + e.getMessage());
                    }
                }

                // Display the monitoring results
                if (totalCount > 0) {
                    long avgTime = totalTime / totalCount;
                    System.out.printf("[%s] Requests in last %d sec: %d, Avg Time: %dms%n",
                            Instant.now(), MONITOR_INTERVAL_SECONDS, totalCount, avgTime);
                } else {
                    System.out.printf("[%s] No requests completed in last %d seconds.%n",
                            Instant.now(), MONITOR_INTERVAL_SECONDS);
                }

            } catch (InterruptedException e) {
                System.err.println("Monitor thread interrupted.");
                Thread.currentThread().interrupt();
            }
        }
    }
}