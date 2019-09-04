package test.org.helium.superpojo.json;

import com.feinno.superpojo.SuperPojoManager;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import test.org.helium.superpojo.bean.Table;
import test.org.helium.superpojo.bean.User;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;


/**
 * Created by Soul on 2015/5/25.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class JsonSerializeTest {

    @Test
    public void simpleTypeTest() {
        Object[] simpleObjects = new Object[]{
                123,
                123.456,
                "some string",
                new String[]{"str1", "str2"},
                new Integer[]{1, 2, 3, 4, 5}
        };
        for (Object simpleObject : simpleObjects) {
            Class<?> aClass = simpleObject.getClass();
            JsonObject jsonObject = SuperPojoManager.toJsonObject(simpleObject);
            Object o2 = SuperPojoManager.parseJsonFrom(jsonObject.toString(), aClass);
            assertThat(String.format("Simple type (%s) encode/decode", aClass), simpleObject, equalTo(o2));
        }
    }

    @Test
    public void customEntityTest() {
        Table table = new Table();
        table.setId(1);
        table.setName("name");
        table.setUser(new User());
        table.getUser().setId(123);
        table.getUser().setName("user");
        JsonObject jsonObject = SuperPojoManager.toJsonObject(table);
        Table o2 = SuperPojoManager.parseJsonFrom(jsonObject.toString(), Table.class);
        assertReflectionEquals(String.format("Custom entity (%s) encode/decode", Table.class), table, o2);
    }
}