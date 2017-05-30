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
public class Publication {
    
    public String title;
    public String journal;
    public String abstrat_;
    public String ani;
    
    public List <String> kwy;
    
    public List <Journal> Candidatos;
    
    public Journal Ok;

    @Override
    public String toString() {
        return "Publication{" + "title=" + title + ", journal=" + journal + ", abstrat_=" + abstrat_ + ", ani=" + ani + ", kwy=" + kwy + ", Candidatos=" + Candidatos + ", Ok=" + Ok + '}';
    }

    
    
    
    
    
}
