package com.roleanalyzer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Scene1Controller {
    @FXML
    private Button btn_import,btn_Cancel,btn_Submit;

    @FXML
    ComboBox cb_Role,cb_User,cb_RBACcol,cb_PBACcol;
    @FXML
    private Label lb_importedFile;

    @FXML
    private TextField tf_RBACperc,tf_PBACperc;

    @FXML
    private CheckBox checkbox_RBAC,checkbox_PBAC;

    private List<String> csvHeaders = new ArrayList<>();

    // ...


    @FXML
    public void chooseFiles() {
        FileChooser fileChooser = new FileChooser();
        //fileChooser.setInitialDirectory(new File("D:\\Programming Tools\\Java Projects\\Role Analyzer"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(btn_import.getScene().getWindow());
        if (selectedFile != null) {
            lb_importedFile.setText(selectedFile.getPath());

            ArrayList<String> columnNames = new ArrayList<>();
            try (Scanner scanner = new Scanner(selectedFile)) {
                if (scanner.hasNextLine()) {
                    String firstLine = scanner.nextLine();
                    csvHeaders = Arrays.asList(firstLine.split(","));
                    populateComboBoxes(new ArrayList<>(csvHeaders));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    @FXML
    public void handleSubmit() {
        // Check that both cb_User and cb_Role are not empty
        if (cb_User.getValue() == null || cb_Role.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a user and a role.");
            alert.showAndWait();
            return;
        }

        // Check that at least one checkbox is selected and the associated combobox and textfield are not empty
        if ((!checkbox_RBAC.isSelected() && !checkbox_PBAC.isSelected()) ||
                (checkbox_RBAC.isSelected() && (cb_RBACcol.getValue() == null || tf_RBACperc.getText().isEmpty())) ||
                (checkbox_PBAC.isSelected() && (cb_PBACcol.getValue() == null || tf_PBACperc.getText().isEmpty()))) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select at least one checkbox and fill the associated combobox and textfield.");
            alert.showAndWait();
            return;
        }
        else{
            String selectedFilePath = lb_importedFile.getText();
            if (checkbox_RBAC.isSelected()) {
                String criteriamanagerRBAC = cb_RBACcol.getSelectionModel().getSelectedItem().toString();
                double percentageRBAC = Double.parseDouble(tf_RBACperc.getText());
                System.out.println("Calling analyzeRoles(selectedFilePath, criteriamanagerRBAC, percentageRBAC); with criteriamanagerRBAC = " + criteriamanagerRBAC + " and percentageRBAC = " + percentageRBAC);
                analyzeRoles(selectedFilePath, criteriamanagerRBAC, percentageRBAC);
                System.out.println("Done");
            }

            if (checkbox_PBAC.isSelected()) {
                String criteriaPBAC = cb_PBACcol.getSelectionModel().getSelectedItem().toString();
                double percentagePBAC = Double.parseDouble(tf_PBACperc.getText());
                System.out.println("Calling analyzeRoles(selectedFilePath, criteriaPBAC, percentagePBAC); with criteriaPBAC = " + criteriaPBAC + " and percentagePBAC = " + percentagePBAC );
                analyzeRoles(selectedFilePath, criteriaPBAC, percentagePBAC);
                System.out.println("Done");
            }

        }

        // Continue with the handling logic after the checks are passed...
    }
    private void analyzeRoles(String selectedFilePath, String managerColName, double percentage) {
        // Get selected values from combo boxes
        //String managerColName = cb_RBACcol.getSelectionModel().getSelectedItem().toString();
        String userColName = cb_User.getSelectionModel().getSelectedItem().toString();
        String roleColName = cb_Role.getSelectionModel().getSelectedItem().toString();

        // Extract directory from input file path
        String outputDirectory = new File(selectedFilePath).getParent();

        // Create a timestamp string
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Create output file path
        String outputFilePath = outputDirectory + File.separator + "output_" +managerColName+timestamp + ".csv";

        Map<String, Map<String, Set<String>>> managerUserRoles = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFilePath))) {
            reader.readLine(); // Ignore the CSV file header line.

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < csvHeaders.size(); i++) {
                    row.put(csvHeaders.get(i), fields[i]);
                    System.out.println("csvHeaders.get(i) = " + csvHeaders.get(i) + " fields[i] = " + fields[i]);
                }

                String manager = row.get(managerColName);
                String user = row.get(userColName);
                String role = row.get(roleColName);

                // Check if the manager already exists in the map. If not, add it.
                managerUserRoles.putIfAbsent(manager, new HashMap<>());
                Map<String, Set<String>> users = managerUserRoles.get(manager);

                // Check if the user already exists in the map. If not, add it.
                users.putIfAbsent(user, new HashSet<>());
                Set<String> roles = users.get(user);

                roles.add(role); // Add the role to the set of roles for the user.
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (Map.Entry<String, Map<String, Set<String>>> managerEntry : managerUserRoles.entrySet()) {
                Map<Set<String>, Integer> sharedRolesCount = new HashMap<>();
                Map<String, Set<String>> userRoles = managerEntry.getValue();

                for (Set<String> roles : userRoles.values()) {
                    if (roles.size() > 1) {
                        sharedRolesCount.putIfAbsent(roles, 0); // Initialize the count for the roles.
                        for (Set<String> potentialSharedRoles : sharedRolesCount.keySet()) {
                            Set<String> intersection = new HashSet<>(roles);
                            intersection.retainAll(potentialSharedRoles);
                            if (intersection.size() >= 2) {
                                sharedRolesCount.put(potentialSharedRoles, sharedRolesCount.get(potentialSharedRoles) + 1);
                            }
                        }
                    }
                }

                for (Map.Entry<Set<String>, Integer> entry : sharedRolesCount.entrySet()) {
                    double proportion = (double) entry.getValue() / userRoles.size();
                    if (proportion >= (percentage/100)) {
                        String managerInfo = managerEntry.getKey() + " has " + (proportion * 100) + "% users with shared roles.";
                        String sharedRolesInfo = "Shared roles: " + entry.getKey();

                        // Write to CSV file.
                        writer.write(managerInfo);
                        writer.newLine();
                        writer.write(sharedRolesInfo);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Display success message
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Analysis Complete");
            alert.setHeaderText(null);
            alert.setContentText("Role Analysis is complete! The output file has been saved to:\n" + outputFilePath);
            alert.showAndWait();
        });
    }


            private void populateComboBoxes(ArrayList colNames) {
        cb_Role.getItems().addAll(colNames);
        cb_User.getItems().addAll(colNames);
        cb_RBACcol.getItems().addAll(colNames);
        cb_PBACcol.getItems().addAll(colNames);;
    }
    @FXML
    public void handleCancel() {
        Platform.exit();
    }

}