# ModaMod — GUIs de las 5 máquinas: botones, funciones y qué normalizar

Inventario del 2026-10-11, armado leyendo el código (no viendo el juego). Está pensado para comparar las máquinas entre sí y decidir qué unificar o simplificar. Las coordenadas son locales a la ventana (x, y desde la esquina de arriba a la izquierda), en px de GUI. "BTN n" es el id del botón en el código.

| Máquina | Ventana | Tema | Qué hace |
|---|---|---|---|
| Modeladora | 560 × 334 | Cobre | Corta la prenda con moldes (pines) |
| Estación de Tintes | 560 × 408 | Verdín | Color y patrones por zona |
| Sublimadora | 560 × 408 | Oro | Fotos y máscaras sobre la prenda |
| Estilista automática | 384 × 256 + paneles afuera (~658) | Lila | Apliques, correas y acabados |
| Telar automático | 176 × 226 | Granate | Teje prendas básicas con lana e hilo |

---

## 1. Matriz: la misma función en cada máquina

Cada celda dice dónde está la función y cómo se llama. "—" es que la máquina no la tiene.

### 1.1 Entrada, acción y salida
| Función | Modeladora | Tintes | Sublimadora | Estilista | Telar |
|---|---|---|---|---|---|
| Slot de entrada | Prenda (167,184) | Entrada (167,184) | Entrada (167,184) | Entrada (403,43), en el panel afuera | Lana (22,34) e Hilo (22,62) |
| Botón de acción | **Modelar** (215,184) | **Teñir** (215,184) | **Prensar** (215,184) | **Aplicar ▶** (428,42) | — (teje solo) |
| Slot de salida | Salida (295,184) | Salida (295,184) | Salida (295,184) | Salida (491,43) | Salida (138,48) |
| Arranca al cerrar la GUI | Sí | No: solo con Teñir | Sí, si hay algo fijado | No | Sí |
| Máximo de unidades en la entrada | 1 | 1 | 1 | 1 | — |

### 1.2 Controles de la columna izquierda (visor 3D)
| Función | Modeladora | Tintes | Sublimadora | Estilista |
|---|---|---|---|---|
| Visor 3D | (8,18) 86×196 | (8,18) 86×196 | (8,18) 86×196 | (8,18) 196×232 |
| Vista (gira 90°) | y 218 | y 218 | y 218 | ⟲ ⟳ (x 312..376, y 108), gira 45° |
| Campo de nombre del diseño | y 238 | y 238 | y 238 | en el panel Diseños (532,196) |
| Guardar diseño | y 256 | y 256 | y 256 | Guardar (532,214) |
| Escanear estampa | — | — | y 276 | — |
| Línea de producción | y 274 | y 276 | y 296 | panel Máquina (398,102) |
| Simetría | y 292 | abajo, junto al inventario (x 285, y 364) | en la fila de controles (243,272) | — |
| Almacén | derecha, 27 + banco de 36 | izquierda, 30 | derecha, 27 | panel Máquina, 18 |

Telar: tiene Línea arriba a la derecha (112,6) y nada más de esta columna.

### 1.3 Diseños guardados
| Función | Modeladora | Tintes | Sublimadora | Estilista |
|---|---|---|---|---|
| Casilleros de diseño | 8 (y 214); BTN 400 + i carga, 420 + i borra | 8 (y 214); BTN 300 + i carga, 320 + i borra | 8 (y 214); BTN 30 + i carga, 40 + i borra | último automático + hasta 8 con nombre; BTN 640 + k carga, 660 + k borra |
| Tope de guardados | 8 | 8 | 8 | 8 con nombre, más 1 "último" por tipo de prenda |
| Se guarda | solo con pines fijados | solo con capas fijadas | solo con caras fijadas | el último, solo; el nombrado, con botón |

