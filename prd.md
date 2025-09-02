# PRD — AI Readiness Web (MVP 90 días)

> **Versión:** 1.0  
> **Fecha:** 2‑Sep‑2025  
> **Owner:** (Tú)  
> **Stack objetivo:** Spring Boot 3 (Java 17), PostgreSQL, Docker, CI/CD (GitLab), UI mínima (React/Angular), Integración IA (proveedor pluggable)

---

## 1) Resumen ejecutivo
**AI Readiness Web** es una aplicación web que permite a una persona **auto‑evaluar su empleabilidad en la era de la IA** y obtener un **plan accionable 30/60/90 días**. El MVP prioriza: (1) cuestionario adaptativo por rol, (2) scoring por pilares, (3) plan generado por IA con recursos curados, (4) exportable a PDF, (5) medición de uso y feedback rápido.

**Resultados esperados en 90 días:** demo pública reproducible, 2 features de IA (plan + validación de gaps), métricas mínimas, 10 usuarios de prueba y 3 testimonios.

---

## 2) Objetivos y No‑Objetivos
### 2.1 Objetivos (MVP)
- **O1.** Guiar a un usuario a entender sus **brechas** y **prioridades** frente a IA.
- **O2.** Entregar un **plan 30/60/90** personalizado y accionable.
- **O3.** **Medir** actividad básica (nº de evaluaciones, roles, conversión a descarga PDF).
- **O4.** Proveer **admin simple** para versionar roles/preguntas/pesos.
- **O5.** Asegurar **compliance básica GDPR** (consentimiento y borrado) y **i18n ES/EN**.

### 2.2 No‑Objetivos (MVP)
- Marketplace de cursos, pasarela de pago real, recomendaciones algorítmicas complejas, autenticación social, coaching 1‑a‑1, multicliente enterprise completo. (Podrán considerarse en siguientes fases.)

---

## 3) Usuarios, personas y casos de uso
### 3.1 Personas
- **P1: Profesional Técnico (Backend Java)** — 25–45 años, quiere saber qué aprender y cómo priorizar.
- **P2: Profesional Soporte/Operativo** — 25–50, teme “quedarse fuera” por IA, busca plan concreto.
- **P3: Estudiante/Junior** — 18–30, necesita ruta clara de empleabilidad con IA.

### 3.2 Casos de uso (alto nivel)
1. **Autoevaluación por rol** → resultado por pilares → **plan 30/60/90**.
2. **Repetir evaluación** y comparar progreso (MVP: snapshot manual por ID/URL).
3. **Admin**: alta/edición de roles, preguntas, pesos, recursos, versiones.

---

## 4) Problema, hipótesis y métricas
- **Problema:** la gente no sabe **qué** aprender **antes** que la IA los desplace.
- **Hipótesis H1:** un plan 30/60/90 personalizado aumenta acción (≥50% descarga PDF).
- **Hipótesis H2:** presentar **pocas prioridades** (máx. 5) reduce parálisis (≥60% finaliza cuestionario).
- **Métricas de producto (MVP):**
  - M1: % de cuestionarios iniciados que se completan (objetivo ≥60%).
  - M2: % de resultados con descarga PDF (≥50%).
  - M3: # evaluaciones/rol/semana (≥10 en demo).

---

## 5) Alcance funcional (MoSCoW)
### Debe tener (Must)
- **FR‑001 Registro opcional y consentimiento** (email opcional + consentimiento GDPR, o anónimo).
- **FR‑002 Landing + selección de rol** (lista inicial de 3 roles; ver §7.4).
- **FR‑003 Cuestionario** con preguntas por rol (Likert 1–5, multiopción, texto breve opcional).
- **FR‑004 Scoring por pilares** (Técnico, IA aplicada, Comunicación, Portafolio).
- **FR‑005 Informe de resultados** con brechas y prioridades (máx. 5).
- **FR‑006 Plan 30/60/90 generado por IA** a partir de gaps + validaciones.
- **FR‑007 Exportación a PDF** del informe y plan.
- **FR‑008 Admin simple**: CRUD roles/preguntas/pesos/versiones/recursos.
- **FR‑009 Telemetría básica** (inicios, finalizaciones, descargas).
- **FR‑010 i18n ES/EN** (UI + contenido, detectado por header/selector).
- **FR‑011 Accesibilidad WCAG AA básica** (teclado, contraste, labels).

### Debería tener (Should)
- **FR‑012 Comparar evaluación actual vs. previa** (por ID compartido).
- **FR‑013 Guardado automático local** (LocalStorage) en el cuestionario.

