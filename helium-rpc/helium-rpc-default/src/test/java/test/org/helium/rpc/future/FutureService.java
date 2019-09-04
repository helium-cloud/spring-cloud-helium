package test.org.helium.rpc.future;

import java.util.Date;

public class FutureService implements IFutureService {

    @Override
    public String method1(String args) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Time = [" + new Date() + "] FutureService.method1() args = [" + args + "]";
    }

    @Override
    public String[] exceptionMethod(String[] args) {
        throw new RuntimeException("Don't worry.This test exceptionMethod.");
    }

    @Override
    public String[] timeOutMethod(String[] args) {
        try {
            // Thread.sleep(RpcTcpTransactionManager.getTimeoutMs() + 1000);
            Thread.sleep(5000 + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
