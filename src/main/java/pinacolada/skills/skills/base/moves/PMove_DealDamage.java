package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Attack;

import java.util.ArrayList;
import java.util.Arrays;

@VisibleSkill
public class PMove_DealDamage extends PMove<PField_Attack> {
    public static final PSkillData<PField_Attack> DATA = register(PMove_DealDamage.class, PField_Attack.class);

    public PMove_DealDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_DealDamage() {
        this(1, AbstractGameAction.AttackEffect.NONE);
    }

    public PMove_DealDamage(int amount, AbstractGameAction.AttackEffect attackEffect) {
        this(PCLCardTarget.Single, amount, attackEffect);
    }

    public PMove_DealDamage(PCLCardTarget target, int amount, AbstractGameAction.AttackEffect attackEffect) {
        super(DATA, target, amount);
        fields.setAttackEffect(attackEffect);
    }

    public PMove_DealDamage(int amount, PCLAttackVFX attackEffect) {
        this(PCLCardTarget.Single, amount, attackEffect.key);
    }

    public PMove_DealDamage(PCLCardTarget target, int amount, PCLAttackVFX attackEffect) {
        this(target, amount, attackEffect.key);
    }

    protected int[] getDamageMatrix(PCLUseInfo info, ArrayList<AbstractCreature> targets) {
        int[] damage = new int[targets.size()];
        for (int i = 0; i < damage.length; i++) {
            damage[i] = refreshAmount(info);
        }
        return damage;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_deal(TEXT.subjects_x, EUIRM.strings.adjNoun(TEXT.subjects_non(PGR.core.tooltips.attack.title), PGR.core.strings.subjects_damage));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String amountString = baseExtra > baseAmount ? xToRangeString(getAmountRawString(), getExtraRawString()) : getAmountRawString();
        if (target == PCLCardTarget.None || (target == PCLCardTarget.Self && perspective == PCLCardTarget.Self)) {
            return TEXT.act_takeDamage(amountString);
        }
        if (target == PCLCardTarget.Single) {
            return TEXT.act_deal(amountString, PGR.core.strings.subjects_damage);
        }
        return TEXT.act_dealTo(amountString, PGR.core.strings.subjects_damage, getTargetStringPerspective(perspective));
    }

    @Override
    public boolean isDetrimental() {
        return target.targetsSelf();
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (target.targetsMulti()) {
            ArrayList<AbstractCreature> targets = new ArrayList<>(target.getTargets(info, scope)); // Because the info list could get modified later
            int[] damage = getDamageMatrix(info, targets);
            order.dealDamageToAll(getSourceCreature(), targets, damage, DamageInfo.DamageType.THORNS, fields.attackEffect);
        }
        else {
            int actualAmount = refreshAmount(info);
            order.dealDamage(getSourceCreature(), target.getTarget(info, scope), actualAmount, DamageInfo.DamageType.THORNS, fields.attackEffect)
                    .canRedirect(!target.targetsSingle())
                    .isCancellable(target != PCLCardTarget.Self && target != PCLCardTarget.None);
        }
        super.use(info, order);
    }
}
