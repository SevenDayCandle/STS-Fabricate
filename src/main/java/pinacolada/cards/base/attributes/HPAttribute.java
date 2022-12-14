package pinacolada.cards.base.attributes;

import pinacolada.cards.base.PCLCard;

public class HPAttribute extends PCLAttribute
{
    public static HPAttribute instance = new HPAttribute();

    public HPAttribute()
    {
        this.icon = ICONS.hp.texture();
        this.largeIcon = ICONS.hpL.texture();
        this.scaleMult = 0.7f;
    }

    @Override
    public PCLAttribute setCard(PCLCard card)
    {
        pclText = card.getSecondaryValueString();
        iconTag = null;
        suffix = null;

        return this;
    }
}
