Web semántica
============
# Introducción
En esta memoria se recogen los pasos seguidos para transformar un conjunto de datos recogidos en CSV a datos enlazos. Además, se recogerá también el proceso a seguir para crear una aplicación que haga uso de estos datos enlazados. 

Concretamente, se utilizará un conjunto de datos que contiene las canciones del top 100 de la lista Billboard desde el año 1960 hasta el año 2010. Una vez procesados los datos y transformados a datos enlazados, se expondrán en forma de servicio web y se creará un cliente que los explote. 

Como guía, se utilizará el material proporcionado en el curso, así como el libro **Practical RDF**. Para estructurar esta memoria se usará el índice propuesto en el enunciado de la práctica. Para la creación del servicio web se usarán los frameworks **materializeCSS** y **JQuery**, mientras que para la creación de los servicios web, **CherryPy**.

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

Por último, se utilizará también la información del campo *decade* que aporta el dataset. Este campo clasifica las canciones en grupos de 10 años en función de su fecha de entrada en lista. 

Además de toda esta información, el data set ofrece otras clasificaciones de las canciones como en lustros, años, eras, etc.  que no vamos a tener en cuenta. Además para cada canción aporta sus pesos harmónicos y tímbricos, pero tampoco serán usados, pues se necesita tener un conocimiento avanzado de la temática para poder interpretarlos. 

A continuación, se presenta una tabla resumen con los datos que vamos a utilizar de este dataset:

| Nombre                | Valores       | Significado   |
| -------------         |:-------------:| :-----:        |
| Título                | String máx 99 chars   | título de la canción       |
| Artista               | String máx 87 chars   | nombre del artista         |
| Fecha de entrada        | String con formato YYYY-mm-dd  | Fecha en que la canción ha entrado en lista por primera vez y que cubre todo el rango de fechas desde 1960 hasta 2010         |
| Década                | Integer de cuatro cifras [1960, 2000]  | Década en que la canción ha entrado en lista por primera vez         |
| Cambios de timbre     | Integer de 4 cifras     | Registro de cambios en el timbre de la melodía. Corresponde con el total de cambios acumulados de las columnas `timb_*`    |
| Cambios de acorde     | Integer de 4 cifras     | Registro de cambios de acordes en la melodía. Corresponde con el total de cambios acumulados de las columnas `harm_*` |

En cuanto a la **licencia** de los datos, podemos ver en la página en la que se encuntra el dataset, que este está bajo la licencia [CreativeCommons 4.0](https://creativecommons.org/licenses/by/4.0/). ¿Cómo afecta esta licencia al proyecto? Esta licencia permite compartir en cualquier medio o formato los datos y también permite su transformación y uso para cualquier fin, incluso el comercial. La únicas condiciones que impone esta licencia son:
- Referenciar a la fuente de datos original.
- Indicar si se han hecho modificaciones en los datos o no. 

A la hora de publicar los datos que se generarán para esta práctica, se utilizará la licencia de software libre [gpl3](https://www.gnu.org/licenses/gpl-3.0.en.html). Esto se debe a que no hay ningún fin lucrativo ni comercial detrás, y se desea facilitar el uso de los mismos por cualquiera que lo desee, pero es interesante que se mantenga así. Al publicar algo bajo esta licencia se permite el uso, modificación y redistribución, pero siempre y cuando se haga bajo la misma licencia.

## Estrategia de nombrado
## Desarrollo del vocabulario
## Proceso de transformación
## Enlazado
## Publicación


------------------


# Aplicación y explotación

------------------
# Conclusiones

------------------
# Bibliografía

------------------
