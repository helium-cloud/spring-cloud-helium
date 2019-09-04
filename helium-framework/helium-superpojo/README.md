# SuperPojo

### Overview

1. 简介

2. 项目描述

3. 引用方式

4. SuperPojo示例

5. SuperPojo 进阶

---

### 1. 简介

POJO（Plain Ordinary Java Objects）简单的Java对象，可以作为VO(value -object)或DTO(Data Transform Object)来使用，[关于POJO的介绍详见](http://baike.baidu.com/link?url=URyUA1EwdMMGnQ3t0X48tUHwb8Eai_NaDmPHtjzCPcG5zz1AfKbgnyA2bHlHOR_l)。

SuperPojo是一个超级POJO，它在实现简单POJO功能的基础上，增加了对多种序列化格式的支持，强化了Data Transform Object职能。它分别支持[XML](http://baike.baidu.com/view/159832.htm)、[JSON](http://baike.baidu.com/view/136475.htm)、[Protocol Buffers](http://baike.baidu.com/view/1708160.htm)格式的序列化与反序列化，并且使用简单，仅需要您继承SuperPoJo类，并为想要序列化的字段加上@Field注解即可。

---

### 2. 引用方式

可以使用jar包或Maven方式对此项目进行引用，若为Maven引用，需添加如下内容到pom.xml中

```
<dependency>
	<groupId>upc</groupId>
	<artifactId>helium-superpojo</artifactId>
	<version>2.0.0-SNAPSHOT</version>
	<type>jar</type>
	<scope>compile</scope>
</dependency>
```

---

### 3. SuperPojo示例

下面这个示例定义了一个UserInfo，继承自SuperPojo，一共有十一个字段需要序列化

```java
@Entity(name = "root")
public class UserInfo extends SuperPojo {

	@Field(id = 11, type = NodeType.ATTR, name = "xmlns:user")
	private String nameSpace;

	@Field(id = 1)
	private int id;

	@Field(id = 2, type = NodeType.ATTR)
	private String name;

	@Field(id = 3, type = NodeType.ATTR)
	private SexEnum sex;

	@Field(id = 4, name = "user:address", isCDATA = true)
	private String address;

	@Field(id = 5, format = "yyyy-MM-dd")
	private Date birthday;

	@Field(id = 6)
	private UserInfo bestFriend;

	@Field(id = 7, name = "friend")
	@FieldExtensions(newParentNode = "friendList")
	private List<UserInfo> friends;

	@Childs(id = 8, child = "friendEntry", parent = "friendMap", useKeyName = true)
	private Map<String, UserInfo> friendMap;

	@Childs(id = 9)
	private Map<String, String> StringMap;

	@Field(id = 10, format = "yyyy-MM-dd HH:mm:ss.SSS")
	private java.sql.Date sqlDate;

    // Getter and Setter ...

	public static enum SexEnum implements EnumInteger {

		MALE(0), FEMALE(1);

		private int value;

		SexEnum(int value) {
			this.value = value;
		}

		@Override
		public int intValue() {
			return value;
		}
	}
}

```

下面开始逐步介绍

首先映入眼帘的是下面内容

```java

@Entity(name = "root")
public class UserInfo extends SuperPojo

```
这里首先提到了`@Entity`注解，该注解是为XML序列化量身定制的，因此如果你不是想序列化为XML，那么此注解可无视甚至不写，该注解包含以下内容。
`name` 默认为空，用于定义XML根节点名称，当名称为空时，默认使用当前类名作为根节点名称，如果当前类实例是另一个类的字段时，那么以上一层类定义的名称为准，当前`@Entity`会被忽略。

除了`@Entity`注解，UserInfo还继承了SuperPojo，因此UserInfo顺势具有了SuperPojo的全部方法，方法如下

```java

public byte[] toPbByteArray()
public void parsePbFrom(InputStream input)
public void parsePbFrom(byte[] buffer)
public JsonObject toJsonObject()
public byte[] toXmlByteArray()
public void parseXmlFrom(InputStream input)
public void parseXmlFrom(String xml)

```

这些方法根据名称可知，方法类型分为两类，一类是序列化方法，如`toPbByteArray()`，一类是反序列化方法，如`parsePbFrom(byte[] buffer)`，因此当一个类继承自SuperPojo后，即可拥有序列化到XML、JSON、Protobuf，或从其反序列化的功能。

当然，也不是仅仅继承就可以，我们还需要指定想要序列化的字段，例如

```java

@Field(id = 11, type = NodeType.ATTR, name = "xmlns:user")
private String nameSpace;

```

至于为何需要`@Field`来注解当前字段，一条原因是某一些对象太大，而我们只需要序列化其中某几个字段，另一个原因是Protobuf协议使用tag来辨识当前字段，而首先我们不能假设经过不同java编译器编译出来的字节码中Field的顺序相同，其次是有tag跳跃的需求出现的，因此综上所述，我们与其使用复杂的逻辑，不如让使用者指定当前序号来的实在。
回答正题，解释一下上面`@Field`所标识的意义，
`id` ： 用于指定当前在段在Protobuf序列化时所表示的索引值，
`type` ： 用于指定当前字段在XML序列化时的类型，分为两种`NODE` or `ATTR`，默认是`NODE`，意为序列化XML时将当前内容写入Attribute中还是新建一个Node节点写入。
`name` ： 用于指定当前字段在XML序列化时的元素名称，当前元素也许是`NODE`，也许是`ATTR`，这个由上面的`type`决定，当然，默认是`NODE`

让我们看一下稍微复杂一些的注解

```java

@Childs(id = 8, child = "friendEntry", parent = "friendMap", useKeyName = true)
private Map<String, UserInfo> friendMap;

```

不要奇怪，除了`@Field`注解，我们还有`@Childs`来完成和`@Field`注解相同的任务，只不过`@Childs`可以描述的更加复杂一些，主要用来描述`List`或`Map`等集合类型
`id` ： 与`@Field`相同，用于指定当前字段在Protobuf序列化时所表示的索引值，主要是下面几条
`child` ： 在`@Childs`中没有`name`属性，此属性被用来替代`name`属性，因此`child`是用来在XML中指定当前节点名称
`parent` ： 此节点在XML中用来创建当前集合的父节点，想想一下，一个集合类型被序列化到XML中，一条一条的罗起来，毫无秩序，因此如果指定了`parent`，那么会创建一个父节点，将当前集合中的数据统一放到这个集合中，当然，这不是必选项。
`useKeyName` ： 这个属性是用来指定当前Map是否以其key作为节点名称，默认为false，如果一旦为true，那么key一定要是int或String等基本类型，否则我们无法想想一个复杂类型UserInfo为Node name。

再看这个，枚举类型想要序列化，需要实现EnumInteger

```java


public static enum SexEnum implements EnumInteger {

	MALE(0), FEMALE(1);

	private int value;

	SexEnum(int value) {
		this.value = value;
	}

	@Override
	public int intValue() {
		return value;
	}
}

```
剩下的事就简单了，序列化方式如下

```java

public byte[] toPbByteArray()
public JsonObject toJsonObject()
public byte[] toXmlByteArray()

```

反序列化方式如下

```java

public void parsePbFrom(InputStream input)
public void parsePbFrom(byte[] buffer)
public void parseXmlFrom(InputStream input)
public void parseXmlFrom(String xml)

```

看到这里一定奇怪，为何没有JSON的反序列化，JSON有些特殊，需要调用如下方法进行反序列化

```java

SuperPojoManager.parseJsonFrom(String json, Class<E> clazz)

```


综上所述，简单示例如下：

```java

UserInfo user = new UserInfo();
user.setId(1);
user.setName("Feinno");
user.setSex(SexEnum.FEMALE);
user.setBirthday(new Date());
user.setNameSpace("http://www.w3school.com.cn/xml/");
user.setAddress("北京市朝阳区北苑路甲13号院北辰泰岳大厦18层");
user.setSqlDate(new java.sql.Date(System.currentTimeMillis()));
UserInfo user2 = new UserInfo();
user2.parseXmlFrom(new String(user.toXmlByteArray()));
List<UserInfo> list = new ArrayList<UserInfo>();
list.add(user2);
list.add(user2);
user.setFriends(list);
user.setBestFriend(user2);
Map<String, UserInfo> map = new HashMap<String, UserInfo>();
user.setFriendMap(map);
map.put("T1", user2);
map.put("T2", user2);
map.put("T3", user2);
Map<String, String> strMap = new HashMap<String, String>();
strMap.put("1", "A");
strMap.put("2", "B");
strMap.put("3", "C");
user.setStringMap(strMap);
// 反序列化
UserInfo result1 = new UserInfo();
result1.parseXmlFrom(new String(user.toXmlByteArray()));
// 使用上一步骤反序列化的结果，再反序列化
UserInfo result2 = new UserInfo();
result2.parseXmlFrom(new String(result1.toXmlByteArray()));
// 比对验证
Assert.assertEquals(result1.toJsonObject(), result2.toJsonObject());

//序列化
byte[] pbBuffer = result1.toPbByteArray();
byte[] xmlBuffer = result1.toXmlByteArray();
JsonObject jsonObject = result1.toJsonObject();

//反序列化
UserInfo result = new UserInfo();
result.parsePbFrom(pbBuffer);
result.parseXmlFrom(new String(xmlBuffer));
result = SuperPojoManager.parseJsonFrom(jsonObject.toString(), UserInfo.class);
```

Protobuf 结果

```

5A 1F 68 74 74 70 3A 2F 2F 77 77 77 2E 77 33 73 63 68 6F 6F 6C 2E 63 6F 6D 2E 63 6E 2F 78 6D 6C 2F 08 01 12 06 46 65 69 6E 6E 6F 18 01 22 3D E5 8C 97 E4 BA AC E5 B8 82 E6 9C 9D E9 98 B3 E5 8C BA E5 8C 97 E8 8B 91 E8 B7 AF E7 94 B2 31 33 E5 8F B7 E9 99 A2 E5 8C 97 E8 BE B0 E6 B3 B0 E5 B2 B3 E5 A4 A7 E5 8E A6 31 38 E5 B1 82 29 00 00 3E 8A D9 F0 30 00 32 7E 5A 1F 68 74 74 70 3A 2F 2F 77 77 77 2E 77 33 73 63 68 6F 6F 6C 2E 63 6F 6D 2E 63 6E 2F 78 6D 6C 2F 08 01 12 06 46 65 69 6E 6E 6F 18 01 22 3D E5 8C 97 E4 BA AC E5 B8 82 E6 9C 9D E9 98 B3 E5 8C BA E5 8C 97 E8 8B 91 E8 B7 AF E7 94 B2 31 33 E5 8F B7 E9 99 A2 E5 8C 97 E8 BE B0 E6 B3 B0 E5 B2 B3 E5 A4 A7 E5 8E A6 31 38 E5 B1 82 29 00 00 3E 8A D9 F0 30 00 51 D0 3C FD 91 7D F1 30 00 3A 7E 5A 1F 68 74 74 70 3A 2F 2F 77 77 77 2E 77 33 73 63 68 6F 6F 6C 2E 63 6F 6D 2E 63 6E 2F 78 6D 6C 2F 08 01 12 06 46 65 69 6E 6E 6F 18 01 22 3D E5 8C 97 E4 BA AC E5 B8 82 E6 9C 9D E9 98 B3 E5 8C BA E5 8C 97 E8 8B 91 E8 B7 AF E7 94 B2 31 33 E5 8F B7 E9 99 A2 E5 8C 97 E8 BE B0 E6 B3 B0 E5 B2 B3 E5 A4 A7 E5 8E A6 31 38 E5 B1 82 29 00 00 3E 8A D9 F0 30 00 51 D0 3C FD 91 7D F1 30 00 3A 7E 5A 1F 68 74 74 70 3A 2F 2F 77 77 77 2E 77 33 73 63 68 6F 6F 6C 2E 63 6F 6D 2E 63 6E 2F 78 6D 6C 2F 08 01 12 06 46 65 69 6E 6E 6F 18 01 22 3D E5 8C 97 E4 BA AC E5 B8 82 E6 9C 9D E9 98 B3 E5 8C BA E5 8C 97 E8 8B 91 E8 B7 AF E7 94 B2 31 33 E5 8F B7 E9 99 A2 E5 8C 97 E8 BE B0 E6 B3 B0 E5 B2 B3 E5 A4 A7 E5 8E A6 31 38 E5 B1 82 29 00 00 3E 8A D9 F0 30 00 51 D0 3C FD 91 7D F1 30 00 42 84 01 0A 02 54 31 12 7E 5A 1F 68 74 74 70 3A 2F 2F 77 77 77 2E 77 33 73 63 68 6F 6F 6C 2E 63 6F 6D 2E 63 6E 2F 78 6D 6C 2F 08 01 12 06 46 65 69 6E 6E 6F 18 01 22 3D E5 8C 97 E4 BA AC E5 B8 82 E6 9C 9D E9 98 B3 E5 8C BA E5 8C 97 E8 8B 91 E8 B7 AF E7 94 B2 31 33 E5 8F B7 E9 99 A2 E5 8C 97 E8 BE B0 E6 B3 B0 E5 B2 B3 E5 A4 A7 E5 8E A6 31 38 E5 B1 82 29 00 00 3E 8A D9 F0 30 00 51 D0 3C FD 91 7D F1 30 00 42 84 01 0A 02 54 33 12 7E 5A 1F 68 74 74 70 3A 2F 2F 77 77 77 2E 77 33 73 63 68 6F 6F 6C 2E 63 6F 6D 2E 63 6E 2F 78 6D 6C 2F 08 01 12 06 46 65 69 6E 6E 6F 18 01 22 3D E5 8C 97 E4 BA AC E5 B8 82 E6 9C 9D E9 98 B3 E5 8C BA E5 8C 97 E8 8B 91 E8 B7 AF E7 94 B2 31 33 E5 8F B7 E9 99 A2 E5 8C 97 E8 BE B0 E6 B3 B0 E5 B2 B3 E5 A4 A7 E5 8E A6 31 38 E5 B1 82 29 00 00 3E 8A D9 F0 30 00 51 D0 3C FD 91 7D F1 30 00 42 84 01 0A 02 54 32 12 7E 5A 1F 68 74 74 70 3A 2F 2F 77 77 77 2E 77 33 73 63 68 6F 6F 6C 2E 63 6F 6D 2E 63 6E 2F 78 6D 6C 2F 08 01 12 06 46 65 69 6E 6E 6F 18 01 22 3D E5 8C 97 E4 BA AC E5 B8 82 E6 9C 9D E9 98 B3 E5 8C BA E5 8C 97 E8 8B 91 E8 B7 AF E7 94 B2 31 33 E5 8F B7 E9 99 A2 E5 8C 97 E8 BE B0 E6 B3 B0 E5 B2 B3 E5 A4 A7 E5 8E A6 31 38 E5 B1 82 29 00 00 3E 8A D9 F0 30 00 51 D0 3C FD 91 7D F1 30 00 4A 06 0A 01 33 12 01 43 4A 06 0A 01 32 12 01 42 4A 06 0A 01 31 12 01 41 51 D0 3C FD 91 7D F1 30 00

```

XML结果

```xml

<?xml version="1.0" encoding="UTF-8" ?>
<root xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
    <id>
        1
    </id>
    <user:address>
        <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
    </user:address>
    <birthday>
        2013-08-27
    </birthday>
    <bestFriend xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno"
    sex="1">
        <id>
            1
        </id>
        <user:address>
            <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
        </user:address>
        <birthday>
            2013-08-27
        </birthday>
        <friendList>
        </friendList>
        <friendMap>
        </friendMap>
        <sqlDate>
            2013-08-27 19:34:56.852
        </sqlDate>
    </bestFriend>
    <friendList>
        <friend xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </friend>
        <friend xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </friend>
    </friendList>
    <friendMap>
        <T1 xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </T1>
        <T3 xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </T3>
        <T2 xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </T2>
    </friendMap>
    <StringMap>
        <key>
            3
        </key>
        <value>
            C
        </value>
    </StringMap>
    <StringMap>
        <key>
            2
        </key>
        <value>
            B
        </value>
    </StringMap>
    <StringMap>
        <key>
            1
        </key>
        <value>
            A
        </value>
    </StringMap>
    <sqlDate>
        2013-08-27 19:34:56.852
    </sqlDate>
</root>

```

JSON结果

```javascript

{
    "nameSpace": "http://www.w3school.com.cn/xml/",
    "id": 1,
    "name": "Feinno",
    "sex": "FEMALE",
    "address": "北京市朝阳区北苑路甲13号院北辰泰岳大厦18层",
    "birthday": "2013-08-27 00:00:00",
    "bestFriend": {
        "nameSpace": "http://www.w3school.com.cn/xml/",
        "id": 1,
        "name": "Feinno",
        "sex": "FEMALE",
        "address": "北京市朝阳区北苑路甲13号院北辰泰岳大厦18层",
        "birthday": "2013-08-27 00:00:00",
        "bestFriend": null,
        "sqlDate": "2013-08-27 19:34:56"
    },
    "friends": [{
        "nameSpace": "http://www.w3school.com.cn/xml/",
        "id": 1,
        "name": "Feinno",
        "sex": "FEMALE",
        "address": "北京市朝阳区北苑路甲13号院北辰泰岳大厦18层",
        "birthday": "2013-08-27 00:00:00",
        "bestFriend": null,
        "sqlDate": "2013-08-27 19:34:56"
    },
    {
        "nameSpace": "http://www.w3school.com.cn/xml/",
        "id": 1,
        "name": "Feinno",
        "sex": "FEMALE",
        "address": "北京市朝阳区北苑路甲13号院北辰泰岳大厦18层",
        "birthday": "2013-08-27 00:00:00",
        "bestFriend": null,
        "sqlDate": "2013-08-27 19:34:56"
    }],
    "friendMap": {
        "T1": {
            "nameSpace": "http://www.w3school.com.cn/xml/",
            "id": 1,
            "name": "Feinno",
            "sex": "FEMALE",
            "address": "北京市朝阳区北苑路甲13号院北辰泰岳大厦18层",
            "birthday": "2013-08-27 00:00:00",
            "bestFriend": null,
            "sqlDate": "2013-08-27 19:34:56"
        },
        "T3": {
            "nameSpace": "http://www.w3school.com.cn/xml/",
            "id": 1,
            "name": "Feinno",
            "sex": "FEMALE",
            "address": "北京市朝阳区北苑路甲13号院北辰泰岳大厦18层",
            "birthday": "2013-08-27 00:00:00",
            "bestFriend": null,
            "sqlDate": "2013-08-27 19:34:56"
        },
        "T2": {
            "nameSpace": "http://www.w3school.com.cn/xml/",
            "id": 1,
            "name": "Feinno",
            "sex": "FEMALE",
            "address": "北京市朝阳区北苑路甲13号院北辰泰岳大厦18层",
            "birthday": "2013-08-27 00:00:00",
            "bestFriend": null,
            "sqlDate": "2013-08-27 19:34:56"
        }
    },
    "StringMap": {
        "3": "C",
        "2": "B",
        "1": "A"
    },
    "sqlDate": "2013-08-27 19:34:56"
}

```

以上是序列化得到得结果，这些结果可以逆向反序列化回对应的类型

---

下面谈一些稍微复杂的情况

### 4. SuperPojo 进阶

#### @Childs 详解

上面仅仅简单描述了`@Childs`注解的作用，下面来详细的聊一下关于`@Childs`注解中不同属性的作用
`@Childs`主要是为刘集合类而出现的，目的是实现集合类复杂对象的可以多样的展示，因此下面分别介绍`@Childs`在List与Map中的使用。
在介绍之前，我们先回顾以下`@Childs`都有什么属性
`id` ： 与`@Field`相同，用于指定当前字段在Protobuf序列化时所表示的索引值，主要是下面几条
`child` ： 在`@Childs`中没有`name`属性，此属性被用来替代`name`属性，因此`child`是用来在XML中指定当前节点名称
`parent` ： 此节点在XML中用来创建当前集合的父节点，想想一下，一个集合类型被序列化到XML中，一条一条的罗起来，毫无秩序，因此如果指定了`parent`，那么会创建一个父节点，将当前集合中的数据统一放到这个集合中，当然，这不是必选项。
`useKeyName` ： 这个属性是用来指定当前Map是否以其key作为节点名称，默认为false，如果一旦为true，那么key一定要是int或String等基本类型，否则我们无法想想一个复杂类型UserInfo为Node name。

##### 在List中使用@Childs

在List中，不需要关注`useKeyName`属性，因为这个属性仅仅是为了Map而存在，`child`属性与`@FIeld`中的`name`相同，因此也很容易理解，复杂的地方在于`parent`属性
`parent` ： 如果指定，该属性，那么会在当前XML中新建一个与parent值相同的节点，并将当前集合类所标识的内容置与此节点中，下面我们来举例说明

无parent属性的SuperPojo定义如下:

```java

//无 parent 属性
@Childs(id = 1, child = "friend")
private List<UserInfo> friends;

```

XML序列化结果为

```xml

<?xml version="1.0" encoding="UTF-8" ?>
<root>
   <friend xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </friend>
        <friend xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </friend>
</root>

```

有parent属性的SuperPojo定义如下:

```java

//有 parent 属性
@Childs(id = 1, child = "friend", parent = "friendList")
private List<UserInfo> friends;

```

XML序列化结果为

```xml

<?xml version="1.0" encoding="UTF-8" ?>
<root>
  <friendList>
     <friend xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </friend>
        <friend xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </friend>
    </friendList>
</root>

```

通过上面的例子，我们可以很清晰得看出其中不同

##### 在Map中使用@Childs

在Map中使用Childs时，其它属性功能同List，唯一不同的是`useKeyName`属性，该属性是用来指定是否以Map中的Key作为Node name，我们继续看下面的例子

未用useKeyName属性的SuperPojo定义如下

```java

@Childs(id = 8, child = "friendEntry", parent = "friendMap")
private Map<String, UserInfo> friendMap;

```

其XML序列化结果为

```xml

<?xml version="1.0" encoding="UTF-8" ?>
<root>
    <friendMap>
        <friendEntry>
            <key>
                T1
            </key>
            <value xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
                <id>
                    1
                </id>
                <user:address>
                    <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
                </user:address>
                <birthday>
                    2013-08-27
                </birthday>
                <friendList>
                </friendList>
                <friendMap>
                </friendMap>
                <sqlDate>
                    2013-08-27 21:10:38.909
                </sqlDate>
            </value>
        </friendEntry>
        <friendEntry>
            <key>
                T3
            </key>
            <value xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
                <id>
                    1
                </id>
                <user:address>
                    <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
                </user:address>
                <birthday>
                    2013-08-27
                </birthday>
                <friendList>
                </friendList>
                <friendMap>
                </friendMap>
                <sqlDate>
                    2013-08-27 21:10:38.909
                </sqlDate>
            </value>
        </friendEntry>
        <friendEntry>
            <key>
                T2
            </key>
            <value xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
                <id>
                    1
                </id>
                <user:address>
                    <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
                </user:address>
                <birthday>
                    2013-08-27
                </birthday>
                <friendList>
                </friendList>
                <friendMap>
                </friendMap>
                <sqlDate>
                    2013-08-27 21:10:38.909
                </sqlDate>
            </value>
        </friendEntry>
    </friendMap>
</root>

```

使用useKeyName属性的SuperPojo定义如下

```java

@Childs(id = 8, child = "friendEntry", parent = "friendMap", useKeyName = true)
private Map<String, UserInfo> friendMap;

```

其XML序列化结果为

```xml

<?xml version="1.0" encoding="UTF-8" ?>
<root>
    <friendMap>
        <T1 xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </T1>
        <T3 xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </T3>
        <T2 xmlns:user="http://www.w3school.com.cn/xml/" name="Feinno" sex="1">
            <id>
                1
            </id>
            <user:address>
                <![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]>
            </user:address>
            <birthday>
                2013-08-27
            </birthday>
            <friendList>
            </friendList>
            <friendMap>
            </friendMap>
            <sqlDate>
                2013-08-27 19:34:56.852
            </sqlDate>
        </T2>
    </friendMap>
</root>

```

通过例子可以明显的看出两个在处理Map元素时的不同，当`useKeyName`属性被设置为`true`时，节点名称既为Map的key，这也是为什么要求当`useKeyName`为`true`时，Map的key必须为int或String等基本类型，而不能为复杂类型。