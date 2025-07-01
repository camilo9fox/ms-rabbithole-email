package com.rabbithole.email.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbithole.email.dto.DisenoEventDTO;
import com.rabbithole.email.dto.OrderEventDTO;
import com.rabbithole.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailKafkaConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Desenvuelve mensajes JSON que vienen como String entrecomillado y escapado.
     * Ej: "{\"key\":\"value\"}" -> {"key":"value"}
     */
    private String unwrapJson(String msg) {
        if (msg == null) return "";
        msg = msg.trim();
        // Detecta comillas dobles externas
        if (msg.length() >= 2 && msg.startsWith("\"") && msg.endsWith("\"")) {
            // quita comillas exteriores y desescapa
            msg = msg.substring(1, msg.length() - 1)
                     .replace("\\\"", "\"")
                     .replace("\\n", "")
                     .replace("\\r", "");
        }
        return msg;
    }
    private final EmailService emailService;

    @KafkaListener(topics = "order-events", groupId = "email-service")
    public void listenOrderEvents(@Payload String message, ConsumerRecord<String, String> record) {
        try {
            OrderEventDTO event = objectMapper.readValue(unwrapJson(message), OrderEventDTO.class);
            log.info("Recibido evento de orden: {}", event);

            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", event.getUserName());
            variables.put("total", event.getTotal());
            variables.put("currency", event.getCurrency());
        variables.put("orderId", event.getOrderId());
            variables.put("items", event.getItems());
            variables.put("newStatus", event.getNewStatus());
            // TODO: obtener más datos de orden/usuario mediante API REST si es necesario

            switch (event.getType()) {
                case "CREATED" -> emailService.sendOrderCreatedEmail(event.getUserEmail(), variables);
                case "STATUS_CHANGED", "UPDATED" -> emailService.sendOrderStatusChangedEmail(event.getUserEmail(), variables);
                default -> log.warn("Tipo de evento de orden no soportado: {}", event.getType());
            }
        } catch (JsonProcessingException e) {
            log.error("Error parseando evento de orden", e);
        } catch (Exception ex) {
            log.error("Error procesando evento de orden", ex);
        }
    }

    @KafkaListener(topics = "diseno-events", groupId = "email-service")
    public void listenDisenoEvents(@Payload String message, ConsumerRecord<String, String> record) {
        try {
            DisenoEventDTO event = objectMapper.readValue(unwrapJson(message), DisenoEventDTO.class);
            log.info("Recibido evento de diseño: {}", event);

            Map<String, Object> variables = new HashMap<>();
            variables.put("designName", event.getDesignName());
            variables.put("previewUrl", event.getPreviewUrl());
            variables.put("newStatus", event.getNewStatus());
            variables.put("userName", event.getUserName());
            // TODO: obtener más datos de diseño/usuario si es necesario

            emailService.sendDisenoStatusChangedEmail(event.getUserEmail(), variables);
        } catch (JsonProcessingException e) {
            log.error("Error parseando evento de diseño", e);
        } catch (Exception ex) {
            log.error("Error procesando evento de diseño", ex);
        }
    }
}
