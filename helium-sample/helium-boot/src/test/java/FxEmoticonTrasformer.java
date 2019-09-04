//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Coral on 8/30/16.
// */
//public class FxEmoticonTrasformer {
//	public static final FxEmoticonTrasformer INSTANCE = new FxEmoticonTrasformer();
//
//	static {
//		INSTANCE.initialize();
//
//	}
//
//	private void initialize() {
//		addItem("微笑","6EB4F02FA042B4C46D16AE64DFD4A4F4");
//		addItem("大笑","B0F1BC9F790792B79231E6F20523FF37");
//		addItem("眨眼","D6305C62C149B8295BBA54E4695C9E1E");
//		addItem("桃心","66868D75F2F49282729B4AB5EEAC4724");
//		addItem("害羞","2BE2797778549403A43D782635532E03");
//		addItem("惊讶","F80EE425C4817663FE3CAF5DA3316645");
//		addItem("疑问","0734845E6BD0760EA258109B28E11FF0");
//		addItem("天真","346EB6BC9BC9D6435414704BE2971D25");
//		addItem("鬼脸","324519D40558F11FD84E156FCC872AAA");
//		addItem("悲伤","54461531D7F27586684C730A01B4E9FC");
//		addItem("白眼","C4CD701183C2292161041633314CDEC8");
//		addItem("坏笑","8EDD9D587A766476B0CE64BC85850005");
//		addItem("流泪","24961959463FCF6FE986AD3FA6EE64D6");
//		addItem("尴尬","7D69DE5F23B12D759BEEFF429EDA33E4");
//		addItem("鄙视","48316730CE76F87AEFCABA2ED2BE76B5");
//		addItem("给力","62F98BCAE37E392D25C5B0C8E02D25D4");
//		addItem("挖鼻孔","B95A8223230121BA8A9E4D228FF90E82");
//		addItem("晕","A317CC27F689EFBB5B4662746CCC8B7A");
//		addItem("切","417BCFB15D3885351F9C1924F717E0AF");
//		addItem("睡觉","53B98B7F762D75CE6FBE7572BCB3233C");
//		addItem("鼓掌","D0BB1228F9EF675C72A123BCB296FDBA");
//		addItem("嘘","27B7533ADC66B0B1D6092866104C22EF");
//		addItem("痛恨","4BA4059D8D34CBA2CF0072B688B8D38D");
//		addItem("忐忑","825A177252F7E13B95BD53AA34DB35F2");
//		addItem("失望","940386687A7C99949A5C65A05661E91E");
//		addItem("困惑","A1BD670BFF003C01FDF343440641B226");
//		addItem("担心","C0A1976BEDC19BAE336D13F9B67E9FF9");
//		addItem("纠结","8A223EAA8A20CFDF4F6178481B66387E");
//		addItem("思考","E07E869DA3F210F0F2B89179ADE230FE");
//		addItem("窃喜","D4FC82D630B7B0BDEE1D3F59DDBBFDE2");
//		addItem("得意","2267AB5A5E2BE0A80C8C15223F049C69");
//		addItem("呆子","6E351339FD769D923B13600E537F21D4");
//		addItem("闭嘴","BDF36EE1D1741E619F6A580741FE6517");
//		addItem("汗","D672DE00BE80F7A68A6427C90F442017");
//		addItem("吐","A1E6079BD8A6D350C5CC5DAFA9B7A0EA");
//		addItem("惊恐","F5BEA5B82ECD481780BEA0BB1C268362");
//		addItem("亲亲","D38A6F3A3E6A340F8168490F6DACDCD6");
//		addItem("胜利","97548776CA035306B6894F9736F0C0BB");
//		addItem("痛扁","5F5B6A825A30C5E483F5BF4BFC4D81E7");
//		addItem("吃饭","FE3999E7707212C325875395B8C88088");
//		addItem("赞","C3CE05519D56B14B941C6068C8C8226F");
//		addItem("喷血","0E78048FECBC350E457093B75F8DC897");
//		addItem("再见","4611EF384D63A0E996ECB372773DF05A");
//		addItem("生病","9BFE77ADA27CF9EEBA4A324E877F66A5");
//		addItem("拥抱","8161FAF17EB3E65D788B077EAF4CDB3E");
//		addItem("无聊","24F5085B0E6741F22E7853750B2673C8");
//		addItem("灵感","CFD815339BFA87665715B1595760A510");
//		addItem("示爱","B219E79E1DEFF2EF0FAABC86EE2FCA83");
//		addItem("同","76E6E97860EAA102BD5FCBA1DFD3EFE5");
//		addItem("拍砖","D34C16E358276BD2F7617A9EF0BA5182");
//		addItem("囧","340FCC695F6EA2B835C001EED739BBB6");
//		addItem("愤怒","36272005F6B496588308B0125C91785E");
//		addItem("抓狂","42CE1E7C08CF0C06308B0ED5DCFB0556");
//		addItem("谢谢","23BE49AE811162A14683071858F591B0");
//		addItem("祈祷","DCC447A64172378284F3B89A1BE523FB");
//		addItem("抱拳","C7068913FBB75DBA4B23A30190F7E3ED");
//		addItem("OK","34FF00E926282E7C61F8360E1AC78BE0");
//		addItem("强","58EB4C0583734B6789C36240EBF8CD7C");
//		addItem("弱","BFB2D8E19C0ABA7153A7A9835738DF7A");
//		addItem("爱心","B45703DF91A40513CFBEB8E73ACA3FA6");
//		addItem("心碎","5FCA74881FC473CEFC318E6C07D9D5C7");
//		addItem("猪头","3724D880A658C9FFEC090518118CB20F");
//		addItem("菜刀","640DEFA21CD005568B404934490BE307");
//		addItem("Hold","50C21263D186AB6B9E269DF444067344");
//		addItem("便便","B481AD3090D02C8869FE6C25A9BCBF0D");
//		addItem("足球","71B977E10BBBCDC00359AE92ABA0223F");
//		addItem("看电视","0B3D6E7B307039D7FF3DE3B151362189");
//		addItem("骷髅","9F6EA1D9588C6C950E96B4C4F64AB36C");
//		addItem("棒棒糖","6D629FB69C432C85C0706C8E40EEC4C8");
//		addItem("萌猫","E122D3A84FBE9FD078CD9F4A112A8CCE");
//		addItem("玫瑰","52F18E1989F9BA1CC647051E7B8317DF");
//		addItem("凋谢","3A714DD6FE774098DB168107B612A9A3");
//		addItem("咖啡","FB5CB1B9FF581B5D89DE55BEC92B8F25");
//		addItem("蛋糕","44920B7FEDB8A3D956E063DE66CE1E86");
//		addItem("顶","3E5832776E4FD74BB5BBFD17478ACBA6");
//		addItem("礼物","2D82592B1F636ED8D5BA1DE711CB9BA0");
//		addItem("钱","FF7E807F1E701133E470F630C81DF009");
//		addItem("天使","8CE6C6C0EEC8AA6540EE1BDCB8497F19");
//		addItem("恶魔","3169C6289EFB56A5064532CCE78DB820");
//	}
//
//	private List<Item> items = new ArrayList;
//	private Map<String, Item> itemsById = new HashMap();
//
//	private void addItem(String name, String id) {
//		Item i = new Item();
//		i.name = name;
//		i.fxName = "/" + name;
//		i.v6Name = "[/" + name + "]";
//		i.id = id;
//		i.ceObject = String.format("<OBJECT type=\"CE\" id="\"");
//		items.add(i);
//		itemsById.put(id, i);
//	}
//
//	private static class Item {
//		String name;
//		String fxName;
//		String v6Name;
//		String id;
//		String ceObject;
//	}
//
//	public String getNameById(String id) {
//		Item item = itemsById.get(id);
//		if (item == null) {
//			return "未知表情:" + id;
//		} else {
//			return item.name;
//		}
//	}
//
//	public String trasformV6ToFx(String text) {
//		for (Item i: items) {
//			text = text.replace(i.v6Name, i.ceObject);
//		}
//		return text;
//	}
//
//	public String trasformFxToV6(String text) {
//		for (Item i: items) {
//			text = text.replace(i.fxName, i.v6Name);
//		}
//		return text;
//	}
//}
