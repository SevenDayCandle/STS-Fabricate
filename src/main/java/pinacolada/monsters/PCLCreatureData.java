package pinacolada.monsters;

import com.megacrit.cardcrawl.localization.MonsterStrings;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PCLStrings;
import pinacolada.resources.PGR;

public class PCLCreatureData
{
    public final Class<? extends PCLCreature> type;
    public final String ID;
    public final MonsterStrings strings;
    public float hbX;
    public float hbY;
    public float hbW = 100;
    public float hbH = 100;
    public int hp = 1;
    public String imgUrl;

    public PCLCreatureData(Class<? extends PCLCreature> type, PCLResources<?,?,?,?> resources)
    {
        this.ID = resources.createID(type.getSimpleName());
        this.strings = PGR.getMonsterStrings(ID);
        this.type = type;
    }

    public PCLCreatureData setHb(float hbX, float hbY, float hbW, float hbH)
    {
        this.hbX = hbX;
        this.hbY = hbY;
        this.hbW = hbW;
        this.hbH = hbH;
        return this;
    }

    public PCLCreatureData setImage(String imgUrl)
    {
        this.imgUrl = imgUrl;
        return this;
    }
}
