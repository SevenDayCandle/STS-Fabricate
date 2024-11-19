package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerOrbSlot extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerOrbSlot.class, PField_Not.class).noTarget();

    public PMod_PerOrbSlot(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerOrbSlot() {
        super(DATA);
    }

    public PMod_PerOrbSlot(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return AbstractDungeon.player != null ? AbstractDungeon.player.maxOrbs : 0;
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.orbSlot.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.tooltips.orbSlot.getTitleOrIcon();
    }
}
