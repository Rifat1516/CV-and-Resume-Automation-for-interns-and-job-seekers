package com.cvmatcher.ui;

import com.cvmatcher.dao.CvDao;
import com.cvmatcher.dao.JobNoticeDao;
import com.cvmatcher.dao.MatchResultDao;
import com.cvmatcher.dao.SqliteCvDao;
import com.cvmatcher.dao.SqliteJobNoticeDao;
import com.cvmatcher.dao.SqliteMatchResultDao;
import com.cvmatcher.model.Cv;
import com.cvmatcher.model.GapRecommendation;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.MatchResult;
import com.cvmatcher.model.RankLevel;
import com.cvmatcher.model.User;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Automatically ranks every open job notice against the logged-in user's
 * CV using the same Strategy/Template-Method/Chain-of-Responsibility
 * pipeline as before (TF-IDF cosine similarity + keyword overlap, both
 * plain Java -- no AI/ML model involved), then splits the results into
 * three tables by RankLevel so the person doesn't have to type notice IDs.
 */
public class MatchesController {

    @FXML private TableView<MatchRow> highTable;
    @FXML private TableView<MatchRow> mediumTable;
    @FXML private TableView<MatchRow> lowTable;

    @FXML private TableColumn<MatchRow, String> highCompanyColumn;
    @FXML private TableColumn<MatchRow, String> highTitleColumn;
    @FXML private TableColumn<MatchRow, String> highScoreColumn;
    @FXML private TableColumn<MatchRow, Number> highGapsColumn;

    @FXML private TableColumn<MatchRow, String> mediumCompanyColumn;
    @FXML private TableColumn<MatchRow, String> mediumTitleColumn;
    @FXML private TableColumn<MatchRow, String> mediumScoreColumn;
    @FXML private TableColumn<MatchRow, Number> mediumGapsColumn;

    @FXML private TableColumn<MatchRow, String> lowCompanyColumn;
    @FXML private TableColumn<MatchRow, String> lowTitleColumn;
    @FXML private TableColumn<MatchRow, String> lowScoreColumn;
    @FXML private TableColumn<MatchRow, Number> lowGapsColumn;

    @FXML private Label statusLabel;

    private final JobNoticeDao jobNoticeDao = new SqliteJobNoticeDao();
    private final CvDao cvDao = new SqliteCvDao();
    private final MatchResultDao matchResultDao = new SqliteMatchResultDao();

    @FXML
    public void initialize() {
        wireColumns(highCompanyColumn, highTitleColumn, highScoreColumn, highGapsColumn);
        wireColumns(mediumCompanyColumn, mediumTitleColumn, mediumScoreColumn, mediumGapsColumn);
        wireColumns(lowCompanyColumn, lowTitleColumn, lowScoreColumn, lowGapsColumn);
        rankAll();
    }

    private void wireColumns(TableColumn<MatchRow, String> company, TableColumn<MatchRow, String> title,
                              TableColumn<MatchRow, String> score, TableColumn<MatchRow, Number> gaps) {
        company.setCellValueFactory(new PropertyValueFactory<>("company"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        score.setCellValueFactory(new PropertyValueFactory<>("scoreDisplay"));
        gaps.setCellValueFactory(new PropertyValueFactory<>("gapCount"));
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        rankAll();
    }

    private void rankAll() {
        User currentUser = SessionContext.getCurrentUser();
        if (currentUser == null) return;

        Optional<Cv> cvOpt = cvDao.findByUserId(currentUser.getId());
        if (cvOpt.isEmpty()) {
            statusLabel.setText("You need to create a CV first (Dashboard -> My CV) before matches can be ranked.");
            highTable.setItems(FXCollections.observableArrayList());
            mediumTable.setItems(FXCollections.observableArrayList());
            lowTable.setItems(FXCollections.observableArrayList());
            return;
        }

        List<JobNotice> allNotices = jobNoticeDao.findAll();
        Map<Integer, JobNotice> noticeById = new HashMap<>();
        for (JobNotice n : allNotices) noticeById.put(n.getId(), n);

        JobProcessor jobProcessor = new JobProcessor(new TfIdfCosineStrategy(), new GapAnalyzer());
        MatchingService matchingService = new MatchingService(jobProcessor, matchResultDao);
        matchingService.addObserver(new HighMatchNotifier());

        // rankNotices() internally skips any notice whose NoticeState is not rankable (e.g. CLOSED).
        List<MatchResult> results = matchingService.rankNotices(currentUser, cvOpt.get(), allNotices);

        ObservableList<MatchRow> highRows = FXCollections.observableArrayList();
        ObservableList<MatchRow> mediumRows = FXCollections.observableArrayList();
        ObservableList<MatchRow> lowRows = FXCollections.observableArrayList();

        for (MatchResult r : results) {
            JobNotice notice = noticeById.get(r.getJobNoticeId());
            if (notice == null) continue;
            MatchRow row = new MatchRow(r, notice);
            switch (r.getRankLevel()) {
                case HIGH -> highRows.add(row);
                case MEDIUM -> mediumRows.add(row);
                case LOW -> lowRows.add(row);
            }
        }

        highTable.setItems(highRows);
        mediumTable.setItems(mediumRows);
        lowTable.setItems(lowRows);

        statusLabel.setText(String.format("%d open notice(s) ranked - %d high, %d medium, %d low match.",
                results.size(), highRows.size(), mediumRows.size(), lowRows.size()));
    }

    @FXML
    private void onViewGaps(ActionEvent event) {
        MatchRow selected = highTable.getSelectionModel().getSelectedItem();
        if (selected == null) selected = mediumTable.getSelectionModel().getSelectedItem();
        if (selected == null) selected = lowTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            statusLabel.setText("Select a notice from one of the tables first.");
            return;
        }

        List<GapRecommendation> gaps = selected.getResult().getGaps();
        StringBuilder sb = new StringBuilder();
        sb.append(selected.getTitle()).append(" at ").append(selected.getCompany())
                .append(" (").append(selected.getScoreDisplay()).append(" match)\n\n");

        if (gaps.isEmpty()) {
            sb.append("No gaps found -- your CV covers everything this notice looked for.");
        } else {
            sb.append("Gaps between your CV and this notice:\n");
            for (GapRecommendation g : gaps) {
                sb.append("- ").append(g.getMissingItem()).append(": ").append(g.getSuggestion()).append("\n");
            }
        }
        new Alert(Alert.AlertType.INFORMATION, sb.toString()).showAndWait();
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) highTable.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/dashboard.fxml", "CV Job Matcher - Dashboard");
        } catch (IOException e) {
            statusLabel.setText("Could not go back: " + e.getMessage());
        }
    }

    /** Row wrapper exposing bean-style getters for the three TableViews. */
    public static class MatchRow {
        private final MatchResult result;
        private final JobNotice notice;

        public MatchRow(MatchResult result, JobNotice notice) {
            this.result = result;
            this.notice = notice;
        }

        public MatchResult getResult() { return result; }
        public String getCompany() { return notice.getCompany(); }
        public String getTitle() { return notice.getTitle(); }
        public String getScoreDisplay() { return String.format("%.0f%%", result.getScore() * 100); }
        public int getGapCount() { return result.getGaps().size(); }
    }
}
