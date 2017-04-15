package app;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.util.FileManager;

public class Acceso {

    private String fichero = "rdf.ttl";
    private String ontologia = "ontology.ttl";
    private Model model;

    public Acceso() {
        this.leer();
    }

    //se lee el fichero rdf
    public void leer() {
        setModel(ModelFactory.createDefaultModel());
        InputStream in = FileManager.get().open(getFichero());

        if (in == null) {
            throw new IllegalArgumentException("Fichero " + this.fichero + " no encontrado");
        }
        getModel().read(in, null, "TTL");
    }

    public String getInfoMadrid(Model model) { // se obtiene informacion de la localidad de Madrid
        Literal info = null;
        String uri = "";
        //se busca en el rdf los elementos que tengan URI
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "SELECT ?nombre ?uri "
                + "WHERE {?s rdfs:label ?nombre;" + "owl:sameAs ?uri.}";

        Query q = QueryFactory.create(query);
        QueryExecution qexec = QueryExecutionFactory.create(q, model);
        ResultSet results = qexec.execSelect();
        //de los resultados obtenidos, buscamos MADRID para obtener su uri
        while (results.hasNext()) {
            QuerySolution binding = results.nextSolution();
            Literal nombre = binding.getLiteral("nombre");
            if (nombre.toString().equals("MADRID")) {
                Resource subj = (Resource) binding.get("uri");
                uri = subj.toString();
            }
        }
        //a partir de la URI, buscamos informacion en la dbpedia sobre esta localidad
        String query1 = "SELECT ?info ?uri " + "WHERE {?uri <http://dbpedia.org/ontology/abstract> ?info . "
                + "FILTER(?uri=<" + uri + ">)}";
        Query query2 = QueryFactory.create(query1);

        QueryEngineHTTP qe = new QueryEngineHTTP("http://es.dbpedia.org/sparql", query2);
        try {
            ResultSet results2 = qe.execSelect();
            QuerySolution binding = results2.nextSolution();
            info = binding.getLiteral("info");
        } catch (Exception e) {
            System.out.println("No hay resultados de Madrid");
        } finally {
            qe.close();
        }
        return info.toString();
    }

    public List<String> getListaMuseos(Model model) { // se obtiene el listado de nombres de los museos
        List<String> nombres = new ArrayList<String>();
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>" + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                + "SELECT ?nombre " + "WHERE {?s rdfs:label ?nombre.}";
        Query q = QueryFactory.create(query);
        QueryExecution qexec = QueryExecutionFactory.create(q, model);
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution binding = results.nextSolution();
                Literal nombre = binding.getLiteral("nombre");
                if (!nombre.toString().equals("MADRID")) {
                    nombres.add(nombre.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("No hay resultados de Madrid");
        } finally {
            qexec.close();
        }
        return nombres;
    }

    public Map getInfoMuseo(Model model, String nombreMuseo) { // se obtiene informacion de un museo a partir de su nombre
        Map datos = new HashMap();
        String uri = "";
        String key = "";
        //se obtiene la informacion general del fichero rdf
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>" + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                + "SELECT ?pk ?nombre ?direccion ?telefono ?email ?web ?infoHorarios "
                + "WHERE {?s rdfs:label ?nombre;" + "vcard:hasAddress ?direccion;"
                + "vcard:hasTelephone ?telefono;"
                + "vcard:hasURL ?web;"
                + "vcard:hasNote ?infoHorarios;"
                + "vcard:hasUID ?pk."
                + "OPTIONAL {?s vcard:hasEmail ?email ."
                + "}"
                + "FILTER(?nombre = '" + nombreMuseo + "')}";
        Query q = QueryFactory.create(query);
        QueryExecution qexec = QueryExecutionFactory.create(q, model);
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution binding = results.nextSolution();
                Literal pk = binding.getLiteral("pk");
                key = pk.toString();
                Literal nombre = binding.getLiteral("nombre");
                Literal direccion = binding.getLiteral("direccion");
                Literal telefono = binding.getLiteral("telefono");
                Literal web = binding.getLiteral("web");
                Literal horarios = binding.getLiteral("infoHorarios");
                Literal email = binding.getLiteral("email");
                datos.put("nombre", nombre.toString());
                datos.put("nombre", direccion.toString());
                datos.put("direccion", direccion.toString());
                datos.put("telefono", telefono.toString());
                datos.put("web", web.toString());
                datos.put("horarios", horarios.toString());
                datos.put("email", email.toString());
            }
        } catch (Exception e) {
        } finally {
            qexec.close();
        }

