# Java Maven Webapp (Servlets + JSP) Standards

## 1. Project Architecture

Adhere to a **Domain-Based Modular Structure** to ensure separation of concerns.

* **Java Source (`src/main/java/`)**
* `shared/`: Global configurations, filters, and utility classes.
* `features/<domain>/`: Feature-specific logic (e.g., `users`, `orders`).
* `models/`: Strict POJOs representing database entities.
* `dao/`: JDBC/Data Access logic only.
* `services/`: Intermediate business logic layer.
* `servlets/`: HTTP controllers (Route handling).

* **Web Source (`src/main/webapp/`)**
* `WEB-INF/views/`: Internal JSP files (protected from direct URL access).
* `assets/`: Static resources (`css/`, `js/`, `img/`).
* `components/`: Reusable JSP fragments (headers, footers).

---

## 2. Implementation Guidelines

* **State Management** — Use `HttpServletRequest` for page-level data flow. Restrict `HttpSession` to global authentication state to prevent session bloat.
* **Persistence** — Enforce `try-with-resources` for all JDBC objects (`Connection`, `PreparedStatement`, `ResultSet`). Catch `SQLExceptions` in the DAO layer and wrap in custom exceptions.
* **Naming Conventions** — Standardize naming: `PascalCase` for Java (`UserDAO.java`), `snake_case` for database schemas, and `kebab-case` for REST-like URLs.
* **Routing** — Utilize Servlet 3.0 `@WebServlet` annotations for mapping. Avoid `web.xml` for standard feature routes to keep logic near the controller.
* **JSP Standards** — **Strictly zero scriptlets.** Render all dynamic content via JSTL and Expression Language (`${}`). Use `<jsp:include>` for modular UI composition.
* **Flow Control** — Implement the **Post-Redirect-Get (PRG)** pattern for all data mutations. Every `POST` must conclude with a `response.sendRedirect()` to a `GET` route.
* **Error Handling** — Map global `<error-page>` entries in `web.xml` for 404/500 codes. Log stack traces server-side; never expose raw SQL or traces to the UI.

---

## 3. Frontend & UX Standards

* **Layout**: Use a CSS Grid/Flexbox framework (Bootstrap/Tailwind).
* **Consistency**: Define global branding via CSS variables in `:root`.
* **Feedback**: Implement standardized Alert/Toast components for request-bound success or error messages.

---

## 4. Build & Validation Workflow

All changes must pass the following Maven lifecycle phases before deployment:

1. **Clean & Compile**: `mvn clean compile`
* Ensures fresh bytecode and dependency resolution.


2. **Static Analysis**: `mvn checkstyle:check` (or `spotless:check`)
* Enforces project-wide formatting and linting rules.


3. **Artifact Packaging**: `mvn package`
* Validates the final `.war` structure and runs unit tests.

---
