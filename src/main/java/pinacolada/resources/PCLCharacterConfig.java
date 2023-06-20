package pinacolada.resources;

import com.badlogic.gdx.math.Vector2;
import extendedui.configuration.STSConfigItem;
import extendedui.configuration.STSSerializedConfigItem;
import extendedui.configuration.STSStringConfigItem;

import java.util.HashSet;

import static pinacolada.ui.characterSelection.PCLLoadoutsContainer.MINIMUM_CARDS;

public class PCLCharacterConfig extends AbstractConfig {
    public STSSerializedConfigItem<HashSet<String>> bannedCards;
    public STSSerializedConfigItem<HashSet<String>> bannedRelics;
    public STSConfigItem<Boolean> seenTutorial;
    public STSConfigItem<Integer> cardsCount;
    public STSSerializedConfigItem<Vector2> meterPosition;
    public STSStringConfigItem lastLoadout;
    public STSStringConfigItem trophies;

    public PCLCharacterConfig(String id, String bannedCardsID, String bannedRelicsID, String cardsCountID, String meterPositionID, String seenTutorialID, String lastLoadoutID, String trophiesID) {
        super(id);
        bannedCards = new STSSerializedConfigItem<HashSet<String>>(bannedCardsID, new HashSet<>());
        bannedRelics = new STSSerializedConfigItem<HashSet<String>>(bannedRelicsID, new HashSet<>());
        cardsCount = new STSConfigItem<Integer>(cardsCountID, MINIMUM_CARDS);
        meterPosition = new STSSerializedConfigItem<Vector2>(meterPositionID, new Vector2(0.35f, 0.8f));
        seenTutorial = new STSConfigItem<Boolean>(seenTutorialID, false);
        lastLoadout = new STSStringConfigItem(lastLoadoutID, "");
        trophies = new STSStringConfigItem(trophiesID, "");
    }

    @Override
    public void loadImpl() {
        bannedCards.addConfig(config);
        bannedRelics.addConfig(config);
        cardsCount.addConfig(config);
        lastLoadout.addConfig(config);
        meterPosition.addConfig(config);
        seenTutorial.addConfig(config);
        trophies.addConfig(config);
    }

    public void resetTutorial() {
        seenTutorial.set(false);
    }
}
