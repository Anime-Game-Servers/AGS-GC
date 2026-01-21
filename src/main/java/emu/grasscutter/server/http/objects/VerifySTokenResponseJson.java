package emu.grasscutter.server.http.objects;

import java.util.List;

public class VerifySTokenResponseJson {
    public String message;
    public int retcode;
    public VerifyData data;

    public static class VerifyData {
        public UserInfoData user_info;
        public List<TokenData> tokens;
        public ExtUserInfoData ext_user_info;
    }

    public static class TokenData {
        public int token_type;
        public String token;
    }

    public static class UserInfoData {
        public String aid;
        public String mid;
        public String account_name;
        public String email;
        public int is_email_verify;
        public String area_code;
        public String mobile;
        public String safe_area_code;
        public String safe_mobile;
        public String realname;
        public String identity_code;
        public String rebind_area_code;
        public String rebind_mobile;
        public String rebind_mobile_time;
        public List<LinkData> links;
        public String country;
        public String password_time;
        public int is_adult;
        public String unmasked_email;
        public int unmasked_email_type;
    }

    public static class LinkData {
        public String thirdparty;
        public String union_id;
        public String nickname;
        public String email;
        public String subType;
        public String sub_union_id;
    }

    public static class ExtUserInfoData {
        public String guardian_email;
        public String birth;
    }
}
