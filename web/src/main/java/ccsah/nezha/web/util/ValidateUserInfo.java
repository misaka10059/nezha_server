package ccsah.nezha.web.util;

/**
 * Created by rexxar on 19-11-11.
 */
public class ValidateUserInfo {
    public static boolean isMobileNumber(String mobileNumber) {
        try {
            long number = Long.parseLong(mobileNumber);
            return number > 10000000000L && number < 20000000000L;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEmail(String email) {
        return email != null && email.contains("@");
    }

    public static int getInnerUsernumber(String info) {
        return Integer.parseInt(info) % 10000;
    }

    public static String getDisplayUsernumber(int usernumber) {
        return "340" + (usernumber + 10000);
    }
}