### 1.4 Categoría y tipo de prenda
| Función | Modeladora | Tintes | Sublimadora | Estilista | Telar |
|---|---|---|---|---|---|
| Selector de categoría | botón (119,20) que cicla 10 | botón (119,20) que cicla 8 | botón (119,20) | no tiene: sigue a la prenda de la entrada | 6 casillas de prenda (tilda varias) |
| Id del botón | BTN 9 | BTN_CATEGORIA | BTN 10 | — | BTN 620 + i |
| Sigue a la prenda que entra | Sí | Sí | Sí | Sí | — |

---

## 2. Catálogo por máquina

### 2.1 Modeladora (560 × 334)

**Slots**
- Prenda: 1, solo prendas modelables.
- Salida: 1, solo extracción.
- Pines: hasta 14 lógicos por categoría. Cada uno recibe 1 molde, y no se pueden sacar con la máquina encendida.
- Almacén compartido: 27, para moldes que sirven en 2 o más categorías.
- Banco "Moldes de <categoría>": 36 visibles, atados a la categoría actual.
- Tijeras: 1 casillero invisible. Se cargan con shift-click o a mano al bloque.
- Slot "Activo": oculto y sin uso.

**Botones y campos**
| Control | Posición | Qué hace | BTN | Cuándo está activo |
|---|---|---|---|---|
| Categoría: <nombre> | (119,20) 240×14 | Cicla las 10 categorías | 9 | siempre |
| Modelar | (215,184) 48×16 | Aplica los moldes a la prenda | 11 | si `puedeModelar()` |
| Simetría: Sí/No | (8,292) | Los pines de un lado mandan a los dos lados | 10 | siempre |
| Línea | (8,274) | Enciende o apaga la línea de producción | 900 | siempre |
| Vista | (8,218) | Gira el visor 90° | cliente | siempre |
| Campo de nombre | (8,238) 86×14, 24 letras | Nombre del diseño | cliente | siempre |
| Guardar diseño | (8,256) | Guarda lo fijado como diseño | payload | si hay fijadas y hay menos de 8 |
| Casilleros 1..8 | y 214 | Izquierdo carga, derecho borra | 400 + i / 420 + i | siempre |
| Chinchetas (14) | sobre cada pin | Fija o desfija ese pin | 200 + pin | si el pin tiene molde |
| Cajones ▾ / ▴ (14) | junto al pin principal | Abre o cierra el pin secundario | 500 + i | donde hay cajón |

**Ocultos (solo en el código):** Fijar (BTN 4), Anclaje (7), Lado (8), Encender (6), Desfijar (100 + i).

**Indicadores:** solo el visor 3D, la chincheta animada y el ícono fantasma de un molde que volvió al almacén. No hay barras ni contadores. La barra de durabilidad de las tijeras está en el modelo 3D, no en la GUI.

### 2.2 Estación de Tintes (560 × 408)

**Slots**
- Entrada (1) y Salida (1).
- Cuadraditos de patrón: 12 por categoría, y solo se ven los que pintan algo (Remera 8, Pantalón 7, Medias 11, Calientabrazos 11, Pollera 3, Capa 3, Aplique 3, Delantal 3). Cada uno recibe 1 patrón.
- Almacén de patrones: 30 (5 × 6).
- Las tintas CMYK no tienen slot en la GUI. Se ven como barras en los sliders.

