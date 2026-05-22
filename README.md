# TA Recruitment System

A lightweight Java Servlet/JSP web application for the BUPT International School Teaching Assistant recruitment workflow.

The system supports three roles:

- Applicant: maintain profile and CV, browse available jobs, view skill match guidance, receive recommended jobs, apply for jobs, withdraw applications, and read messages.
- Module Organiser: create, edit, and cancel jobs, review applicants, compare skill match scores, filter and sort applicant lists, update application status, and read messages.
- Admin: view dashboard data, configure application limits, review applicant usage, supervise jobs, inspect TA workload, and export workload reports.

## Current Scope

The current implementation covers the main end-to-end workflows planned across Iterations 1-4:

- Account registration, login, logout, role-based navigation, authentication, and authorisation.
- Applicant profile management, CV upload/download, job search, application submission, application status tracking, withdrawal, and message viewing.
- Module Organiser job posting, editing, cancellation, applicant review, applicant CV download, match-score comparison, applicant filtering, and final accept/reject decisions.
- Admin dashboard, global and per-applicant application limits, workload monitoring, workload warning, job supervision, and workload CSV export.
- File-based persistence using JSON data files under `data`.
- JUnit 5 tests for DAO, service, utility, filter, workflow, and web contract behaviour.

## Project Structure

- `src/main/java/com/bupt/tarecruit`: Java source code for models, DAOs, services, servlets, filters, and utilities.
- `src/main/webapp`: JSP pages, shared JSP fragments, CSS, JavaScript, images, and `WEB-INF/web.xml`.
- `src/test/java`: JUnit 5 tests for DAO, service, filter, utility, workflow, and web contract coverage.
- `data`: JSON demo data and sample CV files for local testing.
- `docs`: Project documentation, backlog, report drafts, prototype files, and the user manual.
- `run-local.ps1`: Windows helper script for building, deploying to Tomcat, setting the data directory, and optionally opening the browser.
- `deploy.ps1`: Simpler Windows deployment helper for copying the built WAR into Tomcat.
- `deploy-local.sh`: Unix/macOS deployment helper.

## Documentation

- User manual: `docs/user_manual.md`
- Code documentation: `docs/code_documentation.md`
- Product backlog workbook: `docs/backlog.xlsx`
- Report draft: `docs/report.docx`
- Prototype HTML pages: `docs/prototype`

For final assessment packaging, include the source code, tests, README, user manual, code documentation such as JavaDocs, and any required report/video files according to the coursework handout.

## Requirements

- Java 17
- Maven 3.9 or later
- Servlet 6 compatible container, such as Apache Tomcat 10.1 or later

The project intentionally uses Servlet/JSP and JSON files. It does not use Spring Boot or a database.

## Build And Test

Run all automated tests:

```bash
mvn test
```

Build the deployable WAR package:

```bash
mvn package
```

The generated artifact is:

```text
target/ta-recruit.war
```

Generate JavaDocs:

```bash
mvn javadoc:javadoc
```

The generated API documentation is written to:

```text
target/site/apidocs
```

## Local Deployment On Windows

Install Apache Tomcat 10.1 or later, then run PowerShell from the project root.

If Tomcat can be discovered from `TOMCAT_HOME`, `CATALINA_HOME`, or a common install path:

```powershell
.\run-local.ps1
```

If Tomcat is installed in a custom location:

```powershell
.\run-local.ps1 -TomcatDir "C:\path\to\apache-tomcat-10.1.x"
```

Useful options:

```powershell
.\run-local.ps1 -OpenBrowser
.\run-local.ps1 -SkipBuild
.\run-local.ps1 -ContextName ROOT
.\run-local.ps1 -DataDir "D:\path\to\data"
```

After deployment, open the URL printed by the script. With the default context name, the login page is usually:

```text
http://localhost:8080/ta-recruit/login
```

## Manual Tomcat Deployment

1. Run `mvn package`.
2. Copy `target/ta-recruit.war` to Tomcat's `webapps` directory.
3. Start or restart Tomcat.
4. Open `/ta-recruit/login` under the Tomcat host and port.

If the WAR is renamed to `ROOT.war`, open `/login`.

## Runtime Data

Runtime data is stored in JSON files under `data` by default. If the app is started outside the repository directory, set one of the following so the app reads and writes the intended data folder:

```text
TARECRUIT_DATA_DIR
-Dtarecruit.data.dir=...
```

The main data files are:

- `data/users.json`
- `data/applicants.json`
- `data/cvs.json`
- `data/jobs.json`
- `data/applications.json`
- `data/messages.json`
- `data/settings.json`
- `data/applicant_limit_policies.json`
- `data/cv`

## Demo Accounts

All bundled demo accounts use:

```text
password123
```

Applicants:

- `applicant1@example.com`
- `applicant2@example.com`
- `applicant3@example.com`

Module Organisers:

- `organiser1@example.com`
- `organiser2@example.com`

Admin:

- `3423432@qq.com`

## Main Routes

- Public: `/login`, `/register`, `/logout`
- Applicant: `/applicant/jobs`, `/applicant/job-detail`, `/applicant/profile`, `/applicant/cv`, `/applicant/applications`, `/applicant/applications/detail`, `/applicant/messages`
- Module Organiser: `/organiser/jobs`, `/organiser/jobs/create`, `/organiser/jobs/edit`, `/organiser/jobs/applications`, `/organiser/applications/detail`, `/organiser/messages`
- Admin: `/admin/home`, `/admin/settings`, `/admin/users`, `/admin/workloads`, `/admin/workloads/export`, `/admin/jobs`

## Verification Status

The project has been verified with:

```bash
mvn test
mvn package
```

At the time this README was updated, the automated test suite passed with 193 tests and the WAR package was generated successfully.
