package pinacolada.augments;

import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.powers.PCLClickableUse;
import pinacolada.powers.PCLDynamicPower;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;

public class PCLDynamicAugment extends PCLAugment implements FabricateItem {

    protected ArrayList<PCLDynamicAugmentData> forms;
    public PCLDynamicAugmentData builder;
    public int form;

    public PCLDynamicAugment(PCLDynamicAugmentData data) {
        this(data, 0, 0);

    }

    public PCLDynamicAugment(PCLDynamicAugmentData data, int form, int timesUpgraded) {
        this(data, new SaveData(data.ID, form, timesUpgraded));
    }

    public PCLDynamicAugment(PCLDynamicAugmentData data, SaveData save) {
        super(data, save);
        this.builder = data;
        setupMoves(builder);
    }

    @Override
    public PCLDynamicAugmentData getDynamicData() {
        return builder;
    }

    @Override
    public void setForm(int form, int timesUpgraded) {
        this.save.form = form;
        this.save.timesUpgraded = timesUpgraded;


        PCLDynamicAugmentData lastBuilder = null;
        if (forms != null && forms.size() > form) {
            lastBuilder = forms.get(form);
        }
        if (lastBuilder != null && lastBuilder != this.builder) {
            this.builder = lastBuilder;
            setupMoves(this.builder);
        }

        for (PSkill<?> ef : getEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
    }

    public void setupMoves(PCLDynamicAugmentData data) {
        clearSkills();
        for (PSkill<?> skill : data.moves) {
            if (!PSkill.isSkillBlank(skill)) {
                PSkill<?> effect = skill.makeCopy();
                addUseMove(effect);
            }
        }
    }
}
