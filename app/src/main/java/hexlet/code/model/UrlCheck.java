package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor

public class UrlCheck {
    private Long id;
    private Long urlId;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private Timestamp createdAt;

    public UrlCheck(Long urlId, int statusCode, String h1, String title, String description, Timestamp createdAt) {
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.h1 = h1;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public UrlCheck(Long urlId, int statusCode, String h1, String title, String description) {
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.h1 = h1;
        this.title = title;
        this.description = description;
    }
}
