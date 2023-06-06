package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Relic;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckRelic extends PPassiveCond<PField_Relic> {
    public static final PSkillData<PField_Relic> DATA = register(PCond_CheckRelic.class, PField_Relic.class)
            .selfTarget();

    public PCond_CheckRelic(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckRelic(int amount, String... relic) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setRelicID(relic);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        int count =  EUIUtils.count(AbstractDungeon.player.relics, c -> fields.getFullRelicFilter().invoke(c));
        return amount == 0 ? count == 0 : fields.random ^ count >= amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.subjects_relic);
    }

    @Override
    public String getSubText() {
        return getTargetHasString(EUIRM.strings.numNoun(getAmountRawString(), fields.getFullRelicString()));

    }

    @Override
    public String wrapAmount(int input) {
        return fields.getThresholdValString(input);
    }
}
