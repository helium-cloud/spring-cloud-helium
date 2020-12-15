package org.helium.sample.boot.service;


import org.helium.framework.annotations.ServiceInterface;

@ServiceInterface(id = HeliumServiceTest.BEAN_ID)
public interface HeliumServiceTest {
    String BEAN_ID = "test:HeliumServiceTest";
    void test();
}
