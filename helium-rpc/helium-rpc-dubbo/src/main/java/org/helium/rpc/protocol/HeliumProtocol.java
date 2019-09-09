/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.helium.rpc.protocol;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.protocol.AbstractProxyProtocol;
import org.helium.rpc.channel.RpcServerChannel;
import org.helium.rpc.channel.inproc.RpcInprocServerChannel;
import org.helium.rpc.channel.tcp.RpcTcpServerChannel;
import org.helium.rpc.client.RpcProxyFactory;
import org.helium.rpc.server.RpcServiceBootstrap;
import org.helium.threading.ExecutorFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HeliumProtocol
 */
public class HeliumProtocol extends AbstractProxyProtocol {

    public static final int DEFAULT_PORT = 80;

    private final Map<String, RpcServiceBootstrap> serverMap = new ConcurrentHashMap<String, RpcServiceBootstrap>();


    @Override
    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    protected <T> Runnable doExport(final T impl, Class<T> type, URL url) throws RpcException {

        RpcServerChannel channel = RpcServiceBootstrap.INSTANCE.getServerChannel(url.getProtocol(), String.valueOf(url.getPort()));
        if (channel == null) {
            channel = new RpcTcpServerChannel(url.getPort());    // 如何保证端口不重复是个问题
            RpcServiceBootstrap.INSTANCE.registerChannel(channel);

            RpcServiceBootstrap.INSTANCE.registerTransparentService(url.getServiceInterface(), impl, ExecutorFactory.newFixedExecutor(url.getServiceKey(), 10, 1000), type);
        }

        if (RpcServiceBootstrap.INSTANCE.getServerChannel(url.getProtocol(), "") == null) {
            RpcServiceBootstrap.INSTANCE.registerChannel(RpcInprocServerChannel.INSTANCE);
        }
        try {
            RpcServiceBootstrap.INSTANCE.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Runnable() {
            @Override
            public void run() {
            }
        };
    }

	@Override
	protected <T> T doRefer(Class<T> type, URL url) throws RpcException {
		return null;
	}

    @SuppressWarnings("unchecked")
    protected <T> T getFrameworkProxy(final Class<T> serviceType, final URL url) throws RpcException {
        String heliumUrl = url.getProtocol() + "://" +url.getAddress() + url.getAbsolutePath();
        return RpcProxyFactory.getTransparentProxy(heliumUrl, serviceType);
    }

    @Override
    protected int getErrorCode(Throwable e) {

        return super.getErrorCode(e);
    }



}
