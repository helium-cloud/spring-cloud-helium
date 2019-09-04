package test.org.helium.superpojo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.FieldExtensions;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.type.EnumInteger;

/**
 * 以下是一个DTO例子<br>
 * 
 * <pre>
 * <?xml encoding="UTF-8"?>
 * <root id=1 name="Feinno" sex="MALE">
 * 		<user:address><![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]></user:address>
 * 		<birthday>1900-01-01</birthday>
 * 		<friendList>
 * 			<friend id=2 name="Feinno2" sex="MALE">
 * 				<user:address><![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]></user:address>
 * 				<birthday>1900-01-01</birthday>
 * 				<friendList></friendList>
 * 			</friend>
 * 			<friend id=3 name="Feinno2" sex="MALE">
 * 				<user:address><![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]></user:address>
 * 				<birthday>1900-01-01</birthday>
 * 				<friendList></friendList>
 * 			</friend>
 * 		</friendList>
 * </root>
 * </pre>
 * 
 * @author lvmingwei
 * 
 */
// 标识XML根部爲root，否則以當前類名爲根名
@Entity(name = "root")
public class UserInfo extends SuperPojo {

	@Field(id = 11, type = NodeType.ATTR, name = "xmlns:user")
	private String nameSpace;

	// id必填，其他都可以为空走默认
	// 例如在XML中<root id=xxx></root>
	@Field(id = 1)
	private int id;

	// 指定当前的字段是一个attr
	// 例如在XML中<root name="Feinno"></root>
	@Field(id = 2, type = NodeType.ATTR)
	private String name;

	// 指定当前的字段是一个attr
	// 例如在XML中<root sex="MALE"></root>
	@Field(id = 3, type = NodeType.ATTR)
	private SexEnum sex;

	// 当前得字段默认是一个node节点，节点内容为<![CDATA[]]>内容
	// 例如在XML中<user:address><![CDATA[北京市朝阳区北苑路甲13号院北辰泰岳大厦18层]]></user:address>
	@Field(id = 4, name = "user:address", isCDATA = true)
	private String address;

	// 指定生日格式是yyyy-MM-dd，无时分秒
	// 例如在XML中<birthday>1900-01-01</birthday>
	@Field(id = 5, format = "yyyy-MM-dd")
	private Date birthday;

	@Field(id = 6)
	private UserInfo bestFriend;

	// 命名当前的节点名称为friendsList,并且其中存在多个字节点friend
	@Field(id = 7, name = "friend")
	@FieldExtensions(newParentNode = "friendList")
	// @Childs(id = 7, child = "friend", parent = "friendList")
	// @Childs(id = 7, child = "friend")
	// @Field(id = 7)
	private List<UserInfo> friends;

	// 命名当前的节点名称为friendsList,并且其中存在多个字节点friend
	// @Field(id = 8, name = "friend")
	// @FieldExtensions(newParentNode = "friendList")
	// @Childs(id = 8, child = "friend", parent = "friendMap", useKeyName =
	// true)
	@Childs(id = 8, child = "friendEntry", parent = "friendMap", useKeyName = true)
	private Map<String, UserInfo> friendMap;

	@Childs(id = 9)
	private Map<String, String> StringMap;

	@Field(id = 10, format = "yyyy-MM-dd HH:mm:ss.SSS")
	private java.sql.Date sqlDate;

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SexEnum getSex() {
		return sex;
	}

	public void setSex(SexEnum sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public UserInfo getBestFriend() {
		return bestFriend;
	}

	public void setBestFriend(UserInfo bestFriend) {
		this.bestFriend = bestFriend;
	}

	public List<UserInfo> getFriends() {
		return friends;
	}

	public void setFriends(List<UserInfo> friends) {
		this.friends = friends;
	}

	public Map<String, UserInfo> getFriendMap() {
		return friendMap;
	}

	public void setFriendMap(Map<String, UserInfo> friendMap) {
		this.friendMap = friendMap;
	}

	public Map<String, String> getStringMap() {
		return StringMap;
	}

	public void setStringMap(Map<String, String> stringMap) {
		StringMap = stringMap;
	}

	public java.sql.Date getSqlDate() {
		return sqlDate;
	}

	public void setSqlDate(java.sql.Date sqlDate) {
		this.sqlDate = sqlDate;
	}

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
