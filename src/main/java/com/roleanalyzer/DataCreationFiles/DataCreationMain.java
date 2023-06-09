package com.roleanalyzer.DataCreationFiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataCreationMain {
    public static void main(String[] args) {
        String csvFile = "mock_10kuniform.csv";
        String[] header = {"Manager", "User", "RoleName", "Department", "ID"};

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // write the header
            writeLine(writer, header);

            // generate and write mock data
            for (int i = 0; i < 10000; i++) {
                String[] data = {
                        "Manager" + (i % 50),
                        "User" + (i % 100), // now 100 unique users
                        "Role" + ((i / 100) % 100), // now 100 unique roles per user
                        "Dept1",
                        String.valueOf(i % 1000)
                };
                writeLine(writer, data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeLine(BufferedWriter writer, String[] fields) throws IOException {
        for (int i = 0; i < fields.length; i++) {
            writer.write(fields[i]);
            // write separator if not the last item
            if (i < fields.length - 1) {
                writer.write(",");
            }
        }
        writer.newLine();
    }
}





/*import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataCreationMain {
    public static void main(String[] args) {
        String csvFile = "mock_10k.csv";
        String[] header = {"Manager", "User", "RoleName", "Department", "ID"};

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // write the header
            writeLine(writer, header);

            // generate and write mock data
            for (int i = 0; i < 10; i++) {
                String[] data = {
                        "Manager" + i,
                        "User" + i,
                        "Role" + i,
                        "Dept" + i,
                        String.valueOf(i)
                };
                writeLine(writer, data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeLine(BufferedWriter writer, String[] fields) throws IOException {
        for (int i = 0; i < fields.length; i++) {
            writer.write(fields[i]);
            // write separator if not the last item
            if (i < fields.length - 1) {
                writer.write(",");
            }
        }
        writer.newLine();
    }
}*/
