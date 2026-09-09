package com.cvmatcher.ui;

import com.cvmatcher.dao.JobNoticeDao;
import com.cvmatcher.dao.SqliteJobNoticeDao;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.JobPosition;
import com.cvmatcher.model.User;
import com.cvmatcher.service.AvailabilityService;
import com.cvmatcher.service.AvailabilityServiceImpl;
import com.cvmatcher.service.AvailabilityServiceProxy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;

/**
 * Shows every per-role position across all job notices, split into two
 * tables (available / unavailable), viewable by both roles. Adding a new
 * role and editing how many of a role are filled are admin-only actions,
 * enforced by AvailabilityServiceProxy -- the same Proxy already guarding
 * the notice-level availability and description edits.
 */
public class JobPositionsController {

    @FXML private TableView<PositionRow> availableTable;
    @FXML private TableColumn<PositionRow, String> availRoleColumn;
    @FXML private TableColumn<PositionRow, String> availCompanyColumn;
    @FXML private TableColumn<PositionRow, String> availTitleColumn;
    @FXML private TableColumn<PositionRow, String> availCountColumn;

    @FXML private TableView<PositionRow> unavailableTable;
    @FXML private TableColumn<PositionRow, String> unavailRoleColumn;
    @FXML private TableColumn<PositionRow, String> unavailCompanyColumn;
    @FXML private TableColumn<PositionRow, String> unavailTitleColumn;
    @FXML private TableColumn<PositionRow, String> unavailCountColumn;

    @FXML private ComboBox<JobNotice> noticeComboBox;
    @FXML private TextField roleNameField;
    @FXML private TextField totalCountField;
    @FXML private Button addPositionButton;
    @FXML private Button editFilledCountButton;
    @FXML private Label statusLabel;

    private final JobNoticeDao jobNoticeDao = new SqliteJobNoticeDao();
    private final AvailabilityService availabilityService;
    private final boolean isAdmin;

    public JobPositionsController() {
        User currentUser = SessionContext.getCurrentUser();
        isAdmin = currentUser != null && currentUser.isAdmin();
        AvailabilityService realService = new AvailabilityServiceImpl(jobNoticeDao);
        this.availabilityService = new AvailabilityServiceProxy(realService, currentUser);
    }

    @FXML
    public void initialize() {
        availRoleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        availCompanyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        availTitleColumn.setCellValueFactory(new PropertyValueFactory<>("noticeTitle"));
        availCountColumn.setCellValueFactory(new PropertyValueFactory<>("countSummary"));

        unavailRoleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        unavailCompanyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        unavailTitleColumn.setCellValueFactory(new PropertyValueFactory<>("noticeTitle"));
        unavailCountColumn.setCellValueFactory(new PropertyValueFactory<>("countSummary"));

        // Admin-only controls are hidden entirely for a regular user, not just disabled --
        // matches the same pattern used for the "Manage users" dashboard button.
        noticeComboBox.setVisible(isAdmin);
        noticeComboBox.setManaged(isAdmin);
        roleNameField.setVisible(isAdmin);
        roleNameField.setManaged(isAdmin);
        totalCountField.setVisible(isAdmin);
        totalCountField.setManaged(isAdmin);
        addPositionButton.setVisible(isAdmin);
        addPositionButton.setManaged(isAdmin);
        editFilledCountButton.setVisible(isAdmin);
        editFilledCountButton.setManaged(isAdmin);

        noticeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(JobNotice notice) {
                return notice == null ? "" : "#" + notice.getId() + " - " + notice.getCompany() + " - " + notice.getTitle();
            }
            @Override
            public JobNotice fromString(String string) { return null; }
        });

        refresh();
    }

    private void refresh() {
        List<JobNotice> notices = jobNoticeDao.findAll();

        ObservableList<PositionRow> availableRows = FXCollections.observableArrayList();
        ObservableList<PositionRow> unavailableRows = FXCollections.observableArrayList();
        for (JobNotice notice : notices) {
            for (JobPosition position : notice.getPositions()) {
                PositionRow row = new PositionRow(position, notice);
                if (position.isAvailable()) availableRows.add(row);
                else unavailableRows.add(row);
            }
        }
        availableTable.setItems(availableRows);
        unavailableTable.setItems(unavailableRows);

        if (isAdmin) {
            noticeComboBox.setItems(FXCollections.observableArrayList(notices));
        }
    }

    @FXML
    private void onAddPosition(ActionEvent event) {
        JobNotice notice = noticeComboBox.getValue();
        if (notice == null) {
            statusLabel.setText("Select a job notice first.");
            return;
        }
        String roleName = roleNameField.getText().trim();
        int totalCount;
        try {
            totalCount = Integer.parseInt(totalCountField.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("Total count must be a number.");
            return;
        }
        try {
            availabilityService.addPosition(notice, roleName, totalCount);
            statusLabel.setText("Position added.");
            roleNameField.clear();
            totalCountField.clear();
            refresh();
        } catch (IllegalArgumentException | SecurityException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onEditFilledCount(ActionEvent event) {
        PositionRow selected = availableTable.getSelectionModel().getSelectedItem();
        if (selected == null) selected = unavailableTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a position from either table first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getPosition().getFilledCount()));
        dialog.setHeaderText("Update filled count for " + selected.getRoleName() + " (" + selected.getCompany() + ")");
        dialog.setContentText("Filled count (of " + selected.getPosition().getTotalCount() + "):");

        final PositionRow rowToUpdate = selected;
        dialog.showAndWait().ifPresent(value -> {
            try {
                int filled = Integer.parseInt(value.trim());
                availabilityService.updatePositionFilledCount(rowToUpdate.getPosition(), filled);
                statusLabel.setText("Position availability updated.");
                refresh();
            } catch (NumberFormatException e) {
                statusLabel.setText("Enter a valid number.");
            } catch (IllegalArgumentException | SecurityException e) {
                statusLabel.setText(e.getMessage());
            }
        });
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) availableTable.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/dashboard.fxml", "CV Job Matcher - Dashboard");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not go back: " + e.getMessage()).showAndWait();
        }
    }

    /** Row wrapper exposing bean-style getters for the two TableViews. */
    public static class PositionRow {
        private final JobPosition position;
        private final JobNotice notice;

        public PositionRow(JobPosition position, JobNotice notice) {
            this.position = position;
            this.notice = notice;
        }

        public JobPosition getPosition() { return position; }
        public String getRoleName() { return position.getRoleName(); }
        public String getCompany() { return notice.getCompany(); }
        public String getNoticeTitle() { return notice.getTitle(); }
        public String getCountSummary() {
            return position.getAvailableCount() + " available / " + position.getTotalCount() + " total";
        }
    }
}
