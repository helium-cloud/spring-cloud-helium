/*
 * FAE, Feinno App Engine
 * 
 * Create by gaolei 2012-2-15
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package test.org.helium.rpc.sample;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import org.helium.rpc.server.RpcService;

/**
 * Rpc测试服务器端虚拟类
 *
 * Created by Coral
 */
@RpcService("RpcSampleService")
public interface RpcSampleService {
    HelloResult hello(HelloArgs args);

    RpcSampleResults add(RpcSampleArgs args);

    public static class RpcSampleArgs extends SuperPojo {
        @Field(id = 1)
        private String message;

        @Field(id = 2)
        private int x;

        @Field(id = 3)
        private int y;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public static class RpcSampleResults extends SuperPojo {
        @Field(id = 1)
        private String message;

        @Field(id = 2)
        private int r;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }
    }

    public static class HelloArgs extends SuperPojo {
        @Field(id = 1)
        private String str;

        @Field(id = 2)
        private int begin;

        @Field(id = 3)
        private int len;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public int getBegin() {
            return begin;
        }

        public void setBegin(int begin) {
            this.begin = begin;
        }

        public int getLen() {
            return len;
        }

        public void setLen(int len) {
            this.len = len;
        }
    }

    public static class HelloResult extends SuperPojo {
        @Field(id = 1)
        private String str;

		public HelloResult() {
		}
		public HelloResult(String str) {
			this.str = str;
		}

		public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }
}