### Podría tener (Could)
- **FR‑014 Envío por email del PDF** (con doble opt‑in).
- **FR‑015 Multi‑tenant básico** (campo `tenant_id`) para separar entornos.

### No tendrá (Won’t) en MVP
- Pasarela de pago real, SSO corporativo, dashboards avanzados, recomendador ML propio.

**Criterios de aceptación**: ver §9 por requisito.

---

## 6) Flujos de usuario (MVP)
1) **Landing → Elegir rol → Cuestionario → Resultado → Plan → Descargar PDF**  
2) **Admin → Login (básico) → Roles → Preguntas → Versionado → Publicar**  

### 6.1 Wire de alto nivel
- **Landing:** hero breve, selección de rol (cards). CTA: “Comenzar”.
- **Cuestionario:** 20–30 preguntas/rol, agrupadas por pilar, progreso (%) y guardar borrador local.
- **Resultado:** gráfico simple (barras por pilar), top 5 gaps, recomendaciones.
- **Plan 30/60/90:** 3 columnas con objetivos, tareas, entregables y recursos.
- **Admin:** tabla de roles, editor de preguntas (peso, pilar, tipo), versiones y botón “Publicar vX”.

---

## 7) Contenido de evaluación
### 7.1 Pilares y pesos (MVP)
- **P1 Técnico (35%)**
- **P2 IA aplicada (35%)**
- **P3 Comunicación (15%)**
- **P4 Portafolio/Entrega (15%)**

### 7.2 Tipos de preguntas
- **Likert 1–5** (auto‑percepción medida).
- **Opción múltiple** (con claves correctas opcionales para técnico).
- **Texto breve** (detalles que enriquecen el plan, no puntúa directo).

### 7.3 Cálculo de scoring
- **Score de pregunta**: Likert normalizado `x/5`; multiopción `aciertos/total`.
- **Score de sub‑pilar**: promedio ponderado de sus preguntas.
- **Score de pilar**: promedio de sub‑pilares.
- **Score global**: `Σ (peso_pilar × score_pilar)` → 0..100.
- **Brechas**: pilares con score < 70 y preguntas < 3/5.

### 7.4 Roles iniciales (seed)
- **R1 Backend Java (Spring)** — 24–30 preguntas.
- **R2 Atención al Cliente** — 18–24 preguntas.
- **R3 Administrativo/Operaciones** — 18–24 preguntas.

> **Nota:** El admin puede versionar y ajustar pesos sin redeploy (ver §10 Admin y §13 Modelo de datos).

---

## 8) Plan 30/60/90 (IA)
### 8.1 Reglas de negocio
- **Máx. 5 prioridades** totales.
- Cada prioridad debe mapearse a **objetivos medibles**, **tareas**, **evidencias** y **recursos** (EN/ES).
- El plan debe caber en **6–8 h/semana** (configurable) y equilibrar teoría/práctica 40/60.

### 8.2 Formato de salida (JSON contrato)
```json
{
  "summary": "…",
  "time_budget_hours_per_week": 8,
  "priorities": [
    {
      "name": "Spring Security básico",
      "why": "Gap en autenticación y autorización",
      "milestones": {
        "d30": [{"task":"…","deliverable":"…","resource":"URL"}],
        "d60": [{"task":"…","deliverable":"…","resource":"URL"}],
        "d90": [{"task":"…","deliverable":"…","resource":"URL"}]
      },
      "evidence_of_done": ["Repo con módulo auth","Prueba con Testcontainers"]
    }
  ]
}
```

### 8.3 Prompt base (plantilla)
```
Objetivo: Generar un plan 30/60/90 días para mejorar empleabilidad.
Entradas:
- Rol: {{role}}
- Puntuaciones por pilar: {{scores_json}}
- Gaps (preguntas <3/5): {{gaps_list}}
- Tiempo disponible (h/semana): {{hours_per_week}}
Instrucciones:
- Devuelve SOLO JSON válido con el esquema de §8.2.
- Máximo 5 prioridades.
- Cada prioridad debe incluir tareas con entregables verificables y recursos (EN/ES).
- Sé específico. Evita generalidades. No inventes URLs.
Guardarrails:
- Si falta información, asume valores por defecto razonables y decláralos en "summary".
```

### 8.4 Validación del plan
- Validar JSON contra esquema.
- Verificar que cada URL tenga formato válido (no se exige disponibilidad en MVP).
- Limitar total de tareas a 20 (legible en PDF).

