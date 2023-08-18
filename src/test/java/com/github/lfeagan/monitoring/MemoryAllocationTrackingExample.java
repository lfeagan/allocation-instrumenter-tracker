package com.github.lfeagan.monitoring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.PrintWriter;

@Aspect
public class MemoryAllocationTrackingExample {

    static MemoryAllocationTracker tracker;

    public static void main(String [] args) throws Exception {
        tracker = new MemoryAllocationTracker((jp) -> {
            if (jp.getSignature().getName().startsWith("allocate")) {
                int size = (int) jp.getArgs()[0];
                int i = (int) (Math.log(size) / Math.log(2));
                return String.format(jp.getSignature().getName()+"-%02d-%d", i, size);
            } else {
                return jp.getSignature().getName();
            }
        });
        tracker.registerSampler();

        // do work
        MemoryAllocationWorkload workload = new MemoryAllocationWorkload();
        workload.allocateIntArrays((short) 24);
        workload.allocateStrings((short) 16);
        // print results
        tracker.printReport(new PrintWriter(System.out));
    }

    @Around("execution(* *.allocate*(int))")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        return tracker.measureExecutionTime(pjp);
    }

}
