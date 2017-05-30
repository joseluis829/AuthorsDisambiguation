/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsdisambiguation.nwd;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import edu.ucuenca.authorsdisambiguation.Data;
import edu.ucuenca.authorsdisambiguation.ModifiedJaccard;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cedia
 */
public class AD {

    public double AD(String uri1, String end1, String uri2, String end2) throws Exception {

        List<List<String>> lsca1 = c(uri1, end1);
        List<List<String>> lsca2 = c(uri2, end2);

        for (List<String> a : lsca1) {
            for (List<String> b : lsca2) {

                if (a.get(0).compareTo(b.get(0)) == 0) {
                    return 0.9;
                }

            }
        }

        return 1;

    }

    public List<List<String>> c(String ent, String end) {
        List<List<String>> lista = new ArrayList();

        String entidad = ent;
        String endpoint = end;

        String consulta = "select distinct ?a { bind ('" + end + "' as ?a). ?s ?p ?o . }";

        String consulta1 = "select distinct ?a { <" + ent + "> <http://www.elsevier.com/xml/svapi/rdf/dtd/affiliation> ?a } ";

        String consulta2 = "select distinct ?a { <" + ent + "> <http://www.elsevier.com/xml/svapi/rdf/dtd/affiliation> ?a }";

        String consulta3 = "select distinct ?a { <" + ent + "> <http://www.elsevier.com/xml/svapi/rdf/dtd/affiliation> ?a }";

        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = null;
        if (end.compareTo("CEDIA") == 0) {
            qexec = QueryExecutionFactory.sparqlService("http://190.15.141.66:8890/myservice/sparql", query);
        }
        if (end.compareTo("UDC") == 0) {
            qexec = QueryExecutionFactory.sparqlService("http://190.15.141.102:8891/myservice/sparql", query);
        }
        Data instance = Data.getInstance();
        if (end.compareTo("AcademicsKnowlodge") == 0) {
            qexec = QueryExecutionFactory.create(consulta3, instance.AcademicsKnowlodge);
        }
        if (end.compareTo("GoogleScholar") == 0) {
            qexec = QueryExecutionFactory.create(consulta2, instance.GoogleScholar);
        }
        if (end.compareTo("Scopus") == 0) {
            qexec = QueryExecutionFactory.create(consulta1, instance.Scopus);
        }

        try {
            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                List<String> nls = new ArrayList<>();

                //nls.add(soln.getResource("p").getURI());
                nls.add(soln.getLiteral("a").getString());

                lista.add(nls);

            }

            return lista;
        } catch (Exception e) {
            System.out.println("Verificar consulta, no existen datos para mostrar");
            e.printStackTrace();
        } finally {
            qexec.close();

        }
        return lista;
    }
}
