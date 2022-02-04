package com.agriguardian.service.interfaces;

public interface EmailSender {
    void send(String to, String email, String subject);

    static String buildEmailForAccountConfirmation(String name, String code){
        return "<div style=\"margin-bottom: 40px\">Hi " + name + "</div>\n" +
                "<div style=\"margin-bottom: 5px\">Welcome to AgriGuardian. Your verification code is: </div>\n" +
                "<div style=\"border-left: 4px solid gray;\">" + code + "</div>\n" +
                "<div style=\"margin-top:10px\">Enter this code in our app to activate your account</div>\n" +
                "<div style=\"margin-top:20px\">Email us at <a href=\"mailto:info@agriguardian.farm\">info@agriguardian.farm</a> if you require any assistance</div>\n" +
                "<div style=\"margin-top:40px\">Regards,</div>\n" +
                "<div style=\"margin-top:5px\">AgriGuardian Team</div>\n" +
                "<div style=\"margin-top:40px\">Please note this is an unmonitored inbox.</div>\n" +
                "<div style=\"margin-top:20px\">Follow us on social media</div>\n" +
                "<div style=\"margin-top:20px; background-color: gray; display: inline-flex; height: 50px; justify-content: space-around;align-items: center; align-content: center\"><a href=\"https://www.tiktok.com/@agriguardian\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/logo-tiktok-icon-444x512-n9hkw85v.png\"/></a> <a href=\"https://www.instagram.com/agriguardian/\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/instagram-icon-512x512-85ckvxzj.png\"/></a> <a href=\"https://fb.me/AgriGuardianIE\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/social-facebook-icon-487x512-52jsgay6.png\"/></a><a href=\"https://www.youtube.com/channel/UCVT5Wy1Dw1OuSxpyJM4Q2LQ\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/youtube-icon-512x360-3cmbxj37.png\"/></a></div>";
    }

    static String buildEmailWithTemporaryPassword(String name, String otp){
        return "<div style=\"margin-bottom: 40px\">Hi " + name + "</div>\n" +
                "<div style=\"margin-bottom: 5px\">Your temporary password is: </div>\n" +
                "<div style=\"border-left: 4px solid gray;\">" + otp + "</div>\n" +
                "<div style=\"margin-top:10px\">This password will be valid for 30 minutes</div>\n" +
                "<div style=\"margin-top:10px\">Please ignore this message if this was not you</div>\n" +
                "<div style=\"margin-top:20px\">Email us at <a href=\"mailto:info@agriguardian.farm\">info@agriguardian.farm</a> if you require any assistance</div>\n" +
                "<div style=\"margin-top:40px\">Regards,</div>\n" +
                "<div style=\"margin-top:5px\">AgriGuardian Team</div>\n" +
                "<div style=\"margin-top:40px\">Please note this is an unmonitored inbox.</div>\n" +
                "<div style=\"margin-top:20px\">Follow us on social media</div>\n" +
                "<div style=\"margin-top:20px; background-color: gray; display: inline-flex; height: 50px; justify-content: space-around;align-items: center; align-content: center\"><a href=\"https://www.tiktok.com/@agriguardian\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/logo-tiktok-icon-444x512-n9hkw85v.png\"/></a> <a href=\"https://www.instagram.com/agriguardian/\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/instagram-icon-512x512-85ckvxzj.png\"/></a> <a href=\"https://fb.me/AgriGuardianIE\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/social-facebook-icon-487x512-52jsgay6.png\"/></a><a href=\"https://www.youtube.com/channel/UCVT5Wy1Dw1OuSxpyJM4Q2LQ\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/youtube-icon-512x360-3cmbxj37.png\"/></a></div>";
    }

    static String buildEmailWithInstructions(String name){
        return "<div style=\"margin-bottom: 40px\">Hi " + name + "</div>\n" +
                "<div style=\"margin-bottom: 5px\">You have requested temporary password but your account is not activated yet</div>\n" +
                "<div style=\"border-left: 4px solid gray;\">please activate your account first</div>\n" +
                "<div style=\"margin-top:10px\">Please ignore this message if this was not you</div>\n" +
                "<div style=\"margin-top:20px\">Email us at <a href=\"mailto:info@agriguardian.farm\">info@agriguardian.farm</a> if you require any assistance</div>\n" +
                "<div style=\"margin-top:40px\">Regards,</div>\n" +
                "<div style=\"margin-top:5px\">AgriGuardian Team</div>\n" +
                "<div style=\"margin-top:40px\">Please note this is an unmonitored inbox.</div>\n" +
                "<div style=\"margin-top:20px\">Follow us on social media</div>\n" +
                "<div style=\"margin-top:20px; background-color: gray; display: inline-flex; height: 50px; justify-content: space-around;align-items: center; align-content: center\"><a href=\"https://www.tiktok.com/@agriguardian\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/logo-tiktok-icon-444x512-n9hkw85v.png\"/></a> <a href=\"https://www.instagram.com/agriguardian/\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/instagram-icon-512x512-85ckvxzj.png\"/></a> <a href=\"https://fb.me/AgriGuardianIE\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/social-facebook-icon-487x512-52jsgay6.png\"/></a><a href=\"https://www.youtube.com/channel/UCVT5Wy1Dw1OuSxpyJM4Q2LQ\"><img style=\"max-height: 35px; margin-left: 5px; margin-right: 5px\" src=\"https://static-00.iconduck.com/assets.00/youtube-icon-512x360-3cmbxj37.png\"/></a></div>";
    }

}

