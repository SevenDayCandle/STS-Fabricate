package pinacolada.misc;

import com.google.gson.reflect.TypeToken;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class PCLCustomEditorLoadable<T extends EditorMaker, U extends FabricateItem> extends PCLCustomLoadable {
    protected static final TypeToken<EffectItemForm> TTOKENFORM = new TypeToken<EffectItemForm>() {
    };
    public transient ArrayList<T> builders = new ArrayList<>();

    public abstract U make();

    public static class EffectItemForm implements Serializable {
        static final long serialVersionUID = 1L;
        public String[] effects;
        public String[] powerEffects;
    }
}
