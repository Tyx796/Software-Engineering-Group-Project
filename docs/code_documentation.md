# TA Recruitment System Code Documentation

## 1. Overview

The TA Recruitment System is a lightweight Java Servlet/JSP web application for managing Teaching Assistant recruitment. The codebase is intentionally structured around fundamental software engineering layers rather than a heavyweight framework:

- JSP pages render the user interface.
- Servlets handle HTTP requests and responses.
- Services contain business rules and workflow coordination.
- DAOs isolate persistence operations.
- JSON files store all runtime data.
- Utility classes centralise validation, path resolution, password hashing, and presentation helpers.

The application has three main user roles:

- Applicant
- Module Organiser
- Admin

Role-specific pages are protected by authentication and authorisation filters.

## 2. Package Structure

| Package | Responsibility |
| --- | --- |
| `com.bupt.tarecruit.model` | Domain objects and view models such as `User`, `Applicant`, `Job`, `Application`, `Message`, dashboard views, workload views, and skill match views. |
| `com.bupt.tarecruit.dao` | DAO interfaces defining persistence operations. |
| `com.bupt.tarecruit.dao.impl` | JSON-backed DAO implementations. |
| `com.bupt.tarecruit.service` | Business services for registration, login, applicant profiles, CVs, jobs, applications, recommendations, matching, admin settings, workload monitoring, and messages. |
| `com.bupt.tarecruit.servlet` | Servlet controllers for public, applicant, organiser, and admin routes. |
| `com.bupt.tarecruit.filter` | Authentication, authorisation, and error-handling filters. |
| `com.bupt.tarecruit.util` | Shared helpers for JSON storage, validation, filesystem paths, passwords, sessions, role checks, and status display. |

## 3. Main Architecture

The application follows this request flow:

```text
Browser
  -> Servlet Filter Chain
  -> Role-specific Servlet
  -> Service Layer
  -> DAO Interface
  -> JSON-backed DAO Implementation
  -> JSON files under data/
```

JSP pages do not directly access storage. Servlets prepare request attributes, services enforce business rules, and DAOs read or write JSON data.

## 4. Key Services

### `UserService`

Handles account registration, login, and user lookup.

Important behaviour:

- Normalises email addresses before storing.
- Rejects duplicate accounts.
- Hashes new passwords with `PasswordUtil`.
- Verifies password hashes during login.
- Rehashes legacy password hashes after successful login.

### `ApplicantService`

Maintains applicant profile information.

Important behaviour:

- Creates or updates applicant profile data.
- Validates full name, phone, student ID, and programme.
- Normalises skill and availability lists.
- Links uploaded CV filenames to profiles.
- Checks whether an applicant profile is complete before application submission.

### `CvService`

Handles CV upload, replacement, and lookup.

Important behaviour:

- Requires an applicant profile before CV upload.
- Accepts PDF, DOC, and DOCX files.
- Rejects empty files.
- Stores CV files under `data/cv/<userId>`.
- Replaces older CV files for the same applicant.

### `JobService`

Manages the module organiser job lifecycle.

Important behaviour:

- Creates jobs with title, department, description, requirements, weekly hours, assistant quota, and deadline.
- Parses job requirements from comma-separated or newline-separated text.
- Filters available jobs for applicants by open status and deadline.
- Searches jobs by title, department, and requirements.
- Verifies organiser ownership before edit or cancel operations.
- Cancels linked applications and sends applicant notifications when a job is cancelled.

### `ApplicationService`

Coordinates applicant applications and organiser decisions.

Important behaviour:

- Requires a complete applicant profile before applying.
- Requires an uploaded CV before applying.
- Blocks duplicate active applications for the same job.
- Checks active application limits.
- Blocks applications for closed, expired, or full jobs.
- Allows organisers to move pending applications into review.
- Allows organisers to accept or reject applications they own.
- Rejects remaining active applications automatically when a job becomes full.
- Allows applicants to withdraw pending, reviewing, or accepted applications.
- Sends a message when an accepted application is withdrawn.

### `RecruitmentPolicyService`

Centralises reusable policy checks.

Important behaviour:

- Resolves the effective applicant application limit from per-user overrides or global defaults.
- Counts active applications that affect the applicant limit.
- Counts accepted applications for a job.
- Calculates remaining assistant slots.
- Determines whether a job is full.

### `SkillMatchService`

Calculates deterministic skill match data.

Important behaviour:

- Normalises applicant skills and job requirements.
- Compares skills case-insensitively.
- Returns matched skills, missing skills, and a percentage match score.
- Keeps matching explainable for the applicant, organiser, and viva demonstration.

### `JobRecommendationService`

Builds applicant job recommendations.

Important behaviour:

- Requires an applicant profile.
- Excludes jobs already applied for.
- Excludes full, closed, and expired jobs.
- Sorts recommendations by match score, deadline, and title.
- Uses `SkillMatchService` so recommendations can be explained.

### `OrganiserApplicationReviewService`

Builds organiser-facing applicant review rows.

Important behaviour:

- Joins applications with applicant profiles and skill match data.
- Applies status filters.
- Applies keyword search by applicant name, student ID, programme, or user ID.
- Sorts by applied time, match score, or status.

### `AdminService`

Provides administrator dashboard, limit, workload, and job supervision data.

Important behaviour:

