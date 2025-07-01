package com.rabbithole.email.service;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendOrderCreatedEmail(String to, Map<String, Object> variables) {
        sendEmailFromTemplate(to, "¡Tu orden ha sido recibida!", "email/order-created.html", variables);
    }

    public void sendOrderStatusChangedEmail(String to, Map<String, Object> variables) {
        sendEmailFromTemplate(to, "Actualización de estado de orden", "email/order-status-changed.html", variables);
    }

    public void sendDisenoStatusChangedEmail(String to, Map<String, Object> variables) {
        sendEmailFromTemplate(to, "Actualización de diseño personalizado", "email/diseno-status-changed.html",
                variables);
    }

    private void sendEmailFromTemplate(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process(templateName, context);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setFrom(new InternetAddress("notificaciones@rabbithole.com"));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(html, "text/html; charset=utf-8");
        };

        mailSender.send(messagePreparator);
    }
}
