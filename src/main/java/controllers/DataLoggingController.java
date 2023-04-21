package controllers;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataLoggingController {

    private final String CSV_RELATIVE_PATH_NAME;
    private final String startTime;
    private final String relativeDirectory;
    private ArrayList<String> workingDataRow = new ArrayList<>();


    public DataLoggingController(String relativeDirectoryName) {
        this.startTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        this.relativeDirectory = "results/" + relativeDirectoryName + "-----" + startTime;
        this.CSV_RELATIVE_PATH_NAME = relativeDirectory + "/data.csv";
        File csvFile = new File(CSV_RELATIVE_PATH_NAME);

        if (!csvFile.getParentFile().exists()) {
            csvFile.getParentFile().mkdirs();
        }

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

        System.out.println("csv created at: " + this.CSV_RELATIVE_PATH_NAME);
    }

    public void logWorkingDataArrayToCSV() throws IOException {
        FileWriter fileWriter = new FileWriter(CSV_RELATIVE_PATH_NAME, true); // boolean is for append
        CSVWriter csvWriter = new CSVWriter(fileWriter);
        csvWriter.writeNext(workingDataRow.toArray(new String[workingDataRow.size()]));
        csvWriter.close();
        fileWriter.close();
        workingDataRow = new ArrayList<>();
    }

    public void appendDataToWorkingDataRow(String dataPoint) {
        workingDataRow.add(dataPoint);
    }

    public void appendDataToWorkingDataRow(ArrayList<String> dataPointArray) {
        workingDataRow.addAll(dataPointArray);
    }

}
