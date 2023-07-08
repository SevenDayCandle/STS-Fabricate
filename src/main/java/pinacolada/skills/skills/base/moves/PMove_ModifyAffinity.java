package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_CardModifyAffinity;

import java.util.List;

@VisibleSkill
public class PMove_ModifyAffinity extends PMove_Modify<PField_CardModifyAffinity> {
    public static final PSkillData<PField_CardModifyAffinity> DATA = PMove_Modify.register(PMove_ModifyAffinity.class, PField_CardModifyAffinity.class)
            .setAmounts(-PCLAffinity.MAX_LEVEL, PCLAffinity.MAX_LEVEL)
            .setExtra(0, DEFAULT_MAX)
            .selfTarget()
            .pclOnly();

    public PMove_ModifyAffinity() {
        this(1, 1);
    }

    public PMove_ModifyAffinity(int amount, int extra, PCLAffinity... affinities) {
        super(DATA, amount, extra);
        fields.setAddAffinity(affinities);
    }

    public PMove_ModifyAffinity(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_ModifyAffinity(PCLAffinity... affinities) {
        this(1, affinities);
    }

    public PMove_ModifyAffinity(int amount, PCLAffinity... affinities) {
        this(amount, 1, affinities);
    }

    @Override
    public void cardAction(List<AbstractCard> cards, PCLActions order) {
        if (fields.or && fields.addAffinities.size() > 1) {
            chooseEffect(cards, fields.addAffinities, order);
        }
        else {
            super.cardAction(cards, order);
        }
    }

    @Override
    public String getNumericalObjectText() {
        return amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(), getObjectText()) : getObjectText();
    }

    @Override
    public String getObjectSampleText() {
        return TEXT.sui_affinities;
    }

    @Override
    public String wrapExtra(int input) {
        return String.valueOf(Math.abs(input));
    }

    @Override
    public String getSubText() {
        if (fields.forced) {
            String giveString = getNumericalObjectText();
            return useParent ? TEXT.act_setOf(PField.getGeneralAffinityString(), getInheritedThemString(), giveString) :
                    fields.hasGroups() ?
                            TEXT.act_setOfFrom(PField.getGeneralAffinityString(), EUIRM.strings.numNoun(baseExtra <= 0 ? TEXT.subjects_all : getExtraRawString(), pluralCard()), fields.getGroupString(), giveString) :
                            TEXT.act_setOf(PField.getGeneralAffinityString(), TEXT.subjects_thisCard, giveString);
        }
        return getBasicGiveString();
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLActions order) {
        return (c) -> order.modifyAffinityLevel(c, fields.addAffinities, amount, !fields.not, fields.forced);
    }

    @Override
    public String getObjectText() {
        return fields.getAddAffinityChoiceString();
    }

    public void chooseEffect(List<AbstractCard> cards, List<PCLAffinity> choices, PCLActions order) {
        order.tryChooseAffinitySkill(getName(), amount, getSourceCreature(), null, EUIUtils.map(choices, a -> PMove.modifyAffinity(amount, a)));
    }
}
