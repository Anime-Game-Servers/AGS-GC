package emu.grasscutter.server.http.dispatch;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.auth.AuthenticationSystem;
import emu.grasscutter.auth.OAuthAuthenticator.ClientType;
import emu.grasscutter.server.http.Router;
import emu.grasscutter.server.http.objects.*;
import emu.grasscutter.server.http.objects.ComboTokenReqJson.LoginTokenData;
import emu.grasscutter.utils.JsonUtils;
import emu.grasscutter.utils.Utils;
import io.javalin.Javalin;
import io.javalin.http.Context;

import static emu.grasscutter.utils.Language.translate;

/**
 * Handles requests related to authentication. (aka dispatch)
 */
public final class DispatchHandler implements Router {
    @Override public void applyRoutes(Javalin javalin) {
        //OS
        // Username & Password login (from client).
        javalin.post("/hk4e_global/mdk/shield/api/login", DispatchHandler::clientLogin);
        // Cached token login (from registry).
        javalin.post("/hk4e_global/mdk/shield/api/verify", DispatchHandler::tokenLogin);
        // Combo token login (from session key).
        javalin.post("/hk4e_global/combo/granter/login/v2/login", DispatchHandler::sessionKeyLogin);
        // ma-passport
        javalin.post("/hk4e_global/account/ma-passport/api/appLoginByPassword", DispatchHandler::maPassportLogin);
        javalin.post("/hk4e_global/account/ma-passport/token/verifySToken", DispatchHandler::maPassportVerify);

        // CN
        // Username & Password login (from client).
        javalin.post("/hk4e_cn/mdk/shield/api/login", DispatchHandler::clientLogin);
        // Cached token login (from registry).
        javalin.post("/hk4e_cn/mdk/shield/api/verify", DispatchHandler::tokenLogin);
        // Combo token login (from session key).
        javalin.post("/hk4e_cn/combo/granter/login/v2/login", DispatchHandler::sessionKeyLogin);
        // ma-passport
        javalin.post("/hk4e_cn/account/ma-passport/api/appLoginByPassword", DispatchHandler::maPassportLogin);
        javalin.post("/account/ma-cn-passport/app/loginByPassword", DispatchHandler::maPassportLogin);
        javalin.post("/hk4e_cn/account/ma-passport/token/verifySToken", DispatchHandler::maPassportVerify);

        // External login (from other clients).
        javalin.get("/authentication/type", ctx -> ctx.result(Grasscutter.getAuthenticationSystem().getClass().getSimpleName()));
        javalin.post("/authentication/login", ctx -> Grasscutter.getAuthenticationSystem().getExternalAuthenticator()
                .handleLogin(AuthenticationSystem.fromExternalRequest(ctx)));
        javalin.post("/authentication/register", ctx -> Grasscutter.getAuthenticationSystem().getExternalAuthenticator()
                .handleAccountCreation(AuthenticationSystem.fromExternalRequest(ctx)));
        javalin.post("/authentication/change_password", ctx -> Grasscutter.getAuthenticationSystem().getExternalAuthenticator()
                .handlePasswordReset(AuthenticationSystem.fromExternalRequest(ctx)));

        // External login (from OAuth2).
        javalin.post("/hk4e_global/mdk/shield/api/loginByThirdparty", ctx -> Grasscutter.getAuthenticationSystem().getOAuthAuthenticator()
                .handleLogin(AuthenticationSystem.fromExternalRequest(ctx)));
        javalin.get("/authentication/openid/redirect", ctx -> Grasscutter.getAuthenticationSystem().getOAuthAuthenticator()
                .handleTokenProcess(AuthenticationSystem.fromExternalRequest(ctx)));
        javalin.get("/Api/twitter_login", ctx -> Grasscutter.getAuthenticationSystem().getOAuthAuthenticator()
                .handleRedirection(AuthenticationSystem.fromExternalRequest(ctx), ClientType.DESKTOP));
        javalin.get("/sdkTwitterLogin.html", ctx -> Grasscutter.getAuthenticationSystem().getOAuthAuthenticator()
                .handleRedirection(AuthenticationSystem.fromExternalRequest(ctx), ClientType.MOBILE));
    }

