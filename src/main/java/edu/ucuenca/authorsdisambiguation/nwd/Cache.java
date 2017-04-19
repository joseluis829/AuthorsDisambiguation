/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsdisambiguation.nwd;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerCompressionWrapper;

/**
 *
 * @author cedia
 */
public class Cache {

    DB db = null;
    HTreeMap<String, String> create = null;
    List<String> BlackList = new ArrayList();
    JsonObject config = null;
    
    public String prefix="";

    private Cache() {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/config.cnf");
        String theString = null;
        try {
            theString = IOUtils.toString(resourceAsStream, Charset.defaultCharset().toString());
        } catch (IOException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        }
        config = JSON.parse(theString).getAsObject();
        db = DBMaker.fileDB(config.get("CacheFile").getAsString().value()).make();
        create = db.hashMap("cache", Serializer.STRING, new SerializerCompressionWrapper(Serializer.STRING)).expireAfterCreate(30, TimeUnit.DAYS).createOrOpen();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("Killing ");
                    Kill();
                } catch (SQLException ex) {
                    Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        ));
    }

    public void put(String key, String value) {
        create.put(prefix+key, value);
    }

    public void Kill() throws SQLException {
        create.close();
        db.close();
    }

    public String get(String key) {
        String get = create.get(prefix+key);
        return get;
    }

    public static Cache getInstance() {
        return CacheHolder.INSTANCE;
    }

    private static class CacheHolder {

        private static final Cache INSTANCE = new Cache();
    }

    public String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
