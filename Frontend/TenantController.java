package com.template;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TenantController extends BaseController {

    @FXML private ComboBox<String> cbLocation;
    @FXML private TextField tfMaxRent, tfMinSize;
    @FXML private VBox resultsContainer; // The ScrollPane content
    @FXML private Label lblStatus;

    @FXML
    public void initialize() {
        cbLocation.getItems().addAll("Dhanmondi", "Mirpur", "Uttara", "Gulshan", "Banani", "Mohammadpur", "Bashundhara", "Farmgate", "Motijheel");
    }

    @FXML
    public void handleSearch() {
        resultsContainer.getChildren().clear();

        if (cbLocation.getValue() == null || tfMaxRent.getText().isEmpty()) {
            lblStatus.setText("Location & Max Rent required!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // ALGORITHM: Filter -> Sort by Rent ASC (Affordability) -> Size DESC (Space)
            StringBuilder query = new StringBuilder("SELECT * FROM flats WHERE status='Available' AND location=? AND rent <= ?");
            if (!tfMinSize.getText().isEmpty()) query.append(" AND size_sqft >= ").append(tfMinSize.getText());
            query.append(" ORDER BY rent ASC, size_sqft DESC");

            PreparedStatement ps = conn.prepareStatement(query.toString());
            ps.setString(1, cbLocation.getValue());
            ps.setDouble(2, Double.parseDouble(tfMaxRent.getText()));

            ResultSet rs = ps.executeQuery();
            boolean found = false;

            while (rs.next()) {
                found = true;
                resultsContainer.getChildren().add(createModernCard(rs));
            }
            lblStatus.setText(found ? "" : "No flats found.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- DYNAMIC UI GENERATION (No CSS, Pure JavaFX Layouts) ---
    private Node createModernCard(ResultSet rs) throws SQLException {
        int flatId = rs.getInt("flat_id");

        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 0);");
        card.setAlignment(Pos.CENTER_LEFT);

        // 1. CAROUSEL LOGIC
        VBox carousel = new VBox(10);
        carousel.setAlignment(Pos.CENTER);

        ImageView view = new ImageView();
        view.setFitWidth(350); view.setFitHeight(220); // LARGE IMAGE
        view.setPreserveRatio(false); // Fill the box

        List<String> images = getImages(flatId);
        final int[] idx = {0};

        if (!images.isEmpty()) try { view.setImage(new Image(images.get(0))); } catch(Exception e){}

        HBox nav = new HBox(15);
        nav.setAlignment(Pos.CENTER);
        Button btnPrev = new Button("<");
        Button btnNext = new Button(">");
        Label lblCount = new Label(images.isEmpty() ? "0/0" : "1/" + images.size());

        // Button Styles
        String btnStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;";
        btnPrev.setStyle(btnStyle); btnNext.setStyle(btnStyle);

        btnPrev.setOnAction(e -> {
            if (images.isEmpty()) return;
            idx[0] = (idx[0] - 1 + images.size()) % images.size();
            try { view.setImage(new Image(images.get(idx[0]))); } catch(Exception ex){}
            lblCount.setText((idx[0] + 1) + "/" + images.size());
        });

        btnNext.setOnAction(e -> {
            if (images.isEmpty()) return;
            idx[0] = (idx[0] + 1) % images.size();
            try { view.setImage(new Image(images.get(idx[0]))); } catch(Exception ex){}
            lblCount.setText((idx[0] + 1) + "/" + images.size());
        });

        nav.getChildren().addAll(btnPrev, lblCount, btnNext);
        carousel.getChildren().addAll(view, nav);

        // 2. DETAILS LOGIC
        VBox details = new VBox(10);
        Label title = new Label(rs.getString("location"));
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2c3e50"));

        Label price = new Label("BDT " + rs.getDouble("rent") + " / month");
        price.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        price.setTextFill(Color.web("#16a085")); // Teal

        Label specs = new Label(rs.getInt("size_sqft") + " Sq Ft  |  " +
                rs.getInt("bedroom") + " Beds  |  " +
                rs.getInt("washroom") + " Baths");
        specs.setFont(Font.font(16));
        specs.setTextFill(Color.GRAY);

        Button btnBook = new Button("BOOK NOW");
        btnBook.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");

        btnBook.setOnAction(e -> handleBooking(flatId, e));

        details.getChildren().addAll(title, price, new Separator(), specs, new Region(), btnBook);
        card.getChildren().addAll(carousel, details);

        return card;
    }

    private List<String> getImages(int id) {
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT image_path FROM flat_images WHERE flat_id=" + id);
            while (rs.next()) list.add(rs.getString(1));
        } catch (Exception e) {}
        return list;
    }

    private void handleBooking(int flatId, ActionEvent e) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.createStatement().executeUpdate("UPDATE flats SET status='Booked' WHERE flat_id=" + flatId);

            String sql = "INSERT INTO bookings (flat_id, tenant_id) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, flatId); ps.setInt(2, Session.getCurrentUserId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Session.currentBookingId = rs.getInt(1);
                switchScene(e, "TransportUI.fxml"); // Redirect to Transport Page
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void logout(ActionEvent e) { switchScene(e, "LoginUI.fxml"); }
}