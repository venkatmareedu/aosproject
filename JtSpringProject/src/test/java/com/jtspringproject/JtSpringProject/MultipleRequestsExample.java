package com.jtspringproject.JtSpringProject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class MultipleRequestsExample {

    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080/saveStudent";

        // Number of requests to send
        int numberOfRequests = 100;

        // Create an HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Send multiple requests asynchronously
        CompletableFuture.allOf(
                // Create an array of CompletableFuture representing the completion of each request
                sendRequestsAsync(httpClient, baseUrl, numberOfRequests)
        ).join();
    }

    private static CompletableFuture<Void>[] sendRequestsAsync(HttpClient httpClient, String baseUrl, int numberOfRequests) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] futures = new CompletableFuture[numberOfRequests];

        for (int i = 0; i < numberOfRequests; i++) {
            // Build the request payload
            String payload = "studentName=John" + i +
                    "&studentStatus=true" +
                    "&studentEmail=john" + i + "@example.com" +
                    "&courseName=Math" +
                    "&courseId=101";

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            int requestNumber=i;
            // Send the request asynchronously
            System.out.println(i);
            futures[i] = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        // Handle the response if needed
                        System.out.println("Request "+requestNumber+" completed with status code: " + response.statusCode());
                    });
        }

        return futures;
    }
}
