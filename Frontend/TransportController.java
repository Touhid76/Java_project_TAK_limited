package com.template;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TransportController extends BaseController {

    @FXML private TextField tfPickup, tfManpower;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<String> cbTruck;
    @FXML private Label lblCost, lblMsg;


    public void initialize() {
        cbTruck.getItems().addAll("Small", "Medium", "Large");

        // Listener for dynamic calculation
        cbTruck.valueProperty().addListener((o, old, newVal) -> calcCost());
        tfManpower.textProperty().addListener((o, old, newVal) -> calcCost());
    }

    private void calcCost() {
        if (cbTruck.getValue() == null || tfManpower.getText().isEmpty()) return;
        double base = 0;
        switch (cbTruck.getValue()) {
            case "Small": base = 3000; break;
            case "Medium": base = 6000; break;
            case "Large": base = 10000; break;
        }
        try {
            int men = Integer.parseInt(tfManpower.getText());
            double total = base + (men * 800); // 600 per worker
            lblCost.setText("Total Cost: " + total + " BDT");
        } catch (Exception e) { lblCost.setText("Invalid Input"); }
    }

    @FXML
    public void confirmTransport() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO transport (booking_id, pickup_address, moving_date, truck_size, manpower, total_cost) VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Session.currentBookingId);
            ps.setString(2, tfPickup.getText());
            ps.setString(3, dpDate.getValue().toString());
            ps.setString(4, cbTruck.getValue());
            ps.setInt(5, Integer.parseInt(tfManpower.getText()));

            String costStr = lblCost.getText().replaceAll("[^0-9.]", "");
            ps.setDouble(6, Double.parseDouble(costStr));
            ps.executeUpdate();

            lblMsg.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            lblMsg.setText("Transport Booked! We will contact you.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void skip(ActionEvent e) {
        switchScene(e, "TenantDashboard.fxml");
    }
}