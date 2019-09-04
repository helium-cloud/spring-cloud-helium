package test.org.helium.rpc.channel;

import org.helium.rpc.channel.RpcBinaryIdentity;
import org.helium.rpc.channel.RpcBody;
import org.helium.rpc.channel.RpcResponse;
import org.helium.rpc.channel.RpcReturnCode;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestRpcResponse {

    /**
     * @param args
     */
    public static void main(String[] args) {
        new TestRpcResponse().test();
        new TestRpcResponse().testError();
    }

    @Test
    public void test() {
        RpcResponse response = new RpcResponse();
        RpcBody body = new RpcBody("你好");
        response.setBody(body);
        response.setReturnCode(RpcReturnCode.OK);
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            response.writeToStream(outStream);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outStream.toByteArray());
            RpcBinaryIdentity idt = RpcBinaryIdentity.fromStream(inputStream);
            response = RpcResponse.fromBuffer(inputStream, idt);
            System.out.println(body.getValue().toString() + "=" + response.getBody().decode(String.class).toString());
            Assert.assertEquals(body.getValue().toString(), response.getBody().getValue().toString());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testError() {
        RpcResponse response = new RpcResponse();
        RpcBody body = new RpcBody(new Exception("Error"), true);
        response.setBody(body);
        response.setReturnCode(RpcReturnCode.SERVER_ERROR);
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            response.writeToStream(outStream);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outStream.toByteArray());
            RpcBinaryIdentity idt = RpcBinaryIdentity.fromStream(inputStream);
            response = RpcResponse.fromBuffer(inputStream, idt);
            Assert.assertTrue(response.getBody().decode(String.class) instanceof Exception);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

}
