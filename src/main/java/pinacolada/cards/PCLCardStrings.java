package pinacolada.cards;

import java.util.HashMap;

public class PCLCardStrings
{
    public final static HashMap<String, PCLCardStrings> strings = new HashMap<>();

    public String NAME;
    public String AUTHOR;
    public String SUBTEXT;
    public String[] DESCRIPTION;

    public PCLCardStrings()
    {
    }

    public static PCLCardStrings getMockString()
    {
        PCLCardStrings retVal = new PCLCardStrings();
        retVal.NAME = "MISSING";
        return retVal;
    }
}
