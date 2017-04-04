# 1. Introducción
En entornos digitales, los métodos de obtención de recursos tradicionales se basan en la indexación de metadatos. Este tipo de búsquedas proporcionan formas efectivas para poder obtener documentos de interés a partir de una determinada búsqueda que representa la necesidad de obtener información. Sin embargo, estos métodos presentan algunas carencias, como la posibilidad de realizar consultas complejas.

En los últimos años ha surgido un nuevo concepto denominado Web Semántica, una extensión de la web actual donde la información tiene un significado bien definido . Este concepto se basa en que los datos estén bien definidos y enlazados, de manera que se puedan utilizar para conseguir un mejor descubrimiento, automatizacion, integración y reutilización entre aplicaciones.
El término datos enlazados se refiere a un cojunto de buenas prácticas para publicar y conectar datos estructurados en la web de forma que sean legibles tanto para las máquinas como para seres humanos.
Relacionadas con este concepto, surgen tecnologías que permiten conectar datos a través de la web utilizando los principios de la web de datos enlazados:
- Usar URIs (Uniform Resource Identifier) como nombres de las cosas.
- Usar HTTP URIs para que cualquiera los pueda consultar.
- Cuando alguien consulte una URI, proporcionar información útil en lenguajes estándar como RDF o SPARQL.
- Incluir enlaces a otras URIs de forma que ayuden a descubrir más cosas.

Hoy en día se le está dando gran importancia a la Web Semántica y facilitando su aplicación. Por ejemplo, gobiernos e instituciones están haciendo sus datos disponibles bajo licencias de datos abiertos, de forma que puedan ser utilizados.

En este documento se describirán las tareas realizadas para la transformación de un conjunto de datos en datos enlazados junto con las tecnologías utilizadas para este fin.
# 2. Proceso de transformación
- Selección de la fuente de datos, donde se explique el conjunto de datos que se ha seleccionado para transformar, especificando su origen.
La primera tarea del proceso de transformación de datos es selecionar el conjunto de datos con el que se va a trabajar. A su vez, esta tarea se divide en dos subtareas:
    - Definir los requisitos para la selección de la fuente de datos.
    En primer lugar, se deben definir los requisitos que la fuente de datos debe cumplir. Los requisitos para este caso se definen a continuación:
        - Datos reales.
        - La fuente de datos debe proporcionar los datos en un fichero .CSV.

    - Seleccionar una o más fuentes de datos.
    Atendiendo a los requisitos definidos previamente, realizando una búsqueda por la web se ha encontrado la página *datos.gob.es*, que es una iniciativa llevada a cabo por el Gobierno de España publicando datos abiertos de las comunidades autónomas.
En esta página, se publican los datos en múltiples formatos, por lo tanto, teniendo en cuenta que un requisito es que los datos estén accesibles en un fichero .CSV, la búsqueda del dataset se realizará filtrando por el tipo de formato, ya que esta página ofrece esta funcionalidad.
Realizando una búsqueda, se encuentran dos conjuntos de datos que satisfacen estos requisitos. El dataset *http://datos.gob.es/es/catalogo/l01280796-museos-de-la-ciudad-de-madrid*, ofrece información sobre los museos de la ciudad de Madrid.

    Una vez que se ha seleccionado la fuente de datos se debe obtener acceso a los mismos. En caso de que los datos no estén disponibles, será necesario contactar con la persona responsable de los mismos y solicitar acceso. Sin embargo, en este caso, los datos están disponibles para su descarga en el formato requerido, por lo tanto, este paso no es necesario.

- Análisis de los datos, explicando que tipo de datos se manejan, su formato, tipos de valores, y en general cualquier aspecto relevante para su transformación y explotación. Este análisis debe incluir la licencia de origen de los datos y la justificación de la licencia a usar en los datos transformados.
    El análisis de datos, a su vez, está compuesto por diferentes tareas que se describen a continuación.
    - Analizar la licencia de la fuente de datos.
    Las licencias especifican las condiciones legales bajo las que los datos se pueden utilizar.
En este caso, en la página especificada anteriormente, se ofrece información sobre este conjunto de datos; en particular, se determina la licencia que se les aplica. La licencia que se aplica define que la copia, publicación y distribución de los datos textuales es libre, así como su modificación. Además también se define que su explotación es libre para fines tanto comerciales como no comerciales.
Para contenido gráfico la licencia especifica otros términos, pero en este caso no aplican ya que no se va a utilizar información de este tipo.
    - Analizar la fuente de datos.
    El siguiente paso es analizar la fuente de datos. 