- Builds dashboard summary metrics.
- Shows applicant application limit usage.
- Saves and clears per-applicant limit overrides.
- Builds workload views from accepted applications.
- Flags overloaded applicants using a weekly-hours threshold.
- Builds all-job supervision views across organisers.

### `WorkloadReportCsvService`

Exports workload data as CSV.

Important behaviour:

- Includes applicants with accepted assignments.
- Escapes commas, quotes, and line breaks.
- Produces spreadsheet-compatible output for admin export.

### `MessageService`

Stores workflow messages for applicants and organisers.

Important behaviour:

- Creates messages linked to jobs or applications.
- Returns recipient inboxes sorted by newest first.
- Marks messages as read only for the owning recipient.

## 5. Persistence Design

The project follows the coursework restriction to avoid databases. Runtime data is stored as JSON files in the configured data directory.

Main files:

- `users.json`
- `applicants.json`
- `cvs.json`
- `jobs.json`
- `applications.json`
- `messages.json`
- `settings.json`
- `applicant_limit_policies.json`

Uploaded CV files are stored under:

```text
data/cv/<userId>/cv.<extension>
```

The data directory is resolved by `AppPaths` in this order:

1. JVM system property `tarecruit.data.dir`
2. Environment variable `TARECRUIT_DATA_DIR`
3. Project-level `data` directory when the app can find `pom.xml`
4. Tomcat `catalina.base/data`

## 6. Security And Access Control

Security is intentionally simple and transparent:

- `AuthenticationFilter` redirects unauthenticated users to `/login`.
- `AuthorizationFilter` blocks users from accessing another role's URL space.
- `PasswordUtil` hashes new passwords with salted SHA-256 and supports legacy hash migration.
- `ErrorHandlingFilter` logs unexpected errors server-side and forwards users to a generic error page.

Role URL spaces:

- Applicant: `/applicant/*`
- Module Organiser: `/organiser/*`
- Admin: `/admin/*`

## 7. Validation And Error Handling

Validation is split across browser-side form validation and server-side Java validation.

Server-side validation is handled mainly by:

- `DataValidator`
- `ApplicantService`
- `CvService`
- `JobService`
- `ApplicationService`
- `RecruitmentPolicyService`

Examples:

- Required fields cannot be empty.
- Email and phone formats are checked.
- Passwords must meet the minimum length.
- Job deadline must be today or later.
- Weekly hours, assistant quota, workload threshold, and limits must be valid numbers.
- Unsupported CV files are rejected.
- Invalid workflow actions raise clear `IllegalArgumentException` messages.

## 8. Servlet And JSP Flow

Servlets extend `BaseServlet`, which provides common forwarding, redirecting, flash messages, error attributes, and application status display helpers.

Representative routes:

- `/login` -> `LoginServlet` -> `login.jsp`
- `/register` -> `RegisterServlet` -> `register.jsp`
- `/applicant/jobs` -> `JobListServlet` -> `applicant/job_list.jsp`
- `/applicant/job-detail` -> `JobDetailServlet` -> `applicant/job_detail.jsp`
- `/applicant/profile` -> `ApplicantProfileServlet` -> `applicant/profile.jsp`
- `/organiser/jobs` -> `OrganiserJobsServlet` -> `organiser/job_list.jsp`
- `/organiser/jobs/applications` -> `OrganiserJobApplicationsServlet` -> `organiser/job_applications.jsp`
- `/admin/home` -> `AdminHomeServlet` -> `admin/home.jsp`
- `/admin/workloads` -> `AdminWorkloadsServlet` -> `admin/workloads.jsp`

## 9. Testing Structure

Automated tests are under `src/test/java`.

Coverage categories:

- DAO tests for JSON-backed persistence.
- Service tests for profile, CV, job, application, message, settings, recommendation, matching, policy, and admin logic.
- Utility tests for validation, password hashing, role checks, status view helpers, and path resolution.
- Filter tests for role authorisation.
- Workflow tests for Iterations 1-4.
- Web contract tests for JSP content, validation, error handling, frontend modernization, local deployment script behaviour, workload export links, and organiser review filters.

The full suite can be run with:

```bash
mvn test
```

## 10. Generating JavaDocs

JavaDocs are configured through `maven-javadoc-plugin` in `pom.xml`.

Generate API documentation with:

```bash
mvn javadoc:javadoc
```

Generated files are written to:

```text
target/site/apidocs
```

For final assessment packaging, include this folder in the software ZIP as the JavaDocs/code documentation artifact.

## 11. Extension Points

Future development can extend the system in these areas:

- Replace JSON DAOs with another persistence mechanism by implementing the existing DAO interfaces.
- Improve skill matching while preserving explainable results in `SkillMatchView`.
- Add more notification types through `MessageType` and `MessageService`.
- Add more admin reports by reusing `AdminService` view models.
- Add servlet endpoints and JSP pages while keeping business rules in services.

## 12. Design Rationale

The design keeps the system simple, modular, and testable:

- Servlets stay thin and delegate business logic to services.
- Services can be tested without Tomcat.
- DAO interfaces isolate persistence details.
- JSON storage satisfies the coursework restriction against databases.
- Role-based URL spaces make access rules easy to inspect.
- Rule-based matching keeps recommendations explainable for demonstration and viva questions.
