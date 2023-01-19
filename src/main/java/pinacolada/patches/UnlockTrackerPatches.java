package pinacolada.patches;


// Copied and modified from STS-AnimatorMod
// TODO remove, no longer necessary
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class UnlockTrackerPatches
{
    private static final String KEY_UNLOCK_LEVEL = "UnlockLevel";
    private static final String KEY_PROGRESS = "Progress";
    private static final String KEY_CURRENT_COST = "CurrentCost";
    private static final String KEY_TOTAL_SCORE = "TotalScore";
    private static final String KEY_HIGH_SCORE = "HighScore";

    private static String createFullID(PCLResources<?,?,?> resources, String name)
    {
        return resources.createID(name);
    }

    public static void validate(PCLResources<?,?,?> resources)
    {
        final float progress = UnlockTracker.getCurrentProgress(resources.playerClass);
        final int cost = UnlockTracker.getCurrentScoreCost(resources.playerClass);
        final int expectedCost = resources.getUnlockCost();
        if (cost != expectedCost)
        {
            UnlockTracker.unlockProgress.putInteger(KEY_CURRENT_COST, expectedCost);
            if (progress < expectedCost)
            {
                UnlockTracker.unlockProgress.flush();
            }
        }

        if (progress >= expectedCost)
        {
            if (resources.getUnlockLevel() < PCLAbstractPlayerData.MAX_UNLOCK_LEVEL)
            {
                UnlockTracker.addScore(resources.playerClass, 1);
            }
        }
    }

    @SpirePatch(clz = UnlockTracker.class, method = "addScore", paramtypez = {AbstractPlayer.PlayerClass.class, int.class})
    public static class UnlockTracker_addScore
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractPlayer.PlayerClass c, int scoreGained)
        {
            if (!GameUtilities.isPCLPlayerClass(c))
            {
                return SpireReturn.Continue();
            }

            PCLResources<?,?,?> r = PGR.getResources(c);
            int p = UnlockTracker.unlockProgress.getInteger(KEY_PROGRESS, 0);
            p += scoreGained;
            int total;
            int highscore;
            int unlockCost = r.getUnlockCost();
            String unlockKey = r.createID(KEY_UNLOCK_LEVEL);
            String progressKey = r.createID(KEY_PROGRESS);
            String costKey = r.createID(KEY_PROGRESS);
            String scoreKey = r.createID(KEY_TOTAL_SCORE);
            String highScoreKey = r.createID(KEY_HIGH_SCORE);

            if (p >= unlockCost)
            {

                EUIUtils.logInfoIfDebug(UnlockTrackerPatches.class, "[DEBUG] Level up!");
                total = UnlockTracker.unlockProgress.getInteger(unlockKey, 0) + 1;
                UnlockTracker.unlockProgress.putInteger(unlockKey, total); // <------- LEVEL UP
                p -= UnlockTracker.unlockProgress.getInteger(costKey, unlockCost);
                UnlockTracker.unlockProgress.putInteger(progressKey, p);
                EUIUtils.logInfoIfDebug(UnlockTrackerPatches.class, "[DEBUG] Score Progress: " + progressKey);
                //highscore = UnlockTracker.unlockProgress.getInteger(key_current_cost, defaultCost);
                int nextUnlockCost = r.getUnlockCost();
                UnlockTracker.unlockProgress.putInteger(costKey, nextUnlockCost);
                if (p > nextUnlockCost)
                {
                    UnlockTracker.unlockProgress.putInteger(progressKey, nextUnlockCost - 1);
                    EUIUtils.logInfoIfDebug(UnlockTrackerPatches.class, "Overflow maxes out next level");
                }
            }
            else
            {
                UnlockTracker.unlockProgress.putInteger(progressKey, p);
            }

            total = UnlockTracker.unlockProgress.getInteger(scoreKey, 0);
            total += scoreGained;
            UnlockTracker.unlockProgress.putInteger(scoreKey, total);
            EUIUtils.logInfoIfDebug(UnlockTrackerPatches.class, "[DEBUG] Total score: " + total);
            highscore = UnlockTracker.unlockProgress.getInteger(highScoreKey, 0);
            if (scoreGained > highscore)
            {
                UnlockTracker.unlockProgress.putInteger(highScoreKey, scoreGained);
                EUIUtils.logInfoIfDebug(UnlockTrackerPatches.class, "[DEBUG] New high score: " + scoreGained);
            }

            UnlockTracker.unlockProgress.flush();

            return SpireReturn.Return(null);
        }
    }
}