La página web informa de que este conjunto de datos contiene información sobre los museos de la ciudad de Madrid. Además se puede encontrar otra información como quién es el autor de los datos, cuándo el dataset fue creado, la última vez que este conjunto fue modificado, la licencia bajo la que se publican los datos y el idioma en el que está disponible. Sin embargo, estos datos no ofrecen información útil sobre los campos que forman este dataset. Por lo tanto, para obtener información los mismos, se realiza un análisis manual, observando las columnas que forman el dataset y sus valores.

        | Columna | Tipo | Valores | Descripción|
        | ------ | ------ | ------ | ------ |
        |PK|Numérico|Valores únicos|Números identificadores de cada museo|
        |Nombre|String|Valores únicos|Nombre del museo|
        |Descripcion-Entidad|String|Tiene valores vacíos|Descripción del edificio|
        |Horario|String|Contiene valores vacíos|Horarios de apertura|
        |Equipamiento|String|Contiene valores vacíos|Tiendas o servicios que se ofrecen|
        |Transporte|String|Tiene valores vacíos|Líneas de transporte público mediante las cuales se puede llegar|
        |Descripción|String|Contiene valores vacíos|Descripción sobre el público, entradas y otra información|
        |Accesibilidad|Numérico|0 ó 1||
        |Content-URL|String|Contiene valores vacíos|URL a web que contiene información sobre el museo|
        |Nombre-via|String|Contiene valores repetidos|Nombre de la vía donde se encuentra el museo|
        |Clase-vial|String|Contiene valores repetidos|Tipo de vía donde se encuentra el museo|
        |Tipo-num|String|Contiene valores repetidos||
        |Num|Numérico|Contiene valores repetidos|Número del edificio|
        |Planta|String|Contiene valores vacíos|Planta del edificio donde se encuentra el museo|
        |Puerta|String|Contiene valores vacíos|Número de puerta por donde se accede|
        |Escaleras|-|Todos los valores están vacíos||
        |Orientacion|String|Contiene valores vacíos||
        |Localidad|String|Madrid|Localidad de la dirección donde se encuentra el museo|
        |Provincia|String|Madrid|Provincia donde se encuentra el museo|
        |Codigo-postal|Numérico|Contiene valores repetidos|Código postal del edificio|
        |Barrio|String|Contiene valores repetidos|Barrio donde se encuentra el museo|
        |Distrito|String|Contiene valores repetidos|Distrito en el que se encuentra el museo|
        |Coordenada-x|Numérico|Contiene valores repetidos|Coordenada de la ubicación|
        |Coordenada-y|Numérico|Contiene valores repetidos|Coordenada de la ubicación|
        |Latitud|-|Valores nulos||
        |Longitud|-|Valores nulos||
        |Teléfono|String|Contiene valores vacíos|Número de teléfono del museo|
        |Fax|Numérico|Contiene valores vacíos|Fax del museo|
        |Email|String|Contiene valores vacíos|Correo electrónico de contacto|
        |Tipo|String|Contiene valores repetidos|Tipo de establecimiento|

