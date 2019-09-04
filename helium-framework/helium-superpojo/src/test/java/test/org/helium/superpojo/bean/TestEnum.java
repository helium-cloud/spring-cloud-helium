package test.org.helium.superpojo.bean;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.type.EnumInteger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvmingwei on 16-3-28.
 */
public class TestEnum extends SuperPojo {

    @Childs(id = 1, child = "es", parent = "e")
    private List<EnumBean> enumBeans;

    public static enum EnumBean implements EnumInteger {
        E1(0), E2(1);
        private int value;

        EnumBean(int value) {
            this.value = value;
        }

        public int intValue() {
            return value;
        }
    }

    public List<EnumBean> getEnumBeans() {
        return enumBeans;
    }

    public void setEnumBeans(List<EnumBean> enumBeans) {
        this.enumBeans = enumBeans;
    }

    public static void main(String args[]) {
        TestEnum t1 = new TestEnum();
        List<EnumBean> enumBeans = new ArrayList<>();
        enumBeans.add(EnumBean.E1);
        enumBeans.add(EnumBean.E2);
        enumBeans.add(EnumBean.E1);
        enumBeans.add(EnumBean.E2);
        t1.setEnumBeans(enumBeans);
        String xml = t1.toXmlString();
        System.out.println(xml);

        t1 = new TestEnum();
        t1.parseXmlFrom(xml);
        xml = t1.toXmlString();
        System.out.println(xml);


        String json = t1.toJsonObject().toString();
        t1 = new TestEnum();
        xml = t1.toXmlString();
        System.out.println(xml);

    }
}


