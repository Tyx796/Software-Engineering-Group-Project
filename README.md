# TA Recruitment System

A lightweight Java Servlet/JSP web application for the BUPT International School Teaching Assistant recruitment workflow.

The system supports three roles:

- Applicant: maintain profile/CV, browse jobs, view skill-gap guidance, receive recommended jobs, apply for jobs, withdraw applications, and read messages.
- Module Organiser: create/edit/cancel jobs, review applicants, compare skill match scores, filter and sort applicant lists, update application status, and read messages.
- Admin: view dashboard data, configure application limits, review users, supervise jobs, inspect TA workload, and export workload reports.

## Current Scope

The implementation is aligned with the current backlog in:

```text
docs/backlog/current/backlog_aligned_to_current_code.xlsx
```

The workbook contains Product Backlog V3 with 49 stories across Iterations 1-4. The current codebase now covers the main end-to-end flows through Iteration 4, including applicant recommendations, skill-gap guidance, organiser match scoring, workload warnings, and workload CSV export.

## Project Structure

- `src/main/java/com/bupt/tarecruit`: Java source code for models, DAOs, services, servlets, filters, and utilities.
- `src/main/webapp`: JSP pages, shared JSP fragments, CSS, JavaScript, and `WEB-INF/web.xml`.
- `src/test/java`: JUnit 5 tests for DAO, service, filter, utility, and workflow coverage.
- `data`: JSON demo data and sample CV files for local testing.
- `docs`: Project documentation, backlog, coursework handout, prototype, planning notes, reports, and task allocation.
- `deploy.ps1`: Local Tomcat deployment helper script.

## Documentation

Start with [docs/README.md](docs/README.md). It explains which documents are current, which are archived, and where to find backlog, planning, reports, prototype files, and task allocation.

For an implementation summary based on the current code, see [docs/current_status.md](docs/current_status.md).

## Requirements

- Java 17
- Maven 3.9+
- Servlet 6 compatible container, such as Tomcat 10.1+

## Build And Test

Run all tests:

```bash
mvn test
```

Build the WAR package:

```bash
mvn package
```

The generated artifact is:

```text
target/ta-recruit.war
```

Deploy the WAR to a compatible servlet container and open the application in a browser.

On macOS with Homebrew Tomcat 10 installed, you can rebuild and redeploy locally with:

```bash
./deploy-local.sh
```

Useful options:

```bash
./deploy-local.sh --skip-build
./deploy-local.sh --open
./deploy-local.sh --context ROOT
./deploy-local.sh --data-dir /absolute/path/to/data
```

## Runtime Data

Runtime data is stored in JSON files under `data` by default. If the app is started outside the repository directory, set one of the following so the app still reads and writes the intended data folder:

```text
TARECRUIT_DATA_DIR
-Dtarecruit.data.dir=...
```

## Demo Accounts

All bundled demo accounts use:

```text
password123
```

Applicants:

- `applicant1@example.com`
- `applicant2@example.com`
- `applicant3@example.com`

Organisers:

- `organiser1@example.com`
- `organiser2@example.com`

Admin:

- `3423432@qq.com`

## Main Routes

- `/login`, `/register`, `/logout`
- `/applicant/jobs`, `/applicant/job-detail`, `/applicant/profile`, `/applicant/cv`, `/applicant/applications`, `/applicant/messages`
- `/organiser/jobs`, `/organiser/jobs/create`, `/organiser/jobs/edit`, `/organiser/jobs/applications`, `/organiser/messages`
- `/admin/home`, `/admin/settings`, `/admin/users`, `/admin/workloads`, `/admin/workloads/export`, `/admin/jobs`
