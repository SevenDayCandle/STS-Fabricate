package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_ReduceCooldown extends PMove_Modify<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = PMove_Modify.register(PMove_ReduceCooldown.class, PField_CardCategory.class);

    public PMove_ReduceCooldown() {
        this(1, 1);
    }

    public PMove_ReduceCooldown(int amount, int cooldown, PCLCardGroupHelper... groups) {
        super(DATA, amount, cooldown, groups);
    }

    public PMove_ReduceCooldown(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLActions order) {
        return (c) -> order.progressCooldown(getOwnerCreature(), c, extra);
    }

    @Override
    public String getObjectSampleText() {
        return PGR.core.tooltips.cooldown.title;
    }

    @Override
    public String getObjectText() {
        return PGR.core.tooltips.cooldown.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return useParent ? TEXT.act_reduceBy(TEXT.subjects_theirX(getObjectText()), getExtraRawString()) :
                fields.hasGroups() ?
                        TEXT.act_reduceCooldown(EUIRM.strings.numNoun(getAmountRawString(), pluralCard()), getExtraRawString()) :
                        TEXT.act_reduceBy(getObjectText(), getExtraRawString());
    }

    @Override
    public boolean isDetrimental() {
        return extra < 0;
    }

    @Override
    public String wrapExtra(int input) {
        return String.valueOf(input);
    }
}
