Web semántica
============
# Introducción
En esta memoria se recogen los pasos seguidos para transformar un conjunto de datos recogidos en CSV a datos enlazos. Además, se recogerá también el proceso a seguir para crear una aplicación que haga uso de estos datos enlazados.

Concretamente, se utilizará un conjunto de datos que contiene las canciones del top 100 de la lista Billboard desde el año 1960 hasta el año 2010. Una vez procesados los datos y transformados a datos enlazados, se expondrán en forma de servicio web y se creará un cliente que los explote.

Como guía, se utilizará el material proporcionado en el curso, así como el libro **Practical RDF**. Para estructurar esta memoria se usará el índice propuesto en el enunciado de la práctica. Para crar la aplicación se utilizará Python. En concreto, la biblioteca `rdflib` para explotar los datos, y la biblioteca `easygui` para crear una interfaz amigable al usuario.

------------------

# Proceso de transformación

## Selección de la fuente de datos
El primer paso ha sido seleccionar una fuente que cubrirera los **datos necesarios**, que son el título de la canción, el nombre del artista y la fecha de entrada en la lista Billboard. Se ha encontrado un dataset que se ajusta perfectamente a los requisitos cubriendo los datos del top 100 de Billboard desde 1960 a 2010 , y que además, aportaba información sobre los cambios en los acordes, timbres, etc. de cada canción.

La propia página de Billboard proporciona un histórico de todas sus listas desde 1958 hasta la actualidad, pero no proporciona ningún API ni modo de explotación. Por lo tanto, aunque sería posible completar el dataset anterior con más canciones, esto supondría un costo temporal notable, por lo que no se considera necesario en este caso.

