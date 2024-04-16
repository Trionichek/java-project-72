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

public class Url {
    private Long id;

    @ToString.Include
    private String url;

    private Timestamp createdAt;

    public Url(String url, Timestamp createdAt) {
        this.url = url;
        this.createdAt = createdAt;
    }

    public Url(String url) {
        this.url = url;
    }
}
