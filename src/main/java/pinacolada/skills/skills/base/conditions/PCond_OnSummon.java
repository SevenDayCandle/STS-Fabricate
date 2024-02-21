package pinacolada.skills.skills.base.conditions;

import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnAllySummonSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

import java.util.Collections;

@VisibleSkill
public class PCond_OnSummon extends PDelegateCardCond implements OnAllySummonSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnSummon.class, PField_CardCategory.class, 1, 1)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power)
            .noTarget();

    public PCond_OnSummon() {
        super(DATA);
    }

    public PCond_OnSummon(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.summon;
    }

    @Override
    public void onAllySummon(PCLCardAlly ally, PCLCard card, PCLCard returnedCard) {
        // Ally is treated as the source
        if (fields.getFullCardFilter().invoke(card)) {
            PCLUseInfo info = generateInfo(ally, ally);
            info.setTempTargets(ally).setData(Collections.singletonList(card));
            useFromTrigger(info);
        }
    }
}
