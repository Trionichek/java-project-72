package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;

public class UrlsController {

    public static boolean checkURI(String uriStr) {
        try {
            URI uri = new URI(uriStr);
            return !uri.getScheme().isEmpty() && !uri.getAuthority().isEmpty();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static void create(Context ctx) {
        try {
            String rawUrl = ctx.formParamAsClass("url", String.class).get();
            if (!checkURI(rawUrl)) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.sessionAttribute("flash-type", "danger");
                throw new Exception();
            }
            URI uri = new URI(rawUrl);
            String url = uri.getScheme() + "://" + uri.getAuthority();
            Timestamp createdAt = new Timestamp(new Date().getTime());
            if (UrlRepository.find(url).isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "danger");
                throw new Exception();
            } else {
                var createdUrl = new Url(url, createdAt);
                UrlRepository.save(createdUrl);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flash-type", "success");
                ctx.redirect(NamedRoutes.urlsPath());
            }
        } catch (Exception e) {
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void list(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("list.jte", Collections.singletonMap("page", page));
    }

    public static void index(Context ctx) {
        ctx.render("index.jte");
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var urlFound = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        var urlChecks = UrlCheckRepository.getEntities(id);
        var page = new UrlPage(urlFound, urlChecks);
        ctx.render("show.jte", Collections.singletonMap("page", page));
    }

    public static void check(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            var url = UrlRepository.find(id).orElseThrow(() -> new NotFoundResponse("Url not found"));
            HttpResponse<String> response = Unirest.get(url.getUrl()).asString();
            var statusCode = response.getStatus();
            var body = Jsoup.parse(response.getBody());
            var title = body.title();
            var h1 = body.selectFirst("h1").wholeText();
            var description = body.select("meta[name=description]").attr("content");
            Timestamp createdAt = new Timestamp(new Date().getTime());
            UrlCheck urlCheck = new UrlCheck(id, statusCode, title, h1, description, createdAt);
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect(NamedRoutes.urlPath(id));
        } catch (Exception e) {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.urlPath(id));
        }
    }
}
