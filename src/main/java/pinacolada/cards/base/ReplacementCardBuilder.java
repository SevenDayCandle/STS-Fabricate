package pinacolada.cards.base;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.pcl.special.QuestionMark;

// TODO Try to construct effects from card description
public class ReplacementCardBuilder extends PCLDynamicData
{
    public final AbstractCard original;

    public ReplacementCardBuilder(AbstractCard card, boolean copyNumbers)
    {
        this(card, card.name, card.rawDescription, copyNumbers);
    }

    public ReplacementCardBuilder(AbstractCard card, String text, boolean copyNumbers)
    {
        this(card, card.name, text, copyNumbers);
    }

    public ReplacementCardBuilder(AbstractCard original, String name, String text, boolean copyNumbers)
    {
        super(original.cardID);
        this.original = original.makeStatEquivalentCopy();

        if (copyNumbers)
        {
            AbstractCard upgradedCopy = original.makeStatEquivalentCopy();
            upgradedCopy.upgrade();

            setDamage(original.baseDamage, upgradedCopy.baseDamage - original.baseDamage)
                    .setBlock(original.baseBlock, upgradedCopy.baseBlock - original.baseBlock)
                    .setMagicNumber(original.baseMagicNumber, upgradedCopy.baseMagicNumber - original.baseMagicNumber)
                    .setCosts(original.cost)
                    .setCostUpgrades(upgradedCopy.cost - original.cost);
            if (original.type.equals(AbstractCard.CardType.ATTACK))
            {
                setAttackType(PCLAttackType.Normal);
            }

            for (PCLCardTag tag : PCLCardTag.getAll())
            {
                if (tag.has(original))
                {
                    this.tags.put(tag, tag.make());
                }
            }

        }
        else
        {
            setCosts(-2).setCostUpgrades(0);
        }

        // Custom card paths are recorded in CustomCard.imgMap
        String assetUrl = original.assetUrl;
        Texture cardTexture = CustomCard.imgMap.get(assetUrl);
        if (cardTexture != null)
        {
            setImage(new ColoredTexture(cardTexture), null);
        }
        else
        {
            setImagePathFromAtlasUrl(assetUrl);
        }

        PCLCardTarget ct = PCLCardTarget.Single;
        switch (original.target)
        {
            case NONE:
                ct = PCLCardTarget.None;
                break;
            case SELF:
                ct = PCLCardTarget.Self;
                break;
            case ALL:
            case ALL_ENEMY:
                ct = PCLCardTarget.AllEnemy;
                break;
        }
        setTarget(ct);
        setProperties(original.type, original.rarity);
        setText(name, text, null);
    }

    public ReplacementCard buildPCL()
    {
        if (strings == null)
        {
            setText("", "", "");
        }

        if (imagePath == null)
        {
            imagePath = QuestionMark.DATA.imagePath;
        }

        return new ReplacementCard(this);
    }
}