**Botones, sliders y campos**
| Control | Posición | Qué hace | BTN |
|---|---|---|---|
| Categoría | (119,20) | Cicla las 8 categorías | BTN_CATEGORIA |
| Teñir | (215,184) | Arranca el teñido | BTN_TENIR |
| Vista, nombre, Guardar diseño, Línea | izquierda, y 218..276 | Igual que en las otras | — / — / payload / 900 |
| Casilleros de diseño 1..8 | y 214 | Cargar / borrar | 300 + i / 320 + i |
| Chincheta de cuadradito (12) | sobre cada slot | Fija o desfija | 60 + i |
| Seleccionar cuadradito | clic en el slot | Elige qué capa se edita | 40 + i |
| Sliders C, M, Y, K, Transp. | derecha, y 18..104 | Mezcla de color. También son el tanque de tinta ("n/64") | 400 + |
| 3 muestras de color | y 108 | Elige cuál de los colores se edita | 90 + i |
| Envasar | (385,204) | Gasta un frasco y da una muestra de color | BTN_ENVASAR |
| Usar muestra | (461,204) | Carga el color de una muestra | BTN_USAR_MUESTRA |
| Colores: N | (385,226) | Cantidad de colores de la capa | BTN_COLORES |
| Contorno: Sí/No | (461,226) | Contorno del motivo | BTN_CONTORNO |
| Variación | (385,246) | Variación del patrón | BTN_VARIACION |
| Modo de mezcla | fila 1, y 232 | Cómo se funde la capa | BTN_MODO |
| `<` % `>` Opacidad | fila 1 | Opacidad | BTN_OPACIDAD(_ATRAS) |
| ▼ "Capa n/m" ▲ | fila 1 | Orden de la capa | BTN_BAJAR / BTN_SUBIR |
| Tamaño | fila 2, y 252 | Tamaño del motivo | BTN_TAMANO |
| `<` Ángulo `>` | fila 2 | Gira la grilla | BTN_ANGULO(_ATRAS) |
| `<` Posición `>` | fila 2 | Desplaza | BTN_POSICION(_ATRAS) |
| Forma / Repetición | fila 3, y 272 | Cambia la forma o la repetición | BTN_FORMA |
| Azar | fila 3 | Nueva semilla | BTN_SEMILLA |
| Invertir: Sí/No | fila 3 | Invierte la máscara | BTN_INVERTIR |
| `<` Giro motivo `>` | fila 4, y 292 | Gira cada motivo en su lugar | BTN_GIRO_MOTIVO(_ATRAS) |
| Sliders ↔ y ↕ | fila 4 | Distancia entre motivos, 50..300 % | 170 + nivel / 230 + nivel |
| Espejo | (285,324) | Espeja cada motivo | BTN_ESPEJO |
| Alternancia | (285,344) | Filas, columnas o damero espejados | BTN_ALTERNANCIA |
| Simetría | (285,364) | Simetría del torso | BTN_SIMETRIA |
| Panel de Capas | derecha, y 268..399 | Hasta 12 filas con ojo, ▲ y ▼ por fila | ojo 110 + i, ▲ 130 + i, ▼ 150 + i |

**Indicadores:** visor 3D (con el retazo y 3 franjas de color si la categoría es Aplique), texto "Editando…" en (119,296), 4 tanques (en los sliders), muestra grande de color, hint (y 190), marco dorado en la capa elegida.

### 2.3 Sublimadora (560 × 408)

**Slots**
- Foto Frente (1) y Foto Espalda (1): solo fotos de Camerapture. Tocar el slot elige la cara.
- Entrada (1, prenda estampable) y Salida (1).
- Almacén de fotos: 27.
- Máscara: 1 (molde de máscara).
- Papel y tintas: sin slot, se ven como barras.

**Botones**
| Control | Posición | Qué hace | BTN |
|---|---|---|---|
| Categoría | (119,20) | Cicla la categoría de ajustes | 10 |
| Prensar | (215,184) | Estampa | 25 |
| Escala `<` `>` | (119,232) | Tamaño de la foto o máscara | 2 / 1 |
| Posición X `<` `>` | (243,232) | Mueve en X | 4 / 3 |
| Posición Y `<` `>` | (119,252) | Mueve en Y | 6 / 5 |
| Ángulo `<` `>` | (243,252) | Gira | 8 / 7 |
| Cara: Frente/Espalda | (119,272) | Alterna la cara que se edita | 0 |
| Simetría | (243,272) | Espeja el lado, solo en prendas de a pares | 26 |
| Chincheta (una por cara) | sobre cada slot de foto | Fija la cara (solo se estampan las fijadas) | 22 + cara |
| Elegir cara | tocar el slot | Selecciona esa cara | 20 + cara |
| Casilleros de diseño 1..8 | y 214 | Cargar / borrar | 30 + i / 40 + i |
| Guardar diseño, nombre, Vista | izquierda | Igual que en las otras | payload |
| Escanear estampa | (8,276) | Elige una imagen del disco y la convierte en foto | cliente; apagado sin Camerapture |
| Línea | (8,296) | Línea de producción | 900 |
| Capas (12 botones "1F", "2E"…) | derecha, y 274 / 290 | Elige la capa con máscara | 50 + i |
| ▲ ▼ ✕ de capas | derecha, y 308 | Orden y borrado | ids en `SublimadoraBlockEntity` (~líneas 1280-1290) |
| Editar: imagen / máscara | derecha, y 308 | Los controles de arriba mueven la foto o la máscara | 27 |

