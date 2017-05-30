/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucuenca.authorsdisambiguation;

import edu.ucuenca.authorsdisambiguation.nwd.NWD;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cedia
 */
public class SchoolarExp {

    public static void main(String[] args) throws ClassNotFoundException, Exception {

        System.out.println("ME Mejía".replaceAll("(\\p{Lu})(\\p{Lu})","$1 $2"));
        
        
       // if (true)
        //    return;
        
        JSONObject a = new JSONObject();

        a.append("Author", "M Espinoza");
        a.append("Author", "E Mena");
        a.append("Author", "A Gómez-Pérez");

        a.accumulate("Title", "Enriching an ontology with multilingual information");

        a.append("Abstract", "El objetivo final de un sistema de recomendación es satisfacer las necesidades de información de un usuario. En el caso de la TV digital los sistemas de recomendación han mostrado ser una excelente alternativa para hacer frente a la sobrecarga de información y sugerir la selección de los programas más interesantes para ver. Sin embargo, para que esto ocurra se requiere que estos sistemas consideren en su diseño los intereses y preferencias del usuario. Con el fin de proporcionar un enfoque más robusto para la modelación del perfil de un usuario, se propone el uso de tecnologías de laWeb semántica junto con laWeb social. La idea de juntar ambos campos es convertir las declaraciones implícitas de las preferencias televisivas de un usuario en la Web social, a expresiones de la forma sujeto-predicado- objeto (conocidas en términos semánticos como tripletes). Esta combinación permitirá añadir significado a los datos capturados con el fin de que el perfil pueda ser aprovechado de forma sencilla por una máquina. El sistema propuesto confía en un grupo de ontologías de diferentes dominios y se basa en una arquitectura genérica que permite capturar manipular, y serializar el perfil de un usuario de TV digital.");
        a.append("Abstract", "The final goal of a recommender system is to satisfy the user’s information needs. In the case of digital TV, the recommender systems have proven to be an excellent alternative to address the information overload and facilitate the selection of the most interesting programs to watch. However, for this to occur it is required that these systems consider at their design the user interests and preferences. In order to provide a more robust approach for modeling the user profile, we propose the use of the Semantic Web technologies together with the Social Web. The idea of merge both fields is to convert the implicit declarations of the user’s television preferences on the social Web, to expressions of the shape: subject-predicate-object (known in semantic terms as triplets). This combination will add meaning to the captured data so that the profile can easily be exploited by a machine. The proposed system relies on a set of ontologies for different domains and a generic architecture to capture, manipulate, and serialize the profile of a digital TV user.");

        a.append("Subject", "Semantic Web");
        a.append("Subject", "Semantic Web");
        a.append("Subject", "Social Web");
        a.append("Subject", "Ontologies");
        a.append("Subject", "Recommendation Systems");
        a.append("Subject", "User profile");

        JSONObject b = new JSONObject();

        b.append("Author", "ME Mejía");
        b.append("Author", "V Saquicela");
        b.append("Author", "KP Baus");

        b.accumulate("Title", "Extracción de preferencias televisivas desde los perfiles de redes sociales");

        b.append("Abstract", "Organizations working in a multilingual environment demand multilingual ontologies. To solve this problem we propose LabelTranslator, a system that automatically localizes ontologies. Ontology localization consists of adapting an ontology to a concrete language and cultural community.\n"
                + "\n"
                + "LabelTranslator takes as input an ontology whose labels are described in a source natural language and obtains the most probable translation into a target natural language of each ontology label. Our main contribution is the automatization of this process which reduces human efforts to localize an ontology manually. First, our system uses a translation service which obtains automatic translations of each ontology label (name of an ontology term) from/into English, German, or Spanish by consulting different linguistic resources such as lexical databases, bilingual dictionaries, and terminologies. Second, a ranking method is used to sort each ontology label according to similarity with its lexical and semantic context.\n"
                + "\n"
                + "The experiments performed in order to evaluate the quality of translation show that our approach is a good approximation to automatically enrich an ontology with multilingual information.");

        b.append("Subject", "Ontology localization");
        b.append("Subject", "Multilingual ontologies");

        JSONObject c = new JSONObject();

        c.append("Author", "Vinko Tomicic F");
        c.append("Author", "Mauricio Espinoza R");
        c.append("Author", "Javier Torres M");
        c.append("Author", "Juan Abarca Z");
        c.append("Author", "José Miguel Montes S");
        c.append("Author", "Mario Luppi N");

        c.accumulate("Title", "Addition of an arterio-venous shunt during veno-arterial extracorporeal life support in a patient with Hantavirus pulmonary syndrome");

        c.append("Abstract", "A subgroup of patients infected with the Hantavirus develops a pulmonary syndrome (HPS) characterized by severe acute respiratory failure and myocardial depression, that has a high mortality rate. Extracorporeal life support (ECLS) could be a valuable therapeutic tool in such patients. We report a 24 years old male with HPS that was successfully managed when an arterio-venous shunt was added to a conventional veno-arterial ECLS technique. Precise criteria have been developed to predict which patients should be considered for this treatment. ");

        c.append("Subject", "Extracorporeal circulation");
        c.append("Subject", " Hantavirus; hantavirus pulmonary syndrome");

        JSONObject list = new JSONObject();

        list.append("publication", a);
        list.append("publication", b);
        list.append("publication", c);

        ///remover duplicados
        JSONArray jsonArray = list.getJSONArray("publication");
        for (int i = 0; i < jsonArray.length(); i++) {
            for (int j = i + 1; j < jsonArray.length(); j++) {

                JSONObject pa = (JSONObject) jsonArray.get(i);
                JSONObject pb = (JSONObject) jsonArray.get(j);

                JSONArray jsonArraya = pa.getJSONArray("Author");
                JSONArray jsonArrayb = pb.getJSONArray("Author");
                
                JSONArray jsonArraysa = pa.getJSONArray("Subject");
                JSONArray jsonArraysb = pb.getJSONArray("Subject");
                
                
                
                double AuthorDistance = AuthorDistance(jsonArraya, jsonArrayb);
                
                
                double SemticDistance = SemanticDistance(jsonArraysa, jsonArraysb);
                
                System.out.println(pa.get("Title")+", "+pb.get("Title")+", "+AuthorDistance+","+SemticDistance);

            }
        }

        //System.out.println(list);

    }

