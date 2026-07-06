<!--
=============================================================================
SYNC IMPACT REPORT
=============================================================================
Version change: N/A (initial ratification) → 1.0.0

Modified principles: None (first ratification)

Added sections:
  - I. Clean Architecture (Robert Martin)
  - II. BDD Testing (Unit, Integration, Functional)
  - III. Good Programming Practices (SOLID, YAGNI, DRY)
  - IV. API First with OpenAPI & openapi-generator
  - V. Coverage Metrics & Quality Gates (JaCoCo)
  - Technology Standards
  - Development Workflow & Quality Gates
  - Governance

Templates requiring updates:
  ✅ .specify/memory/constitution.md — this file (written now)
  ⚠  .specify/templates/plan-template.md — Constitution Check section should
       reference the 5 principles below; update manually to add checklist items.
  ⚠  .specify/templates/spec-template.md — Acceptance Scenarios already use
       Given/When/Then (BDD-aligned); no structural change needed, but add a
       note that BDD format is MANDATORY per Principle II.
  ⚠  .specify/templates/tasks-template.md — Test tasks must explicitly call out
       BDD scenarios and JaCoCo report generation per Principles II and V;
       update manually.

Deferred TODOs:
  - None. All required fields resolved on first ratification.
=============================================================================
-->

# citasalud-service Constitution

## Core Principles

### I. Clean Architecture (Robert Martin)

The codebase MUST be structured following the Clean Architecture model defined
by Robert C. Martin ("Uncle Bob"). This means:

- **Layer separation is non-negotiable**: Entities → Use Cases → Interface Adapters
  → Frameworks & Drivers. No inner layer may depend on an outer layer.
- **Domain entities and use cases** MUST have zero dependency on frameworks,
  databases, UI, or delivery mechanisms (Spring, JPA, REST controllers, etc.).
- **Dependency Inversion** MUST be applied at every boundary: outer layers depend
  on abstractions (interfaces/ports) defined by inner layers.
- **Entities** encapsulate enterprise-wide business rules and MUST be plain Java
  objects (POJOs) with no framework annotations.
- **Use Cases** (Interactors) encapsulate application-specific business rules and
  MUST be independently unit-testable without starting the application context.
- **Interface Adapters** (controllers, presenters, gateways) translate between
  use-case data structures and external formats (DTOs, JSON, DB rows).
- **Frameworks & Drivers** (Spring Boot, JPA, external HTTP clients) are confined
  to the outermost ring. Replacing a framework MUST not require changes to use
  cases or entities.
- Every architectural boundary violation MUST be documented in the plan's
  Complexity Tracking table with justification.

**Rationale**: Clean Architecture maximises long-term maintainability, testability,
and replaceability of infrastructure concerns. It is the foundational contract for
this service.

### II. BDD Testing (Unit, Integration, Functional)

All tests MUST be written using **Behaviour-Driven Development (BDD)** principles
and the **Given / When / Then** scenario structure:

- **Unit tests** MUST cover each use case, entity, and domain service in isolation
  (no Spring context, no real I/O). Each test class MUST correspond to one
  production class. Use JUnit 5 + Mockito; scenario method names MUST follow
  the pattern `given_<state>_when_<action>_then_<outcome>`.
- **Integration tests** MUST verify the collaboration between layers (e.g.,
  use-case → gateway → database, or controller → use-case). They MAY start a
  partial Spring context (`@SpringBootTest` slices are acceptable). Test data
  MUST be managed through fixtures or test containers, not shared state.
- **Functional / Acceptance tests** MUST validate end-to-end user scenarios
  against the running service. Each functional test MUST map 1:1 to an
  Acceptance Scenario defined in the feature's `spec.md`. Frameworks such as
  Cucumber (JVM) are encouraged to express scenarios in plain language.
- Tests MUST be written **before** implementation (TDD inner loop within BDD):
  Red → Green → Refactor.
- No test MUST rely on execution order, shared mutable state, or hard-coded
  environment values.

**Rationale**: BDD ensures that tests are traceable to business requirements,
serve as living documentation, and are owned equally by developers and
stakeholders.

### III. Good Programming Practices (SOLID, YAGNI, DRY)

All production code MUST conform to the following three families of principles:

**SOLID**
- **S** — Single Responsibility: every class/method has exactly one reason to change.
- **O** — Open/Closed: classes are open for extension, closed for modification.
- **L** — Liskov Substitution: subtypes MUST be substitutable for their base types
  without altering program correctness.
- **I** — Interface Segregation: no client MUST depend on methods it does not use;
  prefer small, focused interfaces (ports).
- **D** — Dependency Inversion: high-level modules MUST NOT depend on low-level
  modules; both MUST depend on abstractions.

