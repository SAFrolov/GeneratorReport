/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generatorreportnew;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author safrolov
 */
public class GeneratorReportNew {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        // TODO code application logic here
        String SettingPar="/Users/safrolov/Documents/JavaProgramming/NewLifeJavaProgramming/GeneratorReportNew/settings.xml";
        String source = "/Users/safrolov/Documents/JavaProgramming/NewLifeJavaProgramming/GeneratorReportNew/source-data.tsv";
        String result = "result.txt";
        
        
        report r2 = new report();
        
        File settingFile = new File (SettingPar);
        DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuild.parse(settingFile);
        r2.getParameters(doc);
        
        
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(source), "UTF-16"));  
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(result), "UTF-16"));
  
        
        r2.writeReport(br,bw);
        
        bw.close();
        br.close();
          
    }
    
}
