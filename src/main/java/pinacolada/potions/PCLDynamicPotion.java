package pinacolada.potions;

import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;

public class PCLDynamicPotion extends PCLPotion implements FabricateItem {
    protected PCLDynamicPotionData builder;
    protected ArrayList<PCLDynamicPotionData> forms;

    public PCLDynamicPotion(PCLDynamicPotionData data) {
        super(data);
        findForms();
        setupBuilder(data);
    }

    protected void findForms() {
        PCLCustomPotionSlot cSlot = PCLCustomPotionSlot.get(ID);
        if (cSlot != null) {
            this.forms = cSlot.builders;
        }
    }

    @Override
    public EditorMaker getDynamicData() {
        return builder;
    }

    @Override
    public PCLDynamicPotion makeCopy() {
        return new PCLDynamicPotion(builder);
    }

    public PCLDynamicPotion setForm(int form) {
        PCLDynamicPotionData lastBuilder = null;
        this.auxiliaryData.form = form;
        if (forms != null && forms.size() > form) {
            lastBuilder = forms.get(form);
        }
        if (lastBuilder != null && lastBuilder != this.builder) {
            this.builder = lastBuilder;
            setupMoves(this.builder);
        }
        initializeTips();
        return this;
    }

    public void setupBuilder(PCLDynamicPotionData builder) {
        this.builder = builder;
        setupMoves(builder);
        initializeTips();
    }

    public void setupMoves(PCLDynamicPotionData builder) {
        for (PSkill<?> effect : builder.moves) {
            if (effect == null || effect.isBlank()) {
                continue;
            }
            addUseMove(effect.makeCopy());
        }

        for (PTrigger pe : builder.powers) {
            if (pe == null || pe.isBlank()) {
                continue;
            }
            addPowerMove(pe.makeCopy());
        }

        initializeTargetRequired();
    }
}
