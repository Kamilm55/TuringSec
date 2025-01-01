package com.turingSecApp.turingSec.model.enums;

public enum EmailTemplate {
    // Company
    COMPANY_SUBMITTED("A new report has been submitted",
            "Hello {companyName},\n\n"
                    + "A new report titled \"{reportTitle}\" has been submitted to {platformName}. The security team should begin reviewing the report shortly.\n\n"
                    + "We'll keep you informed as we make progress.\n\n"
                    + "Best regards,\n{platformTeam}"),

    COMPANY_ACCEPTED("Report accepted",
            "Hello {companyName},\n\n"
                    + "The security team has accepted the report titled \"{reportTitle}\". The next step is to resolve the payment process.\n\n"
                    + "Please review the details and let us know if any further steps are required.\n\n"
                    + "Best regards,\n{platformTeam}"),

    COMPANY_REJECTED("Report not accepted",
            "Hello {companyName},\n\n"
                    + "The security team has reviewed the report titled \"{reportTitle}\" on your company's page and determined that it does not meet the criteria for acceptance.\n\n"
                    + "You can view the details and rationale on your page. We appreciate your attention and will notify you of any future reports that may be relevant.\n\n"
                    + "Best regards,\n{platformTeam}"),

    COMPANY_UNDER_REVIEW("Report under review",
            "The report titled \"{reportTitle}\" has moved to the Under Review stage. The security team is currently analyzing the findings submitted by the hacker.\n\n"
                    + "We'll provide updates as the review progresses.\n\n"
                    + "Best regards,\n{platformTeam}"),

    // Hacker
    HACKER_SUBMITTED("Your report has been submitted successfully!",
            "Hello {hackerName},\n\n"
                    + "A new report titled \"{reportTitle}\" has been successfully submitted to {platformName}.\n\n"
                    + "Company’s security team will begin reviewing your report shortly. We’ll notify you as we make progress.\n\n"
                    + "Thank you for contributing to the security of our platform!\n\n"
                    + "Best regards,\n{platformTeam}"),

    HACKER_UNDER_REVIEW("Your report is now under review!",
            "Hello {hackerName},\n\n"
                    + "Your report titled \"{reportTitle}\" is now under review by the company’s security team.\n\n"
                    + "We’ll keep you informed as we make progress. Thank you for your patience and dedication to improving security!\n\n"
                    + "Best regards,\n{platformTeam}"),

    HACKER_ACCEPTED("Congratulations! Your report has been accepted!",
            "Hello {hackerName},\n\n"
                    + "Congratulations! Your report titled \"{reportTitle}\" has been accepted by the security team on {platformName}.\n\n"
                    + "If applicable, any rewards associated with your submission will be processed shortly.\n\n"
                    + "Thank you for your valuable contribution to platform security!\n\n"
                    + "Best regards,\n{platformTeam}"),

    HACKER_REJECTED("Update on your report: {reportTitle} ❌",
            "Hello {hackerName},\n\n"
                    + "Thank you for submitting your report titled \"{reportTitle}\". After careful review, the security team has decided to reject it.\n\n"
                    + "We appreciate your effort and encourage you to continue participating in our program.\n"
                    + "Thank you for your understanding and dedication!\n\n"
                    + "Best regards,\n{platformTeam}");

    private final String subject;
    private final String body;

    EmailTemplate(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
