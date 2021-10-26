package ru.innotech.education.rxjava.reader.writer;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.Semaphore;

public class ReadWriteListProxy implements MethodInterceptor {

    private final Semaphore writeSemaphore = new Semaphore(1);


    @Override
    public Object intercept(Object object, Method method, Object[] methodArgs,
                            MethodProxy methodProxy) throws Throwable {

        // Check whether the method is write operation type.
        if (method.isAnnotationPresent(WriteOperation.class)) {
            writeSemaphore.acquire();
            Object result =  methodProxy.invokeSuper(object, methodArgs);
            writeSemaphore.release();
            return result;
        }
        return methodProxy.invokeSuper(object, methodArgs);
    }
}
