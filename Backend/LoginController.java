package com.template;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController extends BaseController {

    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private ToggleGroup roleGroup;
    @FXML private Label lblMessage;

    public void handleLogin(ActionEvent event) {
        String user = tfUsername.getText();
        String pass = pfPassword.getText();
        RadioButton selected = (RadioButton) roleGroup.getSelectedToggle();

        if (selected == null) {
            lblMessage.setText("Please select a role.");
            return;
        }
        String role = selected.getText();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT user_id FROM users WHERE username=? AND password=? AND role=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Session.setCurrentUserId(rs.getInt("user_id"));
                Session.setCurrentUserRole(role);

                if (role.equals("Owner")) {
                    switchScene(event, "OwnerDashboard.fxml");
                } else {
                    switchScene(event, "TenantDashboard.fxml");
                }
            } else {
                lblMessage.setStyle("-fx-text-fill: red;");
                lblMessage.setText("Invalid credentials!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToSignup(ActionEvent event) {
        switchScene(event, "SignupUI.fxml");
    }
}