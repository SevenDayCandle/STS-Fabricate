package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.skills.*;
import pinacolada.skills.skills.PDelegateCardCond;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

@AbstractCardModifier.SaveIgnore
public class SkillModifier extends AbstractCardModifier {
    private static transient ArrayList<PSkillData<?>> MOVES;
    private static transient ArrayList<PSkillData<?>> MODS;
    private static transient ArrayList<PSkillData<?>> DELCONDS;
    private static transient ArrayList<PSkillData<?>> CONDS;
    private static transient ArrayList<PSkillData<?>> TRAITS;
    private transient String descCache = EUIUtils.EMPTY_STRING;
    private String serialized;
    private transient PSkill<?> skill;
    private transient PCLUseInfo info;

    public SkillModifier(String serialized) {
        this.serialized = serialized;
        this.skill = PSkill.get(serialized);
    }

    public SkillModifier(PSkill<?> skill) {
        this.skill = skill;
        this.serialized = skill.serialize();
    }

    public static ArrayList<? extends SkillModifier> getAll(AbstractCard c) {
        return EUIUtils.mapAsNonnull(CardModifierManager.modifiers(c), mod -> EUIUtils.safeCast(mod, SkillModifier.class));
    }

    /* Generate a random skill with the following rules:
    *
    * Attacks/Summons will also always have a damage move, handled separately
    * Skills have a 3% chance to have a Block move, 30% if it originally gave Block
    *
    * 8% chance to double value and have a negative tag (Exhaust/Ethereal)
    *
    * For Attack/Skill/Summon
    * 100% chance to have PMove
    * 25% base chance to have a PMod. Chance decreases with effect value.
    * 30% base chance to have a PCond. Chance increases with effect value.
    * If no PMod, and PMove is PCallbackMove, 20% chance to chain a PMove with use parent with the same PField type
    *
    * For Power
    * 100% chance to have PMove
    * 15% chance to have a PMod. Chance decreases with effect value.
    * 100% chance to have a PCond
    * If cond is PFacetCond, use PLimitPassive. Otherwise, PLimitWhen
    * If no PMod, and PMove is PCallbackMove, 15% chance to chain a PMove with use parent with the same PField type
    * Wrap in an apply power skill
    *
    * For Status/Curse
    * 100% chance to have PMove that is negative
    * If unplayable, 100% to have a PDelegateCardCond
    *
    * Given a card ID and a seed, the same skill modifier will always get generated
    * */
    public static PSkill<?> generateRandom(AbstractCard c, boolean exhausts) {
        Long seed = Settings.seed != null ? Settings.seed + c.cardID.hashCode() : c.cardID.hashCode();
        Random generate = new Random(seed);
        int value =
                c.cost < 0 ? exhausts ? c.cost * 2 : c.cost :
                exhausts ? (((c.cost + 2) * 2)) : c.cost + 2;
        PSkill<?> current = null;
        initializeChoices();

        switch (c.type) {
            case STATUS:
            case CURSE:
                current = getChoice(MOVES, generate, value);
                if (c.cost < -1) {
                    current = getChoice(DELCONDS, generate, value);
                }
                break;
            case POWER:
                current = getChoice(MOVES, generate, value);
                if (passesChance(15, current, generate)) {
                    current = getChoice(MODS, generate, value, current);
                }
                current = getChoice(CONDS, generate, value, current);
                if (current instanceof PFacetCond) {
                    current = new PTrigger_Passive().setChild(current);
                }
                else {
                    current = new PTrigger_When().setChild(current);
                }
                break;
            default:
                current = getChoice(MOVES, generate, value);
                if (passesChance(25, current, generate)) {
                    current = getChoice(MODS, generate, value, current);
                }
                if (passesChance(25, current, generate)) {
                    current = getChoice(MODS, generate, value, current);
                }
        }
        return current;
    }

    private static PSkill<?> getChoice(ArrayList<PSkillData<?>> options, Random rng, int value) {
        PSkillData<?> t = GameUtilities.getRandomElement(MOVES, rng);
        assert t != null;
        PSkill<?> res = t.instantiateSkill();
        assert res != null;
        res.randomize(rng, value);
        return res;
    }

