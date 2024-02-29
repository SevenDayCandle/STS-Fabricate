package pinacolada.skills.fields;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import extendedui.EUIUtils;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Creature extends PField_Random {
    public ArrayList<String> creatures = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Creature && creatures.equals(((PField_Creature) other).creatures) && ((PField_Creature) other).random == random && ((PField_Creature) other).not == not;
    }

    public boolean filter(AbstractCreature cr) {
        return filter(cr.id);
    }

    public boolean filter(String id) {
        return EUIUtils.any(creatures, c -> c.equals(id));
    }

    public String getString() {
        return PCLCoreStrings.joinWithOr(GameUtilities::getCreatureName, creatures);
    }

    @Override
    public PField_Creature makeCopy() {
        return (PField_Creature) new PField_Creature().setCreature(creatures).setRandom(random).setNot(not);
    }

    public PField_Creature setCreature(Collection<String> orbs) {
        this.creatures.clear();
        this.creatures.addAll(orbs);
        return this;
    }

    public PField_Creature setCreature(String... orbs) {
        return setCreature(Arrays.asList(orbs));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerCreature(creatures);
        super.setupEditor(editor);
    }
}
