package emu.grasscutter.server.http.objects;

import java.util.List;
import emu.grasscutter.server.http.objects.BaseLoginResponseData.*;

public class VerifySTokenResponseJson {
    public String message;
    public int retcode;
    public VerifyData data;

    public record VerifyData (
        UserInfoData user_info,
        List<TokenData> tokens,
        ExtUserInfoData ext_user_info
    ){}
}