El [dataset](https://figshare.com/articles/Main_Dataset_for_Evolution_of_Popular_Music_USA_1960_2010_/1309953) puede encontrarse de forma sencilla en figshare.com. Ha sido compartido por Matthias Mauch, el 06/04/2015.

## Análisis de los datos
Como se comentaba anteriormente, los datos que planeamos usar son:
- Título de la canción
- Nombre del artísta
- Fecha de entrada en el top 100

Aunque no figuraba entre los datos que se planeaba incluir en un principio, se ha decidido introducir el número de cambios en el timbre de cada canción, ya que puede ser interesante comprobar si autores similares tienen cambios de timbre similares, por ejemplo. En el dataset, los cambios de timbre se dividen en varias categorías, llegando a sumar un total de 35. Para simplificar esto, se optará por no tener en cuenta la clase de cada timbre y simplemente contar el número de timbres total, es decir, sumar todas las columnas relacionadas con el timbre.

Se seguirá un proceso similar al anterior pero con los cambios de acorde. Esta información aparece reflejada en 175 columnas en el dataset, por lo que resumirla en una columna, puede ser una buena idea.

Por último, se utilizará también la información del campo *decade*  y *artist_name_clean*. El primero de los atributos clasifica las canciones en grupos de 10 años en función de su fecha de entrada en lista, mientras el sengudo transforma los nombres de los artistas para que no contengan espacios, ni simbolos extraños.

Además de toda esta información, el data set ofrece otras clasificaciones de las canciones como en lustros, años, eras, etc.  que no vamos a tener en cuenta. Además para cada canción aporta sus pesos harmónicos y tímbricos, pero tampoco serán usados, pues se necesita tener un conocimiento avanzado de la temática para poder interpretarlos.

A continuación, se presenta una tabla resumen con los datos que vamos a utilizar de este dataset:

| Nombre                | Valores       | Significado   |
| -------------         |:-------------:| :-----:        |
| Título                | String máx 99 chars   | título de la canción       |
| Artista               | String máx 87 chars   | nombre del artista         |
| ArtistaFiltrado               | String máx 87 chars   | nombre del artista sin caracteres especiales         |
| Fecha de entrada        | String con formato YYYY-mm-dd  | Fecha en que la canción ha entrado en lista por primera vez y que cubre todo el rango de fechas desde 1960 hasta 2010         |
| Década                | Integer de cuatro cifras [1960, 2000]  | Década en que la canción ha entrado en lista por primera vez         |
| Cambios de timbre     | Integer de 4 cifras     | Registro de cambios en el timbre de la melodía. Corresponde con el total de cambios acumulados de las columnas `timb_*`    |
| Cambios de acorde     | Integer de 4 cifras     | Registro de cambios de acordes en la melodía. Corresponde con el total de cambios acumulados de las columnas `harm_*` |

En cuanto a la **licencia** de los datos, podemos ver en la página en la que se encuntra el dataset, que este está bajo la licencia [CreativeCommons 4.0](https://creativecommons.org/licenses/by/4.0/). ¿Cómo afecta esta licencia al proyecto? Esta licencia permite compartir en cualquier medio o formato los datos y también permite su transformación y uso para cualquier fin, incluso el comercial. La únicas condiciones que impone esta licencia son:
- Referenciar a la fuente de datos original.
- Indicar si se han hecho modificaciones en los datos o no.

A la hora de publicar los datos que se generarán para esta práctica, se utilizará la misma licencia bajo la que se encuentran los datos, ya que se considera adecuado que no limitar la difusión de los mismos, ni se propono hacer un uso comercial de los mismos.

## Estrategia de nombrado

Para nombrar los elementos, es necesario tener en cuenta, que podemos utilizar `/` o `#` para definir la url. Como se comenta en las transparencias de clase, ambos tienen sus ventajas y desventajas. Por ejemplo, al utilizar `#` en este ejemplo: `http://mydata/artist#rollingstones` se estaría recuperando todo el documento `http://mydata/artist`. Si se usara `/` de separador, como en `http://mydata/artist/rollingstones` si se estaría recuperando solamente el documento relativo a los Rolling Stones.

Teniendo en cuenta lo comentado anteriormente, y que se cuenta con un serividor asociado al dominio `top100.com`, tendríamos:
- **Dominio**: `http://top100.com`
- **Ruta para términos ontológicos**: `http://top100.com/onto/`
- **Patrón para términos ontológicos**: `http://top100.com/onto/<term_name>`
- **Ruta para individuos**: `http://top100.com/data#`
- **Patrón para individuos**: `http://top100.com/data#<resource_name>`


## Desarrollo del vocabulario

Para llevar a cabo el desarrollo de nuestro vocabulario, seguiremos los pasos propuestos en los videos lectivos del tema correspondiente. Estos pueden resumirse en los siguientes:
- Especificación de requisitos
- Extracción de términos
- Conceptualización
- Búsqueda de ontologías para reutilizar
- Selección de las ontologías
- Implementación de nuestra ontología
- Evaluación de los resultados

### Especificación de requisitos

A su vez, esta tarea puede dividirse en dos partes, la especificación de requisitos funcionales (RF) y los no funcionales (RNF).

- **RF**: Recogen los requisitos específicos de contenido que la ontología debe cumplir.
- **RNF**: Recogen detalles generales del comportamiento de la misma.

A continuación, se presentan los requisitos que se consideran necesarios para esta ontología. Cada uno tendrá asociado un identificador, que tendrá la forma RFXX si se corresponde a un requisito funcional, o RNFXX en caso contrario.

**RF01**: Debe ser posible conocer el artista de la canción.

**RF02**: Debe ser posible conocer el año de entrada en lista.

**RF03**: Debe ser posible conocer las canciones de un artista en lista.

**RF04**: Debe ser posible conocer el número de cambios de timbre de cada canción.

**RF05**: Debe ser posible conocer el número de cambios de acorde.

**RNF01**: Tener al menos 4 elementos.

**RNF02**: Reutilizar recursos de conocimiento existentes.

**RNF03**: Debe estar disponible en inglés.

**RNF04**: Si se publica, ha de ser con la licencia citada en los apartados anteriores, [CreativeCommons 4.0](https://creativecommons.org/licenses/by/4.0/).

**RNF05**: Se debe presentar documentación sobre la estructura de la misma.

**RNF07**: Se debe validar usando una herramienta apropiada.

**RNF08**: Se debe conseguir que no presente pitfalls importantes en el validador Oops!.


### Extracción de términos

Se crea un glosario para la ontología que se prentende construir. Para construirlo, nos apoyaremos en los requisitos, en los datos y en la sinonimia de los términos que encontremos.

- *Artist*: Persona que se dedica a la música,como compositor o como intérprete.
- *Chord*: Conjunto de tres o más notas diferentes.
- *Musician*: Ver *Artist*.
- *Song*: Composición, por lo general en verso, que se canta o a la que se puede poner música.
- *Timbre*: Cualidad del sonido de la voz de una persona o de un instrumento musical que permite distinguirlo de otro sonido del mismo tono.
- *Track*: Ver *Song*.
- *Tone*: Ver *Timbre*.

### Conceptualización

A continuación se presenta un diagrama que pretende ser un modelo inicial de la ontología a desarrollar.

![Este es el diagrama inicial de la ontología](https://raw.githubusercontent.com/ivan0013/Curso2016-2017/master/IvanRodriguezTorres/images/ontoDiagram.png "Este es el diagrama inicial de la ontología")
### Búsqueda de ontologías para reutilizar

Para realizar la búsqueda de ontologías se utilizarán los recursos proporcionados en los videos. A continuación, se muestra una lista con los recursos utilizados y los resultados más relevantes:

- **[LOV](http://lov.okfn.org/)**: [Music Ontology](https://github.com/motools/musicontology/blob/master/rdf/musicontology.rdfs)
- **[LOD2Stats](http://stats.lod2.eu/)**:  [BBC Music](http://stats.lod2.eu/rdfdocs/127), [Artists on lastfm](http://stats.lod2.eu/rdfdocs/1282)
- **[Google(filetype:owl)](https://www.google.es/search?safe=off&site=&source=hp&q=filetype%3Aowl+music&oq=file&gs_l=hp.3.1.35i39k1l2j0i67k1l3j0j0i67k1j0l3.1829.2890.0.5327.7.7.0.0.0.0.226.778.0j4j1.5.0....0...1c.1.64.hp..2.4.616.0..0i131k1._y3c6z43IBA)**:  [Digital Media Assets](https://gplsi.dlsi.ua.es/gplsi13/sites/default/files/resources/JamesBond_CaseStudy.owl), [Music by Kivash](https://sites.ualberta.ca/~golmoham/SW/music%20ontology/June112008/good/My_OWL_Files/Version_With_Import/Multimedia.owl)

### Selección de las ontologías

Hay varios criterios que debemos tener en cuenta en la selección de las ontologías:

 1. Cómo cubre nuestro dominio
 2. Si presenta problemas de modelado
 3. Si sigue los principios de los datos enlazados
 4. Que se utilice en el mayor número de sitios posibles

A continuación, se presenta una tabla resumen de los puntos anteriores para cada una de las ontologías encontradas anteriormente.

 | Ontología  | Criterio 1       |Criterio 2        |Criterio 3       |Criterio 4       |
| -------------      | -------------       | --------------       |------------         | -------------      |
|Music Ontology| artist, song | No | Si |-|
|BBC Music| - | - | - | - |
|Artist on Lastfm| - | - | - | - |
|Digital Media Assets| artist, song | No | Si |-|
|Music by Kivash| artist, song | No | Si | -|

Las ontologías *BBC Music* y *Artist on Lastfm* no pudieron ser localizadas con éxito. Por ejemplo, en el caso de *BBC Music* el enlace disponible lleva a una web, que explota los datos de la ontología.  

La ontología *Music by Kivash*, en principio, parecía una buena candidata pero tras analizarla en profundidad pudimos comprobar que muchas de las URIs que referencia ya no existen, y tampoco se ha encontrado documentación que nos indique a dónde se han mudado, por tanto, suponemos que es un proyecto en desuso. Este motivo es suficiente para descartarla.

Las últimas dos candidatas, *Digital Media Assets* y *Music Ontology* son bastante completas ambas y ambas serían válidas para el proyecto. Como ambas cubren nuestro dominio en igual grado se escogerá *Music Ontology* ya que parece la mejor documentada.

### Implementación de nuestra ontología

Se ha realizado la implementación de la ontología siguiendo el diagrama propuesto en la sección [Conceptualización](https://github.com/ivan0013/Curso2016-2017/tree/master/IvanRodriguezTorres#conceptualización), tal y como puede verse en el fichero adjunto *ontologia.owl*.

Como se ha mencionado anteriormente, para el diseño de nuestra ontología nos apoyamos en *Music Ontology*, reutilizando sus clases:
- MusicArtist
- Track

De forma indirecta, al utilizar *Music Ontology*, también estamos  utilizando [foaf](http://xmlns.com/foaf/spec/), tal y como se puede ver en la especificación de la misma.

Además de eso, también reutilizamos la [ontología time](https://www.w3.org/TR/owl-time/) para la definición de la fecha en la que la canción se ha incorporado a la lista.

### Evaluación de los resultados
Para validar que la ontología es correcta, se siguen varios pasos:
1. Se comprueba que la ontología está bien formada. Para ello, utilizamos la herramienta [Turtle Validator](http://ttl.summerofcode.be/).
2. Se analizan los pitfalls en Oops!. Han sido necesarias varias iteraciones sobre este paso, ya que, había ciertos errores en las definiciones de dominio y rango de algunas propiedades.

## Proceso de transformación
En este paso, el objetivo es transformar los datos a RDF, para ello, usaremos el software OpenRefine.

Ademś, hay que aclarar que han sido necesarias una serie de transformaciones para adaptar el dataset inicial.
- Varias de las canciones estaban interpretadas por varios artistas, y éstes aparecían todos en el campo artista, relacionados con las palabras "feat", "ft", etc. Para solucionar esto, nos quedamos solo con el primer artista que aparezca.

- Tanto los cambios de acorde como los de timbre aparecen desglosados según su tipo. Es necesario sumarlos para tener el número de cambios total.

- Se eliminan algunas columnas innecesarias, como "year" o "decade".

- El nombre de las canciones es único, por lo que podrá usarse como identificador de cada una. Si se considerase que éste es demasiado largo o inapropiado (debido a que puede contener símbolos), podría crearse una columna adicional con el resumen en md5 de la cadena.

**Nota:** Durante esta transformación, se pudo observar que la cantidad de cambios de acordes totales para todas las canciones era la misma. Por lo tanto, podríamos estar ante un error. Para no mermar demasiado el dataset, ignoraremos el error.

## Enlazado
El enlazado de datos es útil para unir nuestros datos con recursos ya existentes. Si tomamos como ejemplo el problema de las diapositivas, se enlaza la ciudad de Leeds con su recurso correspondiente en dbpedia.

En esta práctica se enlazarán los datos correspondientes a los nombres de artistas, así como los datos correspondientes a nombres de canciones. Aquí se encontraron dos problemas fundamentales:
- Al realizar cualquier reconcilición con OpenRefine contra cualquier servicio añadido, el sistema cae en condición de error y la reconciliación no se produce. Al añadir endpoints de diferentes servicios como: dbpedia, dbtune, musicbrainz, etc. y realizar la reconciliación, OpenRefine lanzaba excepciones relacionadas con la implementación de la comunicación http. Para intentar solventarlo, se han probado diferentes versiones de Java, incluyendo la última versión del OpenJDK y la última versión del JDK mantenido por Oracle.

     Después de varios intentos fallidos, se ha decidido reconciliar los datos contra el endpoint que viene incluido por defecto con el plugin de RDF, wikidata, que es el único que ha funcionado de forma correcta.

- Fruto del problema anterior, en wikidata, no se ha encontrado la clase `Artista` o equivalentes. En cambio tenemos `Humano` y `Banda`. El problema de esto es que ninguno de los dos criterios se adecua pr completo al concepto de artista musical que buscamos, ya que, si reconciliamos contra `Humano` las bandas como pueden ser U2, Red Hot Chilli Peppers, etc. no apareceran. De igual forma, si reconciliamos contra `Banda`, los artistas en solitario no aparecerán. Por tanto, en esta prácticas se ha reconciliado contra ambos y se ha escogido la clase con mayor número de elementos reconciliados. Esta clase es `Humano`, que ha reconciliado en torno al 78% de los elementos, mientras que `Band`, no llegaba al 45%.

## Publicación
En este caso no se ha llevado a cabo la publicación de los datos, pero podemos hacer un resumen de como sería el proceso. Éste se divide en 3 pasos:
- Descripción de los datos: Se debe hacer una descripción adecuada de los datos y su estructura. Para ellos existen varias herramientas disponibles. Destacamos Void y D-Cat.
- Publicación: Exponer los datos al dominio público. Una forma aconsejable de hacerlo es un repositorio público, o un sitio específico como [datahub](http://datahub.io).
- Validación de acceso: Esta tarea puede realizarse mediante el uso de validadores automáticos como [esta](http://validator.linkeddata.org).

------------------


# Aplicación y explotación
Como se comentaba en la introducción, se ha utilizado Python para crear una aplicación que permita la explotaciñón de los datos generados. Concretamente se ha utilizado Python 3, junto con las librerías `rdflib` y `easyguy`.

La aplicación tarda unos instantes en iniciarse, pues es el tiempo que necesita para cargar todo el archivo de datos rdf. Una vez cargado algunos procesos pueden demorarse unos segundos, mediante los cuales, la interfaz gráfica tampoco será visible.

Una vez arrancada, la aplicación permite 3 tipos de búsqueda de canciones diferentes:
- *by song*: En este tipo se nos mostrará una lista con todas las canciones disponibles, y podremos seleccionar cualquiera de la lista para consultar sus detalles.
- *by artist*: Se nos mostrará una lista con todos los artistas existentes en los datos. Al seleccionar uno, se listarán las canciones que tenga que hayan aparecido en el top 100. Finalmente, al igual que en el caso anterior, podremos seleccionar una canción para ver sus detalles.
- *by date*: En este caso, podremos introducir dos fechas para definir un período de tiempo. A continuación, se nos mostrarán las canciones pertenecientes a ese período. Por último, al igual que en el caso anterior, podremos seleccionar una canción para ver sus detalles.


Para recuperar los datos se ha utilizado el lenguaje de consulta SPARQL, las consultas utilizadas son:

Para recuperar todas las canciones ordenadas alfabéticamente:
```
  PREFIX mo:  <http://purl.org/ontology/mo/>
  PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
  SELECT * WHERE {
      ?song a mo:Track .
      ?song rdfs:label ?name .
  }
  order by asc(UCASE(str(?name)))
  ```


Para recuperar el nombre de los artistas por orden alfabético:
```
PREFIX mo:  <http://purl.org/ontology/mo/>
PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>

SELECT DISTINCT ?name WHERE {
    ?person a mo:MusicArtist .
    ?person rdfs:label ?name.
}
order by asc(UCASE(str(?name)))
```

Recuperar las canciones pertenecientes a un intervalo de tiempo de forma alfabética:
```
PREFIX mo:  <http://purl.org/ontology/mo/>
PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
PREFIX time:  <http://www.w3.org/2006/time#>

SELECT * WHERE {
    ?song a mo:Track .
    ?song rdfs:label ?name.
    ?song time:inXSDDate ?date .

    FILTER (strdt(?date, xsd:dateTime) > "1987-09-12T00:00:00"^^xsd:dateTime) .
    FILTER (strdt(?date, xsd:dateTime) < "1989-09-12T00:00:00"^^xsd:dateTime) .
}
order by asc(UCASE(str(?name)))
```

Recuperar los datos de una canción dado el nombre de la canción:
```
PREFIX mo:  <http://purl.org/ontology/mo/>
PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
PREFIX time:  <http://www.w3.org/2006/time#>
PREFIX foaf:  <http://xmlns.com/foaf/0.1/>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT DISTINCT ?artistUrl ?date ?cc ?tc  WHERE {
    ?song a mo:Track .
    ?song rdfs:label  "Heart".
    ?song time:inXSDDate ?date.
    ?song :ChordChange ?cc .
    ?song :TimbreChange ?tc .
    ?song foaf:maker ?artistUrl .
    ?artist a mo:MusicArtist .
}
```

Recuperar las canciones asociadas a un artista:
```
PREFIX mo:  <http://purl.org/ontology/mo/>
  PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
  PREFIX foaf:  <http://xmlns.com/foaf/0.1/>

  SELECT ?name WHERE {
      ?song a mo:Track .
      ?song rdfs:label ?name.
      ?song foaf:maker ?artistUrl .

      FILTER (str(?artistUrl)="%s"^^xsd:string)
  }
```


Entre las dificultades que se encontraron en este desarrollo, se debe mencionar una en concreto, que ha obligado a postprocesar los datos, ya generados en rdf. Las fechas estaban en formato `yyyy-mm-dd` pero deberían haber estado en el formato `yyyy-mm-ddThh:mm:ss` para que fuesen interpretados como fechas correctamente. La solución adoptada ha sido aplicar el siguiente comando:

```bash
sed -i 's/[0-9][0-9][0-9][0-9]\-[0-9][0-9]\-[0-9][0-9]/&T00:00:00/' data.rdf
```

En esencia, busca las fechas en formato `yyyy-mm-dd` y le añade `T00:00:00`. Esta modificación no perjudica la información aportada por los datos.

Otro problema realcionado con el desarrollo de esta aplicación ha sido la compatibilidad d los juegos de caracteres. En principio, la aplicación estaba desarrollada para Python 2.7, pero había algún error de codificación de los datos proporcionados por `rdflib` al ser incorporados a la interfaz. La forma de mitigarlo fue el cambio a Python 3.

Para ejecutar la aplicación basta con instalar las librerías de Python 3 definidas en las primeras líneas del script `app.py` y lanzarlo desde un terminal de la forma: `python3 app.py`.

------------------
# Conclusiones

Esta práctica ha servido como primer acercamiento al mundo de los datos enlazados. Nunca había profundizado en el tema  por tanto no sabía nada acerca de RDF, SPARQL o cualquiera de las tecnologías que se haya utilizado en este curso, exceptuando XML.

En mi opinión, el concepto que se persigue con las iniciativas de proyectos de linked data es muy buena, ya que de conseguirse los objetivos, la información sería menos redundante y dispar. Además, en un mundo donde el concepto de datos enlacados funcionase al 100%, tendríamos información mucho más precisa a nuestra disposición, uniendo información de difernetes sitios como Wikipedia, Geocities, etc.

Un ejemplo de ello podría ser que se estableciese una ontología de biografías de personajes famosos. Si todo el mundo utilizase el modelo de datos enlazados,todas las referencias a un actor, científico, escritor, etc. se harían contra el mismo "objecto", por tanto todo el mundo estaría viendo la misma descripción de él. Siendo más sencillo tener descripciones completas y precisas.

La parte negativa de esta idea reside en su complejidad y es que poner a todo el mundo de acuerdo es difícil. Aunque organizaciones como la W3C luchan por la creación de un estándar es complicado que todo el mundo llegue a adoptarlo por diferentes motivos.

Pese a esto ya se han hecho numerosos avances en lo que se refiere a la adopción de un modelo de datos enlazados, y en mi opinión, tanto los desarrolladores, como investigadores, organizaciones, etc. deben de seguir poniéndo énfasis en su adopción, ya que sería un gran avance.

------------------
# Bibliografía

Como material bibliográfico se ha utilizado:
- Transparencias y vídeos del curso
- Libro: "*Practical resource description framework (rdf)*" de Shelley Powers.
- Recurso web: https://www.w3.org/2004/Talks/17Dec-sparql/intro/all.html
- Recurso web: https://www.w3.org/TR/rdf-sparql-query/

------------------
