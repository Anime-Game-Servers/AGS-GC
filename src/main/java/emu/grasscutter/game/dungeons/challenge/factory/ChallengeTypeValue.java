package emu.grasscutter.game.dungeons.challenge.factory;

import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ChallengeTypeValue {
    ChallengeType[] type();
}
