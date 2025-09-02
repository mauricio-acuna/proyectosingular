# AI Readiness Web - MVP

> **Versión:** 1.0.0-SNAPSHOT  
> **Stack:** Spring Boot 3, Java 17, PostgreSQL, Docker  
> **Demo:** Autoevaluación de empleabilidad en la era de la IA  

## 🎯 Descripción

**AI Readiness Web** es una aplicación web que permite a profesionales **auto-evaluar su empleabilidad en la era de la IA** y obtener un **plan accionable 30/60/90 días**. 

### Características principales:
- 📋 **Cuestionario adaptativo** por rol profesional
- 📊 **Scoring por pilares**: Técnico (35%), IA aplicada (35%), Comunicación (15%), Portafolio (15%)
- 🤖 **Plan personalizado** generado por IA con recursos curados
- 📄 **Exportación a PDF** del informe completo
- 📈 **Métricas y telemetría** de uso
- 🌐 **Multiidioma** (ES/EN)
- ♿ **Accesibilidad** WCAG AA

## 🚀 Inicio rápido (< 5 minutos)

### Prerrequisitos
- Java 17+
- Docker y Docker Compose
- Maven 3.8+

### 1. Clonar el repositorio
```bash
git clone https://github.com/mauricio-acuna/proyectosingular.git
cd proyectosingular
```

### 2. Levantar la base de datos
```bash
docker-compose up postgres -d
```

### 3. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 4. Acceder a la aplicación
- **API:** http://localhost:8080/api/v1
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health
- **Métricas:** http://localhost:8080/api/v1/metrics

## 🚀 Demo de la API

### 1. Obtener roles disponibles
```bash
curl http://localhost:8080/api/v1/roles
```

### 2. Obtener preguntas para Backend Java
```bash
curl "http://localhost:8080/api/v1/roles/backend-java/questions?version=1.0"
```

### 3. Crear evaluación
```bash
curl -X POST http://localhost:8080/api/v1/assessments \
  -H "Content-Type: application/json" \
  -d '{
    "roleId": "backend-java",
    "version": "1.0", 
    "locale": "es-ES",
    "hoursPerWeek": 8,
    "consent": true,
    "answers": [
      {"questionId": "q-spring-boot", "value": 4},
      {"questionId": "q-ai-code-assist", "value": 2}
    ]
  }'
```

### 4. Generar plan 30/60/90
```bash
curl -X POST http://localhost:8080/api/v1/assessments/{assessment-id}/plan \
  -H "Content-Type: application/json" \
  -d '{"hoursPerWeek": 8}'
```

## 🏗️ Arquitectura

```
com.aireadiness/
├── assessment/     # Lógica de evaluaciones y scoring
├── catalog/        # Gestión de roles y preguntas  
├── plan/          # Generación de planes con IA
├── admin/         # Panel de administración
├── telemetry/     # Métricas y analítica
└── common/        # Configuración, seguridad, i18n
```

## 📊 Modelo de datos

Las entidades principales incluyen:
- **ROLE**: Roles profesionales (Backend Java, Atención Cliente, etc.)
- **QUESTION**: Preguntas por pilar y rol
- **ASSESSMENT**: Evaluaciones completadas
- **PLAN**: Planes 30/60/90 generados por IA

Ver [PRD completo](prd.md) para detalles del esquema.

## 🔧 Desarrollo

### Ejecutar tests
```bash
mvn test
```

### Ejecutar con Docker completo
```bash
docker-compose --profile full up
```

### Variables de entorno importantes
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/aireadiness
SPRING_DATASOURCE_USERNAME=aireadiness
SPRING_DATASOURCE_PASSWORD=aireadiness
```

## 📋 Roadmap MVP (90 días)

- [x] **S1-S2**: ✅ Catálogo, scoring, API básica, Docker, seed Backend Java
- [x] **S3-S4**: ✅ Assessment API, entidades, scoring por pilares  
- [x] **S5-S6**: ✅ Integración IA mock, plan 30/60/90, telemetría básica
- [ ] **S7-S8**: Admin, versionado, i18n, accesibilidad
- [ ] **S9-S10**: Tests, observabilidad, performance  
- [ ] **S11-S12**: Demo pública, feedback usuarios, fixes

## 🎉 Estado Actual del MVP

**✅ COMPLETADO (Semanas 1-6):**
- **Backend API completa** con Spring Boot 3 + PostgreSQL
- **Evaluaciones funcionales** con 18 preguntas para Backend Java
- **Scoring automático** por 4 pilares según fórmulas del PRD
- **Planes IA generados** (mock provider) con estructura 30/60/90
- **Telemetría integrada** para métricas M1, M2, M3 del PRD
- **Documentación OpenAPI** completa en `/swagger-ui.html`
- **Docker Compose** listo para desarrollo
- **Tests unitarios** para componentes core

**🔄 PRÓXIMOS PASOS:**
- Admin panel para gestión de roles y preguntas
- UI frontend (React/Angular) 
- Generación y descarga de PDF
- Integración con proveedor IA real (OpenAI/Claude)
- Tests de integración completos

## 🤝 Contribuir

1. Fork del proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Añade nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📄 Documentación

- [PRD completo](prd.md) - Especificación detallada del producto
- [API Documentation](http://localhost:8080/swagger-ui.html) - Endpoints REST
- [Database Schema](src/main/resources/db/migration) - Migraciones Flyway

## 🔒 Seguridad y Privacidad

- ✅ **GDPR compliance**: Consentimiento explícito y derecho al olvido
- ✅ **Datos mínimos**: Evaluación anónima por defecto
- ✅ **TLS requerido** en producción
- ✅ **Rate limiting**: 100 req/min por IP

## 📈 Métricas objetivo (MVP)

- **M1**: ≥60% de cuestionarios iniciados se completan
- **M2**: ≥50% de resultados descargan PDF  
- **M3**: ≥10 evaluaciones/rol/semana en demo

## 📞 Contacto

- **Maintainer**: Mauricio Acuna
- **Repository**: https://github.com/mauricio-acuna/proyectosingular
- **Issues**: https://github.com/mauricio-acuna/proyectosingular/issues

---

## 🎬 Demo

![Demo GIF](docs/demo.gif)

*Próximamente: video demo de 2-3 minutos*
