package pinacolada.augments;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.skills.skills.PMultiTrait;

import java.lang.reflect.Constructor;

public class PCLAugmentData
{
    public final String ID;
    public final Class<? extends PCLAugment> augClass;
    public final int tier;
    public final PCLAffinity affinity;
    public AugmentStrings strings;
    public PSkill skill;
    public PCLAugmentReqs reqs;
    public boolean isSpecial;

    public PCLAugmentData(String id, Class<? extends PCLAugment> augClass, int tier, PCLAffinity affinity)
    {
        this.ID = id;
        this.augClass = augClass;
        this.tier = tier;
        this.affinity = affinity;
        strings = PGR.getAugmentStrings(this.ID);
    }

    public boolean canApply(AbstractCard c)
    {
        return c instanceof PCLCard && canApplyImpl((PCLCard) c);
    }

    protected boolean canApplyImpl(PCLCard c)
    {
        return c != null && (reqs == null || reqs.check(c)) && c.getFreeAugmentSlot() >= 0;
    }

    public PCLAugment create()
    {
        try
        {
            Constructor<? extends PCLAugment> c = EUIUtils.tryGetConstructor(augClass);
            if (c != null)
            {
                return c.newInstance();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EUIUtils.logError(this, "Failed to create");
        }
        return null;
    }

    public PCLAugmentData setReqs(PCLAugmentReqs reqs)
    {
        this.reqs = reqs;
        return this;
    }

    public PCLAugmentData setSkill(PSkill... skills)
    {
        this.skill = PMultiSkill.join(skills);
        return this;
    }

    public PCLAugmentData setSkill(PSkill skill)
    {
        this.skill = skill;
        return this;
    }

    public PCLAugmentData setSkill(PTrait... traits)
    {
        this.skill = PMultiTrait.join(traits);
        return this;
    }

    public PCLAugmentData setSpecial(boolean value)
    {
        this.isSpecial = value;
        return this;
    }
}
