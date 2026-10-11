# ModaMod — GUIs de las 5 máquinas y pestaña creativa

Inventario de lo que hay hoy en pantalla (2026-10-11), pensado para reordenar. Las coordenadas son locales a la ventana (x, y desde la esquina de arriba a la izquierda), en px de GUI. Salió de leer el código, no de verlo en el juego.

Resumen rápido:

| Máquina | Ventana | Tema | Qué hace |
|---|---|---|---|
| Modeladora | 560 × 334 | Cobre | Corta la prenda con moldes (pines) |
| Estación de Tintes | 560 × 408 | Verdín | Color y patrones por zona |
| Sublimadora | 560 × 408 | Oro | Fotos y máscaras sobre la prenda |
| Estilista automática | 384 × 256 (+ paneles afuera, ~658 en total) | Lila | Apliques, correas y acabados |
| Telar automático | 176 × 226 | Granate | Teje prendas básicas con lana e hilo |

Todas, menos el Telar, comparten la misma grilla de 3 columnas: izquierda (visor 3D y controles), medio (esquema, entrada y salida, controles) y derecha (almacén y paneles).

---

## 1. Modeladora (560 × 334)

**Columnas:** izquierda x 0..100, medio x 119..359 (240 de ancho), derecha x 379..541.

### Zonas
- **Izquierda:**
  - Visor 3D de 86 × 196 (y 18..214). Arrastrar gira, la rueda hace zoom.
  - Debajo, apilados cada ~18 px: Vista (y 218), campo de nombre (y 238), Guardar diseño (y 256), Línea (y 274) y Simetría (y 292). Son 5 controles de 86 px.
  - De y 308 a 334 no hay nada.
- **Medio, arriba:**
  - Botón Categoría (119, 20, 240 × 14), que cicla las 10 categorías.
  - Esquema ilustrado (240 × 136, y 36..172) con los pines (slots de molde) y una chincheta por pin encima.
- **Medio, fila entrada y salida (y 184):**
  - Slot Prenda (167, 184).
  - Botón Modelar (215, 184, 48 × 16), sobre la flecha.
  - Slot Salida (295, 184).
- **Medio, y 214:** 8 casilleros de diseño de 18 × 14 (clic izquierdo carga, derecho borra).
- **Medio, abajo:** inventario del jugador, desde y 246.
- **Derecha:**
  - Almacén compartido de 27 slots (9 × 3, y 18..72). Guarda moldes que sirven en 2 o más categorías.
  - Banco "Moldes de <categoría>" de 36 slots (9 × 4, y 90..162).
  - Desde y 162 hasta el final: ~170 px vacíos.

### Pines por categoría (hasta 14 lógicos por categoría)
| Categoría | Pines usados |
|---|---|
| Remera / Top | Cuello (+ cajón Solapa), Trama, Manga izq. y der. (+ cajón Ruedo del puño), Faldón, Calce, Torso (+ cajón Ruedo del torso), Frente, Capucha |
| Pantalón | Tiro, Trama, Calce, Botamanga izq. y der. (+ cajón Ruedo) |
| Medias y Calientabrazos | Corte sup. e inf. por lado, Calce, Trama por lado, Ruedo sup. e inf. por lado |
| Pollera | Forma, Largo (+ cajón Cintura), Calce, Trama, Volado inferior (+ cajón Ruedo), Volado total |
| Capa | Largo, Ruedo, Capucha, Cuello, Trama |
| Sombrero | Ala, Punta |
| Banda | Zona, Ancho, Herraje |
| Calzado | Caña, Suela y Botamanga, izquierdo y derecho |
| Delantal | Forma, Largo (+ cajón Inicio), Calce, Trama, Tirantes, Volado (+ cajón Ruedo), Esquinas, Volado de costados, Volado de arriba |

Los **cajones** son slots secundarios que se abren con un botón ▾ de 9 × 9 al lado del pin principal. Se abren solos si ya tienen molde.

