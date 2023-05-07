package pinacolada.skills.fields;

import pinacolada.ui.cardEditor.PCLCustomEffectEditingPane;

import java.util.Collections;

public class PField_CardTransform extends PField_CardCategory {
    public String result;
    public boolean or;

    public PField_CardTransform() {
        super();
    }

    public PField_CardTransform(PField_CardTransform other) {
        super(other);
        setResult(other.result);
        setOr(other.or);
    }

    @Override
    public boolean equals(PField other) {
        return super.equals(other);
    }

    @Override
    public PField_CardTransform makeCopy() {
        return new PField_CardTransform(this);
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerPile(groupTypes);
        editor.registerCard(Collections.singletonList(result),
                cards -> {
                    if (cards.size() > 0) {
                        result = cards.get(0).cardID;
                    }
                    else {
                        result = null;
                    }
                }
        );
    }

    public String getCardIDString() {
        return result != null ? getCardIDString(result) : "";
    }

    public PField_CardTransform setOr(boolean value) {
        this.or = value;
        return this;
    }

    public PField_CardTransform setResult(String result) {
        this.result = result;
        return this;
    }
}
