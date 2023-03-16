package pinacolada.monsters;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomMonster;
import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BobEffect;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PCLCreature extends CustomMonster implements PointerProvider, TooltipProvider
{
    private static final Map<String, PCLCreatureData> staticData = new HashMap<>();
    public static final int PRIORITY_START_FIRST = 2;
    public static final int PRIORITY_START_LAST = 1;
    public static final int PRIORITY_END_FIRST = 0;
    public static final int PRIORITY_END_LAST = -1;

    public final PCLCreatureData creatureData;
    public PCLAffinity affinity = PCLAffinity.General;
    public boolean stunned;
    public int priority;

    public static PCLCreatureData getStaticData(String id)
    {
        return staticData.get(id);
    }

    protected static PCLCreatureData register(Class<? extends PCLCreature> type)
    {
        return PCLCreature.register(type, PGR.core);
    }

    protected static PCLCreatureData register(Class<? extends PCLCreature> type, PCLResources<?,?,?,?> resources)
    {
        return registerData(new PCLCreatureData(type, resources));
    }

    protected static PCLCreatureData registerData(PCLCreatureData creatureData)
    {
        staticData.put(creatureData.ID, creatureData);
        return creatureData;
    }

    public PCLCreature(PCLCreatureData data)
    {
        super(data.strings.NAME, data.ID, data.hp, data.hbX, data.hbY, data.hbW, data.hbH, data.imgUrl);
        this.creatureData = data;
    }

    public PCLCreature(PCLCreatureData data, float offsetX, float offsetY)
    {
        super(data.strings.NAME, data.ID, data.hp, data.hbX, data.hbY, data.hbW, data.hbH, data.imgUrl, offsetX, offsetY);
        this.creatureData = data;
        setupHitbox(offsetX, offsetY);
    }

    public PCLCreature(PCLCreatureData data, float offsetX, float offsetY, boolean ignoreBlights)
    {
        super(data.strings.NAME, data.ID, data.hp, data.hbX, data.hbY, data.hbW, data.hbH, data.imgUrl, offsetX, offsetY, ignoreBlights);
        this.creatureData = data;
        setupHitbox(offsetX, offsetY);
    }

    // Offset positions should be given with Settings.scaling already applied
    protected void setupHitbox(float offsetX, float offsetY)
    {
        this.drawX = offsetX;
        this.drawY = offsetY;
        updateHitbox(creatureData.hbX, creatureData.hbY, creatureData.hbW, creatureData.hbH);
        refreshHitboxLocation();
        refreshIntentHbLocation();
    }

    @Override
    public String getID()
    {
        return id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void loadAnimation(String atlasUrl, String skeletonUrl, float scale)
    {
        super.loadAnimation(atlasUrl, skeletonUrl, scale);
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return new ArrayList<>();
    }

    @Override
    public EUITooltip getIntentTip()
    {
        return null;
    }

    @Override
    public void addPower(AbstractPower powerToApply)
    {
        super.addPower(powerToApply);
        if (powerToApply instanceof StunMonsterPower)
        {
            stunned = true;
        }
    }

    @Override
    public void takeTurn()
    {
        if (stunned)
        {
            stunned = false;
        }
        else
        {
            performActions();
        }
    }

    @Override
    public void update()
    {
        super.update();
    }

    public void setAnimation(AbstractAnimation animation)
    {
        this.animation = animation;
    }

    public BobEffect getBobEffect()
    {
        return GameUtilities.getBobEffect(this);
    }

    public Color getIntentColor()
    {
        return ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentColor");
    }

    public abstract void performActions();
}
