package com.template;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OwnerController extends BaseController {

    @FXML private ComboBox<String> cbLocation, cbBed, cbBath;
    @FXML private TextField tfRent, tfSize;
    @FXML private Label lblStatus;
    @FXML private FlowPane imagePreviewBox;

    private List<File> selectedFiles = new ArrayList<>();

    @FXML
    public void initialize() {
        cbLocation.getItems().addAll("Dhanmondi", "Mirpur", "Uttara", "Gulshan", "Banani", "Mohammadpur", "Bashundhara", "Farmgate", "Motijheel");
        cbBed.getItems().addAll("1", "2", "3", "4", "5", "6");
        cbBath.getItems().addAll("1", "2", "3", "4", "5");
    }

    @FXML
    public void handleImageUpload() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Images");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        List<File> files = fc.showOpenMultipleDialog(null);

        if (files != null) {
            selectedFiles.addAll(files);
            renderPreviews();
        }
    }

    private void renderPreviews() {
        imagePreviewBox.getChildren().clear();
        for (File f : selectedFiles) {
            try {
                ImageView iv = new ImageView(new Image(f.toURI().toString()));
                iv.setFitWidth(100); iv.setFitHeight(80);
                iv.setPreserveRatio(true);
                iv.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 0);");
                imagePreviewBox.getChildren().add(iv);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @FXML
    public void handlePostFlat() {
        try (Connection conn = DBConnection.getConnection()) {
            if (cbLocation.getValue() == null || tfRent.getText().isEmpty() || selectedFiles.isEmpty()) {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Fill all fields & upload images.");
                return;
            }

            // Transaction Start
            conn.setAutoCommit(false);

            String sql = "INSERT INTO flats (owner_id, location, rent, size_sqft, bedroom, washroom, status) VALUES (?,?,?,?,?,?, 'Available')";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, Session.getCurrentUserId());
            ps.setString(2, cbLocation.getValue());
            ps.setDouble(3, Double.parseDouble(tfRent.getText()));
            ps.setInt(4, Integer.parseInt(tfSize.getText()));
            ps.setInt(5, Integer.parseInt(cbBed.getValue()));
            ps.setInt(6, Integer.parseInt(cbBath.getValue()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int flatId = rs.getInt(1);
                String imgSql = "INSERT INTO flat_images (flat_id, image_path) VALUES (?, ?)";
                PreparedStatement psImg = conn.prepareStatement(imgSql);

                for (File f : selectedFiles) {
                    psImg.setInt(1, flatId);
                    psImg.setString(2, f.toURI().toString());
                    psImg.addBatch();
                }
                psImg.executeBatch();
                conn.commit(); // Transaction End

                lblStatus.setStyle("-fx-text-fill: green;");
                lblStatus.setText("Flat Posted with " + selectedFiles.size() + " photos!");
                tfRent.clear(); tfSize.clear(); selectedFiles.clear(); imagePreviewBox.getChildren().clear();
            }
        } catch (Exception e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }

    public void logout(ActionEvent e) { switchScene(e, "LoginUI.fxml"); }
}