/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.lat;

import java.util.List;

/**
 *
 * @author cedia
 */
public class Journal {
    
    public String URI;
    public String Name;
    public String ISSN;
    public List<String>  Subtemas;
    public String Ani;

    @Override
    public String toString() {
        return "Journal{" + "URI=" + URI + ", Name=" + Name + ", ISSN=" + ISSN + ", Subtemas=" + Subtemas + ", Ani=" + Ani + '}';
    }
    

    
    
}
