package com.cvmatcher.ui;

import com.cvmatcher.dao.CvDao;
import com.cvmatcher.dao.JobNoticeDao;
import com.cvmatcher.dao.MatchResultDao;
import com.cvmatcher.dao.SqliteCvDao;
import com.cvmatcher.dao.SqliteJobNoticeDao;
import com.cvmatcher.dao.SqliteMatchResultDao;
import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.MatchResult;
import com.cvmatcher.model.User;
import com.cvmatcher.service.AvailabilityService;
import com.cvmatcher.service.AvailabilityServiceImpl;
import com.cvmatcher.service.AvailabilityServiceProxy;
import com.cvmatcher.service.GapAnalyzer;
import com.cvmatcher.service.JobProcessor;
import com.cvmatcher.service.MatchingService;
import com.cvmatcher.service.observer.HighMatchNotifier;
import com.cvmatcher.service.strategy.TfIdfCosineStrategy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NoticeListController {

    @FXML private TableView<NoticeRow> noticeTable;
    @FXML private TableColumn<NoticeRow, Number> idColumn;
    @FXML private TableColumn<NoticeRow, String> companyColumn;
    @FXML private TableColumn<NoticeRow, String> titleColumn;
    @FXML private TableColumn<NoticeRow, String> locationColumn;
    @FXML private TableColumn<NoticeRow, String> availabilityColumn;
    @FXML private TableColumn<NoticeRow, String> stateColumn;
    @FXML private TextField rankIdsField;
    @FXML private Label statusLabel;

    private final JobNoticeDao jobNoticeDao = new SqliteJobNoticeDao();
    private final CvDao cvDao = new SqliteCvDao();
    private final MatchResultDao matchResultDao = new SqliteMatchResultDao();
    private final AvailabilityService availabilityService;

    public NoticeListController() {
        User currentUser = SessionContext.getCurrentUser();
        AvailabilityService realService = new AvailabilityServiceImpl(jobNoticeDao);
        // Proxy pattern: UI only ever talks to the proxy; it enforces the admin-only rule
        // for BOTH editing availability and editing the description.
        this.availabilityService = new AvailabilityServiceProxy(realService, currentUser);
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        refreshTable();
    }

    private void refreshTable() {
        List<JobNotice> notices = jobNoticeDao.findAll();
        ObservableList<NoticeRow> rows = FXCollections.observableArrayList();
        for (JobNotice n : notices) {
            rows.add(new NoticeRow(n));
        }
        noticeTable.setItems(rows);
    }

    @FXML
    private void onRankSelected(ActionEvent event) {
        User currentUser = SessionContext.getCurrentUser();
        Optional<Cv> cvOpt = cvDao.findByUserId(currentUser.getId());
        if (cvOpt.isEmpty()) {
            statusLabel.setText("You need to create a CV before ranking notices.");
            return;
        }

        List<Integer> ids;
        try {
            ids = Arrays.stream(rankIdsField.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            statusLabel.setText("Enter notice IDs as comma-separated numbers, e.g. 1, 3, 5");
            return;
        }

        List<JobNotice> notices = jobNoticeDao.findByIds(ids);

        JobProcessor jobProcessor = new JobProcessor(new TfIdfCosineStrategy(), new GapAnalyzer());
        MatchingService matchingService = new MatchingService(jobProcessor, matchResultDao);
        matchingService.addObserver(new HighMatchNotifier());

        List<MatchResult> results = matchingService.rankNotices(currentUser, cvOpt.get(), notices);

        StringBuilder sb = new StringBuilder("Ranking results:\n");
        for (MatchResult r : results) {
            sb.append(String.format("Notice #%d: %s (%.0f%%)%n", r.getJobNoticeId(), r.getRankLevel(), r.getScore() * 100));
            if (!r.getGaps().isEmpty()) {
                r.getGaps().forEach(g -> sb.append("    - ").append(g.getMissingItem())
                        .append(": ").append(g.getSuggestion()).append("\n"));
            }
        }
        new Alert(Alert.AlertType.INFORMATION, sb.toString()).showAndWait();
    }

    @FXML
    private void onEditAvailability(ActionEvent event) {
        NoticeRow selected = noticeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a notice first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getNotice().getFilledPositions()));
        dialog.setHeaderText("Update filled positions for " + selected.getTitle());
        dialog.setContentText("Filled positions:");

        dialog.showAndWait().ifPresent(value -> {
            try {
                int filled = Integer.parseInt(value.trim());
                availabilityService.updateFilledPositions(selected.getNotice(), filled);
                statusLabel.setText("Availability updated.");
                refreshTable();
            } catch (NumberFormatException e) {
                statusLabel.setText("Enter a valid number.");
            } catch (SecurityException e) {
                statusLabel.setText(e.getMessage());
            }
        });
    }

    @FXML
    private void onEditDescription(ActionEvent event) {
        NoticeRow selected = noticeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a notice first.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setHeaderText("Edit description for " + selected.getTitle());
        TextArea textArea = new TextArea(selected.getNotice().getDescription());
        textArea.setWrapText(true);
        textArea.setPrefRowCount(8);
        textArea.setPrefColumnCount(40);
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> buttonType == ButtonType.OK ? textArea.getText() : null);

        dialog.showAndWait().ifPresent(newDescription -> {
            try {
                availabilityService.updateDescription(selected.getNotice(), newDescription);
                statusLabel.setText("Description updated.");
                refreshTable();
            } catch (SecurityException e) {
                statusLabel.setText(e.getMessage());
            }
        });
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) noticeTable.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/dashboard.fxml", "CV Job Matcher - Dashboard");
        } catch (IOException e) {
            statusLabel.setText("Could not go back: " + e.getMessage());
        }
    }

    /** Simple row wrapper exposing bean-style getters for the TableView. */
    public static class NoticeRow {
        private final JobNotice notice;

        public NoticeRow(JobNotice notice) {
            this.notice = notice;
        }

        public JobNotice getNotice() { return notice; }
        public int getId() { return notice.getId(); }
        public String getCompany() { return notice.getCompany(); }
        public String getTitle() { return notice.getTitle(); }
        public String getLocation() { return notice.getLocation() == null ? "" : notice.getLocation(); }
        public String getAvailability() {
            return notice.getAvailablePositions() + " / " + notice.getTotalPositions();
        }
        public String getState() { return notice.getState().label(); }
    }
}