    private static PSkill<?> getChoice(ArrayList<PSkillData<?>> options, Random rng, int value, PSkill<?> current) {
        return getChoice(options, rng, value).setChild(current);
    }

    private static void initializeChoices() {
        if (MOVES == null) {
            MOVES = PSkill.getEligibleClasses(PMove.class);
            MODS = PSkill.getEligibleClasses(PMod.class);
            CONDS = PSkill.getEligibleClasses(PCond.class);
            DELCONDS = PSkill.getEligibleClasses(PDelegateCardCond.class);
            TRAITS = PSkill.getEligibleClasses(PTrait.class);
        }
    }

    private static boolean passesChance(int base, PSkill<?> current, Random rng) {
        int chance = base + current.getWorth();
        return rng.randomBoolean(chance / 100f);
    }

    public boolean canPlayCard(AbstractCard card) {
        return skill.canPlay(getInfo(card, null), null, true);
    }

    public PCLUseInfo getInfo(AbstractCard card, AbstractCreature target) {
        if (info == null) {
            info = CombatManager.playerSystem.getInfo(card, AbstractDungeon.player, target);
        }
        return info;
    }

    public PSkill<?> getSkill() {
        if (skill == null) {
            skill = PSkill.get(serialized);
        }
        return skill;
    }

    @Override
    public String identifier(AbstractCard card) {
        return skill.effectID + skill.getUUID();
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SkillModifier(serialized);
    }

    // Generate infos manually because we cannot attach the skill to the card if it is not an EditorCard
    @Override
    public float modifyBlock(float block, AbstractCard card) {
        return skill.modifyBlockFirst(getInfo(card, null), block);
    }

    @Override
    public float modifyBlockFinal(float block, AbstractCard card) {
        return skill.modifyBlockLast(getInfo(card, null), block);
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return skill.modifyDamageGiveFirst(getInfo(card, target), damage);
    }

    @Override
    public float modifyDamageFinal(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return skill.modifyDamageGiveLast(getInfo(card, target), damage);
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (StringUtils.isEmpty(descCache)) {
            if (card instanceof PCLCard) {
                descCache = rawDescription + EUIUtils.SPLIT_LINE + skill.getText(PCLCardTarget.Self, null, true);
            }
            else {
                descCache = rawDescription + EUIUtils.LEGACY_DOUBLE_SPLIT_LINE + skill.getLegacyText();
            }
        }
        return descCache;
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        info = refreshInfo(card, null);
    }

    @Override
    public void onCalculateCardDamage(AbstractCard card, AbstractMonster mo) {
        info = refreshInfo(card, mo);
    }

    public void onDiscard(AbstractCard card) {
        skill.triggerOnDiscard(card);
    }

    public void onDrawn(AbstractCard card) {
        skill.triggerOnDraw(card);
    }

    public void onExhausted(AbstractCard card) {
        skill.triggerOnExhaust(card);
    }

    public void onFetched(AbstractCard card, CardGroup sourcePile) {
        skill.triggerOnFetch(card, sourcePile);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        PSkill<?> skill = getSkill();
        if (card instanceof PointerProvider) {
            skill.setSource((PointerProvider) card).onAddToCard(card);
        }
        else {
            skill.source = card;
        }
    }

    public void onOtherCardPlayed(AbstractCard card, AbstractCard otherCard, CardGroup group) {
        skill.triggerOnOtherCardPlayed(otherCard);
    }

    public void onPurged(AbstractCard card) {
        skill.triggerOnPurge(card);
    }

    @Override
    public void onRemove(AbstractCard card) {
        skill.onRemoveFromCard(card);
    }

    public void onReshuffled(AbstractCard card, CardGroup group) {
        skill.triggerOnReshuffle(card, group);
    }

    public void onUpgraded(AbstractCard card) {
        skill.triggerOnUpgrade(card);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        skill.use(getInfo(card, target), PCLActions.bottom);
    }

    public PCLUseInfo refreshInfo(AbstractCard card, AbstractCreature target) {
        info = null;
        return getInfo(card, target);
    }
}