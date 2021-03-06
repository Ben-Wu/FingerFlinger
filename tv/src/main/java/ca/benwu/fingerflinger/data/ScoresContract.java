package ca.benwu.fingerflinger.data;

import android.provider.BaseColumns;

/**
 * Created by Ben Wu on 12/19/2015.
 */
public class ScoresContract {

    public static class ScoresColumns implements BaseColumns {
        public static final String TABLE_NAME = "FlingerScores";

        public static final String COLUMN_POINTS = "Points";
        public static final String COLUMN_GAME_MODE = "GameMode";
        public static final String COLUMN_ANIMATION_MODE = "AnimMode";
        public static final String COLUMN_DATE = "TimeStamp";

        public static final String[] PROJ = {_ID, COLUMN_POINTS, COLUMN_GAME_MODE,
                COLUMN_ANIMATION_MODE, COLUMN_DATE};

        // game modes
        public static final String GAME_MODE_NORMAL = "Normal";
        public static final String GAME_MODE_FAST = "Fast";
        public static final String GAME_MODE_TIME = "Time Attack";

        // anim modes
        public static final String ANIM_MODE_NORMAL = "Normal";
        public static final String ANIM_MODE_WACKY = "Wacky";
        public static final String ANIM_MODE_EASY = "Easy";
        public static final String ANIM_MODE_NONE = "No Animation";

        public static final String FOR_GAME_MODE = COLUMN_GAME_MODE + " =?";
    }
}