### Otros controles
- **Ocultos:** hay un slot "Activo" que ya no se usa. Tampoco se ven los botones Fijar, Anclaje, Lado ni Encender: quedan solo en el código.
- **Tijeras:** van en un casillero invisible. Se cargan con shift-click o a mano al bloque, y la barra verde de durabilidad está en el modelo 3D, no en la GUI.
- **Sin indicadores:** no hay barras, tanques ni contadores. El texto de estado (`hint()`) existe pero no se dibuja. La máquina se enciende sola al cerrar la GUI.

### Para ordenar
- La columna izquierda tiene 5 controles pegados.
- La derecha es solo almacenamiento y deja 170 px vacíos.
- Entre la fila de entrada y salida y la fila de diseños queda un hueco (y 198..214), ex Anclaje y Lado.
- En Remera los pines de Ruedo, Puño, Solapa y Capucha compiten por lugar en el esquema (de ahí los cajones).

---

## 2. Estación de Tintes (560 × 408)

### Zonas
- **Izquierda:**
  - Visor 3D (y 18..214).
  - Vista, nombre, Guardar diseño y Línea.
  - Almacén de patrones de 30 slots (5 × 6, y 280..388).
- **Medio, arriba:** Categoría (8 categorías: Remera, Pantalón, Medias, Calientabrazos, Pollera, Capa, Aplique/retazo, Delantal). Debajo, el esquema con un slot de patrón y una chincheta por cuadradito. Cantidad de cuadraditos visibles:

| Categoría | Cuadraditos |
|---|---|
| Remera | 8 |
| Pantalón | 7 |
| Medias | 11 |
| Calientabrazos | 11 |
| Pollera | 3 |
| Capa | 3 |
| Aplique (retazo) | 3 |
| Delantal | 3 |

- **Medio, fila entrada y salida (y 184):** slot Entrada, botón Teñir (215, 184) y slot Salida. Cargar la entrada **no** arranca: solo arranca el botón Teñir.
- **Medio, y 214:** 8 casilleros de diseño.
- **Medio, controles de la capa seleccionada (y 232..308):** 4 filas, unos 24 botones y sliders en 237 px.
  - Fila 1: Modo de mezcla, Opacidad (`<` `%` `>`), ▼ ▲ y "Capa n/m".
  - Fila 2: Tamaño, Ángulo y Posición.
  - Fila 3: Forma o Repetición, Azar e Invertir.
  - Fila 4: Giro del motivo y sliders de distancia ↔ y ↕.
  - Texto "Editando…" en (119, 296), que se pisa con la fila 4.
- **Medio, abajo:** inventario, con Espejo, Alternancia y Simetría colgados a su derecha (x 285, y 324..380).
- **Derecha:**
  - 5 sliders C, M, Y, K y Transparencia (y 18..104), que **son también los tanques de tinta** ("n/64").
  - 3 muestras de color de la capa (y 108).
  - Muestra grande (y 132..180) y texto de estado (y 190).
  - Envasar y Usar muestra (y 204).
  - Colores y Contorno (y 226).
  - Variación (y 246).
  - Panel de Capas dibujado a mano (y 268..399): hasta 12 filas con ojo, muestra, región y patrón, ▲ y ▼.

### Para ordenar
- Las filas 1 y 2 de controles son muy densas, con botones de 14 px.
- Espejo, Alternancia y Simetría están lejos de lo que modifican (los controles de distribución).
- La columna derecha apila unos 14 elementos de arriba a abajo y el texto de estado queda apretado.
- Los rótulos de los sliders (Cyan, Magenta, etc.) no pasan por traducción.
- Los tanques de tinta no tienen slot en esta GUI: se ven solo como barras.

---

## 3. Sublimadora (560 × 408)

### Zonas
- **Izquierda:** visor 3D, Vista, nombre, Guardar diseño, **Escanear estampa** y Línea. Son 6 controles apilados.
- **Medio, arriba:** Categoría. Debajo, la prenda real dibujada a 4 veces, con los rótulos Frente y Espalda y un slot de foto sobre cada cara (con su chincheta).
- **Medio, fila entrada y salida (y 184):** slot Entrada, botón Prensar y slot Salida.
- **Medio, y 214:** 8 casilleros de diseño.
- **Medio, controles de la foto o máscara (y 232..288):**
  - Escala, Posición X, Posición Y y Ángulo (cada uno con `<` valor `>`).
  - Cara Frente/Espalda y Simetría (esta solo aparece en prendas de a pares).
  - Texto "Editando…" en y 296.
