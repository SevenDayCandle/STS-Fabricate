package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;

// TODO make editor for making this
public class PCLCustomLoadout extends PCLLoadout {
    public String author;
    public String name;

    public PCLCustomLoadout(AbstractCard.CardColor color, String id, int unlockLevel) {
        super(color, id, unlockLevel);
    }

    public PCLCustomLoadout(PCLCustomLoadoutInfo info)
    {
        super(info.color, info.ID, info.unlockLevel);
        this.author = info.author;
        this.name = info.name;
    }

    public String getAuthor() {
        return author != null ? author : "";
    }

    public String getName() {
        return name != null ? name : "";
    }

}
