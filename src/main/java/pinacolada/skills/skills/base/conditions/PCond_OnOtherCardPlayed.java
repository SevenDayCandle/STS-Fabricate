package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnCardPlayedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.Collections;

@VisibleSkill
public class PCond_OnOtherCardPlayed extends PDelegateCardCond implements OnCardPlayedSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnOtherCardPlayed.class, PField_CardCategory.class, 1, 1)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power)
            .noTarget();

    public PCond_OnOtherCardPlayed() {
        super(DATA);
    }

    public PCond_OnOtherCardPlayed(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getDelegateSampleText() {
        return TEXT.act_play(TEXT.subjects_x);
    }

    @Override
    public String getDelegateText(Object requestor) {
        return TEXT.subjects_playingXWith(fields.getFullCardString(requestor), TEXT.cpile_hand);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.play;
    }

    @Override
    public void onCardPlayed(AbstractCard card) {
        if (fields.getFullCardFilter().invoke(card)) {
            // Copy targets from last info to allow for chaining effects with Use Parent targeting to target the creatures the played card targeted
            PCLUseInfo info = generateInfo(null);
            PCLUseInfo prevInfo = CombatManager.getLastInfo();
            if (prevInfo != null && prevInfo.card == card) {
                info.setTempTargets(prevInfo.tempTargets);
            }
            useFromTrigger(info.setData(Collections.singletonList(card)));
        }
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.setupEditor(editor);
    }
}
