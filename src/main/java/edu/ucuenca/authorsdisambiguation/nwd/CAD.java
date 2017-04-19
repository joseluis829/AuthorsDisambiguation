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
import edu.ucuenca.authorsdisambiguation.ModifiedJaccard;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cedia
 */
public class CAD {

    public double CAD(String uri1, String end1, String uri2, String end2) throws Exception {

        List<List<String>> lsca1 = c(uri1, end1);
        List<List<String>> lsca2 = c(uri2, end2);
        
        ModifiedJaccard me = new ModifiedJaccard();

        for (List<String> a : lsca1) {
            for (List<String> b : lsca2) {
                
                String aN=a.get(0);
                String aA=a.get(1);
                
                String bN=b.get(0);
                String bA=b.get(1);
                
                
                me.Order=false;
                double Distance = me.Distance(aN, bN);
                me.Order=true;
                double Distance1 = me.Distance(aA, bA);
                
                
                if (Distance > 0.5 && Distance1 > 0.5){
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

        String consulta = "select distinct ?fn ?ln  { ?d a <http://purl.org/ontology/bibo/Document> . <" + ent + "> ?r ?d . ?d ?r2 ?p. ?p a <http://xmlns.com/foaf/0.1/Person> . ?p <http://xmlns.com/foaf/0.1/firstName> ?fn .  ?p <http://xmlns.com/foaf/0.1/lastName> ?ln . filter (str(?p) != '"+ent+"') . }";

        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);

        try {
            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                List<String> nls = new ArrayList<>();

                //nls.add(soln.getResource("p").getURI());
                nls.add(soln.getLiteral("fn").getString());
                nls.add(soln.getLiteral("ln").getString());

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
