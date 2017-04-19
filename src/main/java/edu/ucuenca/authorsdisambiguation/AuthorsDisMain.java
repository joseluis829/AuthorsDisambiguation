/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsdisambiguation;

import edu.ucuenca.authorsdisambiguation.nwd.CAD;
import edu.ucuenca.authorsdisambiguation.nwd.Cache;
import edu.ucuenca.authorsdisambiguation.nwd.NWD;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author José Luis Cullcay
 */

public class AuthorsDisMain {
    
    private static int lowerLimitAuthor = 4;
    private static int lowerLimitPub = 5;
    private static int upperLimitKey = 10;
    
    private static String uri_provenance = "http://ucuenca.edu.ec/wkhuska/endpoint/4d0ebfe0bc494647139f10dfe308551f";
     
    //private static final KeywordGenerator keywordGenerator = new KeywordGenerator();
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, Exception {
        
        //Cache.getInstance().prefix=UUID.randomUUID().toString()+"_";
        
        
        //NWD d2 = new NWD();
        
        //double NWD = d2.NWD("http://190.15.141.66:8899/cedia/contribuyente/GONZALEZ__PATRICIA", "http://190.15.141.66:8890/myservice/sparql", "http://190.15.141.66:8899/cedia/contribuyente/GONZALEZ__PATRICIA_DRA", "http://190.15.141.66:8890/myservice/sparql", "prueba");
        //String traductorYandex = d2.traductorYandex("telesalud");
        //System.out.println(NWD);
        
        
        //if (true)
          // return;
        
        PrintWriter writer = new PrintWriter("/home/cedia/data.txt", "UTF-8");
        
        
        
        ModifiedJaccard me = new ModifiedJaccard();
        me.Order=false;
        System.out.println(me.Distance("Victor Hugo", "Hugo"));
        me.Order=true;
        System.out.println(me.Distance("Espinoza Mejía", "Espinosa Mejía"));
        
        //System.out.println(me.Sim("Josse","Josselin"));
        //System.out.println(me.Sim("Mejía","Mejia"));

        //if (true)
            //return;
        
        
        Distance d = new Distance();
        
        CAD disca = new CAD();
        
        NWD dic = new NWD();
        
        List<List<String>> c = d.c("", "http://190.15.141.102:8891/myservice/sparql");
        
        writer.println("Repositorio;URI;Nombre;Repositorio;URI;Nombre;SimilitudNombre;SimilitudApellido;Distancia;FactorCoautores;FactorAfiliacion;Total");
        
        
        for (int i=0; i< c.size(); i++){
            for (int j=i+1; j< c.size(); j++){
            
                List<String> get = c.get(i);
                List<String> get1 = c.get(j);
                
                
                if (get.get(0).compareTo(get1.get(0)) == 0){
                    continue;
                }
                
                me.Order=false;
                double Distance = me.Distance(get.get(1), get1.get(1));
                me.Order=true;
                double Distance1 = me.Distance(get.get(2), get1.get(2));
                
                
                double prom = (Distance+Distance1)/2;
                
                
                if (Distance > 0.5 && Distance1 > 0.5){
                    
                    String ep1 =get.get(3).compareTo("UDC") ==0 ? "http://190.15.141.102:8891/myservice/sparql" : "http://190.15.141.66:8890/myservice/sparql";
                    String ep2 =get1.get(3).compareTo("UDC") ==0 ? "http://190.15.141.102:8891/myservice/sparql" : "http://190.15.141.66:8890/myservice/sparql";
                    
                    double AF =1;
                    if (get.get(3).compareTo(get1.get(3))==0){
                        AF=0.9;
                    }
                    
                    double NWD = dic.NWD(get.get(0), ep1, get1.get(0), ep2, "");
                    double CAD = disca.CAD(get.get(0), ep1, get1.get(0), ep2);
                    
                    
                    double DisTot=NWD*CAD*AF;
                    
                    
                    String ll= get.get(3)+";"+get.get(0)+";"+get.get(1)+" "+get.get(2)+";"+get1.get(3)+";"+get1.get(0)+";"+get1.get(1)+" "+get1.get(2)+";"+Distance+";"+Distance1+";"+NWD+";"+CAD+";"+AF+";"+DisTot;
                    writer.println(ll);
                    writer.flush();
                    System.out.println(ll);
                    
                    
                    
                    System.out.println(""+NWD);
                }
                
            }
        }
        
        writer.close();
        
        if(true)
        return ;
        

        List<String> lista1 = new ArrayList<>();//keywordGenerator.getKeywords("Facultad de medicina, Investigación científica, Universidad de Guayaquil","");
        lista1.add("medicine");
        lista1.add("doctor");
        
        List<String> lista2 = new ArrayList<>();//keywordGenerator.getKeywords("Enhanced in vitro antitumor activity of a titanocene complex encapsulated into Polycaprolactone (PCL) electrospun fibers" + "Astereaceae, Baccharis latifolia, Limonene, Trichophyton mentagrophytes, Trichophyton rubrum", "");//"Astereaceae, Baccharis latifolia, Limonene, Trichophyton mentagrophytes, Trichophyton rubrum"
        lista2.add("cure");
        lista2.add("cancer");
        //Remove elements when there are more than 13
        for (int i = lista2.size() - 1; i > 13; i--) {
            lista2.remove(i);
        }
        Distance di = new Distance();
        Double NWD1 = di.NWD__(lista1, lista2);
        System.out.println(lista1);
        System.out.println(lista2);
        System.out.println("Distance: " + NWD1.toString());
        System.out.println("Jaccard Distance: " + di.jaccardSimilarity("Victor Hugo Saquicela Galarza", "V. Saquicela"));
        System.out.println("Jaccard Distance: " + di.jaccardSimilarity("Victor Hugo Saquicela Galarza", "Víctor Saquicela"));
        
        return;
        
        
}
    
    

}
