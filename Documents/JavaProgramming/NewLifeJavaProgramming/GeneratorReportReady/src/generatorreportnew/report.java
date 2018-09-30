/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generatorreportnew;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.w3c.dom.*;


/**
 *
 * @author safrolov
 */
public class report {
    
    private int pageWidth;
    private int pageHeight;
    private ArrayList<String> columnTitles = new ArrayList<>();
    private ArrayList<Integer> columnWidths = new ArrayList<>();
    private final String pageSeparator = "~";
    private final String columnSeparator = "|";
    private final String lineSeparator = "-";
    private int lineCounter = 0;
    private int currentReportLineHeight = 0;
    
    //читаем требуемые параметры страницы и столбцов из xml файла
    public void getParameters(Document doc) {
        
        //читаем параметры страницы
        Element rootEl = doc.getDocumentElement(); 
        Element pageElement= (Element) rootEl.getElementsByTagName("page").item(0);
        pageWidth = Integer.parseInt(pageElement.getElementsByTagName("width").item(0).getTextContent());
        pageHeight = Integer.parseInt(pageElement.getElementsByTagName("height").item(0).getTextContent());
        
        Element columnsEl = (Element) rootEl.getElementsByTagName("columns").item(0);  
        
        //читаем загаловки столбцов
        NodeList columTitleNodes=columnsEl.getElementsByTagName("title");
        for (int i = 0; i < columTitleNodes.getLength(); i++) {
            columnTitles.add(i,columTitleNodes.item(i).getTextContent());
        }
        
        //читаем ширину столбцов
        NodeList columWidthNodes=columnsEl.getElementsByTagName("width");
        for (int i = 0; i < columWidthNodes.getLength(); i++) {
            columnWidths.add(i,Integer.parseInt(columWidthNodes.item(i).getTextContent()));
        }
    }
    
    //записываем отчет
    public void writeReport(BufferedReader br, BufferedWriter bw) throws IOException{
        String srcLine;
        
        while((srcLine=br.readLine())!=null){
              //считвыем строчку из файла и записываем ее в arrayList
              String [] srcLineArr = srcLine.split("\\t");
              ArrayList<String> srcLineList=new ArrayList<>(Arrays.asList(srcLineArr)); 
              
              //записываем в файл заголовки, если страница первая
              if(lineCounter==0){
                  bw.write(getReportLine(columnTitles));
                  lineCounter+=currentReportLineHeight;
              }
              
              //добавляем данные для записи в отчет
              String reportLine = getReportLine(srcLineList);
              
              //Проверяем помещаются ли данные на данную страницу. Если нет, то добавляем символ конца страницы и на новую страницу добавляем заголовки
              if(lineCounter+currentReportLineHeight>=pageHeight){
                  bw.write(pageSeparator+"\r\n");
                  lineCounter=0;
                  bw.write(getReportLine(columnTitles));
                  lineCounter+=currentReportLineHeight;
              }
              
              //добавляем разделитель между строчками данных в отчете
              if(lineCounter != 0){
                  String lineSeparatorString = getCompletedString("", pageWidth, lineSeparator).concat("\r\n");
                  bw.write(lineSeparatorString);
                  lineCounter++;
              }
              bw.write(reportLine);
              lineCounter+=currentReportLineHeight;
        }
    }
    
    //формируем строчку из файла, что бы ее можно было записать в отчет с учетом всех параметров
    private String getReportLine(ArrayList<String> srcStringData){
        String reportLine="";
        currentReportLineHeight = 0;
        
        ArrayList<ArrayList<String>> cellLineList = new ArrayList<>();
        
        //ищем на какое максимальное количество строк помещаются данные в отчете с учетом параметров
        for (int i = 0; i < srcStringData.size(); i++) {
            cellLineList.add(splitString(srcStringData.get(i),columnWidths.get(i)));
            if(currentReportLineHeight<=cellLineList.get(i).size()){
                currentReportLineHeight=cellLineList.get(i).size();
            }
        }
        
        for (int i=0; i<currentReportLineHeight;i++){
            reportLine = reportLine.concat(columnSeparator); //добавляем разделитель столбцов
            for (int j = 0; j < cellLineList.size(); j++) {
                reportLine = reportLine.concat(" "); //добавляем в начало каждой строки пробел
                if(cellLineList.get(j).size()>i){//пишем данные, все остальное место занимаем пробелами
                    String cellLine=getCompletedString(cellLineList.get(j).get(i), columnWidths.get(j), " ");
                    reportLine = reportLine.concat(cellLine);
                }
                else{ //если нет данных для строчки, то заполняем пробелами 
                    reportLine=reportLine.concat(getCompletedString("", columnWidths.get(j), " "));
                }
                reportLine=reportLine.concat(" ").concat(columnSeparator);//добавляем в конце пробел и разделитель строчки
            }
            reportLine=reportLine.concat("\r\n"); //переходим на новую строку в отчете 
        }
        return reportLine;
    }
    
    //дополняет до нужной длины строчку в отчете опредленным символом 
    private String getCompletedString(String srcString, int stringLength, String symbol){
        String result = srcString;
        
        for (int i = 0; i < stringLength-srcString.length(); i++) {
            result = result.concat(symbol);
        }
        
        return result;
    }
    
    //разбивает строчку из файла на требуемые строчки в отчете в соответсвии с шириной столбца в отчете 
    //Получаем строчку для анализа и ширину столбца
    private ArrayList<String> splitString(String srcString,int stringLength){
        ArrayList<String> result = new ArrayList<>();
        int separatorIndex=0;
        String remainingString = srcString;        

        for (int i = 0; i < remainingString.length(); i++) {
            char currentChar = remainingString.charAt(i);
            
            //проверяем наличие разделителя
            if(!Character.isLetter(currentChar)&&!Character.isDigit(currentChar)){
                separatorIndex=i;
            }
            
            //Записываем строчки в arrayList
            if(i==stringLength){
                if(separatorIndex==0){ //если не было разделителя, то принудительно разбиваем слово
                    result.add(remainingString.substring(0, i));
                    remainingString = remainingString.substring(i); //отрезаем, что уже добавили в arrayList
                    i=0;
                }
                else{
                    if(separatorIndex<stringLength) separatorIndex++; ///добавляем разделитель, если он помещается в строчку
                    
                    result.add(remainingString.substring(0, separatorIndex)); //добавляем слово до разделителя
                    remainingString = remainingString.substring(separatorIndex).trim(); //отрезаем, что уже добавили и пробелы справла/слева
                    
                    i=0;
                    separatorIndex = 0;
                }
            }
            //когда остаются последнии символы, которые помещаются в строчку по ширине. записываем эту строчку
            if(i==remainingString.length()-1){
                result.add(remainingString);
            }
        }
        return result;
    }
}
