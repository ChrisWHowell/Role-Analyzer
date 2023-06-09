package com.roleanalyzer.DataCreationFiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataCreation1M {
    public static void main(String[] args) {
        String csvFile = "mock_1M.csv";
        String[] header = {"Manager", "User", "RoleName", "Department", "ID"};
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // write the header
            writeLine(writer, header);

            // generate and write mock data
            int id = 0;
            for (int managerIndex = 0; managerIndex < 100; managerIndex++) {
                for (int roleIndex = 0; roleIndex < 100; roleIndex++) {
                    int numUsers = 60 + random.nextInt(41);; // Randomly assigns between 60 to 100 users for each role.
                    for (int userIndex = 0; userIndex < numUsers; userIndex++) {
                        String[] data = {
                                "Manager" + managerIndex,
                                "User" + (managerIndex * 100 + userIndex),
                                "Role" + (managerIndex * 100 + roleIndex),  // Changed here
                                "Dept1",
                                String.valueOf(id++)
                        };
                        writeLine(writer, data);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeLine(BufferedWriter writer, String[] fields) throws IOException {
        for (int i = 0; i < fields.length; i++) {
            writer.write(fields[i]);
            // write separator if not the first 10000 entries
            if (i < fields.length - 1) {
                writer.write(",");
            }
        }
        writer.newLine();
    }
}
