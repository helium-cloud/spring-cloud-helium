package test.org.helium.rpc.future;

import org.helium.rpc.channel.RpcFuture;
import org.helium.rpc.channel.RpcResults;
import org.helium.rpc.channel.tcp.RpcTcpEndpoint;
import org.helium.rpc.client.RpcMethodStub;
import org.helium.rpc.client.RpcProxyFactory;
import org.helium.threading.FutureGroup;
import org.helium.threading.FutureListener;
import org.helium.util.EventHandler;
import org.helium.util.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FutureClient {

    private static RpcTcpEndpoint tcpEndpoint;
    private static RpcMethodStub method1;
    private static RpcMethodStub exceptionMethod;
    private static RpcMethodStub timeOutMethod;

    /**
     * 为这个Test提供的一个快捷创建Listener的方法
     *
     * @return
     */
    public static FutureListener<RpcResults> newListener() {
        return new FutureListener<RpcResults>() {

            @Override
            public void run(Result<RpcResults> result) {
                if (result.getError() != null) {
                    System.out.println(result.getError());
                } else {
                    System.out.println(result.getValue().getValue(String.class));
                }
            }
        };
    }

    public static void printRpcFuture(RpcFuture future) {
        try {
            System.out.print("ClientTime = [" + new Date() + "] Server");
            if (future.getValue().getError() != null) {
                System.out.println(future.getError());
            } else {
                System.out.println(future.getValue().getValue(String.class));
            }
            // 经过上面的同步操作,此时这个future一定是已经完成的future
            Assert.assertTrue(future.isDone());
            System.out.println("  ReturnCode = [" + future.getValue().getReturnCode() + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void priintLine(String methodName) {
        System.out.print("Time = [" + new Date() + "] ");
        System.out.println("--------------------------------" + methodName + "------------------------------");
    }

    /**
     * @param args
     */
    public static <V> void main(String[] args) {
        FutureClient client = new FutureClient();
        client.setUp();
        client.testManyFutureUseWait(10);
        client.testManyFutureUseLintener(10);
        client.testManyFutureGroupUseWaitAll(10);
        client.testManyFutureGroupUseLintenerWaitAll(10);
        client.testExceptionUseWait(1);
        client.testTimeOutMethodUseWait(1);
        client.testSpecialMethod();
        client.testManyFutureGroupUseWaitAll(15);
        client.testManyFutureGroupUseWaitAny(15);
    }

    @Test
    public void test1() {
        FutureClient client = new FutureClient();
        client.setUp();
        client.testManyFutureUseWait(10);
        client.testManyFutureUseLintener(10);
        client.testManyFutureGroupUseWaitAll(10);
        client.testManyFutureGroupUseLintenerWaitAll(10);
        client.testExceptionUseWait(1);
        client.testTimeOutMethodUseWait(1);
        client.testSpecialMethod();
        client.testManyFutureGroupUseWaitAll(15);
        client.testManyFutureGroupUseWaitAny(15);

    }

    @Before
    public void setUp() {
        try {
            if (tcpEndpoint == null) {
                new FutureServer();
                tcpEndpoint = new RpcTcpEndpoint(new InetSocketAddress("127.0.0.1", 8001));
                method1 = RpcProxyFactory.getMethodStub(tcpEndpoint, "FutureService", "method1");
                exceptionMethod = RpcProxyFactory.getMethodStub(tcpEndpoint, "FutureService", "exceptionMethod");
                timeOutMethod = RpcProxyFactory.getMethodStub(tcpEndpoint, "FutureService", "timeOutMethod");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testManyFutureUseWait() {
        testManyFutureUseWait(10);
        testManyFutureUseWait(10);
    }

    @Test
    public void testManyFutureUseLintener() {
        testManyFutureUseLintener(10);
    }

    @Test
    public void testManyFutureGroupUseWaitAll() {
        testManyFutureGroupUseWaitAll(10);
    }

    @Test
    public void testManyFutureGroupUseLintenerWaitAll() {
        testManyFutureGroupUseLintenerWaitAll(10);
    }

    @Test
    public void testExceptionUseWait() {
        testExceptionUseWait(1);
    }

    @Test
    public void testManyFutureGroupUseWaitAny() {
        testManyFutureGroupUseWaitAny(10);
    }

    // @Test
    public void testTimeOutMethodUseWait() {
        testTimeOutMethodUseWait(1);
    }

    public void testManyFutureUseWait(int count) {
        priintLine("testManyFutureUseWait");
        List<RpcFuture> futures = new ArrayList<RpcFuture>();
        for (int i = 0; i < count; i++) {

            RpcFuture future1 = method1.invoke("testManyFutureForWait future" + i);
            futures.add(future1);
        }
        for (RpcFuture future : futures) {
            printRpcFuture(future);
        }
    }

    public void testManyFutureUseLintener(int count) {
        priintLine("testManyFutureUseLintener");
        for (int i = 0; i < count; i++) {
            RpcFuture future1 = method1.invoke("testManyFutureUseLintener future" + i);
            future1.addListener(newListener());
        }
    }

    public void testManyFutureGroupUseWaitAll(int count) {
        priintLine("testManyFutureGroupUseWaitAll");
        List<RpcFuture> futures = new ArrayList<RpcFuture>();
        for (int i = 0; i < count; i++) {
            RpcFuture future1 = method1.invoke("testManyFutureGroupUseWaitAll future" + i);
            futures.add(future1);
        }
        // 1.
        FutureGroup<RpcFuture> group = new FutureGroup<RpcFuture>(futures);
        try {
            System.out.println("1:");
            // 使用group.awaitAll()方式等待group中全部完成
            for (RpcFuture future : group.awaitAll()) {
                // 因为已经awaitAll了，此时必须每一个future已经执行完毕才算是正确的
                Assert.assertTrue(future.isDone());
                printRpcFuture(future);
            }

            System.out.println("2:");
            // 2.使用外部的group.awaitAll(10);方式等待全部完成，再用group.getFutures()取出
            group = new FutureGroup<RpcFuture>(futures);
            group.awaitAll(10);
            for (RpcFuture future : group.getFutures()) {
                // 因为已经awaitAll了，此时必须每一个future已经执行完毕才算是正确的
                Assert.assertTrue(future.isDone());
                printRpcFuture(future);
            }

            System.out.println("3:");
            // 3.普通方式
            RpcFuture future1 = method1.invoke("testManyFutureGroupUseWaitAll future");
            RpcFuture future2 = method1.invoke("testManyFutureGroupUseWaitAll future");
            group = new FutureGroup<RpcFuture>(future1, future2);
            for (RpcFuture future : group.awaitAll()) {
                printRpcFuture(future);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void testManyFutureGroupUseWaitAny(int count) {
        priintLine("testManyFutureGroupUseWaitAny");
        // 不带超时时间的
        List<RpcFuture> futures = new ArrayList<RpcFuture>();
        for (int i = 0; i < count; i++) {
            RpcFuture future1 = method1.invoke("testManyFutureGroupUseWaitAll future" + i);
            futures.add(future1);
        }

        FutureGroup<RpcFuture> group = new FutureGroup<RpcFuture>(futures);
        try {

            RpcFuture resultFuture = null;
            while ((resultFuture = group.awaitAny()) != null) {
                printRpcFuture(resultFuture);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 在超时时间内可返回的
        futures = new ArrayList<RpcFuture>();
        for (int i = 0; i < count; i++) {
            RpcFuture future1 = method1.invoke("testManyFutureGroupUseWaitAll future" + i);
            futures.add(future1);
        }

        group = new FutureGroup<RpcFuture>(futures);
        try {

            RpcFuture resultFuture = null;
            while ((resultFuture = group.awaitAny(1000 * count / 3 + 10000)) != null) {
                printRpcFuture(resultFuture);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 在超时时间内不可返回的，会报告Time out.
        boolean isTimeOut = false;
        futures = new ArrayList<RpcFuture>();
        for (int i = 0; i < count; i++) {
            RpcFuture future1 = method1.invoke("testManyFutureGroupUseWaitAll future" + i);
            futures.add(future1);
        }

        group = new FutureGroup<RpcFuture>(futures);
        try {

            RpcFuture resultFuture = null;
            while ((resultFuture = group.awaitAny(100)) != null) {
                printRpcFuture(resultFuture);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            isTimeOut = true;
        }
        // 只有报告超时了才算正确
        Assert.assertTrue(isTimeOut);
    }

    public void testManyFutureGroupUseLintenerWaitAll(int count) {
        priintLine("testManyFutureGroupUseLintenerWaitAll");
        List<RpcFuture> futures = new ArrayList<RpcFuture>();
        for (int i = 0; i < count; i++) {
            RpcFuture future1 = method1.invoke("testManyFutureForWait future" + i);
            futures.add(future1);
        }
        FutureGroup<RpcFuture> group = new FutureGroup<RpcFuture>(futures);

        group.addListener(new EventHandler<List<RpcFuture>>() {
            @Override
            public void run(Object sender, List<RpcFuture> futures) {
                for (RpcFuture future : futures) {
                    // 因为已经awaitAll模式了，此时必须每一个future已经执行完毕才算是正确的
                    Assert.assertTrue(future.isDone());
                    printRpcFuture(future);
                }
            }
        });
    }

    public void testExceptionUseWait(int count) {
        try {
            priintLine("testExceptionUseWait");
            List<RpcFuture> futures = new ArrayList<RpcFuture>();
            for (int i = 0; i < count; i++) {
                RpcFuture future1 = exceptionMethod.invoke("testExceptionUseWait future" + i);
                futures.add(future1);
            }
            for (RpcFuture future : futures) {
                printRpcFuture(future);
            }

            RpcFuture future1 = exceptionMethod.invoke("testExceptionUseWait future");
            if (future1.getValue().getError() == null) {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testTimeOutMethodUseWait(int count) {
        priintLine("testTimeOutMethodUseWait");
        List<RpcFuture> futures = new ArrayList<RpcFuture>();
        for (int i = 0; i < count; i++) {
            RpcFuture future1 = timeOutMethod.invoke("timeOutMethod future" + i);
            futures.add(future1);
        }
        for (RpcFuture future : futures) {
            printRpcFuture(future);
        }
    }

    @Test
    public void testSpecialMethod() {
        try {
            RpcFuture future1 = method1.invoke("testSpecialMethod future1");
            future1.getValue().getError();
            future1.getValue().getValue(String.class);
            future1.getValue().getReturnCode();
            RpcFuture future2 = method1.invoke("testSpecialMethod future1");
            // 只有出现了异常才正常
            if (future2.getValue().getError() == null) {
                Assert.assertTrue(true);
            }
            RpcFuture future3 = method1.invoke("testSpecialMethod future1");
            future3.getValue().getValue(String.class);
            RpcFuture future4 = method1.invoke("testSpecialMethod future1");
            future4.getValue().getReturnCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
