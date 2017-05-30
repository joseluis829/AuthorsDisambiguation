/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsdisambiguation;

import edu.ucuenca.authorsdisambiguation.nwd.AD;
import edu.ucuenca.authorsdisambiguation.nwd.CAD;
import edu.ucuenca.authorsdisambiguation.nwd.NWD;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author cedia
 */
public class Experiment {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, Exception {
        
        
        ModifiedJaccard me = new ModifiedJaccard();
        //me.Order=false;
        //System.out.println(me.Distance("PAOLA FERNANDA", "F"));
        //me.Order=true;
        //System.out.println(me.Distance("CAIMINAGUA AJILA", "Cueva"));
        
       // if (true)
       // return;
        
        
        // TODO code application logic here
        Data d = Data.getInstance();
        
        NWD dic = new NWD();
        
        
        
        PrintWriter writer = new PrintWriter("/home/cedia/data.txt", "UTF-8");
        writer.println("Repositorio;URI;Nombre;Repositorio;URI;Nombre;SimilitudNombre;SimilitudApellido;Distancia;FactorCoautores;FactorAfiliacion;Total");

        List<List<String>> c = d.Aut();
        //ModifiedJaccard me = new ModifiedJaccard();
        
        CAD disca = new CAD();
        
        AD disaa = new AD();
        
        for (int i = 0; i < c.size(); i++) {
            for (int j = i + 1; j < c.size(); j++) {

                List<String> get = c.get(i);
                List<String> get1 = c.get(j);

                if (get.get(1).compareTo(get1.get(1)) == 0) {
                    continue;
                }

                me.Order = false;
                double Distance = me.Distance(get.get(2), get1.get(2));
                me.Order = true;
                double Distance1 = me.Distance(get.get(3), get1.get(3));

                double prom = (Distance + Distance1) / 2;

                if (prom>0.9) {
                    String n1=get.get(2)+" "+get.get(3);
                    String n2=get1.get(2)+" "+get1.get(3);
                    
                    
                    double NWD = dic.NWD(get.get(1), get.get(0), get1.get(1), get1.get(0), "");
                    double CAD = disca.CAD(get.get(1), get.get(0), get1.get(1), get1.get(0));
                    double AD = disaa.AD(get.get(1), get.get(0), get1.get(1), get1.get(0));
                    
                    
                    double DisTot = NWD*CAD*AD;
                    
                    
                    String ll= get.get(0)+";"+get.get(1)+";"+get.get(2)+" "+get.get(3)+";"+get1.get(0)+";"+get1.get(1)+";"+get1.get(2)+" "+get1.get(3)+";"+Distance+";"+Distance1+";"+NWD+";"+CAD+";"+AD+";"+DisTot;
                    writer.println(ll);
                    writer.flush();
                    System.out.println(ll);
                    
                    
                }
            }
        }
        
                writer.close();
    }

}
