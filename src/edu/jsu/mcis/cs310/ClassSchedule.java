package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
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
    private final String SCHEDULETYPE_ID = "scheduletype";
    private final String COURSE_ID = "course";
    
    public String convertCsvToJsonString(List<String[]> csv){
        
        JsonObject csvToJson = new JsonObject();        /* Create overall JsonObject */
        JsonObject scheduletype = new JsonObject();     /* Create JsonObject to hold scheduletype data */
        JsonObject course = new JsonObject();       /* Create JsonObject to hold course data */
        JsonObject subject = new JsonObject();      /* Create JsonObject to hold subject data */
        ArrayList<JsonObject> section = new ArrayList();        /* Create JsonObject to hold section data */
        
        String[] sectionObject;     /* create array to hold data from iterator */
        
        Iterator<String[]> iterator = csv.iterator();       /* Create iterator to read csv */
        
        sectionObject = iterator.next();        /* Set array equal to first line of the csv */
        
        HashMap<String, Integer> headerRow = new HashMap();     /* Create a HashMap for the header row */
        
        for(int i = 0; i < sectionObject.length; i++){
            headerRow.put(sectionObject[i], i);     /* Enumerate the headers to be used as constants */
        }
        
        while(iterator.hasNext()){
                JsonObject sectionData = new JsonObject();      /* Create JsonObject to build a section */
                sectionObject = iterator.next();        /* Set array equal to iterator */
                
                String[] courseNumAndCode = sectionObject[headerRow.get(NUM_COL_HEADER)].split(" ");     /* Split string and place data into the array */
                String courseInstructor = sectionObject[headerRow.get(INSTRUCTOR_COL_HEADER)];      /* Place string to be formatted as an array into variable */
                
                sectionData.put(CRN_COL_HEADER, new BigDecimal(sectionObject[headerRow.get(CRN_COL_HEADER)]));      /* Add data to sectionData */
                sectionData.put(SUBJECTID_COL_HEADER, courseNumAndCode[0]);     /*"*/
                sectionData.put(NUM_COL_HEADER, courseNumAndCode[1]);       /*"*/
                sectionData.put(SECTION_COL_HEADER, sectionObject[headerRow.get(SECTION_COL_HEADER)]);      /*"*/
                sectionData.put(TYPE_COL_HEADER, sectionObject[headerRow.get(TYPE_COL_HEADER)]);        /*"*/
                sectionData.put(START_COL_HEADER, sectionObject[headerRow.get(START_COL_HEADER)]);      /*"*/
                sectionData.put(END_COL_HEADER, sectionObject[headerRow.get(END_COL_HEADER)]);      /*"*/
                sectionData.put(DAYS_COL_HEADER, sectionObject[headerRow.get(DAYS_COL_HEADER)]);        /*"*/
                sectionData.put(WHERE_COL_HEADER, sectionObject[headerRow.get(WHERE_COL_HEADER)]);      /*"*/
                sectionData.put(INSTRUCTOR_COL_HEADER, courseInstructor.split(", "));       /*"*/
                
                section.add(sectionData);       /* Add sectionData to section array */
                
                
                JsonObject courseData = new JsonObject();       /* Create object to hold courseData */
                
                courseData.put(SUBJECTID_COL_HEADER, courseNumAndCode[0]);      /* Add data to courseData */
                courseData.put(NUM_COL_HEADER, courseNumAndCode[1]);        /*"*/
                courseData.put(DESCRIPTION_COL_HEADER, sectionObject[headerRow.get(DESCRIPTION_COL_HEADER)]);       /*"*/
                courseData.put(CREDITS_COL_HEADER, new BigDecimal(sectionObject[headerRow.get(CREDITS_COL_HEADER)]));       /*"*/
                
                course.put(sectionObject[headerRow.get(NUM_COL_HEADER)], courseData);       /* Place courseData into course object */
                
                
                if(!subject.containsValue(sectionObject[headerRow.get(SUBJECT_COL_HEADER)])){       /* Check to see if value is already present in subject object */
                    subject.put(courseNumAndCode[0], sectionObject[headerRow.get(SUBJECT_COL_HEADER)]);     /* Place value into subject object */
                }
                else{}
                
                
                if(!scheduletype.containsValue(sectionObject[headerRow.get(SCHEDULE_COL_HEADER)])){     /* Check to see if value is already present in scheduletype object */
                    scheduletype.put(sectionObject[headerRow.get(TYPE_COL_HEADER)], sectionObject[headerRow.get(SCHEDULE_COL_HEADER)]);     /* Place value into scheduletype object */
                }
                else{}
                
        }
        
        csvToJson.put(SUBJECT_COL_HEADER, subject);     /* Place data into final JsonObject */
        csvToJson.put(SECTION_COL_HEADER, section);     /*"*/
        csvToJson.put(COURSE_ID, course);       /*"*/
        csvToJson.put(SCHEDULETYPE_ID, scheduletype);       /*"*/
        
        String jsonString = Jsoner.serialize(csvToJson);        /* Serialize final json string */
        
        
        return jsonString;
    }

    public String convertJsonToCsvString(JsonObject json){
        
        JsonArray section = (JsonArray)json.get(SECTION_COL_HEADER);     /* Create JsonArray to hold section data */
        JsonObject subject = (JsonObject)json.get(SUBJECT_COL_HEADER);       /* Create JsonObject to hold subject data */
        JsonObject course = (JsonObject)json.get(COURSE_ID);     /* Create JsonObeject to hold course data */
        JsonObject scheduletype = (JsonObject)json.get(SCHEDULETYPE_ID);     /* Create JsonObject to hold scheduletype data */
        
        ArrayList<ArrayList<String>> sectionArray = new ArrayList();
        ArrayList<String> header = new ArrayList();
        ArrayList<String> printLineHolder;
        
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
            ArrayList<String> sectionData = new ArrayList();      /* Create ArrayList to hold a single class section */
            HashMap<String, String> sectionObject = (HashMap)section.get(i);        /* Create HashMap to hold a single section instance */
            HashMap<String, String> subjectObject = (HashMap)course.get((sectionObject.get(SUBJECTID_COL_HEADER)+" "+sectionObject.get(NUM_COL_HEADER)));        /* Create an HashMap to hold a sible subject instance */
            
            Object crnHolder = sectionObject.get(CRN_COL_HEADER);      /* Place the object into the holder */
            
            Object creditsHolder = subjectObject.get(CREDITS_COL_HEADER);      /* Place the object into the holder */
            
            Object instructorHolder = sectionObject.get(INSTRUCTOR_COL_HEADER);        /* Place the object into the holder */
            String instructorString = instructorHolder.toString();      /* Cast the object to a usable data type */
            
            sectionData.add(crnHolder.toString());       /* Add data to classData ArrayList */
            sectionData.add((String)subject.get(sectionObject.get(SUBJECTID_COL_HEADER)));     /*"*/
            sectionData.add((sectionObject.get(SUBJECTID_COL_HEADER)+" "+sectionObject.get(NUM_COL_HEADER)));      /*"*/
            sectionData.add(subjectObject.get(DESCRIPTION_COL_HEADER));       /*"*/
            sectionData.add(sectionObject.get(SECTION_COL_HEADER));       /*"*/
            sectionData.add(sectionObject.get(TYPE_COL_HEADER));      /*"*/
            sectionData.add(creditsHolder.toString());       /*"*/
            sectionData.add(sectionObject.get(START_COL_HEADER));     /*"*/
            sectionData.add(sectionObject.get(END_COL_HEADER));       /*"*/
            sectionData.add(sectionObject.get(DAYS_COL_HEADER));      /*"*/
            sectionData.add(sectionObject.get(WHERE_COL_HEADER));     /*"*/
            sectionData.add((String)scheduletype.get(sectionObject.get(TYPE_COL_HEADER)));        /*"*/
            sectionData.add(instructorString.replaceAll("[\\p{Ps}\\p{Pe}]", ""));        /*"*/
            
            sectionArray.add(sectionData);        /* Add the fully populated classData Arraylist to the overall classesArray container */
        
        }
        
        StringWriter sw = new StringWriter();
        
        ICSVWriter csvWriter = new CSVWriterBuilder(sw)     /* Create a ICVSWriter with the proper formatting specifications */
            .withSeparator('\t')        /* Specific line seperator */
            .withQuoteChar('"')     /* Wrap string values in double quotes */
            .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)     /* Default escape sequence */
            .withLineEnd(CSVWriter.DEFAULT_LINE_END)        /* Default line end character */
            .build();       /* Build ICSVWriter */
        
        csvWriter.writeNext(header.toArray(String[]::new));     /* Write header row to final string */
        
        for(int i = 0; i < sectionArray.size(); i++){
            printLineHolder = (ArrayList<String>)sectionArray.get(i);       /* Place single classData ArrayList into the printLineHolder */
            
            csvWriter.writeNext(printLineHolder.toArray(String[]::new));       /* Write printLineHolder's contents to final string */
        }
        
       
        try {
            csvWriter.close();      /* Close writer */
        }
        catch (IOException e){}
        
        
        return sw.toString();       /* return final output */
        
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