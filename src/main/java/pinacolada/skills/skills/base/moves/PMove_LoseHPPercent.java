package pinacolada.skills.skills.base.moves;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_LoseHPPercent extends PMove<PField_Empty> implements OutOfCombatMove {
    public static final PSkillData<PField_Empty> DATA = register(PMove_LoseHPPercent.class, PField_Empty.class);

    public PMove_LoseHPPercent() {
        this(1);
    }

    public PMove_LoseHPPercent(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_LoseHPPercent(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_LoseHPPercent(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_loseAmount(TEXT.subjects_x + "%", PGR.core.tooltips.hp.title);
    }

    @Override
    public String getSubText() {
        String percentLoss = getAmountRawString() + "%";
        if (target == PCLCardTarget.Self && !isFromCreature()) {
            return TEXT.act_loseAmount(percentLoss, PGR.core.tooltips.hp.title);
        }
        return TEXT.act_zLoses(getTargetString(), getTargetOrdinal(), percentLoss, PGR.core.tooltips.hp.title);

    }

    @Override
    public boolean isDetrimental() {
        return target.targetsSelf() || target.targetsAllies();
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        int reduction = MathUtils.ceil(AbstractDungeon.player.maxHealth * amount / 100f);
        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, reduction));
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (AbstractCreature t : getTargetList(info)) {
            int reduction = MathUtils.ceil(t.maxHealth * amount / 100f);
            order.loseHP(info.source, t, reduction, AbstractGameAction.AttackEffect.NONE).ignorePowers(true).isCancellable(false);
        }
        super.use(info, order);
    }
}
