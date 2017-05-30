/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsdisambiguation.nwd;

import com.google.common.base.Joiner;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import edu.ucuenca.authorsdisambiguation.Data;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

/**
 *
 * @author bibliodigital
 */
public class NWD {

    public NWD() throws IOException, ClassNotFoundException {

    }

    public double NWD_(List<String> kws1, List<String> kws2) throws Exception {

        Map<String, List<String>> map = new HashMap<>();
        List<String> Authors = new ArrayList();
        Authors.add("1");
        Authors.add("2");
        Map<String, Double> Result = new HashMap<>();
        for (int i = 0; i < Authors.size(); i++) {
            for (int j = i + 1; j < Authors.size(); j++) {
                String a1 = Authors.get(i);
                String a2 = Authors.get(j);
                List<String> ka1 = null;
                List<String> ka2 = null;
                if (map.containsKey(a1)) {
                    ka1 = map.get(a1);
                } else {
                    ka1 = kws1;
                    //String t1_ = traductor(Joiner.on(" | ").join(ka1)).toLowerCase();
                    ka1 = traductor(ka1);//new LinkedList<String>(java.util.Arrays.asList(t1_.split("\\s\\|\\s")));
                    ka1 = clean(ka1);
                    //System.out.println(uri1 + "|E:" + Joiner.on(",").join(ka1));
                    ka1 = TopT(ka1, (int) (2.0 * Math.log(ka1.size())));
                    //System.out.println("1" + "|F:" + Joiner.on(",").join(ka1));
                    map.put(a1, ka1);
                }

                if (map.containsKey(a2)) {
                    ka2 = map.get(a2);
                } else {
                    ka2 = kws2;
                    //String t2_ = traductor(Joiner.on(" | ").join(ka2)).toLowerCase();
                    ka2 = traductor(ka2);//new LinkedList<String>(java.util.Arrays.asList(t2_.split("\\s\\|\\s")));
                    ka2 = clean(ka2);
                    //System.out.println(uri2 + "|E:" + Joiner.on(",").join(ka2));
                    ka2 = TopT(ka2, (int) (2.0 * Math.log(ka2.size())));
                    //System.out.println("2" + "|F:" + Joiner.on(",").join(ka2));
                    map.put(a2, ka2);
                }
                //System.out.println(ka1.size() + "," + ka2.size());

                double sum = 0;
                double num = 0;

                for (String t1 : ka1) {
                    for (String t2 : ka2) {
                        num++;
                        String tt1 = t1;
                        String tt2 = t2;
                        double v = NGD(tt1, tt2);
                        System.out.println(tt1+","+tt2+"="+v);
                        sum += v;
                    }
                }
                double prom = sum / num;
                if (num == 0 && sum == 0) {
                    prom = 2;
                }
                Result.put(i + "," + j, prom);
            }
        }

        double r = 0;
        for (Map.Entry<String, Double> cc : Result.entrySet()) {
            r = cc.getValue();
        }
        return r;
    }

