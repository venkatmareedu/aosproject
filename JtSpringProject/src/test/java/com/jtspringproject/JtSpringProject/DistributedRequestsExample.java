package com.jtspringproject.JtSpringProject;import java.net.URI;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DistributedRequestsExample {

    private static final int NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080/saveStudent";
        int numberOfRequests = 10;

        // Print the number of processors in the system
        System.out.println("Number of processors in the system: " + NUMBER_OF_PROCESSORS);

        // Create an HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Send multiple requests asynchronously
        CompletableFuture<Void>[] futures = sendRequestsAsync(httpClient, baseUrl, numberOfRequests);

        // Wait for all requests to complete
        CompletableFuture.allOf(futures).join();

        // Sort requests based on their ID numbers using round-robin scheduling
        List<List<CompletableFuture<Void>>> processorTasks = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PROCESSORS; i++) {
            processorTasks.add(new ArrayList<>());
        }

        for (int i = 0; i < numberOfRequests; i++) {
            int processorIndex = i % NUMBER_OF_PROCESSORS;
            processorTasks.get(processorIndex).add(futures[i]);
        }

        // Process each processor's tasks
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_PROCESSORS, new ProcessorThreadFactory());
        for (int i = 0; i < NUMBER_OF_PROCESSORS; i++) {
            final int processorNumber = i;
            executorService.submit(() -> {
                System.out.println("Processor " + processorNumber + " started at " + LocalDateTime.now());

                // Wait for each task to complete
                CompletableFuture<Void> processorTask = CompletableFuture.allOf(
                        processorTasks.get(processorNumber).toArray(new CompletableFuture[0])
                );

                processorTask.join();

                System.out.println("Processor " + processorNumber + " completed at " + LocalDateTime.now());
            });
        }

        // Shutdown the executor
        executorService.shutdown();
        System.out.println();
    }

    private static CompletableFuture<Void>[] sendRequestsAsync(HttpClient httpClient, String baseUrl, int numberOfRequests) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] futures = new CompletableFuture[numberOfRequests];

        for (int i = 0; i < numberOfRequests; i++) {
            String payload = "studentName=John" + i +
                    "&studentStatus=true" +
                    "&studentEmail=john" + i + "@example.com" +
                    "&courseName=Math" +
                    "&courseId=101";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            int requestNumber = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                int processorNumber = getProcessorNumber();
                System.out.println("Request " + requestNumber + " handling started at " + LocalDateTime.now()
                        + " on Processor " + processorNumber);

                // Send the request asynchronously
                long start = System.currentTimeMillis();
                try {
                	//response to initial request
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("Request " + requestNumber + " completed ");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    long end = System.currentTimeMillis();
                    System.out.println("Request " + requestNumber + " handling completed at " + LocalDateTime.now()
                            + " on Processor " + processorNumber
                            + " | Time taken: " + Duration.ofMillis(end - start).toSeconds() + " milliseconds");
                }
            });
        }

        return futures;
    }

    private static int getProcessorNumber() {
        // This method should be extended based on your specific needs to assign processor numbers.
        // For simplicity, it uses the current thread ID.
        return (int) Thread.currentThread().getId() % NUMBER_OF_PROCESSORS;
    }

    static class ProcessorThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Processor-" + threadNumber.getAndIncrement());
        }
    }
}
