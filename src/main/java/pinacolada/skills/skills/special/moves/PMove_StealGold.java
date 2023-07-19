package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.common.StolenGoldPower;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.utilities.GameUtilities;

public class PMove_StealGold extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_StealGold.class, PField_Empty.class);

    public PMove_StealGold() {
        this(PCLCardTarget.Single, 1);
    }

    public PMove_StealGold(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    public PMove_StealGold(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_stealX(TEXT.subjects_x, PGR.core.tooltips.gold.title);
    }

    @Override
    public PMove_StealGold onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        if (card.tags.contains(AbstractCard.CardTags.HEALING)) {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.act_stealFrom(getAmountRawString(), PGR.core.tooltips.gold, getTargetStringPerspective(perspective));
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        AbstractCreature m = info.target;
        if (m == null) {
            m = GameUtilities.getRandomEnemy(true);
        }
        if (m instanceof AbstractMonster) {
            order.applyPower(info.source, new StolenGoldPower(m, amount));
        }
        super.use(info, order);
    }
}
