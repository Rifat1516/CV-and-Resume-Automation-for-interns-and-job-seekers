# CV Job Matcher

JavaFX + Maven + SQLite desktop app that matches a user's CV against a shared
pool of job notices, ranks the fit, and reports gaps.

## Requirements
- JDK 17+
- Maven 3.8+
- Internet access on first build (Maven will download JavaFX, SQLite JDBC, and jBCrypt from Maven Central)

## Run it
```bash
mvn clean javafx:run
```

**App flow**: Welcome screen (Continue / Exit) → role selection (User / Admin / Exit) →
- **User** → sign-in-or-sign-up choice → sign in (name + phone + password) or
  sign up (name, phone, password; always creates role `USER`)
- **Admin** → name + phone + password login against the single fixed admin account

Login now checks name + phone + password against every account — the name
is compared (case-insensitively) to what was stored at sign-up, so phone +
password alone is no longer enough to log in as someone else.

Both land on the dashboard, which now shows:
- **My CV**, **Upload job notice**, **View job notices**, **Job availability** — visible to everyone
- **Update job notice (admin)**, **View all users (admin)** — visible only to admin
- **Exit** — returns to role selection

On first launch the app seeds the single fixed admin account:
- name: `Irfan`
- phone: `01719602096`
- password: `12345678`

There is no UI path to create another admin — the admin login screen only
authenticates against this one seeded account, and user self-registration
(name, phone, password) always creates a regular `USER` account.

The SQLite file `cvmatcher.db` is created automatically in the project root
(schema comes from `src/main/resources/db/schema.sql`, applied automatically
by `DatabaseManager` on startup).

**If you already ran an earlier version of this project**: delete your
existing `cvmatcher.db` before running again. The schema gained several new
columns and tables (job notice location/salary/experience/deadline/contact
email, job_requirements, job_positions, languages, cv_highlights, cv email) and
`CREATE TABLE IF NOT EXISTS` will not retroactively alter an existing table —
you'll get a "no such column" error otherwise.

## Important: this has NOT been compiled/run yet
This code was written in a sandboxed environment without access to Maven
Central, so `mvn clean compile` has **not** been executed against it. The
package structure, imports, and SQL are correct to the best of my knowledge,
but please run `mvn clean compile` locally first and expect to fix a handful
of small issues — normal for a first build of a project this size.

## What's implemented
- Full DB schema (`schema.sql`) with FKs/constraints for all entities,
  modeled after real CV and job-vacancy formats:
  - **CV**: summary, education, experience, skills, certificates, languages,
    and awards/affiliations (`cv_highlights`, split by category)
  - **Job notice**: company, title, location, salary, experience required,
    vacancies, deadline, contact email, plus separate responsibility and
    requirement bullet lists (`job_requirements`, split by category), plus
    a per-role position breakdown (`job_positions`) -- e.g. one notice can
    say "1 AI Engineer available, 2 Web Developers not available" instead
    of a single flat total/filled count
- DAO layer (SQLite) for all entities, behind interfaces, with cascading
  transactional saves for every child section
- All 9 design patterns discussed, wired into real working logic:
  - **Builder** — `CvBuilder`
  - **Strategy** — `ScoringStrategy` (`KeywordOverlapStrategy`, `TfIdfCosineStrategy`),
    scored against `JobNotice.toSearchableText()` / `Cv.toSearchableText()`,
    which fold in every structured field, not just free-text description
  - **Template Method** — `JobProcessor`
  - **Chain of Responsibility** — `GapCheckHandler` chain (skill/education/experience),
    now reading the structured requirement bullets and experience-required field directly
  - **Observer** — `MatchObserver` / `HighMatchNotifier`
  - **Proxy** — `AvailabilityServiceProxy`, guarding `updateFilledPositions`,
    `updateDescription`, `addPosition`, and `updatePositionFilledCount` behind
    the same admin-only check
  - **State** — `NoticeState` (Open/PartiallyFilled/Closed)
  - **Singleton** — `DatabaseManager`
- Duplicate-notice detection service (two-tier: exact company+title key, then
  fuzzy cosine similarity over the full structured notice text), reusing the
  same `CosineSimilarity` utility as CV-job scoring
- `UserDirectoryService` / `UserDirectoryServiceProxy` — a second, independent
  use of the Proxy pattern: only an admin can list all registered users
- A working JavaFX flow, described above, including a full CV builder screen
  (`CvBuilderController`/`cv_builder.fxml`) with add-and-review sections for
  every repeatable part of the CV, and a job-position availability screen
  (`JobPositionsController`/`job_positions.fxml`) showing two tables
  (available / unavailable positions across every notice) -- viewable by
  everyone, with admin-only controls to add a new role or edit how many are
  filled

## What's intentionally left for you to build out
This is a scaffold, not a finished submission — you and your teammate should
extend it, not just hand it in as-is:
- **Remove/edit buttons** in the CV builder's list views — right now you can
  only add entries, not remove a mistaken one, before saving
- **"Show unavailable/booked jobs" screen** — currently availability is visible
  in the main notice table; a dedicated filtered view is easy to add
  (`notice.getState().isVisibleAsAvailable()` already gives you the filter)
- **A richer gap-report screen** — `MatchResult.getGaps()` is shown in the
  ranking alert now, but a dedicated results screen (e.g. one row per notice,
  expandable gap list) would present better than a single alert dialog
- **Input validation polish, styling, and further FXML `fx:id` wiring checks**
- **JUnit tests** for the service layer (the DAO/model separation makes this
  straightforward — services take DAOs as constructor params, so you can pass
  in fakes/mocks)
- **ER diagram and UML class diagrams** for your submission — the schema and
  class structure here are stable enough to draw from directly

## Package layout
```
com.cvmatcher
 ├── model/            entities + model/state (State pattern)
 ├── dao/              DatabaseManager (Singleton) + DAOs
 ├── service/          CvBuilder, MatchingService, JobProcessor, GapAnalyzer,
 │                      DuplicateCheckService, AvailabilityService(+Proxy), AuthService
 ├── service/strategy/  ScoringStrategy + implementations
 ├── service/chain/     GapCheckHandler + implementations
 ├── service/observer/  MatchObserver + implementation
 ├── ui/                JavaFX controllers, SceneNavigator, SessionContext
 └── util/              PasswordHasher, Tokenizer, CosineSimilarity
```
