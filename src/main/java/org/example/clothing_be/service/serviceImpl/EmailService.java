package org.example.clothing_be.service.serviceImpl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    public void sendEmail(String toEmail, String otp, String userName) {
        Email from = new Email("vinguyenha29@gmail.com"); // Email đã verify ở Bước 1
        Email to = new Email(toEmail);
        String htmlBody = "<html><body>" +
                "<h2>Chào " + userName + ",</h2>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản tại hệ thống của chúng tôi.</p>" +
                "<p>Vui lòng không cung cấp mã này cho bất kỳ ai</p>" + otp+
                "<br><p>Trân trọng,<br>Đội ngũ hỗ trợ kỹ thuật</p>" +
                "</body></html>";
        Content htmlContent = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, otp, to, htmlContent);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println("Status Code: " + response.getStatusCode());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
