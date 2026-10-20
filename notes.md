
``` text

controller/   — приём HTTP, вызов сервиса, выбор страницы
service/      — LinkService (создание, поиск, валидация), StatsService, CodeGenerator
repository/   — LinkRepository, ClickRepository
model/        — Link, Click
dto/          — CreateLinkRequest, LinkView, DayStat
exception/    — InvalidUrlException, AliasTakenException, LinkExpiredException, LinkNotFoundException
```