---

## 9) Requisitos funcionales (detallados + aceptación)
**FR‑001 Registro opcional y consentimiento**  
- **Descripción:** El usuario puede usar la app anónimamente. Si ingresa email, acepta almacenamiento para enviar PDF/opciones.
- **Aceptación:** Checkbox de consentimiento visible, texto GDPR resumido, endpoint de borrado por token.

**FR‑002 Selección de rol**  
- **Descripción:** Lista roles activos (versión publicada).
- **Aceptación:** GET `/v1/roles` devuelve ≥3 roles con `id`, `name`, `version`.

**FR‑003 Cuestionario**  
- **Descripción:** Descarga preguntas por rol y versión; soporta Likert, multiopción y texto.
- **Aceptación:** GET `/v1/roles/{id}/questions?version=X` → 200 con ~20–30 preguntas.

**FR‑004 Scoring**  
- **Descripción:** Calcular score por pilar y global conforme §7.3.
- **Aceptación:** POST `/v1/assessments` devuelve `scores` 0..100 y `gaps`.

**FR‑005 Resultado**  
- **Descripción:** UI muestra barras por pilar, top 5 gaps y recomendaciones breves.
- **Aceptación:** Visual accesible, exportable a PDF.

**FR‑006 Plan IA**  
- **Descripción:** Generar plan JSON según §8 con proveedor IA (o mock).
- **Aceptación:** Validación de esquema ok; plan max 5 prioridades.

**FR‑007 PDF**  
- **Descripción:** Un único PDF con resumen, scores, prioridades y 30/60/90.
- **Aceptación:** Descarga <3 s en local; tamaño <2 MB.

**FR‑008 Admin**  
- **Descripción:** CRUD roles, preguntas, pesos, recursos, versiones y publicar.
- **Aceptación:** Auth básica (user/pass), audit de cambios, botón “Publicar vX”.

**FR‑009 Telemetría**  
- **Descripción:** Contar inicios, finalizaciones y descargas con timestamp y rol.
- **Aceptación:** Métricas en Actuator/endpoint dedicado.

**FR‑010 i18n**  
- **Descripción:** UI y contenido en ES/EN.
- **Aceptación:** Selector o `Accept-Language` detectado; persistencia de preferencia.

**FR‑012 Comparación evaluaciones**  
- **Descripción:** Mostrar comparativa si el usuario conserva el ID/URL del resultado previo.
- **Aceptación:** Endpoint acepta `prev_assessment_id` y devuelve dif.

**FR‑013 Guardado local**  
- **Descripción:** Auto‑save en LocalStorage cada 5 s.
- **Aceptación:** Al recargar, se restaura el progreso.

**FR‑014 Email PDF (opt‑in)**  
- **Descripción:** Enviar PDF al email si el usuario lo solicita.
- **Aceptación:** Doble confirmación y opción “olvido” por token.

**FR‑015 Multi‑tenant (básico)**  
- **Descripción:** Campo `tenant_id` en evaluaciones/telemetría.
- **Aceptación:** Filtro por tenant en admin.

---

## 10) Requisitos no funcionales
- **NFR‑001 Rendimiento:** TTFB < 600 ms en API locales; cálculo de scoring < 150 ms.
- **NFR‑002 Disponibilidad:** MVP single‑node; reinicio < 2 min.
- **NFR‑003 Seguridad:** TLS requerido en demo pública; JWT para admin; rate‑limit 100 req/min/IP.
- **NFR‑004 Privacidad (GDPR):** Consentimiento explícito, minimización de datos, derecho al olvido (token).
- **NFR‑005 Observabilidad:** Actuator, logs estructurados, trazas básicas, métricas (counter/gauge).
- **NFR‑006 Accesibilidad:** Navegación teclado, labels ARIA, contraste AA.
- **NFR‑007 i18n:** fallback en EN si falta cadena.
- **NFR‑008 Mantenibilidad:** cobertura tests ≥70% en módulos core; Sonar/SpotBugs en CI.
- **NFR‑009 Portabilidad:** Docker Compose; levantar en <5 min.

---

## 11) Arquitectura y componentes
- **Backend API** (Spring Boot 3, Java 17): módulos `assessment`, `catalog` (roles/preguntas), `plan` (IA), `admin`, `telemetry`.
- **Base de datos**: PostgreSQL 14+.
- **Proveedor IA**: interfaz `PlanGenerator` con implementación `LLMProvider` y `MockProvider`.
- **UI**: SPA mínima (React/Angular) con 4 vistas: Landing, Cuestionario, Resultado, Admin.
- **Infra**: Docker Compose (api, db), Health checks, OpenAPI 3.

