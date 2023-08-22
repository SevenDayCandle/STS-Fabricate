package pinacolada.resources;

import com.badlogic.gdx.math.Vector2;
import extendedui.configuration.STSConfigItem;
import extendedui.configuration.STSSerializedConfigItem;
import extendedui.configuration.STSStringConfigItem;

import java.util.HashSet;

public class PCLCharacterConfig extends AbstractConfig {
    protected static final String SUFFIX_BANNED_CARDS = "BannedCards";
    protected static final String SUFFIX_BANNED_RELICS = "BannedRelics";
    protected static final String SUFFIX_CARDS_COUNT = "CardsCount";
    protected static final String SUFFIX_LAST_LOADOUT = "LastLoadout";
    protected static final String SUFFIX_METER_POSITION = "MeterPosition";
    protected static final String SUFFIX_SEEN_TUTORIAL = "SeenTutorial";
    protected static final String SUFFIX_SEEN_SUMMON_TUTORIAL = "SeenSummonTutorial";
    protected static final String SUFFIX_TROPHIES = "Trophies";
    public STSConfigItem<Boolean> seenTutorial;
    public STSSerializedConfigItem<HashSet<String>> bannedCards;
    public STSSerializedConfigItem<HashSet<String>> bannedRelics;
    public STSSerializedConfigItem<HashSet<String>> selectedLoadouts;
    public STSSerializedConfigItem<Vector2> meterPosition;
    public STSStringConfigItem lastLoadout;
    public STSStringConfigItem trophies;

    public PCLCharacterConfig(String id) {
        super(id);
        bannedCards = new STSSerializedConfigItem<HashSet<String>>(PGR.createID(id,SUFFIX_BANNED_CARDS), new HashSet<>());
        bannedRelics = new STSSerializedConfigItem<HashSet<String>>(PGR.createID(id,SUFFIX_BANNED_RELICS), new HashSet<>());
        lastLoadout = new STSStringConfigItem(PGR.createID(id,SUFFIX_LAST_LOADOUT), "");
        meterPosition = new STSSerializedConfigItem<Vector2>(PGR.createID(id,SUFFIX_METER_POSITION), new Vector2(0.35f, 0.8f));
        seenTutorial = new STSConfigItem<Boolean>(PGR.createID(id,SUFFIX_SEEN_TUTORIAL), false);
        selectedLoadouts = new STSSerializedConfigItem<HashSet<String>>(PGR.createID(id,SUFFIX_CARDS_COUNT), new HashSet<>());
        trophies = new STSStringConfigItem(PGR.createID(id,SUFFIX_TROPHIES), "");
    }

    @Override
    public void loadImpl() {
        bannedCards.addConfig(config);
        bannedRelics.addConfig(config);
        lastLoadout.addConfig(config);
        meterPosition.addConfig(config);
        seenTutorial.addConfig(config);
        selectedLoadouts.addConfig(config);
        trophies.addConfig(config);
    }

    public void resetTutorial() {
        seenTutorial.set(false);
    }
}
