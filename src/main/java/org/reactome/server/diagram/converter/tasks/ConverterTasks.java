package org.reactome.server.diagram.converter.tasks;

import org.reactome.server.diagram.converter.graph.output.Graph;
import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.tasks.common.ConverterTask;
import org.reactome.server.diagram.converter.tasks.common.annotation.FinalTask;
import org.reactome.server.diagram.converter.tasks.common.annotation.InitialTask;
import org.reactome.server.graph.domain.model.Species;
import org.reactome.server.graph.domain.result.SimpleDatabaseObject;
import org.reactome.server.graph.service.SpeciesService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The Conversion tasks are satellite tasks to be executed either before the conversion {@link InitialTask}
 * or right after it has finished {@link FinalTask}. Each one is run once at the right moment and those do
 * not get {@link Diagram} nor {@link Graph} objects but can access the graph database.
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("ALL")
public abstract class ConverterTasks {

    private static final Logger logger = LoggerFactory.getLogger("converter");

    private static final List<Class<?>> initialTasks = new ArrayList<>();
    private static final List<Class<?>> finalTasks = new ArrayList<>();
    private static Object target;

    public static void initialise(String[] target, Collection<SimpleDatabaseObject> pathways) {
        System.out.println("\r· Diagram converter tasks initialisation:");
        System.out.print("\t>Initialising converter tasks infrastructure...");

        setTarget(target, pathways);

        Reflections reflections = new Reflections(ConverterTasks.class.getPackage().getName());
        int d = 0;
        Set<Class<? extends ConverterTask>> tasks = reflections.getSubTypesOf(ConverterTask.class);
        for (Class<?> task : tasks) {
            if (task.getAnnotation(Deprecated.class) != null) d++;
            else {
                for (Annotation annotation : task.getAnnotations()) {
                    if (annotation instanceof InitialTask) {
                        initialTasks.add(task);
                    } else if (annotation instanceof FinalTask) {
                        finalTasks.add(task);
                    }
                }
            }
        }
        int a = initialTasks.size() + finalTasks.size();
        int t = a + d;
        System.out.printf("\r\t>%3d task%s found:%n", t, t == 1 ? "" : "s");
        String summary = (a == 0) ? "" : String.format("(%d initial and %d final)", initialTasks.size(), finalTasks.size());
        System.out.printf("\t\t-%3d task%s active %s%n", a, a == 1 ? "" : "s", summary);
        System.out.printf("\t\t-%3d task%s excluded ('@Deprecated')%n", d, d == 1 ? "" : "s");
        System.out.println();
    }

    public static void runInitialTasks() {
        if (initialTasks.isEmpty()) return;
        System.out.printf("\r· Running initial task%s:%n", initialTasks.size() == 1 ? "s" : "");
        for (Class<?> initialTask : initialTasks) run(initialTask);
        System.out.println();
    }

    public static void runFinalTasks() {
        if (finalTasks.isEmpty()) return;
        System.out.printf("\r· Running final task%s:%n", finalTasks.size() == 1 ? "s" : "");
        for (Class<?> finalTask : finalTasks) run(finalTask);
        System.out.println();
    }

    private static void run(Class<?> task) {
        try {
            ConverterTask cTask = (ConverterTask) Arrays.stream(task.getConstructors()).findFirst().get().newInstance();
            System.out.print("\t> Running '" + cTask.getName() + "'...");
            cTask.run(target);
            System.out.println("\r\t> " + cTask.getReportSummary());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            String msg = "There was an error while executing the '" + task.getSimpleName() + "' task";
            System.err.println(msg + ". Please see logs for more details.");
            logger.error(msg, e);
        }
    }

    private static void setTarget(String[] target, Collection<SimpleDatabaseObject> pathways) {
        if (target.length == 1) {
            String t = target[0];
            if (t.toLowerCase().equals("all")) {
                ConverterTasks.target = "all";
                return;
            }
            Species obj = ReactomeGraphCore.getService(SpeciesService.class).getSpecies(t);
            if (obj != null) {
                ConverterTasks.target = obj;
                return;
            }
        }
        List<String> rtn = new ArrayList<>();
        for (SimpleDatabaseObject sdo : pathways) rtn.add(sdo.getStId());
        ConverterTasks.target = rtn;
    }
}
