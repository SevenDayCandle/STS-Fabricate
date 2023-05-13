package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PActiveMod;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_EvokePerOrb extends PActiveMod<PField_Orb> {

    public static final PSkillData<PField_Orb> DATA = register(PMod_EvokePerOrb.class, PField_Orb.class).selfTarget();

    public PMod_EvokePerOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_EvokePerOrb() {
        super(DATA);
    }

    public PMod_EvokePerOrb(int amount, PCLOrbHelper... orbs) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info) {
        return AbstractDungeon.player == null ? 0 : be.baseAmount * (fields.orbs.isEmpty() ? GameUtilities.getOrbCount() : EUIUtils.sumInt(fields.orbs, GameUtilities::getOrbCount)) / Math.max(1, this.amount);
    }

    @Override
    public String getText(boolean addPeriod) {
        return TEXT.act_evoke(TEXT.subjects_allX(fields.getOrbString()) + EFFECT_SEPARATOR + super.getText(addPeriod));
    }

    @Override
    public void use(PCLUseInfo info) {
        if (childEffect != null) {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    public void use(PCLUseInfo info, boolean isUsing) {
        if (isUsing && childEffect != null) {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_evoke(TEXT.cond_per(TEXT.subjects_x, TEXT.cedit_orbs));
    }

    @Override
    public String getSubText() {
        return this.amount <= 1 ? fields.getOrbAndString() : EUIRM.strings.numNoun(getAmountRawString(), fields.getOrbAndString());
    }

    protected void useImpl(PCLUseInfo info, ActionT0 callback) {
        getActions().evokeOrb(1, GameUtilities.getOrbCount()).setFilter(fields.getOrbFilter())
                .addCallback(callback);
    }

}
