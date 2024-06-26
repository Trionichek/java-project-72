package hexlet.code.controller;

import hexlet.code.dto.BuildUrlPage;
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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

public class UrlsController {

    public static boolean checkURI(String uriStr) {
        try {
            URI uri = new URI(uriStr);
            new URL(uriStr).toURI();
            return !uri.getScheme().isEmpty() && !uri.getAuthority().isEmpty();
        } catch (MalformedURLException | URISyntaxException e) {
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
            if (UrlRepository.find(url).isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "danger");
                throw new Exception();
            } else {
                var createdUrl = new Url(url);
                UrlRepository.save(createdUrl);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flash-type", "success");
                ctx.redirect(NamedRoutes.urlsPath());
            }
        } catch (Exception e) {
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static void list(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        Map<String, UrlCheck> urlsCheck = UrlCheckRepository.findLastCheck();
        var page = new UrlsPage(urls, urlsCheck);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("list.jte", Collections.singletonMap("page", page));
    }

    public static void index(Context ctx) {
        var page = new BuildUrlPage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var urlFound = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        var urlChecks = UrlCheckRepository.getEntities(id);
        var page = new UrlPage(urlFound, urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("show.jte", Collections.singletonMap("page", page));
    }

    public static void check(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id).orElseThrow(() -> new NotFoundResponse("Url not found"));
        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            var statusCode = response.getStatus();
            var body = Jsoup.parse(response.getBody());
            var title = body.title();
            var h1 = body.selectFirst("h1") != null ? body.selectFirst("h1").wholeText() : "none";
            var description = body.selectFirst("meta[name=description]") != null
                ? body.selectFirst("meta[name=description]").attr("content") : "none";
            UrlCheck urlCheck = new UrlCheck(id, statusCode, h1, title, description);
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect(NamedRoutes.urlPath(id));
        } catch (Exception e) {
            //Long id = ctx.pathParamAsClass("id", Long.class).get();
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.urlPath(id));
        }
    }
}
