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
import pinacolada.skills.fields.PField_Potion;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckPotion extends PPassiveCond<PField_Potion> {
    public static final PSkillData<PField_Potion> DATA = register(PCond_CheckPotion.class, PField_Potion.class)
            .selfTarget();

    public PCond_CheckPotion(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckPotion(int amount, String... potion) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setPotionID(potion);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        int count =  EUIUtils.count(AbstractDungeon.player.potions, c -> fields.getFullPotionFilter().invoke(c));
        return amount == 0 ? count == 0 : fields.random ^ count >= amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.subjects_potion);
    }

    @Override
    public String getSubText() {
        return getTargetHasString(EUIRM.strings.numNoun(getAmountRawString(), fields.getFullPotionString()));

    }

    @Override
    public String wrapAmount(int input) {
        return fields.getThresholdValString(input);
    }
}