        //se busca si tiene URI (no todos los elementos están enlazados)
        String qAux = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                + "SELECT ?pk ?nombre ?uri "
                + "WHERE {?s rdfs:label ?nombre;" + "vcard:hasUID ?pk;"
                + "owl:sameAs ?uri. "
                + "FILTER(?pk = '" + key + "')}";
        Query qAux1 = QueryFactory.create(qAux);
        QueryExecution qexecAux = QueryExecutionFactory.create(qAux1, model);
        try {
            ResultSet resultsAux = qexecAux.execSelect();
            while (resultsAux.hasNext()) {
                QuerySolution binding = resultsAux.nextSolution();
                Resource subj = (Resource) binding.get("uri");
                uri = subj.toString();
            }
        } catch (Exception e) {

        } finally {
            qexecAux.close();
        }

        //se obtiene informacion de la dbpedia utilizando la uri
        System.out.println("URI: " + uri);
        datos.put("uri", uri);
        String query1 = "SELECT ?info ?uri ?pag"
                + "WHERE {"
                + "OPTIONAL {"
                + "?uri <http://dbpedia.org/ontology/abstract> ?info. "
                + "}"
                + "FILTER(?uri=<" + uri + ">)}";
        Query query2 = QueryFactory.create(query1);

        QueryEngineHTTP qe = new QueryEngineHTTP("http://es.dbpedia.org/sparql", query2);
        try {
            ResultSet results2 = qe.execSelect();
            QuerySolution binding = results2.nextSolution();
            Literal info = binding.getLiteral("info");
            Literal pag = binding.getLiteral("pag");
            datos.put("info", info.toString());
            datos.put("pag", pag);
        } catch (Exception e) {
            System.out.println("No hay resultados");
        } finally {
            qe.close();
        }
        return datos;
    }

    //se obtiene el listado de obras expuestas en un museo
    public List getObras(String URI) {
        List datos = new ArrayList();
        String nombre = URI.substring(URI.lastIndexOf("/") + 1);
        String query1 = "PREFIX esdbpr: <http://es.dbpedia.org/resource/> "
                + "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
                + "SELECT ?obra "
                + "WHERE{ ?obra  dbpedia-owl:location  esdbpr:" + nombre + " . }";
        Query query2;
        try {
            query2 = QueryFactory.create(query1);
        } catch (Exception e) {
            return datos;
        }
        QueryExecution qe = QueryExecutionFactory.sparqlService("http://es.dbpedia.org/sparql", query2);
        try {
            ResultSet results2 = qe.execSelect();
            while (results2.hasNext()) {
                QuerySolution binding = results2.nextSolution();
                Resource subj = (Resource) binding.get("obra");
                datos.add(subj.toString());
            }

        } catch (Exception e) {
            System.out.println("No hay resultados");
            e.printStackTrace();
        } finally {
            qe.close();
        }
        return datos;
    }

    //se obtiene la descripción, el título original y el autor de la obra
    public Map getInfoObra(String obra) {
        Map datos = new HashMap();
        obra = "http://es.dbpedia.org/resource/" + obra;
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT ?info ?uri ?autor ?titulo "
                + "WHERE {"
                + "?uri <http://dbpedia.org/ontology/abstract> ?info; "
                + "<http://dbpedia.org/ontology/author> ?autor ;"
                + "rdfs:label ?titulo . "
                + "FILTER(?uri=<" + obra + ">)}";
        Query q = QueryFactory.create(query);
        QueryExecution qe = QueryExecutionFactory.sparqlService("http://es.dbpedia.org/sparql", q);
        try {
            ResultSet results = qe.execSelect();
            QuerySolution binding = results.nextSolution();
            Literal titulo = binding.getLiteral("titulo");
            Literal info = binding.getLiteral("info");
            Resource autor = (Resource) binding.get("autor");
            qe.close();
            //a partir de la URI del autor obtenida en la anterior consulta, se obtiene su nombre
            String qAutor = "SELECT ?nombre ?uri "
                    + "WHERE {"
                    + "?uri <http://dbpedia.org/ontology/birthName> ?nombre. "
                    + "FILTER(?uri=<" + autor.toString() + ">)}";
            Query queryAutor = QueryFactory.create(qAutor);
            QueryExecution qeAutor = QueryExecutionFactory.sparqlService("http://es.dbpedia.org/sparql", queryAutor);
            ResultSet resAutor = qeAutor.execSelect();
            QuerySolution bindingAutor = resAutor.nextSolution();
            Literal autorS = bindingAutor.getLiteral("nombre");
            datos.put("titulo", titulo.toString());
            datos.put("info", info.toString());
            datos.put("autor", autorS.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datos;
    }

    public String getFichero() {
        return fichero;
    }

    public void setFichero(String fichero) {
        this.fichero = fichero;
    }

    public String getOntologia() {
        return ontologia;
    }

    public void setOntologia(String ontologia) {
        this.ontologia = ontologia;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

}
