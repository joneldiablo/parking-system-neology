---
workflow: general-video
flow: companion
storyboard: yes
message: "Un sistema de control de acceso vehicular que funciona de punta a punta: kiosko QR, panel admin y seguridad por roles"
destination: youtube
aspect: 1920x1080
language: es
length: 3m
angle: recorrido-funcional
---

## Intent

Recorrido funcional completo (show-it-as-is) del sistema de gestión de
estacionamiento construido como prueba técnica full-stack para Neology. El
espectador objetivo es quien evalúa o presenta el proyecto (reclutador,
entrevistador, público técnico). La idea es **mostrar el sistema funcionando de
verdad**, no una maqueta: kiosko QR registrando entrada/salida, panel de
administración dando de alta vehículos, cobrando salidas y mostrando pagos, y el
login con roles (superadmin vs admin).

Tono: developer cercano y seguro, claro y sin relleno. Feel moderno,
tecnológico y limpio. Narración en off por **una sola voz** (identidad
"developer final"); la voz "diablito" queda **apagada**. Música instrumental
tecno/corporativa con SFX de interacción UI (clicks, whoosh, confirmaciones).

## Assets

- public/demo-recording.* — grabación de pantalla del flujo completo (kiosko +
  admin) contra la app en vivo. Es el fondo sobre el que va la explicación.
  Pendiente de captura.

## Customizations

- Narración en off en español por una voz única (developer final).
- Música de fondo tecno/corporativa instrumental + SFX de UI.
- Overlays de explicación encima de la grabación: etiquetas de paso / lower-thirds
  que marcan qué se está viendo, y títulos de sección.
- Apertura y cierre diseñados (no por defecto): intro con identidad del proyecto
  y cierre con stack técnico y CTA.

## Notes

- El video NO debe exponer secretos ni credenciales reales en pantalla; usar
  credenciales de demostración.
- La base visual es nuestra propia grabación, NO una captura automática del sitio
  (no-capture).
- Aplicación: Parking System (Angular + Spring Boot + PostgreSQL), con auth JWT,
  superadmin desde `.env`, servicios de kiosko QR abiertos y SPA servida desde el
  mismo JAR.
- Repo: joneldiablo/parking-system-neology · subproyecto en `video/demo-parking/`.
