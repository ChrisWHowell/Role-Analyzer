package com.roleanalyzer.DataCreationFiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class VariableDataCreation {
    public static void main(String[] args) {
        String csvFile = "mock_10kVar.csv";
        String[] header = {"Manager", "User", "RoleName", "Department", "ID"};
        Random rand = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // write the header
            writeLine(writer, header);

            // generate and write mock data
            for (int i = 0; i < 10000; i++) {
                int managerIndex;
                int userIndex;
                int roleIndex;

                // Ensures that for first 8 entries, four users share the same manager and two roles
                if(i < 8) {
                    managerIndex = 0;
                    userIndex = i / 2;  // creates four users: User0, User1, User2, User3
                    roleIndex = i % 2;  // creates two roles: Role0, Role1
                } else {
                    managerIndex = rand.nextInt(50) + 1;  // +1 to avoid Manager0
                    userIndex = rand.nextInt(1000) + 4;   // +4 to avoid User0, User1, User2, User3
                    roleIndex = rand.nextInt(3000) + 2;   // +2 to avoid Role0, Role1
                }

                String[] data = {
                        "Manager" + managerIndex,
                        "User" + userIndex,
                        "Role" + roleIndex,
                        "Dept1",
                        String.valueOf(userIndex)
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
