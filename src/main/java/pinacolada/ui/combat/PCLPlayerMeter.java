package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.FakeFtue;
import pinacolada.cards.base.AffinityReactions;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.ClickableProvider;
import pinacolada.powers.PCLAffinityPower;
import pinacolada.powers.PCLClickableUse;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.ui.EUICardDraggable;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PCLPlayerMeter extends EUICardDraggable<PCLCard> implements ClickableProvider
{
    public static String createFullID(PCLResources<?,?,?> resources, Class<? extends PCLPlayerMeter> type)
    {
        return resources.createID(type.getSimpleName());
    }

    public static final int TARGET_CURRENT = 0;
    public static final int TARGET_NEXT = 1;
    public static final int DEFAULT_REROLLS = 1;
    protected EUIButton infoIcon;
    protected PCLClickableUse skips;
    protected String id;
    protected int matchesThisCombat;
    protected int currentMatchCombo;
    protected int longestMatchCombo;

    public PCLPlayerMeter(String id, STSConfigItem<Vector2> config, float iconSize)
    {
        super(config, new DraggableHitbox(screenW(0.0366f), screenH(0.425f), iconSize, iconSize, true), iconSize);
        this.id = id;
        infoIcon = new EUIButton(ImageMaster.INTENT_UNKNOWN, new RelativeHitbox(hb, scale(40f), scale(40f), scale(100f), scale(20f)))
                .setTooltip(getInfoTitle(), getInfoMainDescrption() + EUIUtils.DOUBLE_SPLIT_LINE + PGR.core.strings.tutorial.learnMore)
                .setOnClick(() -> {
                    AbstractDungeon.ftue = new FakeFtue(getInfoTitle(), getInfoDescription());
                });
    }

    public void addLevel(PCLAffinity affinity, int amount)
    {
    }

    public void addSkip(int amount)
    {
    }

    public void advance(PCLAffinity affinity)
    {
    }

    public void disableAffinity(PCLAffinity affinity)
    {
    }

    public void flash(int target)
    {
    }

    public void flashAffinity(PCLAffinity affinity)
    {
    }

    public PCLAffinity get(int target)
    {
        return PCLAffinity.General;
    }

    public ArrayList<? extends PCLAffinityPower> getActivePowers() {return new ArrayList<>();}

    public PCLAffinity getCurrentAffinity()
    {
        return get(0);
    }

    public int getCurrentMatchCombo()
    {
        return currentMatchCombo;
    }

    public abstract String[] getInfoDescription();

    public abstract String getInfoMainDescrption();

    public abstract String getInfoTitle();

    public int getLevel(PCLAffinity affinity)
    {
        return 0;
    }

    public int getLongestMatchCombo()
    {
        return longestMatchCombo;
    }

    public int getMatchesThisCombat()
    {
        return matchesThisCombat;
    }

    public PCLAffinity getNextAffinity()
    {
        return get(1);
    }

    public PCLAffinityPower getPower(PCLAffinity affinity)
    {
        return null;
    }

    public int getPowerAmount(PCLAffinity affinity) {return 0;}

    public ArrayList<? extends PCLAffinityPower> getPowers()
    {
        return new ArrayList<>();
    }

    public AffinityReactions getReactions(AbstractCard c, Collection<? extends AbstractCreature> mo) {return new AffinityReactions();}

    public Object getRerollDescription() {return null;}

    public Object getRerollDescription2() {return null;}

    public boolean hasMatch(AbstractCard card)
    {
        PCLAffinity current = getCurrentAffinity();
        if (current == PCLAffinity.General)
        {
            return false;
        }
        PCLCard eCard = EUIUtils.safeCast(card, PCLCard.class);
        if (eCard != null)
        {
            if (current.equals(PCLAffinity.Star))
            {
                return (eCard.affinities.getLevel(PCLAffinity.General, true) > 0);
            }
            return (eCard.affinities.getLevel(current, true) > 0);
        }
        return false;
    }

    public void increaseMatchCombo(int amount)
    {
        currentMatchCombo += amount;
        longestMatchCombo = Math.max(longestMatchCombo, currentMatchCombo);
    }

    public boolean isHovered()
    {
        return super.isHovered() || infoIcon.hb.hovered;
    }

    public boolean isPowerActive(PCLAffinity affinity) {return false;}

    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        infoIcon.renderImpl(sb);
    }

    public int size() {return 0;}

    public void initialize()
    {
        super.initialize();
        matchesThisCombat = 0;
        currentMatchCombo = 0;
        longestMatchCombo = 0;
    }

    public float modifyBlock(float block, PCLCard source, PCLCard card, AbstractCreature target)
    {
        return block;
    }

    public float modifyDamage(float damage, PCLCard source, PCLCard card, AbstractCreature target)
    {
        return damage;
    }

    public float modifyMagicNumber(float magicNumber, PCLCard source, PCLCard card)
    {
        return magicNumber;
    }

    public float modifyOrbOutput(float initial, AbstractCreature target, AbstractOrb orb)
    {
        return initial;
    }

    public void updateImpl(PCLCard card, AbstractCreature target, boolean draggingCard, boolean shouldUpdateForCard, boolean shouldUpdateForTarget)
    {
        infoIcon.tryUpdate();
    }

    public void onCardPlayed(AbstractCard card, AbstractCreature m, PCLUseInfo info, boolean fromSummon)
    {
    }

    public void onEndOfTurn()
    {
    }

    public void onMatch(AbstractCard card)
    {
    }

    public void onNotMatch(AbstractCard card)
    {
    }

    public void onStartOfTurn()
    {
    }

    public PCLAffinity set(PCLAffinity affinity, int target)
    {
        return get(0);
    }

    public String getID()
    {
        return id;
    }

    public EUITooltip getTooltip()
    {
        return null;
    }
}
