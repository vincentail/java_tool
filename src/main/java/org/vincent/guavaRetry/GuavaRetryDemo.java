package org.vincent.guavaRetry;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author vincent
 * @date 2021/12/29
 */
public class GuavaRetryDemo {
    private final static Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
            .retryIfResult(Predicates.isNull()) //当返回空时重试
            .retryIfExceptionOfType(IOException.class)//抛出IOException时重试
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))//尝试执行三次，即重试两次
            .withWaitStrategy(WaitStrategies.fibonacciWait(2, TimeUnit.SECONDS))//重试间隔
            .build();
    static AtomicInteger nullTry = new AtomicInteger(0);
    static AtomicInteger exceptionTry = new AtomicInteger(0);


    public static void main(String[] args) throws ExecutionException, RetryException {
        try {
            retryer.call(() -> {
                System.out.println("null retry time：" + nullTry.addAndGet(1));
                return null;
            });// 模拟返回null
        } catch (Exception e) {

        }
        retryer.call(()->{
            System.out.println("normal retry time：" + nullTry.addAndGet(1));
            return true;
        });// 模拟返回null


        try {
            retryer.call(() -> {
                System.out.println("IoException retry time：" + exceptionTry.addAndGet(1));
                throw new IOException();
            });// 模拟IOException
        } catch (Exception e) {

        }
        retryer.call(()->{
            System.out.println("Exception retry time：" + exceptionTry.addAndGet(1));
            throw new Exception();
        });// 模拟Exception
    }
}
