package com.pictura_backend.services.auth;

import com.pictura_backend.util.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final static Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String to, String username, EmailTemplateName emailTemplate,
                          String confirmationUrl, String activationCode, String subject
    ) {
        try {
            String templateName = emailTemplate != null ? emailTemplate.name() : "confirm_email";

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED,
                    StandardCharsets.UTF_8.name()
            );
            Map<String, Object> properties = new HashMap<>();
            properties.put("username", username);
            properties.put("confirmation", confirmationUrl);
            properties.put("activation_code", activationCode);

            Context context = new Context();
            context.setVariables(properties);

            helper.setFrom("contact@pictura.com");
            helper.setTo(to);
            helper.setSubject(subject);

            String template = templateEngine.process(templateName, context);

            helper.setText(template, true);
            mailSender.send(mimeMessage);

            logger.info("Email sent to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }
}