- **Derecha:**
  - Cinco barras de tanques: C, M, Y, K y papel.
  - Texto guía (y 150).
  - Almacén de fotos de 27 slots (9 × 3, y 200).
  - Capas (hasta 12 botones, "1F", "2E"…), ▲ ▼ ✕ y Editar.
  - Slot de Máscara (y 334).

### Para ordenar
- Es la GUI más separada: la izquierda son solo botones y la derecha mezcla almacén, capas y máscara.
- El texto guía de 4 líneas queda pegado al almacén.
- El slot de Máscara está al fondo, lejos de las capas que usa.
- La izquierda tiene 6 controles pegados.

---

## 4. Estilista automática (384 × 256 + paneles afuera)

Hereda la pantalla de la Mesa de estilado (ya jubilada) como editor interno. Los dos paneles de la máquina están **fuera de la ventana de 384**, así que el conjunto mide ~658 de ancho (más 170 si es la creativa).

### Editor heredado de la Mesa (x 0..384)
- **Visor 3D (x 8..204, y 18..250):** muestra la prenda de la entrada como muestra. Tiene ⟲ ⟳ y Sacudir (x 312..376), una franja de arriba para objeto y espejo, y una línea de ayuda abajo.
- **Fila de slots (y 30, cada 26 px):** Prenda (oculta en la Estilista), Molde, Retazo, Objeto (solo creativa) y Material.
- **3 pestañas (y 147):**
  - **Aplique:** 12 botones numerados, Giro, Tamaño, Quitar y el slider Blandura.
  - **Correa:** lista de 8 correas, Modo, Ancho, Quitar/Cancelar, Recolorear y Largo.
  - **Acabado:** botón Textura/Acabado. Para sombrero, banda y borcegos: 3 zonas con 3 colores y un patrón cada una, y "Retazo entero". Para los borcegos, también Espejo y Pie izq./der.
- **Inventario** desde y 172.
- **Panel lateral creativo** (160 px, solo Mesa creativa o Estilista creativa): Colocar, Mover y Moldes.

### Panel Máquina (x 390..520, y 18..236)
- Entrada y Salida con Aplicar ▶ en el medio.
- Borrar y Línea.
- Texto de info (diseño, o lo que falta).
- Contadores de Hilo y Cuero (se cargan soltando el ítem encima).
- Almacén de 18 slots (6 × 3) para plantillas, retazos, muestras y objetos.

### Panel Diseños (x 526..654)
- Cabecera con el tipo de prenda.
- Fila "★ Último" (automática, es el que se aplica).
- Hasta 8 diseños con nombre: clic carga y ✕ borra.
- Campo de nombre y botón Guardar.

### Para ordenar
- Son 3 bloques anchos (editor, máquina y diseños) más el panel creativo opcional.
- El slider Blandura se pisa con las pestañas en y 150..160.
- La columna del editor (162 px) concentra slots, pestañas y botones de las 3 pestañas.
- Fijar existe en el código (id 601) pero no tiene botón.
- Los botones ⟲ ⟳ y Sacudir comparten zona con Quitar y Tamaño.

---

## 5. Telar automático (176 × 226)

Es la única con la ventana chica, dibujada por código y sin textura de fondo.

- **Izquierda:** slots Lana (22, 34) e Hilo (22, 62).
- **Centro (x 52..134):** 6 botones de prenda en 2 columnas (Remera, Pantalón, Medias, Calientabrazos, Pollera, Capa). Se tildan varias y se tejen de a una, en orden. Cada tooltip muestra lo que gasta.
- **Derecha:** slot Salida (138, 48).
- **Arriba a la derecha:** botón Línea (112, 6).
- **Fila del lote (y 86):** −10, −, +, +10 y reiniciar ↻.
- **Barra de progreso (y 106).**
- **Texto (y 116..136):** lote (sin límite o "n, faltan m"), la prenda que sigue con su costo, y "Listo" o "Falta".
- **Inventario** desde y 146.

