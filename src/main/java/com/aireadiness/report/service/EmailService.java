package com.aireadiness.report.service;

import com.aireadiness.report.dto.ReportResponse;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Service for sending emails with report attachments
 * For MVP, this provides basic email functionality
 */
@Service
public class EmailService {
    
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
    
    /**
     * Send assessment report via email
     * For MVP, this logs the email action instead of actually sending
     * In production, this would integrate with email service (SendGrid, AWS SES, etc.)
     */
    public void sendReportEmail(String emailAddress, ReportResponse report, Resource reportFile) {
        
        // For MVP, log the email action
        logger.info("Email would be sent to: " + emailAddress);
        logger.info("Report ID: " + report.getReportId());
        logger.info("Report Title: " + report.getTitle());
        logger.info("File: " + reportFile.getFilename());
        
        // In production, implement actual email sending:
        /*
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(emailAddress);
            helper.setSubject("Your AI Readiness Assessment Report");
            helper.setText(generateEmailBody(report), true);
            helper.addAttachment(reportFile.getFilename(), reportFile);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
        */
    }
    
    /**
     * Generate email body content
     */
    private String generateEmailBody(ReportResponse report) {
        return String.format("""
            <html>
            <body>
                <h2>Your AI Readiness Assessment Report is Ready!</h2>
                <p>Dear Assessment Participant,</p>
                
                <p>Thank you for completing the AI Readiness Assessment. We're pleased to share your personalized report.</p>
                
                <h3>Report Summary:</h3>
                <ul>
                    <li><strong>Assessment ID:</strong> %s</li>
                    <li><strong>Report Title:</strong> %s</li>
                    <li><strong>Generated:</strong> %s</li>
                </ul>
                
                <p>Your comprehensive report is attached to this email. It includes:</p>
                <ul>
                    <li>Detailed score breakdown across all assessment pillars</li>
                    <li>Personalized recommendations for improvement</li>
                    <li>Gap analysis and action items</li>
                    <li>30-60-90 day learning plan suggestions</li>
                </ul>
                
                <h3>Next Steps:</h3>
                <ol>
                    <li>Review your assessment results</li>
                    <li>Focus on the highest impact recommendations</li>
                    <li>Create your personal development plan</li>
                    <li>Consider retaking the assessment in 3 months to track progress</li>
                </ol>
                
                <p>If you have any questions about your results or need support with your AI readiness journey, please don't hesitate to contact us.</p>
                
                <p>Best regards,<br>
                The AI Readiness Assessment Team</p>
                
                <hr>
                <p><small>This report is confidential and intended only for the assessment participant. Please do not share without permission.</small></p>
            </body>
            </html>
            """, 
            report.getAssessmentId(),
            report.getTitle(),
            report.getCreatedAt().toString()
        );
    }
    
    /**
     * Send notification email about assessment completion
     */
    public void sendAssessmentCompleteNotification(String emailAddress, String assessmentId) {
        logger.info("Assessment completion notification would be sent to: " + emailAddress);
        logger.info("Assessment ID: " + assessmentId);
        
        // In production, send actual notification email
    }
    
    /**
     * Send reminder email for incomplete assessments
     */
    public void sendAssessmentReminderEmail(String emailAddress, String assessmentId) {
        logger.info("Assessment reminder would be sent to: " + emailAddress);
        logger.info("Assessment ID: " + assessmentId);
        
        // In production, send actual reminder email
    }
}
