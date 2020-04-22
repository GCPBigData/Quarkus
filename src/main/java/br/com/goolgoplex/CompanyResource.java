package br.com.goolgoplex;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author Jose R F Junior
 * Web2ajax@gmail.com
 */
@Path("/company")
public class CompanyResource {

    private static final ConcurrentMap<HttpRequest, CompletableFuture<HttpResponse<String>>> promisesMap
            = new ConcurrentHashMap<>();

    private static final Function<HttpRequest, HttpResponse.BodyHandler<String>> promiseHandler
            = (HttpRequest req) -> HttpResponse.BodyHandlers.ofString();

    private static void accept(Map.Entry<HttpRequest, CompletableFuture<HttpResponse<String>>> entry) {
        entry.getValue().join().body();
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/allResult")
    public String FindCompany() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://177.47.18.202/zuul/empresa/company/allResult/"))
                .build();
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString(),
                pushPromiseHandler())
                .thenApply(HttpResponse::body)
                .thenAccept((b) -> System.out.println(":" + b))
                .join();
            promisesMap.entrySet()
                    .parallelStream()
                    .forEach((entry) -> {
                    entry.getValue().join().body();
        });
        return "ok";
      }

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/allResult2")
    public String FindCompany2() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://177.47.18.202/zuul/empresa/company/allResult/"))
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
       return response.body();
  }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/post1")
    public String PostCompany1() throws Exception {

        var values = new HashMap<String, String>() {{
            put("name", "John Doe");
            put ("occupation", "gardener");
        }};

        var objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        return requestBody;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/post2")
    public String PostCompany2() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // BodyPublishers.ofString() - create a String body
        HttpRequest requestBodyOfString = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"name\": \"Fred Gruger\",\"job\": \"Java\"}"))
                .uri(URI.create("https://reqres.in/api/users"))
                .build();
        HttpResponse<String> responseBodyOfString = client.send(
                requestBodyOfString, HttpResponse.BodyHandlers.ofString());

        // BodyPublishers.ofInputStream() - create an input stream body
        HttpRequest requestBodyOfInputStream = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> inputStream("user.json")))
                .uri(URI.create("https://reqres.in/api/users"))
                .build();


        HttpResponse<String> responseBodyOfInputStream = client.send(
                requestBodyOfInputStream, HttpResponse.BodyHandlers.ofString());

        return null;
    }

    private static HttpResponse.PushPromiseHandler<String> pushPromiseHandler() {
        return HttpResponse.PushPromiseHandler.of(promiseHandler, promisesMap);
    }

    private static ByteArrayInputStream inputStream(String fileName) {
      return  null;
    }
}
