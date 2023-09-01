package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardPlayedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PCond_OnOtherCardPlayed extends PDelegateCardCond implements OnCardPlayedSubscriber {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnOtherCardPlayed.class, PField_CardCategory.class, 1, 1)
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
    public String getDelegateText() {
        return TEXT.subjects_playingXWith(fields.getFullCardString(), TEXT.cpile_hand);
    }

    @Override
    public EUIKeywordTooltip getDelegateTooltip() {
        return PGR.core.tooltips.play;
    }

    @Override
    public void onCardPlayed(AbstractCard card) {
        triggerOnCard(card);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.setupEditor(editor);
    }
}