**Indicadores:** visor 3D, 5 barras de tanques (C, M, Y, K y papel, "n/64"), texto guía (385,150), "Editando <cara> (fijada o borrador)" en (119,296), rótulo "Capas (n/12)".

### 2.4 Estilista automática (384 × 256 + paneles afuera)

Hereda la pantalla de la Mesa de estilado (ya jubilada) como editor interno. El panel Máquina y el panel Diseños quedan **fuera de la ventana de 384**.

**Slots**
- Entrada (1) y Salida (1) en el panel Máquina.
- Almacén: 18 (6 × 3), para plantillas, retazos, muestras y objetos.
- Del editor: Molde (240,30), Retazo (266,30), Material (318,30) y Objeto (292,30, solo creativa).
- El slot Prenda del editor está oculto: la muestra es la prenda de la entrada.
- Hilo y cuero no son slots: se cargan soltando el ítem sobre el contador (BTN 603 / 604), con shift-click o con la mano al bloque.

**Panel Máquina (x 390..520)**
| Control | Posición | Qué hace | BTN |
|---|---|---|---|
| Aplicar ▶ | (428,42) | Aplica el último diseño a la prenda de la entrada | 600 |
| Borrar | (398,70) | Olvida el último diseño de ese tipo y limpia la muestra | 602 |
| Línea | (398,102) | Línea de producción | 900 |
| Contador de hilo | (398,126) | Apliques que se pueden pagar (se llena con hilo) | 603 |
| Contador de cuero | (398,145) | Correas que se pueden pagar | 604 |
| Texto de info | (398,92) | Diseño actual o "Falta N x ítem" | — |

**Panel Diseños (x 526..654)**
| Control | Qué hace | BTN |
|---|---|---|
| Fila "★ Último" | El que usa Aplicar; se guarda solo | — |
| Filas con nombre (hasta 8) | Clic carga ese diseño en el editor | 640 + k |
| ✕ de cada fila | Borra ese diseño | 660 + k |
| Campo de nombre + Guardar | Guarda el actual con nombre | payload |

**Editor (x 0..384)**
| Control | Posición | Qué hace | BTN |
|---|---|---|---|
| Visor 3D | (8,18) | Gira, zoom con la rueda, clic para poner o apuntar | — |
| ⟲ ⟳ | (312,108) / (345,108) | Gira la vista 45° | — |
| Sacudir | (312,132) | Prueba la física del aplique | — |
| Obj. Modo / Variante | franja superior (12,21) / (72,21) | Ítem o bloque, y estado, de un aplique de objeto (solo Mesa creativa) | 40..41 |
| Espejo | (132,21) | Con borcegos, pone también en el otro pie | cliente |
| Pestañas Aplique / Correa / Acabado | y 147 | Cambia de grupo de controles | cliente |

*Pestaña Aplique:* 12 botones numerados (y 66, BTN 100 + i), Giro `<` `>` (y 88, BTN 11 / 10), Tamaño `<` `>` (y 108, BTN 13 / 12), Quitar (312,88, BTN 14), slider Blandura (214,150).

