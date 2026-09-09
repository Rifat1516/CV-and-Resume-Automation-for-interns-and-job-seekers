package com.cvmatcher.ui;

import com.cvmatcher.dao.CvDao;
import com.cvmatcher.dao.SqliteCvDao;
import com.cvmatcher.model.Certification;
import com.cvmatcher.model.Cv;
import com.cvmatcher.model.Education;
import com.cvmatcher.model.Experience;
import com.cvmatcher.model.User;
import com.cvmatcher.service.CvBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Builds a Cv section by section, backed by the same CvBuilder (Builder
 * pattern) the rest of the app already uses. Each repeatable section
 * (education, experience, skills, certifications, languages, awards,
 * affiliations) is entered one item at a time and appended to an
 * in-memory list + a ListView the person can review before saving.
 */
public class CvBuilderController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextArea summaryArea;

    @FXML private TextField skillField;
    @FXML private ListView<String> skillListView;

    @FXML private TextField eduInstitutionField;
    @FXML private TextField eduDegreeField;
    @FXML private TextField eduFieldField;
    @FXML private TextField eduStartYearField;
    @FXML private TextField eduEndYearField;
    @FXML private ListView<String> educationListView;

    @FXML private TextField expCompanyField;
    @FXML private TextField expTitleField;
    @FXML private TextArea expDescriptionArea;
    @FXML private TextField expStartYearField;
    @FXML private TextField expEndYearField;
    @FXML private ListView<String> experienceListView;

    @FXML private TextField certNameField;
    @FXML private TextField certIssuerField;
    @FXML private TextField certYearField;
    @FXML private ListView<String> certificationListView;

    @FXML private TextField langNameField;
    @FXML private TextField langProficiencyField;
    @FXML private ListView<String> languageListView;

    @FXML private TextField awardField;
    @FXML private ListView<String> awardListView;

    @FXML private TextField affiliationField;
    @FXML private ListView<String> affiliationListView;

    @FXML private Label statusLabel;

    private final CvDao cvDao = new SqliteCvDao();

    private final List<com.cvmatcher.model.Skill> skills = new ArrayList<>();
    private final List<Education> educationEntries = new ArrayList<>();
    private final List<Experience> experienceEntries = new ArrayList<>();
    private final List<Certification> certifications = new ArrayList<>();
    private final List<com.cvmatcher.model.Language> languages = new ArrayList<>();
    private final List<String> awards = new ArrayList<>();
    private final List<String> affiliations = new ArrayList<>();

    @FXML
    public void initialize() {
        User currentUser = SessionContext.getCurrentUser();
        if (currentUser != null && fullNameField.getText().isEmpty()) {
            fullNameField.setText(currentUser.getFullName());
        }

        Optional<Cv> existing = cvDao.findByUserId(currentUser.getId());
        existing.ifPresent(this::prefillFrom);
    }

    private void prefillFrom(Cv cv) {
        fullNameField.setText(cv.getFullName());
        emailField.setText(cv.getEmail());
        summaryArea.setText(cv.getSummary());

        cv.getSkills().forEach(s -> { skills.add(s); skillListView.getItems().add(s.getName()); });
        cv.getEducationList().forEach(e -> { educationEntries.add(e); educationListView.getItems().add(summarize(e)); });
        cv.getExperienceList().forEach(e -> { experienceEntries.add(e); experienceListView.getItems().add(summarize(e)); });
        cv.getCertifications().forEach(c -> { certifications.add(c); certificationListView.getItems().add(summarize(c)); });
        cv.getLanguages().forEach(l -> { languages.add(l); languageListView.getItems().add(summarize(l)); });
        cv.getAwards().forEach(a -> { awards.add(a.getDescription()); awardListView.getItems().add(a.getDescription()); });
        cv.getAffiliations().forEach(a -> { affiliations.add(a.getDescription()); affiliationListView.getItems().add(a.getDescription()); });

        statusLabel.setText("Loaded your existing CV. Saving again will replace it.");
    }

    @FXML
    private void onAddSkill(ActionEvent event) {
        String name = skillField.getText().trim();
        if (name.isEmpty()) return;
        skills.add(new com.cvmatcher.model.Skill(name));
        skillListView.getItems().add(name);
        skillField.clear();
    }

    @FXML
    private void onAddEducation(ActionEvent event) {
        Integer startYear = parseYearOrNull(eduStartYearField.getText());
        Integer endYear = parseYearOrNull(eduEndYearField.getText());
        Education e = new Education(
                eduInstitutionField.getText().trim(),
                eduDegreeField.getText().trim(),
                eduFieldField.getText().trim(),
                startYear, endYear
        );
        educationEntries.add(e);
        educationListView.getItems().add(summarize(e));
        eduInstitutionField.clear();
        eduDegreeField.clear();
        eduFieldField.clear();
        eduStartYearField.clear();
        eduEndYearField.clear();
    }

    @FXML
    private void onAddExperience(ActionEvent event) {
        Integer startYear = parseYearOrNull(expStartYearField.getText());
        Integer endYear = parseYearOrNull(expEndYearField.getText());
        Experience e = new Experience(
                expCompanyField.getText().trim(),
                expTitleField.getText().trim(),
                expDescriptionArea.getText().trim(),
                startYear, endYear
        );
        experienceEntries.add(e);
        experienceListView.getItems().add(summarize(e));
        expCompanyField.clear();
        expTitleField.clear();
        expDescriptionArea.clear();
        expStartYearField.clear();
        expEndYearField.clear();
    }

    @FXML
    private void onAddCertification(ActionEvent event) {
        Integer year = parseYearOrNull(certYearField.getText());
        Certification c = new Certification(certNameField.getText().trim(), certIssuerField.getText().trim(), year);
        certifications.add(c);
        certificationListView.getItems().add(summarize(c));
        certNameField.clear();
        certIssuerField.clear();
        certYearField.clear();
    }

    @FXML
    private void onAddLanguage(ActionEvent event) {
        String name = langNameField.getText().trim();
        if (name.isEmpty()) return;
        com.cvmatcher.model.Language l = new com.cvmatcher.model.Language(name, langProficiencyField.getText().trim());
        languages.add(l);
        languageListView.getItems().add(summarize(l));
        langNameField.clear();
        langProficiencyField.clear();
    }

    @FXML
    private void onAddAward(ActionEvent event) {
        String text = awardField.getText().trim();
        if (text.isEmpty()) return;
        awards.add(text);
        awardListView.getItems().add(text);
        awardField.clear();
    }

    @FXML
    private void onAddAffiliation(ActionEvent event) {
        String text = affiliationField.getText().trim();
        if (text.isEmpty()) return;
        affiliations.add(text);
        affiliationListView.getItems().add(text);
        affiliationField.clear();
    }

    @FXML
    private void onSaveCv(ActionEvent event) {
        User currentUser = SessionContext.getCurrentUser();
        if (currentUser == null) return;

        try {
            CvBuilder builder = new CvBuilder()
                    .forUser(currentUser.getId())
                    .withFullName(fullNameField.getText().trim())
                    .withEmail(emailField.getText().trim())
                    .withSummary(summaryArea.getText().trim());

            skills.forEach(builder::addSkill);
            educationEntries.forEach(builder::addEducation);
            experienceEntries.forEach(builder::addExperience);
            certifications.forEach(builder::addCertification);
            languages.forEach(builder::addLanguage);
            awards.forEach(builder::addAward);
            affiliations.forEach(builder::addAffiliation);

            Cv cv = builder.build();
            cvDao.save(cv);
            statusLabel.setText("CV saved (ID: " + cv.getId() + ")");
        } catch (IllegalStateException e) {
            statusLabel.setText(e.getMessage());
        } catch (RuntimeException e) {
            statusLabel.setText("Could not save CV: " + e.getMessage());
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) fullNameField.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/dashboard.fxml", "CV Job Matcher - Dashboard");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not go back: " + e.getMessage()).showAndWait();
        }
    }

    private Integer parseYearOrNull(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String summarize(Education e) {
        return e.getDegree() + " in " + e.getFieldOfStudy() + " - " + e.getInstitution()
                + (e.getStartYear() != null ? " (" + e.getStartYear() + "-" + e.getEndYear() + ")" : "");
    }

    private String summarize(Experience e) {
        return e.getTitle() + " at " + e.getCompany()
                + (e.getStartYear() != null ? " (" + e.getStartYear() + "-" + e.getEndYear() + ")" : "");
    }

    private String summarize(Certification c) {
        return c.getName() + (c.getIssuer() != null && !c.getIssuer().isBlank() ? " - " + c.getIssuer() : "")
                + (c.getYear() != null ? " (" + c.getYear() + ")" : "");
    }

    private String summarize(com.cvmatcher.model.Language l) {
        return l.getName() + (l.getProficiency() != null && !l.getProficiency().isBlank() ? " - " + l.getProficiency() : "");
    }
}
