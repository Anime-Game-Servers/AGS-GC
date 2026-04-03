package emu.grasscutter.server.http.objects;

import emu.grasscutter.game.Account;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

public class BaseLoginResponseData{

    public record TokenData(
        int token_type,
        String token
    ){}

    @Builder
    public record UserInfoData (
        String aid,
        String mid,
        String account_name,
        String email,
        int is_email_verify,
        String area_code,
        String mobile,
        String safe_area_code,
        String safe_mobile,
        String realname,
        String identity_code,
        String rebind_area_code,
        String rebind_mobile,
        String rebind_mobile_time,
        List<LinkData> links,
        String country,
        String password_time,
        int is_adult,
        String unmasked_email,
        int unmasked_email_type
    ){
        public static UserInfoData fromAccount(Account account){
            return new BaseLoginResponseData.UserInfoData(
                account.getId(),
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                0,
                "**",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "315532800",
                new ArrayList<>(),
                "US",
                "1762297200",
                1,
                "",
                0
                );
        }
    }

    public record LinkData (
        String thirdparty,
        String union_id,
        String nickname,
        String email,
        String subType,
        String sub_union_id
    ){}

    public record ExtUserInfoData (
        String guardian_email,
        String birth
    ){}
}
