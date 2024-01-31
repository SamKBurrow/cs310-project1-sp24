package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
        return "";
    }

    public String convertJsonToCsvString(JsonObject json) throws IOException {
        
        JsonArray section = (JsonArray)json.get("section");     /* Create JsonArray to hold section data */
        JsonObject subject = (JsonObject)json.get("subject");       /* Create JsonObject to hold subject data */
        JsonObject course = (JsonObject)json.get("course");     /* Create JsonObeject to hold course data */
        JsonObject scheduletype = (JsonObject)json.get("scheduletype");     /* Create JsonObject to hold scheduletype data */
        
        ArrayList<ArrayList<String>> classesArray = new ArrayList();
        ArrayList<String> header = new ArrayList();
        ArrayList<String> printLineHolder = new ArrayList();
        
        header.add(CRN_COL_HEADER);     /* Create header row */
        header.add(SUBJECT_COL_HEADER);     /*"*/
        header.add(NUM_COL_HEADER);     /*"*/
        header.add(DESCRIPTION_COL_HEADER);     /*"*/
        header.add(SECTION_COL_HEADER);     /*"*/
        header.add(TYPE_COL_HEADER);        /*"*/
        header.add(CREDITS_COL_HEADER);     /*"*/
        header.add(START_COL_HEADER);       /*"*/
        header.add(END_COL_HEADER);     /*"*/
        header.add(DAYS_COL_HEADER);        /*"*/
        header.add(WHERE_COL_HEADER);       /*"*/
        header.add(SCHEDULE_COL_HEADER);        /*"*/
        header.add(INSTRUCTOR_COL_HEADER);      /*"*/
        
        for(int i = 0; i < section.size(); i++){
            ArrayList<String> classData = new ArrayList();      /* Create ArrayList to hold a single class section */
            HashMap<String, String> sectionObject = (HashMap)section.get(i);        /* Create HashMap to hold a single section instance */
            HashMap<String, String> subjectObject = (HashMap)course.get((sectionObject.get("subjectid")+" "+sectionObject.get(NUM_COL_HEADER)));        /* Create an HashMap to hold a sible subject instance */
            
            Object crnHolder = new Object();        /* Create a holder for uncastable data type */
            crnHolder = sectionObject.get(CRN_COL_HEADER);      /* Place the object into the holder */
            String crnString = crnHolder.toString();        /* Cast the object to a usable data type */
            
            Object creditsHolder = new Object();        /* Create a holder for uncastable data type */
            creditsHolder = subjectObject.get(CREDITS_COL_HEADER);      /* Place the object into the holder */
            String creditsString = creditsHolder.toString();        /* Cast the object to a usable data type */
            
            Object instructorHolder = new Object();     /* Create a holder for uncastable data type */
            instructorHolder = sectionObject.get(INSTRUCTOR_COL_HEADER);        /* Place the object into the holder */
            String instructorString = instructorHolder.toString();      /* Cast the object to a usable data type */
            instructorString = instructorString.replaceAll("[\\p{Ps}\\p{Pe}]", "");     /* Clean string for adding to ArrayList */
            
            classData.add(crnString);       /* Add data to classData ArrayList */
            classData.add((String)subject.get(sectionObject.get("subjectid")));     /*"*/
            classData.add((sectionObject.get("subjectid")+" "+sectionObject.get(NUM_COL_HEADER)));      /*"*/
            classData.add(subjectObject.get(DESCRIPTION_COL_HEADER));       /*"*/
            classData.add(sectionObject.get(SECTION_COL_HEADER));       /*"*/
            classData.add(sectionObject.get(TYPE_COL_HEADER));      /*"*/
            classData.add(creditsString);       /*"*/
            classData.add(sectionObject.get(START_COL_HEADER));     /*"*/
            classData.add(sectionObject.get(END_COL_HEADER));       /*"*/
            classData.add(sectionObject.get(DAYS_COL_HEADER));      /*"*/
            classData.add(sectionObject.get(WHERE_COL_HEADER));     /*"*/
            classData.add((String)scheduletype.get(sectionObject.get(TYPE_COL_HEADER)));        /*"*/
            classData.add(instructorString);        /*"*/
            
            classesArray.add(classData);        /* Add the fully populated classData Arraylist to the overall classesArray container */
        
        }
        
        StringWriter sw = new StringWriter();       /* Create a writer for writing to the final string */
        
        ICSVWriter csvWriter = new CSVWriterBuilder(sw)     /* Create a ICVSWriter with the proper formatting specifications */
            .withSeparator('\t')        /* Specific line seperator */
            .withQuoteChar('"')     /* Wrap string values in double quotes */
            .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)     /* Default escape sequence */
            .withLineEnd(CSVWriter.DEFAULT_LINE_END)        /* Default line end character */
            .build();       /* Build ICSVWriter */
        
        csvWriter.writeNext(header.toArray(new String[header.size()]));     /* Write header row to final string */
        
        for(int i = 0; i < classesArray.size(); i++){
            printLineHolder = (ArrayList<String>)classesArray.get(i);       /* Place single classData ArrayList into the printLineHolder */
            
            csvWriter.writeNext(printLineHolder.toArray(new String[printLineHolder.size()]));       /* Write printLineHolder's contents to final string */
        }
        
        csvWriter.close();      /* Close writer */
        
        String jsonToCSV = sw.toString();       /* Create final string output */
        
        return jsonToCSV;       /* return final output */
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}