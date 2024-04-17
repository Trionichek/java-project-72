@import hexlet.code.dto.UrlsPage
@import hexlet.code.repository.UrlCheckRepository
@import java.time.format.DateTimeFormatter
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <div class="container-lg mt-5">
        <h1>Сайты</h1>

        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th>Имя</th>
                <th class="col-2">Последняя проверка</th>
                <th class="col-1">Код ответа</th>
            </tr>
            </thead>
            <tbody>

            @for(var url : page.getUrls())
                <tr>
                    <td>
                        ${url.getId()}
                    </td>
                    <td>
                        <a href="urls/${url.getId()}">${url.getUrl()}</a>
                    </td>
                    <td>
                        @if(!UrlCheckRepository.getEntities(url.getId()).isEmpty())
                            ${UrlCheckRepository.getEntities(url.getId())
                            .get(UrlCheckRepository.getEntities(url.getId()).size() - 1)
                            .getCreatedAt().toLocalDateTime()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}
                        @endif
                    </td>
                    <td>
                        @if(!UrlCheckRepository.getEntities(url.getId()).isEmpty())
                            ${UrlCheckRepository.getEntities(url.getId())
                            .get(UrlCheckRepository.getEntities(url.getId()).size() - 1)
                            .getStatusCode()}
                        @endif
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`
)