*Pestaña Correa:* lista de 8 correas (y 66), Modo (214,88), Ancho (296,88), Quitar/Cancelar (214,110), Recolorear (296,110), Largo −/+ (y 128, solo si el modo cuelga). Ids: quitar 480, colorear 490, ancho 520 + i·4, modo 700 + i·4, largo 760 + i·16.

*Pestaña Acabado:* botón Textura/Acabado (214,66, pone o quita el molde o el trim). Con sombrero, banda o borcegos: 3 filas de zona con 3 botones de color (BTN 300 + zona·4 + color), botón de patrón (BTN 330 + zona), "Retazo entero" (BTN 320) y, con borcegos, Pie izq./der. (BTN 500; los del pie izquierdo van de 800 a 832).

**Panel creativo (solo creativa, 160 px):** páginas Colocar, Mover y Moldes, con 10 sliders de colocación, pivote, intensidad, velocidad y amplitud, y la biblioteca de moldes creados.

**Sin botón en pantalla pero con id:** Fijar (BTN 601).

### 2.5 Telar automático (176 × 226)

**Slots:** Lana (1), Hilo (1), Salida (1). Se llena por cualquier lado del bloque y entrega por la derecha.

**Botones**
| Control | Posición | Qué hace | BTN |
|---|---|---|---|
| 6 casillas de prenda | (52..134, 24..) | Tildan qué prendas se tejen, de a una y en orden | 620 + i |
| −10, −, +, +10 | y 86 | Cantidad por lote (0 = sin límite, máximo 64) | 630..633 |
| ↻ | (138,86) | Reinicia el lote con la misma cantidad | 634 |
| Línea | (112,6) | Línea de producción | 900 |

**Indicadores:** barra de progreso (y 106), texto del lote, de la prenda que sigue con su costo, y "Listo" o "Falta N lana, M hilo".

---

## 3. Inconsistencias entre máquinas

Todas son observaciones del código, sin tocar nada.

1. **El botón de acción** (Modelar, Teñir, Prensar, Aplicar) está en el mismo lugar en las 3 máquinas grandes, pero en la Estilista cambió de posición y de forma. Además se comportan distinto: Modelar y Prensar también arrancan solas al cerrar la GUI, Teñir no, y Aplicar no.
2. **Línea de producción:** está en lugares distintos en cada una. En la Modeladora está sobre Simetría, en Tintes pegada al almacén, en la Sublimadora al final de la columna, en la Estilista en el panel Máquina y en el Telar arriba a la derecha.
3. **Simetría** significa tres cosas distintas:
   - En la Modeladora y la Sublimadora, los pines o la foto de un lado mandan al otro lado.
   - En Tintes es la simetría del torso del patrón.
   - Cambia de lugar en cada máquina (columna izquierda, fila de controles, junto al inventario).
4. **Ids de botón repetidos con otro significado:** `BTN 1..14` se usan en la Sublimadora (escala, posición, ángulo), en la Estilista (giro, tamaño, quitar) y en la Modeladora (fijar, anclaje, lado). Los diseños son 400/420, 300/320 y 30/40, y la categoría es 9 y 10.
5. **Guardar diseño:** en Modeladora, Tintes y Sublimadora son botón + campo de nombre + 8 casilleros numerados. En la Estilista es otra mecánica: un "último" automático más una lista con nombre.
6. **Almacén:** la Modeladora y la Sublimadora lo tienen a la derecha, Tintes a la izquierda, la Estilista en el panel Máquina y el Telar no tiene.
7. **Insumos:** hay 4 formas distintas de cargarlos:
   - Slots visibles (lana e hilo en el Telar).
   - Un slot invisible (tijeras de la Modeladora).
   - Barras sin slot (tinta, papel).
   - Soltar sobre el contador (hilo y cuero de la Estilista).
