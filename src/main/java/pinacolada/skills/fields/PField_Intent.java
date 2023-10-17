package pinacolada.skills.fields;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.monsters.PCLIntentInfo;
import pinacolada.monsters.PCLIntentType;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Intent extends PField_Not {
    public ArrayList<PCLIntentType> intents = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Intent && intents.equals(((PField_Intent) other).intents) && ((PField_Intent) other).not == not;
    }

    public String getAnyIntentString() {
        return intents.isEmpty() ? TEXT.cond_any(PGR.core.strings.subjects_intent) : getIntentString();
    }

    public String getIntentString() {
        return getIntentString(intents);
    }

    public boolean hasIntent(AbstractCreature creature) {
        if (creature instanceof AbstractMonster) {
            PCLIntentInfo intent = PCLIntentInfo.get((AbstractMonster) creature);
            return EUIUtils.any(intents, i -> i.hasIntent(intent.intent));
        }
        return false;
    }

    public boolean hasIntent(AbstractMonster.Intent intent) {
        return EUIUtils.any(intents, i -> i.hasIntent(intent));
    }

    @Override
    public PField_Intent makeCopy() {
        return (PField_Intent) new PField_Intent().setIntent(intents).setNot(not);
    }

    public PField_Intent setIntent(Collection<PCLIntentType> nt) {
        this.intents.clear();
        for (PCLIntentType t : nt) {
            if (t != null) {
                this.intents.add(t);
            }
        }
        return this;
    }

    public PField_Intent setIntent(PCLIntentType... orbs) {
        return setIntent(Arrays.asList(orbs));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerIntent(intents);
        super.setupEditor(editor);
    }
}
