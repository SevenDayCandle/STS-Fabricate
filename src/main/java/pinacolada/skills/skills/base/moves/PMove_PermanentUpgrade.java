package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.actions.piles.UpgradeFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PMove_PermanentUpgrade extends PMove_Select<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_PermanentUpgrade.class, PField_CardCategory.class)
            .selfTarget();

    public PMove_PermanentUpgrade() {
        this(1);
    }

    public PMove_PermanentUpgrade(int amount, PCLCardGroupHelper... h) {
        super(DATA, amount, h);
    }

    public PMove_PermanentUpgrade(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction() {
        return (s, c, i, o, g) -> (SelectFromPile) new UpgradeFromPile(s, c, i, o, g).isPermanent(true).isCancellable(false);
    }

    @Override
    public EUITooltip getActionTooltip() {
        return PGR.core.tooltips.upgrade;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.subjects_permanentlyX(TEXT.act_upgrade(TEXT.subjects_x));
    }

    @Override
    public String getSubText() {
        return TEXT.subjects_permanentlyX(super.getSubText());
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }
}
