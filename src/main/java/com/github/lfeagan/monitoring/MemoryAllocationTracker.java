package com.github.lfeagan.monitoring;

import com.github.lfeagan.wheat.time.SplitNanoStopWatch;
import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.google.monitoring.runtime.instrumentation.Sampler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MemoryAllocationTracker {
    Map<String,Long> methodBytes = new ConcurrentHashMap<>();

    volatile boolean printAllAllocations = false;

    protected final SplitNanoStopWatch stopWatch = SplitNanoStopWatch.createUnstarted();

    protected final Deque<String> methodStack = new ConcurrentLinkedDeque<>();

    protected final Function<JoinPoint,String> methodKey;

    public MemoryAllocationTracker(Function<JoinPoint,String> methodKey) {
        methodStack.push("main");
        this.methodKey = methodKey;
    }

    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        final String key = methodKey.apply(pjp);
        methodStack.push(key);
        stopWatch.start();
        // DO WORK
        Object retVal = pjp.proceed();
        // END DO WORK
        methodStack.pop();
        stopWatch.split(key);
        stopWatch.stop();
        return retVal;
    }

    public void printReport(PrintWriter pw) {
        pw.println("Method Allocations");
        final List<Map.Entry<String,Long>> sortedMethods = methodBytes.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList());
        for (Map.Entry<String,Long> entry : sortedMethods) {
            final Optional<Double> totalTime = stopWatch.getSplits(entry.getKey()).stream().map(e -> e.getElapsedSeconds()).reduce(Double::sum);
            if (totalTime.isPresent()) {
                pw.println(entry.getKey() + ":" + totalTime.get() + ":" + entry.getValue());
            } else {
                pw.println(entry.getKey() + ":NaN:" + entry.getValue());
            }
        }
        pw.flush();
    }

    public void registerSampler() {
        AllocationRecorder.addSampler(new Sampler() {
            public void sampleAllocation(int count, String desc, Object newObj, long size) {
                if (printAllAllocations) {
                    if (count != -1) {
                        System.out.printf("Allocated %s[%d], sizeof()=%d\n", desc, count, size);
                    } else {
                        System.out.printf("Allocated %s, type %s, sizeof()=%d\n",
                                newObj, desc, size);
                    }
                }
                methodBytes.computeIfPresent(methodStack.peek(), (k,v) -> v + size);
                methodBytes.computeIfAbsent(methodStack.peek(), (k) -> size);
            }
        });
    }
}