    public double NWD(String uri1, String end1, String uri2, String end2, String quy) throws Exception {

        List<String> prms = new ArrayList();
        prms.add(uri1 + "+" + end1);
        prms.add(uri2 + "+" + end2);

        prms.add(quy);

        Collections.sort(prms);

        Double rspc = null;//GetCacheDistance("cacheNWD" + prms.toString());
        if (rspc == null) {
            Map<String, List<String>> map = new HashMap<>();
            List<String> Authors = new ArrayList();
            Authors.add(uri1);
            Authors.add(uri2);
            List<String> Endpoints = new ArrayList();
            Endpoints.add(end1);
            Endpoints.add(end2);
            Map<String, Double> Result = new HashMap<>();
            for (int i = 0; i < Authors.size(); i++) {
                for (int j = i + 1; j < Authors.size(); j++) {
                    String a1 = Authors.get(i);
                    String a2 = Authors.get(j);
                    List<String> ka1 = null;
                    List<String> ka2 = null;
                    if (map.containsKey(a1)) {
                        ka1 = map.get(a1);
                    } else {
                        ka1 = consultado2(a1, Endpoints.get(i));
                        //String t1_ = traductor(Joiner.on(" | ").join(ka1)).toLowerCase();
                        ka1 = traductor(ka1);//new LinkedList<String>(java.util.Arrays.asList(t1_.split("\\s\\|\\s")));
                        ka1 = clean(ka1);
                        //System.out.println(uri1 + "|E:" + Joiner.on(",").join(ka1));
                        ka1 = TopT(ka1, (int) (3.0 * Math.log(ka1.size())));
                        //System.out.println(uri1 + "|F:" + Joiner.on(",").join(ka1));
                        map.put(a1, ka1);
                    }

                    if (map.containsKey(a2)) {
                        ka2 = map.get(a2);
                    } else {
                        ka2 = consultado2(a2, Endpoints.get(j));
                        //String t2_ = traductor(Joiner.on(" | ").join(ka2)).toLowerCase();
                        ka2 = traductor(ka2);//new LinkedList<String>(java.util.Arrays.asList(t2_.split("\\s\\|\\s")));
                        ka2 = clean(ka2);
                        //System.out.println(uri2 + "|E:" + Joiner.on(",").join(ka2));
                        ka2 = TopT(ka2, (int) (3.0 * Math.log(ka2.size())));
                       // System.out.println(uri2 + "|F:" + Joiner.on(",").join(ka2));
                        map.put(a2, ka2);
                    }
                    //System.out.println(ka1.size() + "," + ka2.size());

                    double sum = 0;
                    double num = 0;

                    for (String t1 : ka1) {
                        for (String t2 : ka2) {
                            num++;
                            String tt1 = t1;
                            String tt2 = t2;
                            double v = NGD(tt1, tt2);
                            sum += v;
                        }
                    }
                    double prom = sum / num;
                    if (num == 0 && sum == 0) {
                        prom = 2;
                    }
                    Result.put(i + "," + j, prom);
                }
            }

            double r = 0;
            for (Map.Entry<String, Double> cc : Result.entrySet()) {
                r = cc.getValue();
            }
            rspc = r;
            PutCacheDistance("cacheNWD" + prms.toString(), rspc);
        }
        return rspc;
    }

    public List<String> TopT(List<String> m, int n) throws IOException, SQLException {
        n = (n <= 0) ? 1 : n;
        if (m.size() == 1) {
            m.add(m.get(0));
        }
        if (Cache.getInstance().config.get("stochastic").getAsBoolean().value()) {
            Collections.shuffle(m);
            if (2 * n < m.size()) {
                m = m.subList(0, 2 * n);
            }
        }
        Map<String, Double> Mapa = new HashMap();
        for (int i = 0; i < m.size(); i++) {
            for (int j = i + 1; j < m.size(); j++) {
                double v = NGD(m.get(i), m.get(j));
                //System.out.println(m.get(i) + "," + m.get(j) + "=" + v);

                if (Mapa.containsKey(m.get(i))) {
                    Mapa.put(m.get(i), Mapa.get(m.get(i)) + v);
                } else {
                    Mapa.put(m.get(i), v);
                }

                if (Mapa.containsKey(m.get(j))) {
                    Mapa.put(m.get(j), Mapa.get(m.get(j)) + v);
                } else {
                    Mapa.put(m.get(j), v);
                }
            }
        }
        Map<String, Double> sortByValue = sortByValue(Mapa);
        List<String> ls = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList(sortByValue.keySet());
        ArrayList<Double> arrayList2 = new ArrayList(sortByValue.values());
        for (int i = 0; i < n; i++) {
            if (i < sortByValue.size()) {
                ls.add(arrayList.get(i));
            }
        }
        return ls;
    }

