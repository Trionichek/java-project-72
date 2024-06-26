import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.stream.Collectors;

import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.util.NamedRoutes;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
public class TestApp {
    Javalin app;
    private static MockWebServer mockServer;

    public static String getSite(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }

    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        var mockResponse = new MockResponse()
                .setBody(getSite("example.html"));
        mockServer.enqueue(mockResponse);
    }

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
    }
    @Test
    void testRoot() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });

    }

    @Test
    void testRegisterNewSites() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            client.post(NamedRoutes.urlsPath(), requestBody);
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            var bodyString = response.body().string();

            assertEquals("https://www.example.com",
                    UrlRepository.find("https://www.example.com").get().getName());
            assertThat(bodyString).contains("Сайты");
            assertThat(bodyString).contains("https://www.example.com");
        });
    }

    @Test
    void testDoubleSite() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            client.post(NamedRoutes.urlsPath(), requestBody);
            client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(UrlRepository.getEntities()).hasSize(1);
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        var url = new Url("https://www.example.com", new Timestamp(new Date().getTime()));
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testShowUrl() {
        JavalinTest.test(app, (server, client) -> {
            var urlName = "https://www.example.com";
            var url = new Url(urlName, new Timestamp(new Date().getTime()));
            UrlRepository.save(url);

            assertTrue(UrlRepository.find(url.getId()).isPresent());
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            var bodyString = response.body().string();
            assertThat(bodyString).contains("Сайт:");
            assertThat(bodyString).contains(urlName);
            assertThat(bodyString).contains("Запустить проверку");
        });
    }

    @Test
    void testCheckUrl() throws SQLException {
        var url = "https://www.example.com";
        Url urlForCheck = new Url(url, new Timestamp(new Date().getTime()));
        UrlRepository.save(urlForCheck);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + urlForCheck.getId() + "/checks");
            assertThat(response.code()).isEqualTo(200);
            var lastCheck = UrlCheckRepository.find(urlForCheck.getId()).orElseThrow();
            assertThat(lastCheck.getTitle()).isEqualTo("Example Domain");
            assertThat(lastCheck.getH1()).isEqualTo("Example Domain");
            assertThat(lastCheck.getDescription()).isEqualTo("none");
        });
    }
}
