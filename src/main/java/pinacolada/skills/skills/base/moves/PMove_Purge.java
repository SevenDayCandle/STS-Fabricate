package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.PurgeFromPile;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_Purge extends PMove_Select<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Purge.class, PField_CardCategory.class)
            .selfTarget()
            .setExtra(0, DEFAULT_MAX);

    public PMove_Purge() {
        this(1);
    }

    public PMove_Purge(int amount, PCLCardGroupHelper... h) {
        super(DATA, amount, h);
    }

    public PMove_Purge(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_Purge(int amount, int extra, PCLCardGroupHelper... h) {
        super(DATA, amount, extra, h);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return PurgeFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.purge;
    }
}
