package com.template;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class SignupController extends BaseController {
    @FXML private TextField tfUser, tfPhone, tfEmail;
    @FXML private PasswordField pfPass;
    @FXML private ToggleGroup roleGroup;
    @FXML private Label lblMsg;

    public void handleSignup(ActionEvent event) {
        String u = tfUser.getText();
        String p = pfPass.getText();
        RadioButton selected = (RadioButton) roleGroup.getSelectedToggle();

        if (selected == null || u.isEmpty() || p.isEmpty()) {
            lblMsg.setText("All fields required."); return;
        }
        String role = selected.getText();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u); ps.setString(2, p); ps.setString(3, role);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int uid = rs.getInt(1);
                if (role.equals("Owner")) {
                    String infoSql = "INSERT INTO owner_info (user_id, name, phone, email) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps2 = conn.prepareStatement(infoSql);
                    ps2.setInt(1, uid); ps2.setString(2, u);
                    ps2.setString(3, tfPhone.getText()); ps2.setString(4, tfEmail.getText());
                    ps2.executeUpdate();
                }
                lblMsg.setStyle("-fx-text-fill: green;");
                lblMsg.setText("Success! Please Login.");
            }
        } catch (Exception e) {
            lblMsg.setText("Username already exists.");
        }
    }

    public void backToLogin(ActionEvent event) {
        switchScene(event, "LoginUI.fxml");
    }
}