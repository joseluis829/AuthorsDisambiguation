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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cedia
 */
public class Data {
    public Model AcademicsKnowlodge;
    public Model GoogleScholar;
    public Model Scopus;
    
        public static Data getInstance() {
        return DataHolder.INSTANCE;
    }

    private static class DataHolder {

        private static final Data INSTANCE = new Data();
    }

    
    
    private Data(){
        
        
        AcademicsKnowlodge = ModelFactory.createDefaultModel();
        
        GoogleScholar = ModelFactory.createDefaultModel();
        
        Scopus = ModelFactory.createDefaultModel();
    
        InputStream in1 = FileManager.get().open( "/home/cedia/CLEI/Data/AcademicsKnowlodge.rdf" );
        InputStream in2 = FileManager.get().open( "/home/cedia/CLEI/Data/GoogleScholar.rdf" );
        InputStream in3 = FileManager.get().open( "/home/cedia/CLEI/Data/Scopus.rdf" );
        
        AcademicsKnowlodge.read(in1, null);
        GoogleScholar.read(in2, null);
        Scopus.read(in3, null);
        
    }
    
    
    public List<List<String>> Aut() {
        List<List<String>> lista = new ArrayList();
    
        
        String Q1="select distinct ?r ?p ?fn ?ln { bind ('CEDIA' as ?r) . ?p a <http://xmlns.com/foaf/0.1/Person> . ?p <http://xmlns.com/foaf/0.1/firstName> ?fn .  ?p <http://xmlns.com/foaf/0.1/lastName> ?ln . }";
        
        String Q2="select distinct ?r ?p ?fn ?ln { bind ('UDC' as ?r) . <http://190.15.141.66:8899/ucuenca/coleccion/com_17> ?rr ?d. ?d ?k ?p. ?p a <http://xmlns.com/foaf/0.1/Person> . ?p <http://xmlns.com/foaf/0.1/firstName> ?fn .  ?p <http://xmlns.com/foaf/0.1/lastName> ?ln . filter ( regex(str(?p), 'ucuenca')  ) . }";
        
        
        List<List<String>> Aut = Aut("http://190.15.141.66:8890/myservice/sparql",Q1);
        List<List<String>> Aut1 = Aut("http://190.15.141.102:8891/myservice/sparql",Q2);
        lista.addAll(Aut);
        lista.addAll(Aut1);
    
        String Q3="select distinct ?r ?p ?fn ?ln { bind ('AcademicsKnowlodge' as ?r) . ?p a <http://xmlns.com/foaf/0.1/Person> . ?p <http://xmlns.com/foaf/0.1/firstName> ?fn .  ?p <http://xmlns.com/foaf/0.1/lastName> ?ln . }";
        String Q4="select distinct ?r ?p ?fn ?ln { bind ('GoogleScholar' as ?r) . ?p a <http://xmlns.com/foaf/0.1/Person> . ?p <http://xmlns.com/foaf/0.1/firstName> ?fn .  ?p <http://xmlns.com/foaf/0.1/lastName> ?ln . }";
        String Q5="select distinct ?r ?p ?fn ?ln { bind ('Scopus' as ?r) . ?p a <http://www.elsevier.com/xml/svapi/rdf/dtd/Author> . ?p <http://www.elsevier.com/xml/svapi/rdf/dtd/givenName> ?fn .  ?p <http://www.elsevier.com/xml/svapi/rdf/dtd/surname> ?ln . }";
        
        
        List<List<String>> Aut2 = Aut(AcademicsKnowlodge, Q3);
        List<List<String>> Aut3 = Aut(GoogleScholar, Q4);
        List<List<String>> Aut4 = Aut(Scopus, Q5);
        
        lista.addAll(Aut2);
        lista.addAll(Aut3);
        lista.addAll(Aut4);
        return lista;
    }
    
    
    
    
    
    public List<List<String>> Aut(Model end, String Q) {
        List<List<String>> lista = new ArrayList();
        
        String consulta =Q;
        
        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = QueryExecutionFactory.create(consulta,end);

        try {
            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                String uri = soln.getResource("p").getURI();
                List<String> nls = new ArrayList<>();

                nls.add(soln.getLiteral("r").getString());
                nls.add(soln.getResource("p").getURI());
                nls.add(soln.getLiteral("fn").getString());
                nls.add(soln.getLiteral("ln").getString());

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
    
    
    public List<List<String>> Aut(String end, String Q) {
        List<List<String>> lista = new ArrayList();
        
        String consulta =Q;
        
        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(end, query);

        try {
            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                String uri = soln.getResource("p").getURI();
                List<String> nls = new ArrayList<>();

                nls.add(soln.getLiteral("r").getString());
                nls.add(soln.getResource("p").getURI());
                nls.add(soln.getLiteral("fn").getString());
                nls.add(soln.getLiteral("ln").getString());
                

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

    
    
    
    
    
}
