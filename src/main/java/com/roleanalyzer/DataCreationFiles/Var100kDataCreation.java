
package com.roleanalyzer.DataCreationFiles;

        import java.io.BufferedWriter;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.Random;

public class Var100kDataCreation {
    public static void main(String[] args) {
        String csvFile = "mock_100kVar.csv";
        String[] header = {"Manager", "User", "RoleName", "Department", "ID"};
        Random rand = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // write the header
            writeLine(writer, header);

            // generate and write mock data
            for (int i = 0; i < 100000; i++) {
                int managerIndex;
                int userIndex;
                int roleIndex;

                // Ensures that for first 8*1250 entries, four users share the same manager and two roles
                if(i < 10000) {
                    managerIndex = i / 8;       // creates 1250 managers: Manager0 to Manager1249
                    userIndex = i / 2;          // creates 5000 users: User0 to User4999
                    roleIndex = i % 2 + i / 4;  // creates 2500 roles: Role0 to Role2499
                } else {
                    managerIndex = rand.nextInt(1000) + 500; // +500 to avoid Manager0 to Manager499
                    userIndex = rand.nextInt(8000) + 2000;  // +2000 to avoid User0 to User1999
                    roleIndex = rand.nextInt(8000) + 2000;  // +2000 to avoid Role0 to Role1999
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
            // write separator if not the first 10000 entries
            if (i < fields.length - 1) {
                writer.write(",");
            }
        }
        writer.newLine();
    }
}
