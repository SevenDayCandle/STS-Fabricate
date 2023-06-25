package pinacolada.misc;

import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;

import java.util.ArrayList;

public abstract class PCLCustomEditorLoadable<T extends EditorMaker, U extends FabricateItem> extends PCLCustomLoadable {
    public transient ArrayList<T> builders = new ArrayList<>();

    public abstract U make();
}
