package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_CardModifyAffinity;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@VisibleSkill
public class PMove_ModifyAffinity extends PMove_Modify<PField_CardModifyAffinity> {
    public static final PSkillData<PField_CardModifyAffinity> DATA = PMove_Modify.register(PMove_ModifyAffinity.class, PField_CardModifyAffinity.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(0, DEFAULT_MAX)
            .noTarget()
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
        if (fields.addAffinities.size() == 0) {
            if (fields.or) {
                chooseEffect(PCLAffinity.getAvailableAffinities(), order);
            }
            else {
                PCLAffinity random = PCLAffinity.getRandomAvailableAffinity();
                for (AbstractCard c : cards) {
                    order.modifyAffinityLevel(c, Collections.singletonList(random), amount, !fields.not, fields.forced);
                }
            }
        }
        else if (fields.or && fields.addAffinities.size() > 1) {
            chooseEffect(fields.addAffinities, order);
        }
        else {
            super.cardAction(cards, order);
        }
    }

    public void chooseEffect(Collection<PCLAffinity> choices, PCLActions order) {
        order.tryChooseAffinitySkill(getName(), Math.max(1, extra2), getSourceCreature(), null, EUIUtils.map(choices, a -> PMove.modifyAffinity(amount, extra, a)));
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLActions order) {
        return (c) -> order.modifyAffinityLevel(c, fields.addAffinities, amount, !fields.not, fields.forced);
    }

    @Override
    public String getNumericalObjectText() {
        return amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(), getObjectText()) : getObjectText();
    }

    @Override
    public String getObjectSampleText() {
        return PGR.core.tooltips.affinityGeneral.title;
    }

    @Override
    public String getObjectText() {
        return fields.getAddAffinityChoiceString();
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (fields.forced) {
            String giveString = getNumericalObjectText();
            return useParent ? TEXT.act_setOf(PField.getGeneralAffinityString(), getInheritedThemString(), giveString) :
                    fields.hasGroups() ?
                            TEXT.act_setOfFrom(PField.getGeneralAffinityString(), EUIRM.strings.numNoun(baseExtra <= 0 ? TEXT.subjects_all : getExtraRawString(), pluralCard()), fields.getGroupString(), giveString) :
                            TEXT.act_setOf(PField.getGeneralAffinityString(), TEXT.subjects_thisCard(), giveString);
        }
        return amount < 0 ? getBasicRemoveString() : getBasicGiveString();
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerOrBoolean(editor);
    }

    @Override
    public String wrapTextAmount(int input) {
        return String.valueOf(Math.abs(input));
    }
}
