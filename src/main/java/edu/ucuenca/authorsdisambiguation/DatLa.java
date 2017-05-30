/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsdisambiguation;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import edu.ucuenca.authorsdisambiguation.nwd.Cache;
import edu.ucuenca.lat.Journal;
import edu.ucuenca.lat.Publication;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

/**
 *
 * @author cedia
 */
public class DatLa {

    public Model Data;
    
    public String qu;
    

    private DatLa() {

        Data = ModelFactory.createDefaultModel();

        InputStream in1 = FileManager.get().open("/home/cedia/CLEI/Lat/lat.rdf");

        Data.read(in1, null);

    }

    public boolean filtA(String Pub, String Rev) {
        boolean re = true;
        if (Pub != null && Rev != null && !Pub.trim().isEmpty() && !Rev.trim().isEmpty()) {

            try {
                int pub = Integer.parseInt(Pub);
                int rev = Integer.parseInt(Rev);

                if (pub < rev) {
                    re = false;
                }

            } catch (Exception e) {
            }

        }
        return re;
    }

    public List<Publication> PubliWOJ() throws FileNotFoundException {

        File file = new File("/home/cedia/CLEI/Lat/WithOutJournals.json");
        FileInputStream fileInputStream = new FileInputStream(file);
        JsonArray parse = JSON.parse(fileInputStream).get("data").getAsArray();
        List<Publication> ls = new ArrayList<>();

        Map<String, Publication> mp = new HashMap<>();

        for (int i = 0; i < parse.size(); i++) {
            JsonObject asObject = parse.get(i).getAsObject();

            String title = asObject.get("title").getAsString().value();
            String abstrac = asObject.get("abstract").getAsString().value();
            List<String> palabras = Arrays.asList(asObject.get("keywords").getAsString().value().split(";"));
            List<String> asList2 = new ArrayList<>();
            for (String s : palabras) {
                asList2.add(s.trim());
            }
            asList2.removeAll(Arrays.asList("", null));
            asList2 = new ArrayList<String>(new LinkedHashSet<String>(asList2));
            palabras=asList2;

            String ani = asObject.get("year").getAsString().value();

            Publication get = new Publication();
            get.title = title;
            get.abstrat_ = abstrac;
            get.kwy = palabras;
            get.ani = ani;
            ls.add(get);
        }
        return ls;
    }

    public List<Publication> PubliWJ(List<Journal> Aut) throws FileNotFoundException {

        Map<String, Journal> jm = new HashMap<>();

        for (Journal j : Aut) {
            jm.put(j.ISSN, j);
        }

        File file = new File("/home/cedia/CLEI/Lat/WithJournals.json");
        FileInputStream fileInputStream = new FileInputStream(file);
        JsonArray parse = JSON.parse(fileInputStream).get("data").getAsArray();
        List<Publication> ls = new ArrayList<>();

        Map<String, Publication> mp = new HashMap<>();

         for (int i = 0; i < parse.size(); i++) {
            JsonObject asObject = parse.get(i).getAsObject();
            String title = asObject.get("titleArticle").getAsString().value();
            String abstrac = asObject.get("abstractArticle").getAsString().value();
            //List<String> palabras = Arrays.asList(asObject.get("keywords").getAsString().value().split(";"));
            //List<String> asList2 = new ArrayList<>();
           // for (String s : palabras) {
            //    asList2.add(s.trim());
           // }
           // asList2.removeAll(Arrays.asList("", null));
           // asList2 = new ArrayList<String>(new LinkedHashSet<String>(asList2));
            //palabras=asList2;

            String ani = asObject.get("yearArticle").getAsString().value();

            String jor = asObject.get("journalArticle").getAsString().value();
            
            String issn = asObject.get("issnLatindex").getAsString().value().trim();
            
            Journal get1 = jm.get(issn);
            if (get1 == null){
                //System.out.println("W");
                continue;
            }
            
            
            Publication get = mp.get(title);

            //Anios no pueden ser
            if (!filtA(ani, get1.Ani)) {
                continue;
            }

            if (get != null) {

                if (!get.Candidatos.contains(get1)) {
                    get.Candidatos.add(get1);
                }

            } else {
                get = new Publication();
                get.title = title;
                get.abstrat_ = abstrac;
                get.kwy = null;
                get.journal = jor;
                get.ani = ani;
                get.Candidatos = new ArrayList<>();
                get.Candidatos.add(get1);
                ls.add(get);
            }

        }

        return ls;
    }

    public List<Journal> Aut() {

        String q = "select ?uri ?name ?issn ?sub ?ani  { ?uri a <http://www.ucuenca.edu.ec/ontology/journal> . "
                + "?uri <http://www.ucuenca.edu.ec/ontology/tit_clave> ?name . "
                + "?uri <http://www.ucuenca.edu.ec/ontology/issn> ?issn . "
                + "?uri <http://www.ucuenca.edu.ec/ontology/subtema> ?sub . "
                + "?uri <http://www.ucuenca.edu.ec/ontology/ano_ini> ?ani . "
                + "    } ";

        List<List<String>> Aut = Aut(Data, q);

        List<Journal> ls = new ArrayList<>();

        for (List<String> da : Aut) {
            Journal n = new Journal();
            n.URI = da.get(0).trim();
            n.ISSN = da.get(2).trim();
            n.Name = cleanName(da.get(1)).trim();
            n.Subtemas = cleanSub(da.get(3));
            n.Ani = da.get(4).trim();
            ls.add(n);
        }
        return ls;
    }

    public List<String> cleanSub(String n) {

        String[] split = n.split(",|;|\\sy\\s");
        List<String> asList = Arrays.asList(split);
        List<String> asList2 = new ArrayList<>();
        for (String s : asList) {
            asList2.add(s.trim());
        }
        asList2.removeAll(Arrays.asList("", null));
        asList2 = new ArrayList<String>(new LinkedHashSet<String>(asList2));
        return asList2;
    }

    public String cleanName(String n) {

        return n.replaceAll("\\(.*?\\)", " ").trim();
    }

    public List<List<String>> Aut(Model end, String Q) {
        List<List<String>> lista = new ArrayList();

        String consulta = Q;

        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = QueryExecutionFactory.create(consulta, end);

        try {
            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                List<String> nls = new ArrayList<>();

                nls.add(soln.getResource("uri").getURI());
                nls.add(soln.getLiteral("name").getString());
                nls.add(soln.getLiteral("issn").getString());
                nls.add(soln.getLiteral("sub").getString());
                nls.add(soln.getLiteral("ani").getString());
                lista.add(nls);
            }
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            qexec.close();

        }
        return lista;

    }

    public static DatLa getInstance() {
        return DatLaHolder.INSTANCE;
    }

    private static class DatLaHolder {

        private static final DatLa INSTANCE = new DatLa();
    }
}
