package pinacolada.cards.base.attributes;

import pinacolada.cards.base.PCLCard;

public class DamageAttribute extends PCLAttribute
{
    public static final DamageAttribute instance = new DamageAttribute();

    @Override
    public PCLAttribute setCard(PCLCard card)
    {
        suffix = null;

        switch (card.attackType)
        {
            case Brutal:
                icon = ICONS.brutalL.texture();
                largeIcon = ICONS.brutal.texture();
                break;

            case Magical:
                icon = ICONS.magic.texture();
                largeIcon = ICONS.magicL.texture();
                break;

            case Piercing:
                icon = ICONS.piercing.texture();
                largeIcon = ICONS.piercingL.texture();
                break;

            case Ranged:
                icon = ICONS.ranged.texture();
                largeIcon = ICONS.rangedL.texture();
                break;

            case Normal:
            default:
                icon = ICONS.damage.texture();
                largeIcon = ICONS.damageL.texture();
                break;
        }

        if (card.pclTarget != null)
        {
            iconTag = card.pclTarget.getTag();
        }

        pclText = card.getDamageString();

        return this;
    }
}
