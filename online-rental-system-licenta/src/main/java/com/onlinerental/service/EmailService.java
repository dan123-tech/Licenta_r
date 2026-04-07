package com.onlinerental.service;

import com.onlinerental.config.AppProperties;
import com.onlinerental.domain.Rental;
import com.onlinerental.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final AppProperties appProperties;

    @SuppressWarnings("null")
    public void sendConfirmationEmail(User user, String token) {
        String baseUrl = appProperties.getPublicBaseUrl();
        String confirmUrl = (baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl)
                + "/api/v1/auth/confirm?token=" + token;

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("JavaMailSender indisponibil. Link confirmare pentru {}: {}", user.getEmail(), confirmUrl);
            return;
        }

        try {
            @SuppressWarnings("null")
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(requiredValue(appProperties.getMail().getFrom(), "APP_MAIL_FROM"));
            helper.setTo(requiredValue(user.getEmail(), "user email"));
            helper.setSubject("Confirmare cont - Online Rental System");
            helper.setText(buildConfirmationEmailHtml(user.getUsername(), confirmUrl), true);
            helper.addInline("appLogo", new ClassPathResource("mail/logo-horizontal.svg"), "image/svg+xml");
            mailSender.send(mime);
        } catch (Exception ex) {
            log.warn("Nu s-a putut trimite emailul de confirmare către {}: {}. Link: {}",
                    user.getEmail(), ex.getMessage(), confirmUrl);
        }
    }

    @SuppressWarnings("null")
    public boolean sendPaymentInvoiceEmail(User user, Rental rental, String transactionId, byte[] invoicePdf) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("JavaMailSender indisponibil. Factura pentru {} nu a fost trimisă.", user.getEmail());
            return false;
        }
        try {
            @SuppressWarnings("null")
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(requiredValue(appProperties.getMail().getFrom(), "APP_MAIL_FROM"));
            helper.setTo(requiredValue(user.getEmail(), "user email"));
            helper.setSubject("Confirmare plată + factură");
            helper.setText(
                    buildInvoiceEmailHtml(
                            user.getUsername(),
                            rental.getId(),
                            transactionId,
                            rental.getTotalPrice().toPlainString()
                    ),
                    true
            );
            helper.addInline("appLogo", new ClassPathResource("mail/logo-horizontal.svg"), "image/svg+xml");
            if (invoicePdf != null && invoicePdf.length > 0) {
                helper.addAttachment(
                        "invoice-" + rental.getId() + ".pdf",
                        new ByteArrayResource(invoicePdf),
                        "application/pdf"
                );
            }
            mailSender.send(mime);
            log.info("Factura trimisă către {} pentru rental #{}", user.getEmail(), rental.getId());
            return true;
        } catch (Exception ex) {
            log.warn("Nu s-a putut trimite factura către {} pentru rental #{}",
                    user.getEmail(), rental.getId(), ex);
            return false;
        }
    }

    @SuppressWarnings("null")
    public void sendReservationCreatedEmail(User user, Rental rental) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("JavaMailSender indisponibil. Confirmarea rezervării pentru {} nu a fost trimisă.", user.getEmail());
            return;
        }
        try {
            @SuppressWarnings("null")
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(requiredValue(appProperties.getMail().getFrom(), "APP_MAIL_FROM"));
            helper.setTo(requiredValue(user.getEmail(), "user email"));
            helper.setSubject("Rezervare creată - Online Rental System");
            helper.setText(
                    buildReservationCreatedEmailHtml(
                            user.getUsername(),
                            rental.getId(),
                            rental.getInventory().getProduct().getName(),
                            rental.getStartDate().toString(),
                            rental.getEndDate().toString(),
                            rental.getTotalPrice().toPlainString()
                    ),
                    true
            );
            helper.addInline("appLogo", new ClassPathResource("mail/logo-horizontal.svg"), "image/svg+xml");
            mailSender.send(mime);
        } catch (Exception ex) {
            log.warn("Nu s-a putut trimite confirmarea rezervării către {}: {}", user.getEmail(), ex.getMessage());
        }
    }

    private String buildConfirmationEmailHtml(String username, String confirmUrl) {
        return "<!DOCTYPE html>"
                + "<html><body style=\"margin:0;padding:0;background:#f2f4f8;font-family:'Segoe UI',Arial,sans-serif;color:#1f2937;\">"
                + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"padding:28px 12px;\">"
                + "<tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"640\" cellspacing=\"0\" cellpadding=\"0\" style=\"max-width:640px;background:#ffffff;border:1px solid #dde3ec;border-radius:16px;overflow:hidden;\">"
                + "<tr><td style=\"background:linear-gradient(135deg,#0f1318,#1f2a38);padding:22px 28px;\">"
                + "<img src=\"cid:appLogo\" alt=\"Online Rental System\" style=\"display:block;height:40px;width:auto;max-width:100%;\"/>"
                + "</td></tr>"
                + "<tr><td style=\"padding:28px;\">"
                + "<div style=\"font-size:12px;font-weight:700;letter-spacing:0.7px;color:#2563eb;text-transform:uppercase;margin-bottom:10px;\">Activare cont</div>"
                + "<div style=\"font-size:25px;font-weight:800;line-height:1.2;margin-bottom:10px;color:#111827;\">Confirmă contul tău</div>"
                + "<div style=\"font-size:15px;line-height:1.65;color:#374151;margin-bottom:20px;\">"
                + "Salut <strong>" + escapeHtml(username) + "</strong>,<br/>"
                + "Mulțumim pentru înregistrare. Pentru a activa contul, apasă butonul de mai jos."
                + "</div>"
                + "<div style=\"background:#f8fafc;border:1px solid #e2e8f0;border-radius:12px;padding:14px 16px;margin-bottom:18px;color:#334155;font-size:14px;line-height:1.6;\">"
                + "Experiență similară cu aplicația: dashboard clar, pași rapizi și confirmări instant pentru rezervări."
                + "</div>"
                + "<div style=\"text-align:center;padding:4px 0 16px 0;\">"
                + "<a href=\"" + confirmUrl + "\" style=\"display:inline-block;background:#2563eb;color:#ffffff;text-decoration:none;padding:13px 24px;border-radius:10px;font-weight:700;\">Confirmă emailul</a>"
                + "</div>"
                + "<div style=\"font-size:14px;line-height:1.6;color:#4b5563;padding-bottom:8px;\">"
                + "Dacă butonul nu funcționează, folosește acest "
                + "<a href=\"" + confirmUrl + "\" style=\"color:#2563eb;text-decoration:underline;\">link de confirmare</a>."
                + "</div>"
                + "<div style=\"font-size:13px;line-height:1.6;color:#6b7280;\">"
                + "Dacă nu ai creat acest cont, poți ignora acest mesaj."
                + "</div>"
                + "</td></tr>"
                + "<tr><td style=\"border-top:1px solid #e5e7eb;padding:14px 28px;font-size:12px;color:#64748b;background:#fafcff;\">"
                + "Online Rental System - rapid, sigur, modern."
                + "</td></tr>"
                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }

    private String buildInvoiceEmailHtml(String username, Long rentalId, String transactionId, String totalPrice) {
        return "<!DOCTYPE html>"
                + "<html><body style=\"margin:0;padding:0;background:#f2f4f8;font-family:'Segoe UI',Arial,sans-serif;color:#1f2937;\">"
                + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"padding:28px 12px;\">"
                + "<tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"640\" cellspacing=\"0\" cellpadding=\"0\" style=\"max-width:640px;background:#ffffff;border:1px solid #dde3ec;border-radius:16px;overflow:hidden;\">"
                + "<tr><td style=\"background:linear-gradient(135deg,#0f1318,#1f2a38);padding:22px 28px;\">"
                + "<img src=\"cid:appLogo\" alt=\"Online Rental System\" style=\"display:block;height:40px;width:auto;max-width:100%;\"/>"
                + "</td></tr>"
                + "<tr><td style=\"padding:28px;\">"
                + "<div style=\"font-size:12px;font-weight:700;letter-spacing:0.7px;color:#16a34a;text-transform:uppercase;margin-bottom:10px;\">Plată confirmată</div>"
                + "<div style=\"font-size:25px;font-weight:800;line-height:1.2;margin-bottom:10px;color:#111827;\">Totul este în regulă</div>"
                + "<div style=\"font-size:15px;line-height:1.65;color:#374151;margin-bottom:18px;\">"
                + "Salut <strong>" + escapeHtml(username) + "</strong>,<br/>"
                + "Am confirmat plata pentru închirierea ta. Factura PDF este atașată la acest email."
                + "</div>"
                + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #e2e8f0;border-radius:12px;background:#f8fafc;\">"
                + "<tr><td style=\"padding:14px 16px;font-size:14px;color:#334155;line-height:1.8;\">"
                + "<strong>Închiriere #:</strong> " + rentalId + "<br/>"
                + "<strong>Transaction ID:</strong> " + escapeHtml(transactionId) + "<br/>"
                + "<strong>Total:</strong> <span style=\"color:#0f172a;font-weight:800;\">" + escapeHtml(totalPrice) + " RON</span>"
                + "</td></tr></table>"
                + "</td></tr>"
                + "<tr><td style=\"border-top:1px solid #e5e7eb;padding:14px 28px;font-size:12px;color:#64748b;background:#fafcff;\">"
                + "Mulțumim că folosești Online Rental System."
                + "</td></tr>"
                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }

    private String buildReservationCreatedEmailHtml(
            String username,
            Long rentalId,
            String productName,
            String startDate,
            String endDate,
            String totalPrice
    ) {
        return "<!DOCTYPE html>"
                + "<html><body style=\"margin:0;padding:0;background:#f2f4f8;font-family:'Segoe UI',Arial,sans-serif;color:#1f2937;\">"
                + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"padding:28px 12px;\">"
                + "<tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"640\" cellspacing=\"0\" cellpadding=\"0\" style=\"max-width:640px;background:#ffffff;border:1px solid #dde3ec;border-radius:16px;overflow:hidden;\">"
                + "<tr><td style=\"background:linear-gradient(135deg,#0f1318,#1f2a38);padding:22px 28px;\">"
                + "<img src=\"cid:appLogo\" alt=\"Online Rental System\" style=\"display:block;height:40px;width:auto;max-width:100%;\"/>"
                + "</td></tr>"
                + "<tr><td style=\"padding:28px;\">"
                + "<div style=\"font-size:12px;font-weight:700;letter-spacing:0.7px;color:#2563eb;text-transform:uppercase;margin-bottom:10px;\">Rezervare nouă</div>"
                + "<div style=\"font-size:25px;font-weight:800;line-height:1.2;margin-bottom:10px;color:#111827;\">Rezervarea ta a fost creată</div>"
                + "<div style=\"font-size:15px;line-height:1.65;color:#374151;margin-bottom:18px;\">"
                + "Salut <strong>" + escapeHtml(username) + "</strong>,<br/>"
                + "Rezervarea a fost înregistrată cu succes. Detaliile principale sunt mai jos."
                + "</div>"
                + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #e2e8f0;border-radius:12px;background:#f8fafc;\">"
                + "<tr><td style=\"padding:14px 16px;font-size:14px;color:#334155;line-height:1.8;\">"
                + "<strong>Rezervare #:</strong> " + rentalId + "<br/>"
                + "<strong>Produs:</strong> " + escapeHtml(productName) + "<br/>"
                + "<strong>Perioadă:</strong> " + escapeHtml(startDate) + " - " + escapeHtml(endDate) + "<br/>"
                + "<strong>Total:</strong> <span style=\"color:#0f172a;font-weight:800;\">" + escapeHtml(totalPrice) + " RON</span>"
                + "</td></tr></table>"
                + "<div style=\"font-size:13px;line-height:1.6;color:#6b7280;margin-top:16px;\">"
                + "După confirmarea plății vei primi și emailul cu factura."
                + "</div>"
                + "</td></tr>"
                + "<tr><td style=\"border-top:1px solid #e5e7eb;padding:14px 28px;font-size:12px;color:#64748b;background:#fafcff;\">"
                + "Online Rental System - rapid, sigur, modern."
                + "</td></tr>"
                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String requiredValue(String value, String field) {
        return Objects.requireNonNull(value, field + " must not be null");
    }
}

