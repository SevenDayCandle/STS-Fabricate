package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
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
    public void cardAction(List<AbstractCard> cards, PCLUseInfo info, PCLActions order) {
        if (fields.addAffinities.isEmpty()) {
            if (fields.or) {
                chooseEffect(PCLAffinity.getAvailableAffinities(), order, refreshAmount(info));
            }
            else {
                PCLAffinity random = PCLAffinity.getRandomAvailableAffinity();
                for (AbstractCard c : cards) {
                    order.modifyAffinityLevel(c, Collections.singletonList(random), refreshAmount(info), !fields.not, fields.forced);
                }
            }
        }
        else if (fields.or && fields.addAffinities.size() > 1) {
            chooseEffect(fields.addAffinities, order, refreshAmount(info));
        }
        else {
            super.cardAction(cards, info, order);
        }
    }

    public void chooseEffect(Collection<PCLAffinity> choices, PCLActions order, int amount) {
        order.tryChooseAffinitySkill(getName(), Math.max(1, extra2), getSourceCreature(), null, EUIUtils.map(choices, a -> PMove.modifyAffinity(amount, extra, a)));
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLUseInfo info, PCLActions order) {
        return (c) -> order.modifyAffinityLevel(c, fields.addAffinities, refreshAmount(info), !fields.not, fields.forced);
    }

    @Override
    public String getNumericalObjectText(Object requestor) {
        return amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(requestor), getObjectText(requestor)) : getObjectText(requestor);
    }

    @Override
    public String getObjectSampleText() {
        return PGR.core.tooltips.affinityGeneral.title;
    }

    @Override
    public String getObjectText(Object requestor) {
        return fields.getAddAffinityChoiceString();
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (fields.forced) {
            String giveString = getNumericalObjectText(requestor);
            return useParent ? TEXT.act_setOf(PField.getGeneralAffinityString(), getInheritedThemString(), giveString) :
                    fields.hasGroups() ?
                            TEXT.act_setOfFrom(PField.getGeneralAffinityString(), EUIRM.strings.numNoun(baseExtra <= 0 ? TEXT.subjects_all : getExtraRawString(requestor), pluralCard()), fields.getGroupString(), giveString) :
                            TEXT.act_setOf(PField.getGeneralAffinityString(), TEXT.subjects_thisCard(), giveString);
        }
        return amount < 0 ? getBasicRemoveString(requestor) : getBasicGiveString(requestor);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerOrBoolean(editor);
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return String.valueOf(Math.abs(input));
    }
}