    public List<String> consultado2(String ent, String end) {
        List<String> lista = new ArrayList();
        /*
         String consulta2 = " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                          + "PREFIX esdbpr: <http://es.dbpedia.org/resource/> "+ 
                           " PREFIX owl: <http://dbpedia.org/ontology/> "+
        "SELECT ?person WHERE{ "+
        "?person  rdf:type              owl:Scientist . "+
        "?person  owl:country  esdbpr:Ecuador.  }"; */

        String entidad = ent;
        String endpoint = end;

        String consulta = Cache.getInstance().config.get("contextQuery").getAsString().value().replaceAll("\\|\\?\\|", entidad);

        String consulta1 = "select distinct ?d where { { ?doc <http://purl.org/dc/terms/contributor> <|?|> . ?doc <http://prismstandard.org/namespaces/basic/2.0/keyword> ?d . } union { ?doc <http://purl.org/dc/terms/contributor> <|?|> . ?doc <http://purl.org/dc/elements/1.1/subject> ?su . ?su <http://www.w3.org/2004/02/skos/core#prefLabel> ?d .} union { ?doc <http://purl.org/dc/terms/contributor> <|?|> . ?doc <http://purl.org/dc/elements/1.1/subject> ?su . ?su <http://www.w3.org/2004/02/skos/core#altLabel> ?d . } }".replaceAll("\\|\\?\\|", entidad);
        
        String consulta2 = "select distinct ?d where { <|?|> <http://purl.org/dc/terms/subject> ?d . }".replaceAll("\\|\\?\\|", entidad);
        
        String consulta3 = "select distinct ?d where { { ?doc <http://purl.org/dc/elements/1.1/creator> <|?|>. ?doc <http://purl.org/ontology/bibo/Quote> ?d . } union { ?doc <http://purl.org/dc/elements/1.1/creator> <|?|>. ?doc <http://purl.org/dc/terms/subject> ?d . } union { ?doc <http://purl.org/dc/terms/contributor> <|?|>. ?doc <http://purl.org/ontology/bibo/Quote> ?d . } union { ?doc <http://purl.org/dc/terms/contributor> <|?|>. ?doc <http://purl.org/dc/terms/subject> ?d . } }".replaceAll("\\|\\?\\|", entidad);
        
        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = null;
        if (end.compareTo("CEDIA")==0){
            qexec = QueryExecutionFactory.sparqlService("http://190.15.141.66:8890/myservice/sparql", query);
        }
        if (end.compareTo("UDC")==0){
            qexec = QueryExecutionFactory.sparqlService("http://190.15.141.102:8891/myservice/sparql", query);
        }
        Data instance = Data.getInstance();
        if (end.compareTo("AcademicsKnowlodge")==0){
            qexec = QueryExecutionFactory.create(consulta3,instance.AcademicsKnowlodge);
        }
        if (end.compareTo("GoogleScholar")==0){
            qexec = QueryExecutionFactory.create(consulta2,instance.GoogleScholar);
        }
        if (end.compareTo("Scopus")==0){
            qexec = QueryExecutionFactory.create(consulta1,instance.Scopus);
        }
        
        
        try {
            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                //System.out.println(soln.getLiteral("d").getString());
                lista.add(soln.getLiteral("d").getString().replace("\"", " ").replace("\'", " ").trim());
                // System.out.println ( "Val "+soln.getResource("d").getLocalName());

            }

            return lista;
// ResultSet results = qexec.execSelect();
//QuerySolution solucion = results.nextSolution();
//ResultSetFormatter.out(System.out, results);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            qexec.close();

        }
        return lista;
    }

    public double NGD(String a, String b) throws IOException, SQLException {

        a = a.trim();
        b = b.trim();

        if (a.compareToIgnoreCase(b) == 0) {
            return 0;
        }

        //double n0 = getResultsCount(""+a+"");
        //double n1 = getResultsCount(""+b+"");
        //String c = ""+a+" "+b+"";
        String _a = "\"" + a + "\"~10";
        String _b = "\"" + b + "\"~10";
        String c = "\"" + a + " " + b + "\"~50";
        if (Cache.getInstance().config.get("relaxMode").getAsBoolean().value()) {
            _a = "" + a;
            _b = "" + b;
            c = a + " " + b;
        }

        double n0 = getResultsCount(_a);
        double n1 = getResultsCount(_b);
        double n2 = 0;

        if (n0 == 0 || n1 == 0) {
            n2 = 0;
        } else {
            n2 = getResultsCount(c);
        }

        //double m = 5026040.0 * 590;
        double m = getResultsCount("the");

        double distance = 0;

        int Measure = 0;

        double l1 = Math.max(Math.log10(n0), Math.log10(n1)) - Math.log10(n2);
        double l2 = Math.log10(m) - Math.min(Math.log10(n0), Math.log10(n1));

        if (Measure == 0) {
            distance = l1 / l2;
        }

        if (Measure == 1) {
            distance = 1 - (Math.log10(n2) / Math.log10(n0 + n1 - n2));
        }

        if (n0 == 0 || n1 == 0 || n2 == 0) {
            distance = 1;
        }

        //System.out.println("n0="+n0);
        //System.out.println("n1="+n1);
        //System.out.println("n2="+n2);
        //System.out.println(a + "," + b + "=" + distance2);
        return distance;
    }

    private double getResultsCount(String query) throws IOException, SQLException {

        double c = 0;
        c = getResultsCount1(query);
        return c;
    }

    private double getResultsCount1(final String query) throws IOException, SQLException {

        String url = "https://en.wikipedia.org/w/api.php?action=query&list=search&format=json&srsearch=" + URLEncoder.encode(query, "UTF-8");
        String s = Http(url, "wikipedia+");
        
        double v = 0;

        try {
            JsonObject parse = JSON.parse(s);
            v = parse.get("query").getAsObject().get("searchinfo").getAsObject().get("totalhits").getAsNumber().value().doubleValue();
        } catch (Exception e) {
            //System.out.println(query + s);
        }
        return v;
    }

    public List<String> clean(List<String> ls) {
        List<String> al = ls;
        Set<String> hs = new HashSet<>();
        hs.addAll(al);
        al.clear();
        al.addAll(hs);

        JsonArray asArray = Cache.getInstance().config.get("stopwords").getAsArray();

        for (JsonValue s : asArray) {
            al.remove(s.getAsString().value());
        }

        return al;
    }

    public <K, V extends Comparable<? super V>> Map<K, V>
            sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list
                = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public synchronized Double GetCacheDistance(String s) throws SQLException {

        Double resp = null;
        try {
            String get = Cache.getInstance().get(s);
            resp = Double.parseDouble(get);
        } catch (Exception e) {
        }
        return resp;
    }

    public synchronized void PutCacheDistance(String s, double d) {
        Cache.getInstance().put(s, d + "");
    }

    public synchronized String Http(String s, String prefix) throws SQLException, IOException {

        String get = Cache.getInstance().get(prefix + s);
        String resp = "";
        if (get != null) {
            //System.out.print(".");
            resp = get;
        } else {
            final URL url = new URL(s);
            final URLConnection connection = url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:44.0) Gecko/20100101 Firefox/44.0");
            connection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8");
            while (reader.hasNextLine()) {
                final String line = reader.nextLine();
                resp += line + "\n";
            }
            reader.close();

            Cache.getInstance().put(prefix + s, resp);
        }

        return resp;
    }

    public synchronized String Http2(String s, Map<String, String> mp, String prefix) throws SQLException, IOException {
        String md = s + mp.toString();
        String get = Cache.getInstance().get(prefix + md);
        String resp = "";
        if (get != null) {
            resp = get;
        } else {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(s);
            method.getParams().setContentCharset("utf-8");

            //Add any parameter if u want to send it with Post req.
            for (Entry<String, String> mcc : mp.entrySet()) {
                method.addParameter(mcc.getKey(), mcc.getValue());
            }

            int statusCode = client.executeMethod(method);

            if (statusCode != -1) {
                InputStream in = method.getResponseBodyAsStream();
                final Scanner reader = new Scanner(in, "UTF-8");
                while (reader.hasNextLine()) {
                    final String line = reader.nextLine();
                    resp += line + "\n";
                }
                reader.close();
                Cache.getInstance().put(prefix + md, resp);

            }

        }

        return resp;
    }

    public String traductorYandex(String palabras) throws UnsupportedEncodingException, SQLException, IOException {
        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate";

        //String url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20160321T160516Z.43cfb95e23a69315.6c0a2ae19f56388c134615f4740fbb1d400f15d3&lang=en&text=" + URLEncoder.encode(palabras, "UTF-8");
        Map<String, String> mp = new HashMap<>();
        mp.put("key", "trnsl.1.1.20160321T160516Z.43cfb95e23a69315.6c0a2ae19f56388c134615f4740fbb1d400f15d3");
        mp.put("lang", "en");
        mp.put("text", palabras);
        mp.put("options", "1");

        String rs = "";
        try {
            String Http = Http2(url, mp, "yandex+");
            rs = Http;
            String res = Http;
            JsonObject parse = JSON.parse(res).getAsObject();
            JsonArray asArray = parse.get("text").getAsArray();
            res = asArray.get(0).getAsString().value();
            palabras = res;
        } catch (Exception e) {
            System.out.println(palabras + rs);
            e.printStackTrace(new PrintStream(System.out));
        }
        return palabras;
    }

    public List<String> traductor(List<String> join) throws SQLException, IOException {
        List<String> ls = new ArrayList();
        for (String w : join) {
            if (true) {

                String con = "contexto, " + w;
                String toLowerCase = traductorYandex(con.trim()).trim();

                if (toLowerCase.startsWith("context, ")) {
                    toLowerCase = toLowerCase.replaceFirst("context, ", "");
                } else if (toLowerCase.startsWith("contexto, ")) {
                    toLowerCase = toLowerCase.replaceFirst("contexto, ", "");
                }

                ls.add(toLowerCase);
            } else {
                //ls.add(traductorBing(w.trim()).trim().toLowerCase());
            }
        }
        return ls;
    }

}
