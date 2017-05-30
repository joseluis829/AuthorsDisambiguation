/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsdisambiguation;

import edu.ucuenca.authorsdisambiguation.nwd.NWD;
import edu.ucuenca.lat.Bing;
import edu.ucuenca.lat.Journal;
import edu.ucuenca.lat.Publication;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cedia
 */
public class ExpLatIndex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedEncodingException, SQLException, InterruptedException {

        Bing aa = new Bing();

        //aa.check("\"Validación de un test de ureasa para diagnóstico de helicobacter pylori en comparación con el clotest y en referencia a la histología\" \"Non commercial ureasa tests are used to detect\n"
        //   + "Helicobacter pylori. They\" (\"0325-5611\" OR \"Revista de la Facultad de Ciencias Médicas de la Plata\")");
        //   if (true) {
        //      return;
        //  }
        //boolean check = aa.check("Cuenca");
        //System.out.println(check);
        //check = aa.check("Cue434234551rf4f5ssdfnca");
        //System.out.println(check);
        // if (true)
        //   return;
        // TODO code application logic here
        List<Journal> Aut = DatLa.getInstance().Aut();
        //System.out.println(Aut.toString().contains("0328-6312"));

        List<Publication> Pub1 = DatLa.getInstance().PubliWOJ();
        // Publication p = Pub1.get(0);
        //NWD dis = new NWD();
        // List<String> traductor = dis.traductor(p.kwy);
        //  List<String> med = new ArrayList<>();
        //  med.add("Medicina");
        //  List<String> traductor2 = dis.traductor(med);
        //   double sum =0.0;
        //   double n=0.0;
        //  for (String d : traductor) {
        //      for (String dd : traductor2) {
        //         n=n+1.0;
        //         double NGDO = dis.NGD(d, dd);
        //        double NGD = 1.0/(NGDO*NGDO);
        //        sum+= NGD;
        //        System.out.println(d+"/"+dd+"="+NGD);
        //  //    }
        // }
        // double ss=n/sum;
        //  System.out.println(ss*ss);
        //System.out.println(Pub1);
        for (Publication p : Pub1) {
           addCand(p, Aut, aa);
        }
       // List<Publication> Pub2 = DatLa.getInstance().PubliWJ(Aut);
        //System.out.println(Pub2.size());
        //List<Publication> PubT = new ArrayList<>();
        //PubT.addAll(Pub2);
        // PubT.addAll(Pub1);
       // for (Publication p : Pub2) {
        //    validate(p, aa);
       // }
    }

    public static String getFirstNStrings(String str, int n) {
        String[] sArr = str.split(" ");
        String firstStrs = "";
        for (int i = 0; i < n && i < sArr.length; i++) {
            firstStrs += sArr[i] + " ";
        }
        return firstStrs.trim();
    }

    public static int validate(Publication p, Journal j, Bing bus) throws InterruptedException {

        System.out.print(p.title + "|" + j.Name + "|" + j.ISSN + "|");

        String Query = "\"" + getFirstNStrings(p.title, 10) + "\"";
        String Query1 = Query;
        if (p.abstrat_ != null && !p.abstrat_.trim().isEmpty()) {
            Query += " \"" + getFirstNStrings(p.abstrat_.replace("\n", " "), 10) + "\"";
        } else {
            if (p.kwy != null) {
                int i = 0;
                for (String kw : p.kwy) {
                    if (kw != null && !kw.trim().isEmpty()) {
                        if (i < 3) {
                            Query += " \"" + kw + "\"";
                        }
                        i++;
                    }
                }
            }
        }
        //Query += " (";
        String Q1 = Query + " \"" + j.ISSN + "\"";
        String Q1_ = Query1 + " \"" + j.ISSN + "\"";
        // Query += " OR ";
        String Q2 = Query + " \"" + j.Name + "\"";
        String Q2_ = Query1 + " \"" + j.Name + "\"";
        // Query += ")";

        boolean ISSNV= j.ISSN.trim().compareTo("-")!=0;
        
        String St = "";
        boolean k = false;
        if (ISSNV && bus.check(Q1_)) {
            if (bus.check(Q1)) {
                St = "ISSN+";
                k = true;
            } else {
                St = "ISSN";
            }
        } else {
            if (bus.check(Q2_)) {
                if (bus.check(Q2)) {
                    St = "Name+";
                } else {
                    St = "Name";
                }
            } else {
                St = "None";
            }
        }
        System.out.println(St);

        return k ? 0 : 1;
    }

    public static void validate(Publication p, Bing bus) throws InterruptedException {
        for (Journal j : p.Candidatos) {
            System.out.print(p.title + "|" + p.journal + "|" + j.Name + "|" + j.ISSN + "|");
            //    if(true)
            //  continue;
            String Query = "\"" + getFirstNStrings(p.title, 10) + "\"";

            String Query1 = Query;

            if (p.abstrat_ != null && !p.abstrat_.trim().isEmpty()) {
                Query += " \"" + getFirstNStrings(p.abstrat_.replace("\n", " "), 10) + "\"";
            } else {
                if (p.kwy != null) {
                    int i = 0;
                    for (String kw : p.kwy) {
                        if (kw != null && !kw.trim().isEmpty()) {
                            if (i < 3) {
                                Query += " \"" + kw + "\"";
                            }
                            i++;
                        }
                    }
                }
            }
            //Query += " (";
            String Q1 = Query + " \"" + j.ISSN + "\"";
            String Q1_ = Query1 + " \"" + j.ISSN + "\"";
            // Query += " OR ";
            String Q2 = Query + " \"" + j.Name + "\"";
            String Q2_ = Query1 + " \"" + j.Name + "\"";
            // Query += ")";

            String St = "";
            if (bus.check(Q1_)) {
                if (bus.check(Q1)) {
                    St = "ISSN+";
                } else {
                    St = "ISSN";
                }
            } else {
                if (bus.check(Q2_)) {
                    if (bus.check(Q2)) {
                        St = "Name+";
                    } else {
                        St = "Name";
                    }
                } else {
                    St = "None";
                }
            }
            System.out.println(St);
        }
    }

    public static void addCand(Publication p, List<Journal> Aut, Bing bus) throws IOException, ClassNotFoundException, UnsupportedEncodingException, SQLException, InterruptedException {

        NWD d = new NWD();

        p.Candidatos = new ArrayList<>();

        int con = 0;
        for (Journal j : Aut) {
            con++;
            double sum = 0.0;
            double n = 0.0;
            if (!DatLa.getInstance().filtA(p.ani, j.Ani)) {
                continue;
            }
            List<String> traductor = d.traductor(j.Subtemas);
            List<String> traductor1 = d.traductor(p.kwy);

            for (String s1 : traductor) {
                for (String s2 : traductor1) {
                    double NGD = d.NGD(s1, s2);
                    double NGDC = NGD * NGD;
                    sum += 1 / NGDC;
                    n = n + 1;
                }
            }
            double med = n / sum;
            double medc = med * med;

            if (medc <= 0.75) {
                p.Candidatos.add(j);

                int validate = validate(p, j, bus);
                if (validate == 0) {
                    break;

                }

            }

        }

    }

}
