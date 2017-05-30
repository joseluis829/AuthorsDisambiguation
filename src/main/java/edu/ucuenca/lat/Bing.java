/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.lat;

import edu.ucuenca.authorsdisambiguation.DatLa;
import edu.ucuenca.authorsdisambiguation.nwd.Cache;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cedia
 */
public class Bing {

    String[] URL_ = {"https://www.bing.com/search?q=", "https://duckduckgo.com/?q="};

    public synchronized boolean check(String q) throws InterruptedException {
        //System.out.println(q);
        DatLa.getInstance().qu=q;
        String[] Http;
        do {
            try {
                String ur = URL_[0];
                Http = Http(ur + URLEncoder.encode(q, "UTF-8"), "Bing");
                //System.out.println(Http);
                break;
            } catch (Exception ex) {
                System.out.println(q);
                Logger.getLogger(Bing.class.getName()).log(Level.SEVERE, null, ex);
                Thread.sleep(1000 * 60);
            }
        } while (true);

        if (Http[1].compareTo("C") != 0) {
            int num = Math.random() < 0.5 ? 1 : 2;
            Thread.sleep(1000 * 5 + num);
        }
        return !Http[0].contains("No se encontraron resultados para") && !Http[0].contains("No results");
    }

    public synchronized String[] Http(String s, String prefix) throws SQLException, IOException {

        String get = Cache.getInstance().get(prefix + s);
        String resp = "";
        String resp2 = "";
        if (get != null) {
            resp2 = "C";
            //System.out.print(".");
            resp = get;
        } else {
            resp2 = "P";
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

        return new String[]{resp, resp2};
    }

}
