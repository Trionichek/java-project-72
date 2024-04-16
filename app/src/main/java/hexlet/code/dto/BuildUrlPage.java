package hexlet.code.dto;

import io.javalin.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BuildUrlPage extends BasePage {
    private String url;
    private Map<String, List<ValidationError<Object>>> errors;
    public BuildUrlPage(String url) {
    }
}
