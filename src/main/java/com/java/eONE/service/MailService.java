package com.java.eONE.service;

import com.java.eONE.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendApprovalEmail(User user) {
        String subject = "Your Account Has Been Approved";
        String content = "<p>Dear " + user.getName() + ",</p>"
                + "<p>Your account has been approved. You can now log in to your account.</p>"
                + "<p>Thank you!</p>";

        sendEmail(user.getEmail(), subject, content);
    }

    public void sendRejectionEmail(User user) {
        String subject = "Your Account Has Been Rejected";
        String content = "<p>Dear " + user.getName() + ",</p>"
                + "<p>We regret to inform you that your account registration has been rejected.</p>"
                + "<p>Please contact support if you believe this is a mistake.</p>"
                + "<p>Thank you!</p>";

        sendEmail(user.getEmail(), subject, content);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);  // true = HTML

            mailSender.send(message);

            System.out.println("Email sent to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email to " + to);
        }
    }
}