**YAGNI (You Aren't Gonna Need It)**
- Implement only what is required by the current, confirmed user story.
- Speculative abstractions, configurable extension points, and unused parameters
  are PROHIBITED. Every line of code MUST justify its existence against a
  concrete acceptance scenario.

**DRY (Don't Repeat Yourself)**
- Every piece of knowledge MUST have a single, authoritative representation.
- Duplication of logic (not just syntax) MUST be extracted into a named concept.
- Copy-paste of business rules across layers is a constitution violation.

**Rationale**: These principles collectively reduce accidental complexity, prevent
scope creep, and keep the codebase coherent as the team scales.

### IV. API First with OpenAPI & openapi-generator

Every external and inter-service API exposed by citasalud-service MUST follow
the **API First** design approach:

- An **OpenAPI 3.x contract** (`openapi.yml`) MUST be authored and reviewed
  **before** any implementation code is written for that endpoint.
- The contract lives under `src/main/resources/openapi/` (or equivalent) and is
  the single source of truth for the API surface.
- Server stubs and model classes MUST be generated via **openapi-generator** as
  part of the build lifecycle (Maven/Gradle plugin). Hand-writing generated code
  is PROHIBITED.
- The generated interfaces define the contract; implementation classes MUST
  implement those interfaces — not extend generated base classes where avoidable.
- Any breaking change (removed field, changed type, removed endpoint) MUST bump
  the API major version and be communicated to consumers before merging.
- Non-breaking additions (new optional fields, new endpoints) MUST bump the minor
  version.
- Contract-level tests (e.g., using Spring Cloud Contract or Pact) MUST validate
  that the running service conforms to its published OpenAPI spec.

**Rationale**: API First guarantees that consumer needs drive the API shape, that
contracts are machine-verifiable, and that the team avoids implementation-first
bias.

### V. Coverage Metrics & Quality Gates (JaCoCo)

Code coverage is a **hard quality gate** enforced by the CI pipeline:

- **Per-class coverage MUST exceed 80%** (line + branch combined) for every
  non-generated, non-framework class in the domain and use-case layers.
- **Global project coverage MUST be ≥ 80%** (line coverage) across all non-generated
  sources.
- Coverage reports MUST be generated by **JaCoCo** and published as part of every
  build. The build MUST fail if either threshold is not met.
- Generated code (openapi-generator output) MUST be excluded from coverage
  calculations via JaCoCo exclude patterns.
- Coverage thresholds are enforced in the build tool configuration
  (e.g., `jacoco:check` goal in Maven or `jacocoTestCoverageVerification` in Gradle)
  and MUST NOT be manually bypassed without a documented exception in this
  constitution.
- Coverage reports MUST be archived as build artifacts and reviewed during code
  review for new under-covered classes.

**Rationale**: Quantified coverage gates prevent coverage regression, surface
untested paths, and provide an objective signal for release readiness.

## Technology Standards

This section captures the non-negotiable technology choices that apply across all
features of citasalud-service.

- **Language & Runtime**: Java 17+ (LTS). Kotlin MAY be introduced for new modules
  with team consensus, but MUST NOT be mixed within a single module.
- **Framework**: Spring Boot 3.x. Spring dependencies MUST be confined to the
  Frameworks & Drivers layer (Principle I).
- **Build Tool**: Maven or Gradle (project-wide consistency required — do not mix).
- **API Specification**: OpenAPI 3.x (`openapi.yml`). Generation via
  `openapi-generator-maven-plugin` or `openapi-generator-gradle-plugin`.
- **Testing Stack**: JUnit 5, Mockito, AssertJ, Testcontainers (integration),
  Cucumber-JVM (functional/acceptance), JaCoCo (coverage).
- **Database Migrations**: Flyway or Liquibase. Schema changes MUST never be
  applied manually to any environment.
- **Persistence**: Spring Data JPA is acceptable in the Frameworks & Drivers layer.
  Direct use of EntityManager in use cases is PROHIBITED.
- **Secrets & Configuration**: Externalised via environment variables or a secrets
  manager. No credentials in source code or committed config files.

## Development Workflow & Quality Gates

The following workflow MUST be followed for every feature increment:

1. **Spec first**: Author or update `spec.md` with BDD acceptance scenarios before
   any code is written.
2. **API contract first**: Draft `openapi.yml` and run openapi-generator to produce
   stubs before implementing handlers.
3. **Tests red**: Write BDD-aligned unit, integration, and functional tests. Confirm
   they all fail (Red phase).
4. **Implement**: Write the minimum production code to make all tests pass (Green).
5. **Refactor**: Clean up while keeping all tests green (Refactor).
6. **Coverage gate**: Run `mvn verify` (or `gradle check`) and confirm JaCoCo
   thresholds are met. Fix gaps before opening a PR.
7. **Code review**: At least one peer reviewer MUST verify constitution compliance
   (architecture layers, SOLID, no hand-written generated code, OpenAPI contract
   present, coverage reports passing).
8. **Merge**: Only green builds with passing coverage gates may be merged to `main`.

**Constitution violations block merge.** Any exception requires explicit approval
from the tech lead and a dated entry in the Complexity Tracking table of the
feature's `plan.md`.

## Governance

This constitution supersedes all prior development guidelines, verbal agreements,
and individual preferences for citasalud-service.

**Amendment procedure**:
1. Propose the amendment in writing (PR to `.specify/memory/constitution.md`).
2. Describe the motivation, impact on existing code, and migration plan.
3. Obtain approval from at least two team members (including the tech lead).
4. Increment the version following semantic versioning rules (see below).
5. Update all dependent templates and propagate changes within the same PR.

**Versioning policy**:
- MAJOR: Removal or redefinition of a core principle.
- MINOR: New principle or section added; material expansion of existing guidance.
- PATCH: Clarifications, wording improvements, typo fixes.

**Compliance review**: Constitution compliance MUST be verified during every code
review. Violations found in merged code MUST be tracked as tech-debt tasks and
resolved within the next sprint.

**Version**: 1.0.0 | **Ratified**: 2026-07-05 | **Last Amended**: 2026-07-05
