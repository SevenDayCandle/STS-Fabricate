package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PActiveMod;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_EvokePerOrb extends PActiveMod<PField_Orb> {

    public static final PSkillData<PField_Orb> DATA = register(PMod_EvokePerOrb.class, PField_Orb.class).noTarget();

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
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info, boolean isUsing) {
        return AbstractDungeon.player == null ? 0 : be.baseAmount * (fields.orbs.isEmpty() ? GameUtilities.getOrbCount() : EUIUtils.sumInt(fields.orbs, GameUtilities::getOrbCount)) / Math.max(1, this.amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_evoke(TEXT.cond_xPerY(TEXT.subjects_x, PGR.core.tooltips.orb.title));
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return this.amount <= 1 ? fields.getOrbAndString() : EUIRM.strings.numNoun(getAmountRawString(), fields.getOrbAndString());
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        return TEXT.act_evoke(TEXT.subjects_allX(fields.getOrbString()) + EFFECT_SEPARATOR + super.getText(perspective, addPeriod));
    }

    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if (shouldPay && childEffect != null) {
            useImpl(info, order, () -> childEffect.use(info, order));
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (childEffect != null) {
            useImpl(info, order, () -> childEffect.use(info, order));
        }
    }

    protected void useImpl(PCLUseInfo info, PCLActions order, ActionT0 callback) {
        updateChildAmount(info, true);
        order.evokeOrb(1, GameUtilities.getOrbCount()).setFilter(fields.getOrbFilter())
                .addCallback((orbs) -> {
                    info.setData(orbs);
                    callback.invoke();
                });
    }

}
