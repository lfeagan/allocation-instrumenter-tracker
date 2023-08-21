There are five basic steps to use the memory allocation tracker:
1. Construct a new `MemoryAllocationTracker` and provide it with a lambda function that takes as input the join point (type: `org.aspectj.lang.JoinPoint`) and that returns the key (type `java.lang.String`) that will be used to store the allocation statistics in a `Map<String,Long>`.
2. Register the sampler.
3. Create an AspectJ join point that measures `@Around` the calls you are interested in and call the tracker method `measureExecutionTime` within.
4. Run your code with additional VM arguments for javaagent to specify the path to the Java allocation instrumenter agent library.
   1. `-javaagent:<PATH TO>/java-allocation-instrumenter-<VERSION>.jar`
5. Analyze the results.

## Example Java Code

```java
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
```

## AspectJ
AspectJ is an aspect-oriented extension to the Java programming language. Aspects enable clean modularization of cross-cutting concerns, such as monitoring, logging, error checking, context-sensitive behaviors, debugging, etc. AspectJ adds to Java just one new concept, a join point -- and that's really just a name for an existing Java concept. It adds to Java only a few new constructs: pointcuts, advice, inter-type declarations and aspects. Pointcuts and advice dynamically affect program flow, inter-type declarations statically affects a program's class hierarchy, and aspects encapsulate these new constructs. A join point is a well-defined point in the program flow. A pointcut picks out certain join points and values at those points. A piece of advice is code that is executed when a join point is reached. These are the dynamic parts of AspectJ. For Spring users, [this](https://www.baeldung.com/spring-aop-vs-aspectj) comparison of Spring AOP with AspectJ may be helpful. You can find a decent set of pointcut expression examples to use when instrumenting your code [here](https://howtodoinjava.com/spring-aop/aspectj-pointcut-expressions/). 