---

## 6. Qué comparten y qué difiere

- **Línea (BTN 900)** está en las 5, pero cada una la pone en un lugar distinto: Modeladora y Tintes en la columna izquierda, Sublimadora al final de la izquierda, Estilista en el panel Máquina y Telar arriba a la derecha. También tiene un botón físico en el frente de cada máquina.
- **Vista / nombre / Guardar diseño / 8 casilleros** se repiten en Modeladora, Tintes y Sublimadora, con ids de botón distintos.
- **Categoría** (botón de 240 × 14 que cicla) aparece en las 3 de la grilla grande, con 10, 8 y N categorías.
- **Entrada y salida** a 1.5 veces están en las mismas coordenadas (167 y 295, y 184) en las 3 grandes.
- La Estilista usa paneles dibujados por código a la derecha. El Telar es todo por código.
- Cuando la máquina trabaja, las 5 cierran la GUI sola y rechazan los botones.

---

## 7. Pestaña creativa "ModaMod" (la agregada al inventario creativo de vanilla)

El ícono es la remera. Los ítems salen en este orden:

1. **Máquinas normales:** Modeladora, Telar, Estilista, Tintes, Sublimadora, Guardarropas, Maniquí, Cinta y Empalme.
2. **Máquinas creativas** (sin espera, sin insumos y con todo cargado): Modeladora, Telar, Estilista, Tintes y Sublimadora.
3. **Kits en shulkers**, repartidos de a 27:
   - Todos los patrones.
   - **Todos** los moldes. Se reconocen por el nombre de la clase y entran todos, incluso los que no están sueltos más abajo.
   - Insumos: 16 colorantes en pilas de 64, papel y frascos de vidrio.
4. **Prendas y accesorios:**
   - Remera, Hoodie.
   - Medias (3/4, lisas y de red), Pantalón, Pollera, **Delantal**, Calientabrazos, Capa y Maid outfit.
   - Sombreros: el de fábrica y 3 variantes.
   - Banda, Collar (la banda con zona de cuello y ancho fino) y Borcegos.
5. **Moldes de aplique** (Moño, Mariposa, Flor, Canguro, Corbata), moldes de textura (Fruncido, Acolchado) y Estrógenos.
6. **Retazos de aplique** en 3 combinaciones de color.
7. **Tinte mezcla** (muestra de color).
8. **Moldes de correa:** Lisa, Cadena, Cadena fina, Ojalillos y Cordón.
9. **Patrones sueltos:** Rayas, Corazones, Estrellas, Lunares y Vichy.
10. **Moldes sueltos**, por familia:
    - Corte.
    - Cuello: Redondo, V, Polera, Cuadrado, Corazón y Camisa.
    - Máscaras de sublimado.
    - Rango: 7.
    - Calce: 5.
    - Trama: Fina, Gruesa, Hexagonal, Perforada, Encaje, Rayas, Escocesa, Denim y Cuero.
    - Arnés: X, Tirantes y Bandas.
    - Lisa.
    - Pollera: 5 formas.
    - Delantal: 5 formas y 3 tirantes.
    - Volado: 2.
    - Capa: ruedo y cuello.
    - Sombrero: ala y punta.
    - Banda: zona, ancho y herraje.
    - Ruedo: 6.
    - Chaqueta: frente, capucha y solapa.
    - Calzado: 7.

Observaciones:
- El orden mezcla máquinas, prendas, moldes y patrones sin separadores, así que cuesta ubicarse. Una opción sería partirla en 2 o 3 pestañas (Máquinas, Prendas y accesorios, Moldes y patrones).
- La pestaña tiene más de 150 entradas.
- Los moldes aparecen en el kit **y** sueltos, lo que duplica la lista.
- Ni el Maniquí ni el Guardarropas tienen versión creativa.
- Los retazos de aplique y la muestra de color tienen un solo ejemplar de cada uno.
