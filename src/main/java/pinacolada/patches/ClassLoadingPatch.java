package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import extendedui.EUIUtils;
import javassist.*;
import org.clapper.util.classutil.*;
import pinacolada.misc.CombatManager;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassLoadingPatch
{
    private static ClassFinder finder;
    private static ClassPool pool;

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class ClassLoadingTrigger
    {
        public static void Raw(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException
        {
            finder = new ClassFinder();
            finder.add(
                    Arrays.stream(Loader.MODINFOS)
                            .map(modInfo -> modInfo.jarURL)
                            .filter(Objects::nonNull)
                            .map(url -> {
                                try {
                                    return url.toURI();
                                } catch (URISyntaxException e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .map(File::new)
                            .collect(Collectors.toList())
            );

            pool = ctBehavior.getDeclaringClass().getClassPool();

            // Add things that can should read from the class pool right away
            CombatManager.initializeEvents();
        }
    }

    public static List<CtClass> getClassesWithAnnotation(Class<?> targetClass, Class<?> annotation)
    {
        ClassFilter filter = new AndClassFilter(
                new NotClassFilter(new InterfaceOnlyClassFilter()),
                new NotClassFilter(new AbstractClassFilter()),
                new AnnotationClassFilter(annotation),
                new ClassModifiersClassFilter(Modifier.PUBLIC),
                new OrClassFilter(
                        new SubclassClassFilter(targetClass),
                        (classInfo, classFinder) -> classInfo.getClassName().equals(targetClass.getName())
                )
        );
        return getClasses(filter);
    }

    public static List<CtClass> getClasses(Class<?> targetClass)
    {
        ClassFilter filter = new AndClassFilter(
                new NotClassFilter(new InterfaceOnlyClassFilter()),
                new NotClassFilter(new AbstractClassFilter()),
                new ClassModifiersClassFilter(Modifier.PUBLIC),
                new OrClassFilter(
                        new SubclassClassFilter(targetClass),
                        (classInfo, classFinder) -> classInfo.getClassName().equals(targetClass.getName())
                )
        );
        return getClasses(filter);
    }

    private static List<CtClass> getClasses(ClassFilter filter)
    {
        List<ClassInfo> foundClasses = new ArrayList<>();
        finder.findClasses(foundClasses, filter);
        return EUIUtils.map(foundClasses, c -> {
            try
            {
                return pool.get(c.getClassName());
            }
            catch (NotFoundException ignored)
            {
                return null;
            }
        });
    }

    private static class AnnotationClassFilter implements ClassFilter
    {
        private final Class<?> annotation;

        public AnnotationClassFilter(Class<?> annotation)
        {
            this.annotation = annotation;
        }

        @Override
        public boolean accept(ClassInfo classInfo, ClassFinder classFinder)
        {
            try {
                CtClass ctClass = pool.get(classInfo.getClassName());
                return ctClass.hasAnnotation(annotation);
            } catch (NotFoundException ignored) {
            }

            return false;
        }
    }
}
