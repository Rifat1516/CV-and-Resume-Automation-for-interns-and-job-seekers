-- CV Job Matcher schema
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          TEXT NOT NULL,
    phone         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    age           INTEGER,
    city          TEXT,
    role          TEXT NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    created_at    TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS cvs (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    full_name  TEXT,
    email      TEXT,
    summary    TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS education (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    cv_id          INTEGER NOT NULL REFERENCES cvs(id) ON DELETE CASCADE,
    institution    TEXT NOT NULL,
    degree         TEXT NOT NULL,
    field_of_study TEXT,
    start_year     INTEGER,
    end_year       INTEGER
);

CREATE TABLE IF NOT EXISTS experience (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    cv_id       INTEGER NOT NULL REFERENCES cvs(id) ON DELETE CASCADE,
    company     TEXT NOT NULL,
    title       TEXT NOT NULL,
    description TEXT,
    start_year  INTEGER,
    end_year    INTEGER
);

CREATE TABLE IF NOT EXISTS skills (
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    cv_id INTEGER NOT NULL REFERENCES cvs(id) ON DELETE CASCADE,
    name  TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS certifications (
    id     INTEGER PRIMARY KEY AUTOINCREMENT,
    cv_id  INTEGER NOT NULL REFERENCES cvs(id) ON DELETE CASCADE,
    name   TEXT NOT NULL,
    issuer TEXT,
    year   INTEGER
);

-- Languages spoken, e.g. "English - Native", "Mandarin - Advanced"
CREATE TABLE IF NOT EXISTS languages (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    cv_id        INTEGER NOT NULL REFERENCES cvs(id) ON DELETE CASCADE,
    name         TEXT NOT NULL,
    proficiency  TEXT
);

-- Awards and memberships/affiliations from the CV, distinguished by category.
CREATE TABLE IF NOT EXISTS cv_highlights (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    cv_id       INTEGER NOT NULL REFERENCES cvs(id) ON DELETE CASCADE,
    category    TEXT NOT NULL CHECK (category IN ('AWARD', 'AFFILIATION')),
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS job_notices (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    company              TEXT NOT NULL,
    title                TEXT NOT NULL,
    location             TEXT,
    salary               TEXT,
    experience_required  TEXT,
    description          TEXT,
    total_positions      INTEGER NOT NULL DEFAULT 1,
    filled_positions     INTEGER NOT NULL DEFAULT 0,
    deadline             TEXT,
    contact_email        TEXT,
    uploaded_by_user_id  INTEGER NOT NULL REFERENCES users(id),
    created_at           TEXT NOT NULL DEFAULT (datetime('now'))
);

-- Bullet-point responsibilities and requirements extracted from the notice,
-- e.g. "Design, develop, and maintain Windows applications using C# and C++."
CREATE TABLE IF NOT EXISTS job_requirements (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    job_notice_id INTEGER NOT NULL REFERENCES job_notices(id) ON DELETE CASCADE,
    category      TEXT NOT NULL CHECK (category IN ('RESPONSIBILITY', 'REQUIREMENT')),
    description   TEXT NOT NULL
);

-- Per-role breakdown within a notice, e.g. a "hiring 10 engineers" notice
-- might break down into "AI Engineer" (1 of 1 available), "Web Developer"
-- (0 of 2 available), etc.
CREATE TABLE IF NOT EXISTS job_positions (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    job_notice_id INTEGER NOT NULL REFERENCES job_notices(id) ON DELETE CASCADE,
    role_name     TEXT NOT NULL,
    total_count   INTEGER NOT NULL DEFAULT 1,
    filled_count  INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS match_results (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    job_notice_id  INTEGER NOT NULL REFERENCES job_notices(id) ON DELETE CASCADE,
    score          REAL NOT NULL,
    rank_level     TEXT NOT NULL CHECK (rank_level IN ('HIGH', 'MEDIUM', 'LOW')),
    created_at     TEXT NOT NULL DEFAULT (datetime('now')),
    UNIQUE (user_id, job_notice_id)
);

CREATE TABLE IF NOT EXISTS gap_recommendations (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    match_result_id INTEGER NOT NULL REFERENCES match_results(id) ON DELETE CASCADE,
    missing_item    TEXT NOT NULL,
    suggestion      TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_job_notices_company_title ON job_notices(company, title);
CREATE INDEX IF NOT EXISTS idx_match_results_user ON match_results(user_id);
