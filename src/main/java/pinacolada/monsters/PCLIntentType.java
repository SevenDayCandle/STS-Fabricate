package pinacolada.monsters;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;

import java.util.*;
import java.util.stream.Collectors;

public enum PCLIntentType implements TooltipProvider {
    Attack(AbstractMonster.Intent.ATTACK, AbstractMonster.Intent.ATTACK_BUFF, AbstractMonster.Intent.ATTACK_DEBUFF, AbstractMonster.Intent.ATTACK_DEFEND),
    Buff(AbstractMonster.Intent.ATTACK_BUFF, AbstractMonster.Intent.BUFF, AbstractMonster.Intent.DEFEND_BUFF, AbstractMonster.Intent.MAGIC),
    Debuff(AbstractMonster.Intent.ATTACK_DEBUFF, AbstractMonster.Intent.DEBUFF, AbstractMonster.Intent.DEFEND_DEBUFF, AbstractMonster.Intent.STRONG_DEBUFF),
    Defend(AbstractMonster.Intent.ATTACK_DEFEND, AbstractMonster.Intent.DEFEND, AbstractMonster.Intent.DEFEND_BUFF, AbstractMonster.Intent.DEFEND_DEBUFF),
    Escape(AbstractMonster.Intent.ESCAPE),
    Stun(AbstractMonster.Intent.SLEEP, AbstractMonster.Intent.STUN);

    public final HashSet<AbstractMonster.Intent> intents = new HashSet<>();

    public static Collection<PCLIntentType> sorted() {
        ArrayList<PCLIntentType> list = new ArrayList<>(Arrays.asList(PCLIntentType.values()));
        list.sort((a, b) -> StringUtils.compare(a.getActionString(), b.getActionString()));
        return list;
    }

    PCLIntentType(AbstractMonster.Intent... intents) {
        this.intents.addAll(Arrays.asList(intents));
    }

    public String getActionString() {
        if (this == Stun) {
            return PGR.core.tooltips.stun.past();
        }
        EUIKeywordTooltip tip = getTooltip();
        return tip != null ? tip.progressive() : "";
    }

    public String getHeaderString() {
        EUIKeywordTooltip tip = getTooltip();
        return tip != null ? tip.title : PGR.core.strings.subjects_intent;
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        switch (this) {
            case Attack:
                return PGR.core.tooltips.attack;
            case Buff:
                return PGR.core.tooltips.buff;
            case Debuff:
                return PGR.core.tooltips.debuff;
            case Defend:
                return PGR.core.tooltips.block;
            case Escape:
                return PGR.core.tooltips.escape;
            case Stun:
                return PGR.core.tooltips.stun;
        }
        return null;
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return Collections.singletonList(getTooltip());
    }

    public boolean hasIntent(AbstractMonster creature) {
        return hasIntent(creature.intent);
    }

    public boolean hasIntent(AbstractMonster.Intent intent) {
        return intents.contains(intent);
    }
}