    public static double SemanticDistance(JSONArray a, JSONArray b) throws IOException, ClassNotFoundException, Exception {
    
        List <String> la = new ArrayList<>();
        for (int i=0; i<a.length(); i++) {
            la.add(a.getString(i));
        }
        
        List <String> lb = new ArrayList<>();
        for (int i=0; i<b.length(); i++) {
            lb.add(b.getString(i));
        }
        
        NWD d = new NWD();
        return d.NWD_(la, lb);
    }
    
    
    public static double AuthorDistance(JSONArray a, JSONArray b) {

        ModifiedJaccard disJaccard = new ModifiedJaccard();
        
        List<Integer> usedA = new ArrayList();
        List<Integer> usedB = new ArrayList();
        
        double cont=0;
        double cont2=0;
        
        for (int i = 0; i < a.length(); i++) {
            for (int j = 0; j < b.length(); j++) {

                if (!usedA.contains(i) && !usedB.contains(j)) {

                    String na = a.getString(i);
                    String nb = b.getString(j);
                    //Limpiar nombres
                    disJaccard.Order=true;
                    double Distance = disJaccard.Distance(na, nb);
                    
                    if (Distance>0.5){
                        cont+=1.0;
                        cont2+=Distance;
                        usedA.add(i);
                        usedB.add(j);
                    }

                }

            }
        }
        
        double nat = a.length()-cont;
        double nbt = b.length()-cont;

        double ntt = Math.min(nat, nbt);
        
        return cont2 / (ntt + cont);
    }

}
