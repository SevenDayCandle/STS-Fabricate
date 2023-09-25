package pinacolada.skills.fields;

import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public class PField_Random extends PField_Not {
    public boolean random;

    public <T> boolean allOrAnyR(Iterable<T> items, FuncT1<Boolean, T> valFunc) {
        return random ? EUIUtils.any(items, valFunc) : EUIUtils.all(items, valFunc);
    }

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Random && not == ((PField_Random) other).not && random == ((PField_Random) other).random;
    }

    @Override
    public PField_Random makeCopy() {
        return (PField_Random) new PField_Random().setRandom(random).setNot(not);
    }

    public void registerRBoolean(PCLCustomEffectEditingPane editor, String name, String desc) {
        editor.registerBoolean(name, desc, v -> random = v, random);
    }

    public void registerRandom(PCLCustomEffectEditingPane editor) {
        editor.registerBoolean(PGR.core.strings.cedit_random, v -> random = v, random);
    }

    public PField_Random setRandom(boolean value) {
        this.random = value;
        return this;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        registerRandom(editor);
        super.setupEditor(editor);
    }
}
