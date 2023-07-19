package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMod_PerCard extends PMod_Per<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCard.class, PField_CardCategory.class)
            .setGroups(PCLCardGroupHelper.getAll())
            .selfTarget();

    public PMod_PerCard(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCard() {
        super(DATA);
    }

    public PMod_PerCard(int amount, PCLCardGroupHelper... groups) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public String getConditionText(PCLCardTarget perspective, String childText) {
        if (fields.not) {
            return TEXT.cond_xConditional(childText, TEXT.cond_xPerIn(getAmountRawString(), fields.getFullCardStringSingular(), fields.getGroupString()));
        }
        return TEXT.cond_xPerIn(childText,
                this.amount <= 1 ? fields.getFullCardStringSingular() : EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardString()), fields.getGroupString());
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return EUIUtils.sumInt(fields.groupTypes, g -> EUIUtils.count(g.getCards(),
                c -> fields.getFullCardFilter().invoke(c))
        );
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.subjects_card;
    }
}
