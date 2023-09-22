package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_PlayCopy extends PMove_GenerateCard {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_PlayCopy.class, PField_CardCategory.class)
            .setExtra(1, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.Hand);

    public PMove_PlayCopy() {
        this(1, PCLCardTarget.RandomEnemy);
    }

    public PMove_PlayCopy(int copies, PCLCardTarget target, String... cards) {
        super(DATA, target, copies, cards);
    }

    public PMove_PlayCopy(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.play;
    }

    // Prevent ephemeral cards from being copied to avoid infinite loops
    @Override
    protected boolean canMakeCopy(AbstractCard card) {
        return !card.purgeOnUse;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_play(TEXT.subjects_copiesOf(TEXT.subjects_x));
    }

    @Override
    public void performAction(PCLUseInfo info, PCLActions order, AbstractCard c) {
        order.playCopy(c, target.getTarget(info, scope));
    }
}
