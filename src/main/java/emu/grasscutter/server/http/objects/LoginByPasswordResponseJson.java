package emu.grasscutter.server.http.objects;
import emu.grasscutter.server.http.objects.BaseLoginResponseData.*;

public class LoginByPasswordResponseJson {
    public String message;
    public int retcode;
    public LoginData data;

    public record LoginData (
        TokenData token,
        UserInfoData user_info,
        ExtUserInfoData ext_user_info,
        String reactivate_action_ticket,
        String bind_email_action_ticket
    ){}
}