    /**
     * @route /hk4e_global/mdk/shield/api/login
     */
    private static void clientLogin(Context ctx) {
        // Parse body data.
        String rawBodyData = ctx.body();
        var bodyData = JsonUtils.decode(rawBodyData, LoginAccountRequestJson.class);

        // Validate body data.
        if (bodyData == null)
            return;

        // Pass data to authentication handler.
        var responseData = Grasscutter.getAuthenticationSystem()
                .getPasswordAuthenticator()
                .authenticate(AuthenticationSystem.fromPasswordRequest(ctx, bodyData));
        // Send response.
        ctx.json(responseData);

        // Log to console.
        Grasscutter.getLogger().info(translate("messages.dispatch.account.login_attempt", ctx.ip()));
    }

    /**
     * @route /hk4e_global/mdk/shield/api/verify
     */
    private static void tokenLogin(Context ctx) {
        // Parse body data.
        String rawBodyData = ctx.body();
        var bodyData = JsonUtils.decode(rawBodyData, LoginTokenRequestJson.class);

        // Validate body data.
        if (bodyData == null)
            return;

        // Pass data to authentication handler.
        var responseData = Grasscutter.getAuthenticationSystem()
                .getTokenAuthenticator()
                .authenticate(AuthenticationSystem.fromTokenRequest(ctx, bodyData));
        // Send response.
        ctx.json(responseData);

        // Log to console.
        Grasscutter.getLogger().info(translate("messages.dispatch.account.login_attempt", ctx.ip()));
    }

    /**
     * @route /hk4e_global/combo/granter/login/v2/login
     */
    private static void sessionKeyLogin(Context ctx) {
        // Parse body data.
        String rawBodyData = ctx.body();
        var bodyData = JsonUtils.decode(rawBodyData, ComboTokenReqJson.class);

        // Validate body data.
        if (bodyData == null || bodyData.data == null)
            return;

        // Decode additional body data.
        var tokenData = JsonUtils.decode(bodyData.data, LoginTokenData.class);

        // Pass data to authentication handler.
        var responseData = Grasscutter.getAuthenticationSystem()
                .getSessionKeyAuthenticator()
                .authenticate(AuthenticationSystem.fromComboTokenRequest(ctx, bodyData, tokenData));
        // Send response.
        ctx.json(responseData);

        // Log to console.
        Grasscutter.getLogger().info(translate("messages.dispatch.account.login_attempt", ctx.ip()));
    }


    /**
     * @route /hk4e_global/account/ma-passport/api/appLoginByPassword
     * @route /hk4e_cn/account/ma-passport/api/appLoginByPassword
     */
    private static void maPassportLogin(Context ctx) {
        //Grasscutter.getLogger().info("Ma-passport login request from: " + Utils.address(ctx));

        try {
            String rawBodyData = ctx.body();
            Grasscutter.getLogger().debug("Ma-passport request body: " + rawBodyData);

            var request = JsonUtils.decode(rawBodyData, LoginByPasswordRequestJson.class);
            if (request == null) {
                Grasscutter.getLogger().warn("Failed to parse Ma-Passport login request");
                ctx.status(400).result("{\"retcode\":-1,\"message\":\"Invalid Request\",\"data\":null}");
                return;
            }

            var response = MaPassportAuthenticator.appLoginByPassword(request);

            ctx.json(response);

        } catch (Exception e) {
            Grasscutter.getLogger().error("Error in Ma-Passport login", e);
            e.printStackTrace();
            ctx.status(500).result("{\"retcode\":-1,\"message\":\"Internal server error\",\"data\":null}");
        }
    }

    /**
     * @route /hk4e_global/account/ma-passport/token/verifySToken
     * @route /hk4e_cn/account/ma-passport/token/verifySToken
     */
    private static void maPassportVerify(Context ctx) {
        //Grasscutter.getLogger().info("Ma-passport token verify request from: " + Utils.address(ctx));

        try {
            String rawBodyData = ctx.body();
            Grasscutter.getLogger().debug("Ma-passport verify body: " + rawBodyData);

            var request = JsonUtils.decode(rawBodyData, VerifySTokenRequestJson.class);
            if (request == null) {
                Grasscutter.getLogger().warn("Failed to parse Ma-Passport verify request");
                ctx.status(400).result("{\"retcode\":-1,\"message\":\"Invalid Request\",\"data\":null}");
                return;
            }

            var response = MaPassportAuthenticator.verifySToken(request);

            ctx.json(response);

        } catch (Exception e) {
            Grasscutter.getLogger().error("Error in Ma-Passport verify", e);
            e.printStackTrace();
            ctx.status(500).result("{\"retcode\":-1,\"message\":\"Internal server error\",\"data\":null}");
        }
    }


}
