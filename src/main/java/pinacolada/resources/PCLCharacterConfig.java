package pinacolada.resources;

import com.badlogic.gdx.math.Vector2;
import extendedui.configuration.STSConfigItem;
import extendedui.configuration.STSSerializedConfigItem;
import extendedui.configuration.STSStringConfigItem;

import java.util.HashSet;

public abstract class PCLCharacterConfig extends AbstractConfig
{
    public STSSerializedConfigItem<HashSet<String>> bannedCards;
    public STSSerializedConfigItem<HashSet<String>> bannedRelics;
    public STSConfigItem<Integer> cardsCount;
    public STSSerializedConfigItem<Vector2> meterPosition;
    public STSStringConfigItem trophies;

    public PCLCharacterConfig(String id)
    {
        super(id);
    }
}
