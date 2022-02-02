package com.agriguardian.service.interfaces;

public interface EmailSender {
    void send(String to, String email);

    static String buildEmailForAccountConfirmation(String name, String code){
        return "<div style=\"margin-bottom: 40px\">Hi " + name + "</div>\n" +
                "<div style=\"margin-bottom: 5px\">Welcome to AgriGuardian. Your verification code is: </div>\n" +
                "<div style=\"border-left: 4px solid gray;\">" + code + "</div>\n" +
                "<div style=\"margin-top:10px\">Enter this code in our app to activate your account</div>\n" +
                "<div style=\"margin-top:20px\">Email us at <a href=\"mailto:info@agriguardian.farm\">info@agriguardian.farm</a> if you require any assistance</div>\n" +
                "<div style=\"margin-top:40px\">Regards,</div>\n" +
                "<div style=\"margin-top:5px\">AgriGuardian Team</div>\n" +
                "<div style=\"margin-top:40px\">Please note this is an unmonitored inbox.</div>\n" +
                "<div style=\"margin-top:20px\">Follow us on social media</div>";
    }

    static String buildEmailWithTemporaryPassword(String name, String otp){
        return "<div>" + otp + "</div>";
    }
}

