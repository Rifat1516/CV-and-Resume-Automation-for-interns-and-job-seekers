package com.cvmatcher.ui;

import com.cvmatcher.dao.JobNoticeDao;
import com.cvmatcher.dao.SqliteJobNoticeDao;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.JobRequirement;
import com.cvmatcher.model.RequirementCategory;
import com.cvmatcher.service.DuplicateCheckService;
import com.cvmatcher.service.DuplicateResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Mirrors a real job-vacancy poster: company/title/location/salary/experience
 * up top, then separate "Responsibilities" and "Requirements" bullet lists
 * (one line per bullet), matching the sample vacancy notices this was
 * modeled on.
 */
public class UploadNoticeController {

    @FXML private TextField companyField;
    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private TextField salaryField;
    @FXML private TextField experienceField;
    @FXML private TextField totalPositionsField;
    @FXML private TextField deadlineField;
    @FXML private TextField contactEmailField;
    @FXML private TextArea responsibilitiesArea;
    @FXML private TextArea requirementsArea;
    @FXML private TextArea descriptionArea;
    @FXML private Label statusLabel;

    private final JobNoticeDao jobNoticeDao = new SqliteJobNoticeDao();
    private final DuplicateCheckService duplicateCheckService = new DuplicateCheckService(jobNoticeDao);

    @FXML
    private void onSubmit(ActionEvent event) {
        var currentUser = SessionContext.getCurrentUser();
        if (currentUser == null) return;

        String company = companyField.getText().trim();
        String title = titleField.getText().trim();
        if (company.isEmpty() || title.isEmpty()) {
            statusLabel.setText("Company and job title are required.");
            return;
        }

        int totalPositions;
        try {
            totalPositions = Integer.parseInt(totalPositionsField.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("Total positions must be a number.");
            return;
        }

        JobNotice candidate = new JobNotice(company, title, totalPositions, currentUser.getId());
        candidate.setLocation(blankToNull(locationField.getText()));
        candidate.setSalary(blankToNull(salaryField.getText()));
        candidate.setExperienceRequired(blankToNull(experienceField.getText()));
        candidate.setDeadline(blankToNull(deadlineField.getText()));
        candidate.setContactEmail(blankToNull(contactEmailField.getText()));
        candidate.setDescription(blankToNull(descriptionArea.getText()));

        for (String line : linesOf(responsibilitiesArea.getText())) {
            candidate.getRequirements().add(new JobRequirement(RequirementCategory.RESPONSIBILITY, line));
        }
        for (String line : linesOf(requirementsArea.getText())) {
            candidate.getRequirements().add(new JobRequirement(RequirementCategory.REQUIREMENT, line));
        }

        DuplicateResult duplicateResult = duplicateCheckService.check(candidate);
        if (duplicateResult.isDuplicate()) {
            statusLabel.setText(duplicateResult.describe());
            new Alert(Alert.AlertType.WARNING, duplicateResult.describe()).showAndWait();
            return;
        }

        jobNoticeDao.save(candidate);
        statusLabel.setText("Notice uploaded successfully (ID: " + candidate.getId() + ")");
        clearForm();
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) companyField.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/dashboard.fxml", "CV Job Matcher - Dashboard");
        } catch (IOException e) {
            statusLabel.setText("Could not go back: " + e.getMessage());
        }
    }

    private String[] linesOf(String text) {
        if (text == null || text.isBlank()) return new String[0];
        return text.lines().map(String::trim).filter(l -> !l.isEmpty()).toArray(String[]::new);
    }

    private String blankToNull(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void clearForm() {
        companyField.clear();
        titleField.clear();
        locationField.clear();
        salaryField.clear();
        experienceField.clear();
        totalPositionsField.clear();
        deadlineField.clear();
        contactEmailField.clear();
        responsibilitiesArea.clear();
        requirementsArea.clear();
        descriptionArea.clear();
    }
}