### 11.1 Paquetes sugeridos
```
com.aireadiness
 ├── assessment
 ├── catalog
 ├── plan
 ├── admin
 ├── telemetry
 └── common (i18n, security, config)
```

### 11.2 Actuator & seguridad
- Endpoints `/actuator/health`, `/actuator/metrics`.
- Admin protegido con JWT (login simple + expiración 24h).

---

## 12) Contratos de API (v1)
**Base:** `/api/v1`

### 12.1 Roles
- `GET /roles` → `200` `[ {"id":"backend-java","name":"Backend Java","version":"1.0"} ]`
- `GET /roles/{id}/questions?version=1.0` → lista de preguntas.

**Pregunta (ejemplo):**
```json
{
  "id":"q-spring-security",
  "text":"¿Dominas autenticación y autorización en Spring Security?",
  "type":"likert",
  "pillar":"TECH",
  "weight":1.0,
  "help":"Piensa en JWT, filtros, method security"
}
```

### 12.2 Evaluaciones
- `POST /assessments`
```json
{
  "roleId":"backend-java",
  "version":"1.0",
  "answers":[{"questionId":"q-spring-security","value":3}],
  "locale":"es-ES",
  "hoursPerWeek":8,
  "email": null,
  "consent": true,
  "prevAssessmentId": null
}
```
**Respuesta 201:**
```json
{
  "assessmentId":"a_01H...",
  "scores":{"TECH":62,"AI":40,"COMMS":70,"PORTFOLIO":50,"GLOBAL":53},
  "gaps":["q-spring-security", "q-ci-cd"],
  "plan": { /* opcional si se genera sincrónicamente */ },
  "pdfUrl": null
}
```

### 12.3 Plan
- `POST /assessments/{id}/plan`  
**Req:** `{ "hoursPerWeek": 8 }`  
**Resp:** JSON del §8.2.

### 12.4 PDF
- `POST /assessments/{id}/pdf` → `201` `{ "pdfUrl":"/download/a_01H...pdf" }`

### 12.5 Admin
- `POST /admin/roles` (JWT)
- `PUT /admin/roles/{id}/publish` → establece `version` activa.
- `POST /admin/resources` (EN/ES, por pilar y rol)

### 12.6 Errores
- `400` VALIDATION_ERROR (detalles por campo)
- `401/403` AUTH_ERROR
- `404` NOT_FOUND
- `429` RATE_LIMIT
- `500` INTERNAL

---

## 13) Modelo de datos (SQL lógico)
```
TENANT(id)
USER(id, email_hash, created_at)
ROLE(id, name, description)
ROLE_VERSION(role_id, version, is_published, created_at)
QUESTION(id, text_es, text_en, type, pillar)
ROLE_QUESTION(role_id, version, question_id, weight, order)
ASSESSMENT(id, role_id, version, tenant_id, user_id, locale, hours_per_week, created_at)
ANSWER(assessment_id, question_id, value_numeric, value_text)
PLAN(assessment_id, plan_json, created_at)
RESOURCE(id, role_id, pillar, locale, title, url)
TELEMETRY(event_id, assessment_id, event_type, created_at)
AUDIT_LOG(id, actor, action, entity, before_json, after_json, created_at)
```
**Índices:** por `role_id+version`, `assessment_id`, `event_type`.

**PII:** sólo email (opcional) con hash + token para borrado.

---

## 14) Internacionalización (i18n)
- UI y preguntas con claves; fallback EN si falta ES/EN.
- Recursos por `locale`.
- Detección por `Accept-Language` + selector manual.

---

## 15) Seguridad, privacidad y cumplimiento (GDPR)
- **Consentimiento explícito** para guardar email/telemetría.
- **Minimización:** anónimo por defecto; no guardar texto libre sensible.
- **Derecho al olvido:** endpoint `DELETE /privacy/forget?token=...`.
- **Transparencia:** página `/privacy` con descripción de datos y retención (90 días demo).
- **Seguridad:** TLS, JWT admin, rate‑limit, logs sin PII.

---

## 16) Observabilidad y analítica
- **Actuator**: health, metrics.
- **Métricas propias:** `assessments_started`, `assessments_completed`, `pdf_downloads` por rol/versión.
- **Logs estructurados** (JSON) con `assessment_id` (no email).

---

