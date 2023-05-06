package pinacolada.misc;

import pinacolada.interfaces.markers.EditorMaker;

import java.util.ArrayList;

public abstract class PCLCustomEditorLoadable<T extends EditorMaker> extends PCLCustomLoadable {
    public transient ArrayList<T> builders = new ArrayList<>();
}
