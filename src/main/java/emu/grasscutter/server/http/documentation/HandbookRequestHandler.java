package emu.grasscutter.server.http.documentation;

import static emu.grasscutter.config.Configuration.*;
import static emu.grasscutter.utils.Language.TextStrings.NUM_LANGUAGES;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.CommandMap;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.MonsterData;
import emu.grasscutter.utils.FileUtils;
import emu.grasscutter.utils.Language;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.anime_game_servers.game_data_models.gi.data.entities.avatar.AvatarData;
import org.anime_game_servers.game_data_models.gi.data.scene.SceneData;

final class HandbookRequestHandler implements DocumentationHandler {
    private List<String> handbookHtmls;

    public HandbookRequestHandler() {
        var templatePath = FileUtils.getDataPath("documentation/handbook.html");
        try {
            this.handbookHtmls = generateHandbookHtmls(Files.readString(templatePath));
        } catch (IOException ignored) {
            Grasscutter.getLogger().warn("File does not exist: " + templatePath);
        }
    }

    @Override
    public void handle(Context ctx) {
        final int langIdx = Language.TextStrings.MAP_LANGUAGES.getOrDefault(DOCUMENT_LANGUAGE, 0);  // TODO: This should really be based off the client language somehow
        if (this.handbookHtmls == null) {
            ctx.status(500);
        } else {
            ctx.contentType(ContentType.TEXT_HTML);
            ctx.result(this.handbookHtmls.get(langIdx));
        }
    }

    private void addTableRowString(StringBuilder builder, int id, String value){
        builder.append("<tr><td><code>").append(id).append("</code></td>")
            .append("<td>").append(value).append("</td></tr>\n");
    }
    private void addTableRowString(StringBuilder builder, String key, String value){
        builder.append("<tr><td><code>").append(key).append("</code></td>")
            .append("<td>").append(value).append("</td></tr>\n");
    }
    private void addTableRowString(StringBuilder builder, int id, long textMapHash, int langIdx){
        Language.TextStrings name = Language.getTextMapKey(textMapHash);

        builder.append("<tr><td><code>").append(id).append("</code></td><td>");
        if (name !=null){
            builder.append(name.get(langIdx));
        } else {
            builder.append(textMapHash);
        }
        builder.append("</td></tr>\n");
    }

    private <T> void addAllEntries(final List<StringBuilder> sbs, Int2ObjectMap<T> map, Function<T, Integer> idGetter, Function<T, Long> nameTextMapHashGetter){
        sbs.forEach(sb -> sb.setLength(0));
        map.keySet().intStream().sorted().mapToObj(map::get).forEach(data -> {
            int id = idGetter.apply(data);
            for (int langIdx = 0; langIdx < NUM_LANGUAGES; langIdx++)
                addTableRowString(sbs.get(langIdx), id, nameTextMapHashGetter.apply(data), langIdx);
        });
        sbs.forEach(sb -> {
            if(!sb.isEmpty())
                sb.setLength(sb.length()-1);  // Remove trailing \n
        });
    }

    private List<String> generateHandbookHtmls(String template) {
        final int NUM_LANGUAGES = Language.TextStrings.NUM_LANGUAGES;
        final List<String> output = new ArrayList<>(NUM_LANGUAGES);
        final List<Language> languages = Language.TextStrings.getLanguages();
        final List<StringBuilder> sbs = new ArrayList<>(NUM_LANGUAGES);
        for (int langIdx = 0; langIdx < NUM_LANGUAGES; langIdx++)
            sbs.add(new StringBuilder(""));

        // Commands table
        CommandMap.getInstance().getHandlersAsList().forEach(cmd -> {
            String label = cmd.getLabel();
            String descKey = cmd.getDescriptionKey();
            for (int langIdx = 0; langIdx < NUM_LANGUAGES; langIdx++)
                addTableRowString(sbs.get(langIdx),label, languages.get(langIdx).get(descKey));
        });
        sbs.forEach(sb -> sb.setLength(sb.length()-1));  // Remove trailing \n
        final List<String> cmdsTable = sbs.stream().map(StringBuilder::toString).toList();

        // Avatars table
        final Int2ObjectMap<AvatarData> avatarMap = GameData.getAvatarDataMap();
        addAllEntries(sbs, avatarMap, AvatarData::getId, AvatarData::getNameTextMapHash);
        final List<String> avatarsTable = sbs.stream().map(StringBuilder::toString).toList();

        // Items table
        final Int2ObjectMap<ItemData> itemMap = GameData.getItemDataMap();
        addAllEntries(sbs, itemMap, ItemData::getId, ItemData::getNameTextMapHash);
        final List<String> itemsTable = sbs.stream().map(StringBuilder::toString).toList();

        // Scenes table
        final Int2ObjectMap<SceneData> sceneMap = GameData.getSceneDataMap();
        sceneMap.keySet().intStream().sorted().mapToObj(sceneMap::get).forEach(data -> {
            int id = data.getId();
            for (int langIdx = 0; langIdx < NUM_LANGUAGES; langIdx++)
                addTableRowString(sbs.get(langIdx), id, data.getScriptData());
        });
        sbs.forEach(sb -> sb.setLength(sb.length()-1));  // Remove trailing \n
        final List<String> scenesTable = sbs.stream().map(StringBuilder::toString).toList();

        // Monsters table
        final Int2ObjectMap<MonsterData> monsterMap = GameData.getMonsterDataMap();
        addAllEntries(sbs, monsterMap, MonsterData::getId, MonsterData::getNameTextMapHash);
        final List<String> monstersTable = sbs.stream().map(StringBuilder::toString).toList();

        // Add translated title etc. to the page.
        for (int langIdx = 0; langIdx < NUM_LANGUAGES; langIdx++) {
            Language lang = languages.get(langIdx);
            output.add(template
                .replace("{{TITLE}}", lang.get("documentation.handbook.title"))
                .replace("{{TITLE_COMMANDS}}", lang.get("documentation.handbook.title_commands"))
                .replace("{{TITLE_AVATARS}}", lang.get("documentation.handbook.title_avatars"))
                .replace("{{TITLE_ITEMS}}", lang.get("documentation.handbook.title_items"))
                .replace("{{TITLE_SCENES}}", lang.get("documentation.handbook.title_scenes"))
                .replace("{{TITLE_MONSTERS}}", lang.get("documentation.handbook.title_monsters"))
                .replace("{{HEADER_ID}}", lang.get("documentation.handbook.header_id"))
                .replace("{{HEADER_COMMAND}}", lang.get("documentation.handbook.header_command"))
                .replace("{{HEADER_DESCRIPTION}}", lang.get("documentation.handbook.header_description"))
                .replace("{{HEADER_AVATAR}}", lang.get("documentation.handbook.header_avatar"))
                .replace("{{HEADER_ITEM}}", lang.get("documentation.handbook.header_item"))
                .replace("{{HEADER_SCENE}}", lang.get("documentation.handbook.header_scene"))
                .replace("{{HEADER_MONSTER}}", lang.get("documentation.handbook.header_monster"))
                // Commands table
                .replace("{{COMMANDS_TABLE}}", cmdsTable.get(langIdx))
                .replace("{{AVATARS_TABLE}}", avatarsTable.get(langIdx))
                .replace("{{ITEMS_TABLE}}", itemsTable.get(langIdx))
                .replace("{{SCENES_TABLE}}", scenesTable.get(langIdx))
                .replace("{{MONSTERS_TABLE}}", monstersTable.get(langIdx))
            );
        }
        return output;
    }
}