## 17) Generación de PDF
- Motor HTML→PDF (OpenPDF/Flying Saucer/Libre) con plantilla.
- Debe soportar ES/EN y tablas 30/60/90.

---

## 18) UX / Accesibilidad
- Componentes de formulario con labels y errores claros.
- Teclado navegable, foco visible, contraste AA.
- Feedback en tiempo real de progreso (%).
- Textos concisos, evitamos jerga técnica si el rol no lo requiere.

---

## 19) CI/CD y dev‑ops
- **CI:** build + test + lint + cobertura + SCA (OWASP) + image build + push.
- **CD (demo):** deploy a contenedor único (Railway/Render/Dokku/VM).
- **Versionado:** SemVer en API y `ROLE_VERSION`.
- **Infra local:** `docker compose up` levanta API+DB en <5 min.

---

## 20) Calidad y pruebas
- **Unitarias:** cálculo de scoring, validación de plan JSON.
- **Integración:** repos y controladores con Testcontainers (Postgres).
- **Contrato:** validar ejemplos de API con OpenAPI.
- **E2E básico:** flujo completo (headless) con dataset semilla.
- **Cobertura meta:** ≥70% en `assessment` y `plan`.

### 20.1 Casos de prueba clave
- Puntuaciones límite (todo 1 vs. todo 5).
- Preguntas faltantes → VALIDATION_ERROR.
- Plan con >5 prioridades → rechazo.
- URL inválidas en plan → limpieza/normalización.

---

## 21) Roadmap y hitos (90 días)
- **S1 (Semanas 1–2):** Catálogo (roles/preguntas), scoring, API básica, Docker, OpenAPI, seed de **R1 Backend Java**.
- **S2 (Semanas 3–4):** UI mínima, resultados y PDF, telemetría básica.
- **S3 (Semanas 5–6):** Integración IA (plan 30/60/90), validación/guardrails, recursos curados.
- **S4 (Semanas 7–8):** Admin + versionado, i18n, accesibilidad AA.
- **S5 (Semanas 9–10):** Dureza: tests, observabilidad, performance.
- **S6 (Semanas 11–12):** Demo pública, video 2–3 min, feedback con 10 usuarios, fixes.

**Entregables:** repo público, README ES/EN “levántalo en 5 min”, GIF demo, PDF de ejemplo, 3 testimonios.

---

## 22) Riesgos y mitigaciones
- **Contenido pobre o sesgado** → Admin versionable + revisión semanal de preguntas.
- **Dependencia IA** → `MockProvider` y degradación: plan “estándar” por thresholds.
- **Tiempo limitado** → alcance cerrado MoSCoW + métricas de foco (4/5).
- **Privacidad** → anónimo por defecto, sin PII en logs.

---

## 23) Definición de Hecho (DoD)
- API v1 documentada (OpenAPI), tests verdes, cobertura ≥70% core.
- Docker Compose funciona en máquina limpia.
- UI fluye de landing a PDF sin errores.
- Admin publica una versión de rol.
- Página de privacidad y consentimiento operativos.

---

## 24) Preguntas abiertas
- ¿Lista de recursos curados inicial (ES/EN) por rol/pilar?
- ¿Qué proveedor IA se usará en demo pública (coste/LLM)?
- ¿Se habilita email PDF en MVP o en v1.1?

---

## 25) Glosario
- **Pilar:** categoría de habilidades para scoring.
- **Versión de rol:** snapshot de preguntas/pesos publicado.
- **Plan 30/60/90:** hitos y tareas por mes con evidencias.

---

## 26) Anexos
### 26.1 Ejemplo de `ROLE_QUESTION` (seed)
```json
{
  "roleId":"backend-java",
  "version":"1.0",
  "questions":[
    {"id":"q-spring-security","type":"likert","pillar":"TECH","weight":1.0},
    {"id":"q-ci-cd","type":"likert","pillar":"PORTFOLIO","weight":0.8},
    {"id":"q-ai-use","type":"likert","pillar":"AI","weight":1.2},
    {"id":"q-communication","type":"likert","pillar":"COMMS","weight":1.0}
  ]
}
```

### 26.2 Esquema OpenAPI (extracto)
```yaml
openapi: 3.0.3
info:
  title: AI Readiness API
  version: 1.0.0
paths:
  /api/v1/roles:
    get:
      responses:
        '200': { description: OK }
```

---

> **Nota final:** Este PRD busca eliminar ambigüedades. Cualquier cambio de alcance se hará vía PR en el repo, actualizando este documento y la versión de rol correspondiente.

