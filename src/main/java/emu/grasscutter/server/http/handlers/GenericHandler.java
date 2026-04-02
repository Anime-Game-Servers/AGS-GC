package emu.grasscutter.server.http.handlers;

import static emu.grasscutter.config.Configuration.ACCOUNT;

import emu.grasscutter.GameConstants;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.server.http.objects.HttpJsonResponse;
import emu.grasscutter.server.http.Router;
import emu.grasscutter.server.http.objects.WebStaticVersionResponse;
import emu.grasscutter.utils.JsonUtils;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import lombok.val;

/**
 * Handles all generic, hard-coded responses.
 */
public final class GenericHandler implements Router {
    @Override public void applyRoutes(Javalin javalin) {
        // hk4e-sdk-os.hoyoverse.com
        javalin.get("/hk4e_global/mdk/agreement/api/getAgreementInfos", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"marketing_agreements\":[]}}"));
        // hk4e-sdk-os.hoyoverse.com (this could be either GET or POST based on the observation of different clients)
        this.allRoutes(javalin, "/hk4e_global/combo/granter/api/compareProtocolVersion", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"modified\":true,\"protocol\":{\"id\":0,\"app_id\":4,\"language\":\"en\",\"user_proto\":\"\",\"priv_proto\":\"\",\"major\":7,\"minimum\":0,\"create_time\":\"0\",\"teenager_proto\":\"\",\"third_proto\":\"\"}}}"));
        this.allRoutes(javalin, "/hk4e_cn/combo/granter/api/compareProtocolVersion", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"modified\":true,\"protocol\":{\"id\":0,\"app_id\":4,\"language\":\"en\",\"user_proto\":\"\",\"priv_proto\":\"\",\"major\":7,\"minimum\":0,\"create_time\":\"0\",\"teenager_proto\":\"\",\"third_proto\":\"\"}}}"));

        // api-account-os.hoyoverse.com
        javalin.post("/account/risky/api/check", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"id\":\"none\",\"action\":\"ACTION_NONE\",\"geetest\":null}}"));

        // sdk-os-static.hoyoverse.com
        javalin.get("/combo/box/api/config/sdk/combo", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"vals\":{\"enable_register_autologin\":\"true\",\"webview_rendermethod_config\":\"{ \\\"useLegacy\\\": true }\",\"account_list_page_enable\":\"true\",\"kcp_enable\":\"false\",\"enable_web_dpi\":\"true\",\"enable_user_center_v2\":\"true\",\"pay_payco_centered_host\":\"bill.payco.com\",\"disable_email_bind_skip\":\"false\",\"webview_apm_config\":\"{ \\\"crash_capture_enable\\\": false }\",\"email_bind_remind_interval\":\"7\",\"list_price_tierv2_enable\":\"false\",\"new_register_page_enable\":\"true\",\"network_report_config\":\"{ \\\"enable\\\": 1, \\\"status_codes\\\": [206], \\\"url_paths\\\": [\\\"dataUpload\\\"] }\",\"email_bind_remind\":\"true\",\"h5log_filter_config\":\"{\\n\\t\\\"function\\\": {\\n\\t\\t\\\"event_name\\\": [\\\"info_get_cps\\\", \\\"notice_close_notice\\\", \\\"info_get_uapc\\\", \\\"report_set_info\\\", \\\"info_get_channel_id\\\", \\\"info_get_sub_channel_id\\\"]\\n\\t}\\n}\",\"telemetry_config\":\"{\\n \\\"dataupload_enable\\\": 1,\\n}\",\"new_forgotpwd_page_enable\":\"true\",\"kibana_pc_config\":\"{ \\\"enable\\\": 1, \\\"level\\\": \\\"Info\\\",\\\"modules\\\": [\\\"download\\\"]\"}}}"));
        // hk4e-sdk-os-static.hoyoverse.com
        javalin.get("/hk4e_global/combo/granter/api/getConfig", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"protocol\":true,\"qr_enabled\":true,\"log_level\":\"DEBUG\",\"announce_url\":\"https://sdk.hoyoverse.com/hk4e/announcement/index.html?sdk_presentation_style=fullscreen\\u0026announcement_version=2.42\\u0026sdk_screen_transparent=true\\u0026game_biz=hk4e_global\\u0026auth_appid=announcement\\u0026game=hk4e#/\",\"push_alias_type\":2,\"disable_ysdk_guard\":false,\"enable_announce_pic_popup\":true,\"app_name\":\"原神海外\",\"qr_enabled_apps\":{\"bbs\":true,\"cloud\":true},\"qr_app_icons\":{\"app\":\"\",\"bbs\":\"\",\"cloud\":\"\"},\"qr_cloud_display_name\":\"\",\"enable_user_center\":true,\"functional_switch_configs\":{},\"ugc_protocol\":true}}"));
        javalin.get("/hk4e_cn/combo/granter/api/getConfig", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"protocol\":true,\"qr_enabled\":true,\"log_level\":\"DEBUG\",\"announce_url\":\"https://sdk.hoyoverse.com/hk4e/announcement/index.html?sdk_presentation_style=fullscreen\\u0026announcement_version=2.42\\u0026sdk_screen_transparent=true\\u0026game_biz=hk4e_global\\u0026auth_appid=announcement\\u0026game=hk4e#/\",\"push_alias_type\":2,\"disable_ysdk_guard\":false,\"enable_announce_pic_popup\":true,\"app_name\":\"原神海外\",\"qr_enabled_apps\":{\"bbs\":true,\"cloud\":true},\"qr_app_icons\":{\"app\":\"\",\"bbs\":\"https://hartie95.de/Data/index/bbs-me.png\",\"cloud\":\"\"},\"qr_cloud_display_name\":\"\",\"enable_user_center\":true,\"functional_switch_configs\":{},\"ugc_protocol\":true}}"));
        // hk4e-sdk-os-static.hoyoverse.com
        javalin.get("/hk4e_global/mdk/shield/api/loadConfig", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"id\":6,\"game_key\":\"hk4e_global\",\"client\":\"PC\",\"identity\":\"I_IDENTITY\",\"guest\":true,\"ignore_versions\":\"\",\"scene\":\"S_NORMAL\",\"name\":\"原神海外\",\"disable_regist\":false,\"enable_email_captcha\":false,\"thirdparty\":[\"fb\",\"tw\",\"gl\",\"ap\"],\"disable_mmt\":false,\"server_guest\":true,\"thirdparty_ignore\":{},\"enable_ps_bind_account\":false,\"thirdparty_login_configs\":{\"gl\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":604800},\"tw\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":2592000},\"ap\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":604800},\"fb\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":2592000}},\"initialize_firebase\":false,\"bbs_auth_login\":false,\"bbs_auth_login_ignore\":[],\"fetch_instance_id\":false,\"enable_flash_login\":false,\"enable_logo_18\":false,\"logo_height\":\"0\",\"logo_width\":\"0\",\"enable_cx_bind_account\":false,\"firebase_blacklist_devices_switch\":false,\"firebase_blacklist_devices_version\":0,\"hoyolab_auth_login\":false,\"hoyolab_auth_login_ignore\":[],\"hoyoplay_auth_login\":true,\"enable_douyin_flash_login\":false,\"enable_age_gate\":true,\"enable_age_gate_ignore\":[]}}"));
        javalin.get("/hk4e_cn/mdk/shield/api/loadConfig", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"id\":6,\"game_key\":\"hk4e_global\",\"client\":\"PC\",\"identity\":\"I_IDENTITY\",\"guest\":true,\"ignore_versions\":\"\",\"scene\":\"S_NORMAL\",\"name\":\"原神海外\",\"disable_regist\":false,\"enable_email_captcha\":false,\"thirdparty\":[\"fb\",\"tw\",\"gl\",\"ap\"],\"disable_mmt\":false,\"server_guest\":true,\"thirdparty_ignore\":{},\"enable_ps_bind_account\":false,\"thirdparty_login_configs\":{\"gl\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":604800},\"tw\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":2592000},\"ap\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":604800},\"fb\":{\"token_type\":\"TK_GAME_TOKEN\",\"game_token_expires_in\":2592000}},\"initialize_firebase\":false,\"bbs_auth_login\":false,\"bbs_auth_login_ignore\":[],\"fetch_instance_id\":false,\"enable_flash_login\":false,\"enable_logo_18\":false,\"logo_height\":\"0\",\"logo_width\":\"0\",\"enable_cx_bind_account\":false,\"firebase_blacklist_devices_switch\":false,\"firebase_blacklist_devices_version\":0,\"hoyolab_auth_login\":false,\"hoyolab_auth_login_ignore\":[],\"hoyoplay_auth_login\":true,\"enable_douyin_flash_login\":false,\"enable_age_gate\":true,\"enable_age_gate_ignore\":[]}}"));
        // Test api?
        // abtest-api-data-sg.hoyoverse.com
        javalin.post("/data_abtest_api/config/experiment/list", GenericHandler::abtest);

        // log-upload-os.mihoyo.com
        this.allRoutes(javalin, "/log/sdk/upload", GenericHandler::log);
        this.allRoutes(javalin, "/sdk/upload", GenericHandler::log);
        javalin.post("/sdk/dataUpload", GenericHandler::log);
        // /perf/config/verify?device_id=xxx&platform=x&name=xxx
        this.allRoutes(javalin, "/perf/config/verify", new HttpJsonResponse("{\"code\":0}"));

        // webstatic-sea.hoyoverse.com
        javalin.get("/admin/mi18n/plat_oversea/*", new WebStaticVersionResponse());

        javalin.get("/status/server", GenericHandler::serverStatus);
    }

    private static void serverStatus(Context ctx) {
        int playerCount = Grasscutter.getGameServer().getPlayers().size();
        int maxPlayer = ACCOUNT.maxPlayer;
        String version = GameConstants.VERSION;

        ctx.result("{\"retcode\":0,\"status\":{\"playerCount\":" + playerCount + ",\"maxPlayer\":" + maxPlayer + ",\"version\":\"" + version + "\"}}");
    }

    private static void log(Context ctx) {
        ctx.result("{\"code\":0}");
    }
    private static void abtest(Context ctx) {
        ctx.result("{\"retcode\":0,\"success\":true,\"message\":\"\",\"data\":[{\"code\":1000,\"type\":2,\"config_id\":\"14\",\"period_id\":\"6036_99\",\"version\":\"1\",\"configs\":{\"cardType\":\"old\",\"hoyopass_enable\":true},\"experimentWhiteList\":false,\"sceneWhiteList\":false}}]}");
    }
}
