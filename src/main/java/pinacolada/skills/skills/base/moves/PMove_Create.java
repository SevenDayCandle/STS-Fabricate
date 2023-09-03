package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_Create extends PMove_GenerateCard {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Create.class, PField_CardCategory.class)
            .setExtra(1, DEFAULT_MAX)
            .noTarget();

    public PMove_Create() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PMove_Create(int copies, String... cardData) {
        super(DATA, PCLCardTarget.None, copies, cardData);
    }

    public PMove_Create(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_Create(int copies, PCLCardGroupHelper... gt) {
        super(DATA, PCLCardTarget.None, copies, gt);
    }

    public PMove_Create(int copies, int extra, PCLCardGroupHelper... gt) {
        super(DATA, PCLCardTarget.None, copies, extra, gt);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.create;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String groupName = fields.getDestinationString(fields.groupTypes.size() > 0 ? fields.groupTypes.get(0).name : PCLCardGroupHelper.Hand.name);
        String base = TEXT.act_addAmountToPile(getAmountRawOrAllString(), getCopiesOfString(), groupName);
        return fields.origin != PCLCardSelection.Manual && !fields.cardIDs.isEmpty() ? TEXT.subjects_randomly(base) : base;
    }

    @Override
    public void performAction(PCLUseInfo info, PCLActions order, AbstractCard c) {
        order.makeCard(c, fields.groupTypes.size() > 0 ? fields.groupTypes.get(0).getCardGroup() : AbstractDungeon.player.hand)
                .setDestination(fields.destination);
    }
}
