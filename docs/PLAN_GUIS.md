# Plan de reorganización de las GUIs de las máquinas

Decidido el 2026-10-12 con el usuario, sobre el inventario de `docs/GUIS_MAQUINAS.md`.

## Decisiones cerradas
1. **Sector contextual a la DERECHA** (columna fija donde hoy viven los controles de Tintes).
2. **Arranque:** la acción corre **con el botón Y también al cerrar la GUI**, igual en las 5 (Tintes y la Estilista hoy no arrancan al cerrar: se igualan a Modelar y Prensar).
3. **Diseños:** todas pasan a "★ Último + con nombre" (como la Estilista).
4. **Tamaño de ventana estándar** para las 4 grandes (el de Tintes y Sublimadora, 560 × 408; la Modeladora crece).

## Esqueleto común (todas las máquinas)
| Zona | Contenido | Se ve |
|---|---|---|
| A. Visor | Visor 3D, Vista, zoom | siempre |
| B. Cabecera fija | Entrada → Acción → Salida, barra de progreso, Línea, estado | siempre |
| C. Controles contextuales | Solo los controles del ajuste en edición (título "Editando: ___") | solo con selección |
| D. Cajones | Almacén, Diseños, insumos | plegables |

Reglas de C: sin selección, un texto guía de una línea; un solo contexto a la vez; siempre en el mismo lugar; mismos componentes (paso `<` `>`, slider, interruptor, selector cíclico).

## Qué muestra C en cada máquina
- **Tintes:** pestañas Color / Patrón / Mezcla / Capas; Espejo, Alternancia y Simetría solo si el patrón los usa.
- **Sublimadora:** Escala, Posición X/Y, Ángulo, Imagen/Máscara al elegir una cara o capa; Simetría en prendas de a pares.
- **Modeladora:** al elegir un pin con molde, Fijar/desfijar, cajón, lado y opciones del molde.
- **Estilista:** las pestañas Aplique / Correa / Acabado y, con un aplique elegido, Colocación y Oscilación.
- **Telar:** sin C (sus controles van en la cabecera).

## Fases
0. **Limpieza sin cambio visual.**
   - Hecho: rótulos de los canales de Tintes traducidos (`modamod.tinturas.canal.*`).
   - Se reparte con cada fase y no en bloque (están enredados con el guardado y los handlers): rangos de ids de botón por máquina, y el código muerto de la Modeladora (botones ocultos, slot Activo) y de la Estilista (Fijar).
   - Pendiente de la fase 0: capturas de referencia (las saca el usuario en el juego).
1. **Esqueleto común:** clase base de pantalla con la cabecera fija (Acción, Salida, Línea, barra de progreso) y la regla de arranque única.
2. **Componente del sector contextual** (panel con selección, título y filas) y primera migración: **Sublimadora**.
3. **Tintes:** controles de capa a las 4 pestañas.
4. **Modeladora:** sector C por pin, ventana a tamaño estándar.
5. **Estilista:** paneles Máquina y Diseños adentro de la ventana; pestañas en el sector C.
6. **Telar:** adopta la cabecera común.
7. **Diseños unificados** en las 4 máquinas.

## Riesgos
- Los fondos de las pantallas están horneados por `tools/generar_textura_*.py`: regenerar al mover zonas.
- Mover botones cambia ids de red: cada fase renumera los de su máquina de una vez.
- Cambiar el arranque de Tintes y la Estilista cambia el comportamiento de la línea de producción: probarlo con cinta.
