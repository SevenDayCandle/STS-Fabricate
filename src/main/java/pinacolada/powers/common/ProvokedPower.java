package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisiblePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnMonsterMoveSubscriber;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;

import java.lang.reflect.Field;
import java.util.ArrayList;

@VisiblePower
public class ProvokedPower extends PCLPower implements OnMonsterMoveSubscriber {
    public static final PCLPowerData DATA = register(ProvokedPower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setPriority(99)
            .setTooltip(PGR.core.tooltips.provoked);
    public static final int ATTACK_MULTIPLIER = 50;
    private byte moveByte;
    private AbstractMonster.Intent moveIntent;
    private EnemyMoveInfo move;
    protected int lastDamage;
    protected int lastMultiplier;
    protected boolean lastIsMultiDamage;


    public ProvokedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? damage * (1 + (ATTACK_MULTIPLIER / 100f)) : damage;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atEndOfTurn(isPlayer);

        reducePower(1);
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, ATTACK_MULTIPLIER);
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        CombatManager.subscribe(this);

        final AbstractMonster monster = EUIUtils.safeCast(owner, AbstractMonster.class);
        if (monster != null) {
            this.moveByte = monster.nextMove;
            this.moveIntent = monster.intent;
            PCLActions.last.callback(() -> {
                try {
                    Field f = AbstractMonster.class.getDeclaredField("move");
                    f.setAccessible(true);
                    this.move = (EnemyMoveInfo) f.get(monster);
                    this.lastDamage = this.move.baseDamage;
                    this.lastMultiplier = this.move.multiplier;
                    this.lastIsMultiDamage = this.move.isMultiDamage;
                    this.move.intent = AbstractMonster.Intent.ATTACK;
                    ArrayList<DamageInfo> damages = monster.damage;
                    if (damages == null || damages.isEmpty()) {
                        this.move.baseDamage = 1;
                    }
                    else {
                        this.move.baseDamage = damages.get(0).base;
                    }
                    monster.createIntent();
                }
                catch (NoSuchFieldException | IllegalAccessException var2) {
                    EUIUtils.logWarning(this, "Monster could not be provoked");
                }
            });
        }
    }

    @Override
    public boolean onMonsterMove(AbstractMonster m) {
        ArrayList<DamageInfo> damages = m.damage;
        if (damages == null || damages.isEmpty()) {
            PCLActions.bottom.dealDamage(m, AbstractDungeon.player, 1, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        }
        else {
            PCLActions.bottom.dealDamage(m, AbstractDungeon.player, m.damage.get(0).base, m.damage.get(0).type, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        }
        return false;
    }

    @Override
    public void onRemove() {
        super.onRemove();
        CombatManager.unsubscribe(this);

        AbstractMonster m = EUIUtils.safeCast(this.owner, AbstractMonster.class);
        if (m != null && this.moveIntent != null) {
            m.setMove(this.moveByte, this.moveIntent, this.lastDamage, this.lastMultiplier, this.lastIsMultiDamage);
            m.createIntent();
            m.applyPowers();
        }

    }
}