8. **Barras de progreso:** solo el Telar la muestra en la GUI. Las otras 4 la muestran en el modelo 3D del bloque.
9. **Cambio de vista:** Vista gira 90° en 3 máquinas, y ⟲ ⟳ gira 45° en la Estilista.
10. **Textos sin traducir:** los rótulos Cyan, Magenta, Yellow, Key y Transp. de Tintes están escritos a mano.
11. **Código muerto:** botones ocultos en la Modeladora, el texto `hint()`, el slot Activo y el botón Fijar de la Estilista.
12. **Densidad:** Tintes tiene unos 24 controles de capa en 4 filas de 237 px, y la Estilista concentra todo el editor en 162 px.

---

## 4. Ideas para normalizar (a charlar, nada hecho)

**Lo que se puede dejar igual en las 5**
- **Línea:** un único lugar fijo, por ejemplo arriba a la derecha de la ventana, como en el Telar.
- **Acción:** un único lugar (el actual de las 3 grandes sirve) y una única regla de arranque.
- **Salida:** misma posición relativa respecto de la entrada.
- **Ids:** repartir rangos por máquina para que no se pisen.

**Lo que se podría juntar**
- **Diseños:** que Modeladora, Tintes y Sublimadora usen la misma lista "último + con nombre" que la Estilista, en vez de 8 casilleros numerados.
- **Vista y giro:** una sola convención (90° o 45°).
- **Simetría:** renombrar la de Tintes para que no se confunda con las otras dos.
- **Almacén:** una sola ubicación y una sola forma de mostrarlo.

**Lo que se podría sacar**
- **Modelar y Prensar:** si arrancan al cerrar la GUI, el botón sobra, o al revés.
- **Botones ocultos y `hint()`** de la Modeladora.
- **El slot Activo** y el botón Fijar de la Estilista.
- **Escanear estampa:** podría ir a un menú contextual.

**Lo que habría que ordenar adentro**
- **Tintes:** partir los controles de capa en pestañas (Color, Patrón, Mezcla, Capas), que ya estaba en pendientes.
- **Estilista:** el slider Blandura contra las pestañas, y los 3 bloques de ancho.
- **Sublimadora:** el slot Máscara junto a las capas.

---

## 5. Pestaña creativa "ModaMod"

El ícono es la remera. Los ítems salen en este orden:

1. **Máquinas normales:** Modeladora, Telar, Estilista, Tintes, Sublimadora, Guardarropas, Maniquí, Cinta y Empalme.
2. **Máquinas creativas** (sin espera, sin insumos y con todo cargado): Modeladora, Telar, Estilista, Tintes y Sublimadora.
3. **Kits en shulkers**, repartidos de a 27: todos los patrones, **todos** los moldes (se reconocen por el nombre de la clase), y los insumos (16 colorantes en pilas de 64, papel y frascos de vidrio).
4. **Prendas y accesorios:** Remera, Hoodie, Medias (3/4, lisas y de red), Pantalón, Pollera, Delantal, Calientabrazos, Capa, Maid outfit, Sombreros (de fábrica y 3 variantes), Banda, Collar y Borcegos.
5. **Moldes de aplique** (Moño, Mariposa, Flor, Canguro, Corbata), moldes de textura (Fruncido, Acolchado) y Estrógenos.
6. **Retazos de aplique** en 3 combinaciones de color, y el **tinte mezcla**.
7. **Moldes de correa** (Lisa, Cadena, Cadena fina, Ojalillos, Cordón) y **patrones sueltos** (Rayas, Corazones, Estrellas, Lunares, Vichy).
8. **Moldes sueltos** por familia: corte, cuello (6), máscaras, rango (7), calce (5), trama (9), arnés (3), lisa, pollera (5), delantal (5 formas y 3 tirantes), volado (2), capa, sombrero, banda, ruedo (6), chaqueta, calzado (7).

Observaciones:
- Más de 150 entradas, sin separadores.
- Los moldes salen dos veces: en los kits y sueltos.
- Una opción es partirla en 2 o 3 pestañas: Máquinas, Prendas y accesorios, Moldes y patrones.
- El Maniquí y el Guardarropas no tienen versión creativa.
