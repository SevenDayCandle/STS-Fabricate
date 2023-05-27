package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
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
        this(amount, attackEffect, PCLCardTarget.Single);
    }

    public PMove_DealDamage(int amount, AbstractGameAction.AttackEffect attackEffect, PCLCardTarget target) {
        super(DATA, target, amount);
        fields.setAttackEffect(attackEffect);
    }

    public PMove_DealDamage(int amount, PCLAttackVFX attackEffect) {
        this(amount, attackEffect.key, PCLCardTarget.Single);
    }

    public PMove_DealDamage(int amount, PCLAttackVFX attackEffect, PCLCardTarget target) {
        this(amount, attackEffect.key, target);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_deal(TEXT.subjects_x, PGR.core.strings.subjects_damage);
    }

    @Override
    public String getSubText() {
        if (target == PCLCardTarget.Self) {
            return TEXT.act_takeDamage(getAmountRawString());
        }
        if (target == PCLCardTarget.Single) {
            return TEXT.act_deal(getAmountRawString(), PGR.core.strings.subjects_damage);
        }
        return TEXT.act_dealTo(getAmountRawString(), PGR.core.strings.subjects_damage, getTargetString());
    }

    @Override
    public boolean isDetrimental() {
        return target.targetsSelf();
    }

    @Override
    public void use(PCLUseInfo info) {
        if (target.targetsMulti()) {
            int[] damage = DamageInfo.createDamageMatrix(amount, true, false);
            getActions().dealDamageToAll(damage, DamageInfo.DamageType.THORNS, fields.attackEffect);
        }
        else {
            getActions().dealDamage(getSourceCreature(), target == PCLCardTarget.Self ? getSourceCreature() : target.getTarget(info.target), amount, DamageInfo.DamageType.THORNS, fields.attackEffect).isCancellable(target != PCLCardTarget.Self);
        }
        super.use(info);
    }
}