- Estrategia de nombrado, donde se explique cómo se van a nombrar los recursos tanto del vocabulario a desarrollar como de los datos a generar.
Los pasos a seguir para realizar esta tarea se describen a continuación.
    - Elegir la forma de las URIs.
    En este primer paso se debe elegir si se utilizan almohadillas (#) en las URIs o barras inclinadas (/). 
En este caso, contamos con un conjunto de datos pequeño que no van a sufrir modificaciones frecuentemente. Por lo tanto, atendiendo a estas características, se utilizará la barra inclinada para los elementos. Para los términos ontológicos se utilizará almohadilla.
    - Elegir el dominio de las URIs.
El dominio es http://museosmadrud.com. 
    - Elegir la ruta de las URIs.
Para términos ontológicos la ruta es http://museosmadrid.com/ontologia/Establecimiento#.
Para los elementos, la ruta es http://museosmadrid.com/museo/.
    - Elegir patrones para clases, propiedades e individuos.
El patrón para los términos ontológicos es http://museosmadrid.com/ontologia/establecimiento#<nombre_elemento>. 
El patrón para los elementos es http://museosmadrid.com/museo/<id_item>/<nombre_museo>.

- Desarrollo del vocabulario, indicando el proceso de implementación del vocabulario y como este soporta los datos de origen. No se exige una ontología compleja, sino un vocabulario suficiente para describir los conceptos y propiedades de los datos a transformar.
A continuación se describen los pasos llevados a cabo para la realización de esta tarea.
    - Especificación de requisitos.
        - Funcionales. Estos requisitos están descritos mediante preguntas de competencia.
            - RF1. ¿Cuál es el horario de apertura de los museos?
            - RF2. ¿Qué museos tienen visitas guiadas?
            - RF3. ¿Qué equipamiento ofrece el museo?
            - RF4. ¿Mediante qué líneas de transporte se puede ir?
            - RF5.  Teléfono del museo.
            - RF6. Página web de información.
            - RF7. Fax del museo.
            - RF8. Correo electrónico.
            - RF9. Tipo de museo.
        - No funcionales.
            - RFN1. La ontología debe estar en español.
            - RFN2. La ontología debe estar basada en estándares existentes.
    - Extraer términos.
    Estos son los términos extraídos:
        - Museo: lugar en que se conservan y exponen colecciones de objetos artísticos, científicos, etc. Sinónimos: galería, exposición, edificio artístico.
        - Fundación cultural: institución que tiene por finalidad fomentar y promocionar el arte y la cultura.
        - Página web: documento o información electrónica capaz de contener texto, sonido, vídeo, programas, enlaces, imágenes, y muchas otras cosas, adaptada para la llamada World Wide Web (WWW) y que puede ser accedida mediante un navegador.
        - Dirección: lugar en el que una pesona o un local está establecido. Sinónimos: rumbo, itinerario, trayectoria, camino, ruta.
        - Código postal: relación de números formados por cifras que funcionan como clave de zonas, poblaciones y distritos, a efectos de la clasificación y distribución del correo.
        - Número: expresión formada por números que identifica cada edificio.
        - Calle: vía pública, habitualmente asfaltada o empedrada, entre edificios o solares. Sinónimos: vía, paseo, avenida, bulevar, rambla, ronda, carrera, arteria, corredera, callejón, travesía, pasadizo, pasaje, rúa, vial, costanilla.
        - Distrito: cada una de las demarcaciones en que se subdivide un territorio o una población para distribuir y ordenar el ejercicio de los derechos civiles y políticos, o de las funciones públicas, o de los servicios administrativos. Sinónimos: circunscripción, comarca.
        - Localidad: lugar o pueblo. Sinónimos: población, villa, ciudad.
        - Provincia: demarcación territorial administrativa de las varias en que se organizan algunos Estados o instituciones. Sinónimos: circunscripción, demarcación, comarca.
        - Barrio: cada una de las partes en que se dividen los pueblos y ciudades o sus distritos. Sinónimos: barriada, suburbio.
        - Teléfono: número que se le asigna a cada teléfono.
        - Fax: sistema que permite transmitir a distancia por la línea telefónica escritos o gráficos.

    - Elaborar conceptualización.
    El dominio es cultura.
    Una dirección está formada por una calle, un número, una planta, una puerta, escaleras, localidad, provincia, un código postal, un barrio y un distrito.
    Un establecimiento tiene una dirección.
    Un establecimiento tiene un horario.
    Un establecimiento es de un tipo determinado.
    La ubicación de un establecimiento tiene unas coordenadas.
    Un establecimiento puede tener teléfono.
    Un establecimiento puede tener fax.
    Un establecimiento puede tener página web.

- Proceso de transformación, justificando qué herramientas se han usado para la transformación de los datos y qué pasos se han seguido para su limpieza y adecuación al resultado esperado.
Los datos que tenemos están en un fichero .CSV, por lo tanto, es posible acceder a ellos utilizando hojas de cálculo. Al abrir este fichero, se puede ver que hay filas al comienzo del mismo vacías que se van a eliminar ya que no aportan información.
Teniendo en cuenta esta característica, para transformar los datos a RDF utilizaremos la herramienta OpenRefine. Por lo tanto, después de instalar esta herramienta, lo primero que se hace es crear un proyecto con nuestros datos. Para ello, lo primero es seleccionar elarchivo que se encuentra en el equipo, se selecciona y se crea el proyecto. En las opciones de parseo se selecciona como símbolo de separación de columnas la opción *custom* y se introduce un punto y coma, *;*, ya que es el símbolo que se utiliza en este caso. Los demás valores de parseo se dejan por defecto ya que son adecuados. Después de esto, se le asigna un nombre a este proyecto y se crea. A continuación se muestra una vista con las columnas que forman el dataset.

    Llegados a este momento, se debe realizar un análisis de los datos e identificar posibles errores que puede haber. Lo primero que se debe hacer es analizar columna a columna para asignar el tipo correcto a cada uno. Todas las columnas tienen un tipo correcto.
A continuación vamos a eliminar las columnas  *Latitud* y *Longitud*, ya que como se describió en el análisis previo de las columnas, contiene valores no válidos que no aportan información.

    A la hora de generar las URIs es necesario que sean únicas para cada ítem, por lo tanto, un objetivo del análisis de datos es comprobar si alguna columna tiene valores únicos para cada elemento. En este caso, tanto la columna *PK* como *NOMBRE* cumplen estos requisitos.
    
    Lo siguiente es agrupar celdas similares. Esto es útil ya que hay celdas con valores muy parecidos que se están refiriendo al mismo elemento. Sin embargo, en este caso no se han encontrado datos que se puedan agrupar, ya que, por ejemplo, establecimientos con el mismo nombre se referían a tipos de establecimiento diferntes.
    
    Por último, vamos a crear los mappings entre la ontología y los datos y a convertir los datos a RDF mediante la opción *Edit RDF Skeleton* de la extensión RDF. Los pasos llevados a cabo se detallan a continuación:
    - Incluir la estrategia de nombrado de recursos.
        - Se añade un nuevo prefijo *ontology* con la URI asignada para los elementos ontológicos.
        - Se asigna como URI base *http://museosmadrid.com/museo/*.
        - Se asigna como tipo de los individuos *schema:CivicStructure*, ya que es el tipo que mejor se adapta a los tipos de establecimientos que existen en el conjunto de datos.
        - Como columna base para definir las URIs se utiliza *PK*.
        - Lo siguiente es añadir propiedades a los recursos. Las propiedades que se han añadido son *NOMBRE*, *HORARIO*, *TELEFONO*, *CONTENT-URL*, *TIPO* y *DIRECCION*, columna creada a partir de las columnas *CLASE-VIAL*, *NOMBRE-VIA* y *NUM*.
        
- Enlazado, donde se explique qué enlaces se han generado con fuentes externas y mediante qué herramientas.
Para realizar el enlazado se utiliza la opción de RDF de OpenRefine. Lo primero que hacemos es definir servicios de reconciliación para usar posteriormente.
El primero es DBpedia en español, *http://es.dbpedia.org/sparql*.

    En este punto, lo primero que hay que hacer es observar qué columnas pueden ser útiles para realizar el enlazado. 
La primera columna que se utiliza es *LOCALIDAD*, cuyo valor es *Madrid*. Utilizando la opción *Start reconciling*, se selecciona el servicio añadido anteriormente. Se elige *http://dbpedia.org/ontology/PopulatedPlace* como la entidad con la que se va a realizar. Las demás opciones se dejan por defecto. A continuación, se crea una nueva columna, *populatedPlace*, a partir de ésta que contiene los enlaces obtenidos al realizar el enlazado. Lo último es realizar el enlazado propiamente dicho editando el esqueleto de RDF:
    - Se añade un nuevo nodo raíz que contiene la URI de la columna *LOCALIDAD* del tipo *http://dbpedia.org/ontology/PopulatedPlace*.
    - Se añade la propiedad *owl:sameAs* a este nodo que apunta a la columna creada anteriormente.

    La siguiente columna que se va a enlazar es *NOMBRE*, ya que nos interesa obtener información de los museos. Para esto, se va a realizar la reconciliación con la DBpedia sin elegir ningún tipo de entidad, ya que diferentes edificios son categorizados de distinta manera.

     

- Publicación. Opcionalmente, si se ha llevado a cabo la  publicación de los datos, se valorará que se explique cómo se ha llevado a cabo.
# 3. Aplicación y explotación, explicando qué funcionalidades aporta la solución desarrollada y cómo ésta hace uso de los datos y enlaces generados para aportar valor al usuario final. En este punto de deben explicar las queries SPARQL o el código en Jena usado para su implementación.
# 4. Conclusiones.
# 5. Bibliografía.