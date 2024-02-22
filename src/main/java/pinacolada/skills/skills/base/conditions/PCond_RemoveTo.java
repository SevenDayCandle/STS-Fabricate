package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.RemoveFromPile;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PCond_RemoveTo extends PCond_DoToCard {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_RemoveTo.class, PField_CardCategory.class)
            .noTarget()
            .setExtra(0, DEFAULT_MAX);

    public PCond_RemoveTo() {
        this(1, PCLCardGroupHelper.Hand);
    }

    public PCond_RemoveTo(int amount, PCLCardGroupHelper... h) {
        super(DATA, PCLCardTarget.None, amount, h);
    }

    public PCond_RemoveTo(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return RemoveFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.remove;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (useParent) {
            return TEXT.act_removeFrom(TEXT.subjects_themX, TEXT.cpile_deck);
        }
        else if (fields.groupTypes.isEmpty()) {
            return TEXT.act_removeFrom(TEXT.subjects_thisCard(), TEXT.cpile_deck);
        }
        String cString = EUIRM.strings.numNoun(getAmountRawOrAllString(), fields.getCardOrString(getRawString(EXTRA_CHAR)));
        return fields.groupTypes.size() == 1 && fields.groupTypes.get(0) == PCLCardGroupHelper.MasterDeck ? TEXT.act_removeFrom(cString, TEXT.cpile_deck) :
                TEXT.act_removeInPlace(cString, fields.getGroupString(), TEXT.cpile_deck);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }
}
