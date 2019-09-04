import java.util.HashMap;

/**
 * 1.X text/plain 默认表情 增加库标签转成 Fx text/ce
 *
 *
 * <CustomEmotions version="0"> <Groups><Group Name="默认表情" /></Groups>
 * <ce Name="微笑" ShortCut="/微笑" FileName="6EB4F02FA042B4C46D16AE64DFD4A4F4.fce"
 * GroupName="默认表情" /> <ce Name="大笑" ShortCut="/大笑" FileName=
 * "B0F1BC9F790792B79231E6F20523FF37.fce" GroupName="默认表情" /> <ce Name="眨眼"
 * ShortCut="/眨眼" FileName="D6305C62C149B8295BBA54E4695C9E1E.fce" GroupName=
 * "默认表情" /> <ce Name="桃心" ShortCut="/桃心" FileName=
 * "66868D75F2F49282729B4AB5EEAC4724.fce" GroupName="默认表情" /> <ce Name="害羞"
 * ShortCut="/害羞" FileName="2BE2797778549403A43D782635532E03.fce" GroupName=
 * "默认表情" /> <ce Name="惊讶" ShortCut="/惊讶" FileName=
 * "F80EE425C4817663FE3CAF5DA3316645.fce" GroupName="默认表情" /> <ce Name="疑问"
 * ShortCut="/疑问" FileName="0734845E6BD0760EA258109B28E11FF0.fce" GroupName=
 * "默认表情" /> <ce Name="天真" ShortCut="/天真" FileName=
 * "346EB6BC9BC9D6435414704BE2971D25.fce" GroupName="默认表情" /> <ce Name="鬼脸"
 * ShortCut="/鬼脸" FileName="324519D40558F11FD84E156FCC872AAA.fce" GroupName=
 * "默认表情" /> <ce Name="悲伤" ShortCut="/悲伤" FileName=
 * "54461531D7F27586684C730A01B4E9FC.fce" GroupName="默认表情" /> <ce Name="白眼"
 * ShortCut="/白眼" FileName="C4CD701183C2292161041633314CDEC8.fce" GroupName=
 * "默认表情" /> <ce Name="坏笑" ShortCut="/坏笑" FileName=
 * "8EDD9D587A766476B0CE64BC85850005.fce" GroupName="默认表情" /> <ce Name="流泪"
 * ShortCut="/流泪" FileName="24961959463FCF6FE986AD3FA6EE64D6.fce" GroupName=
 * "默认表情" /> <ce Name="尴尬" ShortCut="/尴尬" FileName=
 * "7D69DE5F23B12D759BEEFF429EDA33E4.fce" GroupName="默认表情" /> <ce Name="鄙视"
 * ShortCut="/鄙视" FileName="48316730CE76F87AEFCABA2ED2BE76B5.fce" GroupName=
 * "默认表情" /> <ce Name="给力" ShortCut="/给力" FileName=
 * "62F98BCAE37E392D25C5B0C8E02D25D4.fce" GroupName="默认表情" /> <ce Name="挖鼻孔"
 * ShortCut="/挖鼻孔" FileName="B95A8223230121BA8A9E4D228FF90E82.fce" GroupName=
 * "默认表情" /> <ce Name="晕" ShortCut="/晕" FileName=
 * "A317CC27F689EFBB5B4662746CCC8B7A.fce" GroupName="默认表情" /> <ce Name="切"
 * ShortCut="/切" FileName="417BCFB15D3885351F9C1924F717E0AF.fce" GroupName=
 * "默认表情" /> <ce Name="睡觉" ShortCut="/睡觉" FileName=
 * "53B98B7F762D75CE6FBE7572BCB3233C.fce" GroupName="默认表情" /> <ce Name="鼓掌"
 * ShortCut="/鼓掌" FileName="D0BB1228F9EF675C72A123BCB296FDBA.fce" GroupName=
 * "默认表情" /> <ce Name="嘘" ShortCut="/嘘" FileName=
 * "27B7533ADC66B0B1D6092866104C22EF.fce" GroupName="默认表情" /> <ce Name="痛恨"
 * ShortCut="/痛恨" FileName="4BA4059D8D34CBA2CF0072B688B8D38D.fce" GroupName=
 * "默认表情" /> <ce Name="忐忑" ShortCut="/忐忑" FileName=
 * "825A177252F7E13B95BD53AA34DB35F2.fce" GroupName="默认表情" /> <ce Name="失望"
 * ShortCut="/失望" FileName="940386687A7C99949A5C65A05661E91E.fce" GroupName=
 * "默认表情" /> <ce Name="困惑" ShortCut="/困惑" FileName=
 * "A1BD670BFF003C01FDF343440641B226.fce" GroupName="默认表情" /> <ce Name="担心"
 * ShortCut="/担心" FileName="C0A1976BEDC19BAE336D13F9B67E9FF9.fce" GroupName=
 * "默认表情" /> <ce Name="纠结" ShortCut="/纠结" FileName=
 * "8A223EAA8A20CFDF4F6178481B66387E.fce" GroupName="默认表情" /> <ce Name="思考"
 * ShortCut="/思考" FileName="E07E869DA3F210F0F2B89179ADE230FE.fce" GroupName=
 * "默认表情" /> <ce Name="窃喜" ShortCut="/窃喜" FileName=
 * "D4FC82D630B7B0BDEE1D3F59DDBBFDE2.fce" GroupName="默认表情" /> <ce Name="得意"
 * ShortCut="/得意" FileName="2267AB5A5E2BE0A80C8C15223F049C69.fce" GroupName=
 * "默认表情" /> <ce Name="呆子" ShortCut="/呆子" FileName=
 * "6E351339FD769D923B13600E537F21D4.fce" GroupName="默认表情" /> <ce Name="闭嘴"
 * ShortCut="/闭嘴" FileName="BDF36EE1D1741E619F6A580741FE6517.fce" GroupName=
 * "默认表情" /> <ce Name="汗" ShortCut="/汗" FileName=
 * "D672DE00BE80F7A68A6427C90F442017.fce" GroupName="默认表情" /> <ce Name="吐"
 * ShortCut="/吐" FileName="A1E6079BD8A6D350C5CC5DAFA9B7A0EA.fce" GroupName=
 * "默认表情" /> <ce Name="惊恐" ShortCut="/惊恐" FileName=
 * "F5BEA5B82ECD481780BEA0BB1C268362.fce" GroupName="默认表情" /> <ce Name="亲亲"
 * ShortCut="/亲亲" FileName="D38A6F3A3E6A340F8168490F6DACDCD6.fce" GroupName=
 * "默认表情" /> <ce Name="胜利" ShortCut="/胜利" FileName=
 * "97548776CA035306B6894F9736F0C0BB.fce" GroupName="默认表情" /> <ce Name="痛扁"
 * ShortCut="/痛扁" FileName="5F5B6A825A30C5E483F5BF4BFC4D81E7.fce" GroupName=
 * "默认表情" /> <ce Name="吃饭" ShortCut="/吃饭" FileName=
 * "FE3999E7707212C325875395B8C88088.fce" GroupName="默认表情" /> <ce Name="赞"
 * ShortCut="/赞" FileName="C3CE05519D56B14B941C6068C8C8226F.fce" GroupName=
 * "默认表情" /> <ce Name="喷血" ShortCut="/喷血" FileName=
 * "0E78048FECBC350E457093B75F8DC897.fce" GroupName="默认表情" /> <ce Name="再见"
 * ShortCut="/再见" FileName="4611EF384D63A0E996ECB372773DF05A.fce" GroupName=
 * "默认表情" /> <ce Name="生病" ShortCut="/生病" FileName=
 * "9BFE77ADA27CF9EEBA4A324E877F66A5.fce" GroupName="默认表情" /> <ce Name="拥抱"
 * ShortCut="/拥抱" FileName="8161FAF17EB3E65D788B077EAF4CDB3E.fce" GroupName=
 * "默认表情" /> <ce Name="无聊" ShortCut="/无聊" FileName=
 * "24F5085B0E6741F22E7853750B2673C8.fce" GroupName="默认表情" /> <ce Name="灵感"
 * ShortCut="/灵感" FileName="CFD815339BFA87665715B1595760A510.fce" GroupName=
 * "默认表情" /> <ce Name="示爱" ShortCut="/示爱" FileName=
 * "B219E79E1DEFF2EF0FAABC86EE2FCA83.fce" GroupName="默认表情" /> <ce Name="赞同"
 * ShortCut="/赞同" FileName="76E6E97860EAA102BD5FCBA1DFD3EFE5.fce" GroupName=
 * "默认表情" /> <ce Name="拍砖" ShortCut="/拍砖" FileName=
 * "D34C16E358276BD2F7617A9EF0BA5182.fce" GroupName="默认表情" /> <ce Name="囧"
 * ShortCut="/囧" FileName="340FCC695F6EA2B835C001EED739BBB6.fce" GroupName=
 * "默认表情" /> <ce Name="愤怒" ShortCut="/愤怒" FileName=
 * "36272005F6B496588308B0125C91785E.fce" GroupName="默认表情" /> <ce Name="抓狂"
 * ShortCut="/抓狂" FileName="42CE1E7C08CF0C06308B0ED5DCFB0556.fce" GroupName=
 * "默认表情" /> <ce Name="谢谢" ShortCut="/谢谢" FileName=
 * "23BE49AE811162A14683071858F591B0.fce" GroupName="默认表情" /> <ce Name="祈祷"
 * ShortCut="/祈祷" FileName="DCC447A64172378284F3B89A1BE523FB.fce" GroupName=
 * "默认表情" /> <ce Name="抱拳" ShortCut="/抱拳" FileName=
 * "C7068913FBB75DBA4B23A30190F7E3ED.fce" GroupName="默认表情" /> <ce Name="OK"
 * ShortCut="/OK" FileName="34FF00E926282E7C61F8360E1AC78BE0.fce" GroupName=
 * "默认表情" /> <ce Name="强" ShortCut="/强" FileName=
 * "58EB4C0583734B6789C36240EBF8CD7C.fce" GroupName="默认表情" /> <ce Name="弱"
 * ShortCut="/弱" FileName="BFB2D8E19C0ABA7153A7A9835738DF7A.fce" GroupName=
 * "默认表情" /> <ce Name="爱心" ShortCut="/爱心" FileName=
 * "B45703DF91A40513CFBEB8E73ACA3FA6.fce" GroupName="默认表情" /> <ce Name="心碎"
 * ShortCut="/心碎" FileName="5FCA74881FC473CEFC318E6C07D9D5C7.fce" GroupName=
 * "默认表情" /> <ce Name="猪头" ShortCut="/猪头" FileName=
 * "3724D880A658C9FFEC090518118CB20F.fce" GroupName="默认表情" /> <ce Name="菜刀"
 * ShortCut="/菜刀" FileName="640DEFA21CD005568B404934490BE307.fce" GroupName=
 * "默认表情" /> <ce Name="Hold" ShortCut="/Hold" FileName=
 * "50C21263D186AB6B9E269DF444067344.fce" GroupName="默认表情" /> <ce Name="便便"
 * ShortCut="/便便" FileName="B481AD3090D02C8869FE6C25A9BCBF0D.fce" GroupName=
 * "默认表情" /> <ce Name="足球" ShortCut="/足球" FileName=
 * "71B977E10BBBCDC00359AE92ABA0223F.fce" GroupName="默认表情" /> <ce Name="看电视"
 * ShortCut="/看电视" FileName="0B3D6E7B307039D7FF3DE3B151362189.fce" GroupName=
 * "默认表情" /> <ce Name="骷髅" ShortCut="/骷髅" FileName=
 * "9F6EA1D9588C6C950E96B4C4F64AB36C.fce" GroupName="默认表情" /> <ce Name="棒棒糖"
 * ShortCut="/棒棒糖" FileName="6D629FB69C432C85C0706C8E40EEC4C8.fce" GroupName=
 * "默认表情" /> <ce Name="萌猫" ShortCut="/萌猫" FileName=
 * "E122D3A84FBE9FD078CD9F4A112A8CCE.fce" GroupName="默认表情" /> <ce Name="玫瑰"
 * ShortCut="/玫瑰" FileName="52F18E1989F9BA1CC647051E7B8317DF.fce" GroupName=
 * "默认表情" /> <ce Name="凋谢" ShortCut="/凋谢" FileName=
 * "3A714DD6FE774098DB168107B612A9A3.fce" GroupName="默认表情" /> <ce Name="咖啡"
 * ShortCut="/咖啡" FileName="FB5CB1B9FF581B5D89DE55BEC92B8F25.fce" GroupName=
 * "默认表情" /> <ce Name="蛋糕" ShortCut="/蛋糕" FileName=
 * "44920B7FEDB8A3D956E063DE66CE1E86.fce" GroupName="默认表情" /> <ce Name="顶"
 * ShortCut="/顶" FileName="3E5832776E4FD74BB5BBFD17478ACBA6.fce" GroupName=
 * "默认表情" /> <ce Name="礼物" ShortCut="/礼物" FileName=
 * "2D82592B1F636ED8D5BA1DE711CB9BA0.fce" GroupName="默认表情" /> <ce Name="钱"
 * ShortCut="/钱" FileName="FF7E807F1E701133E470F630C81DF009.fce" GroupName=
 * "默认表情" /> <ce Name="天使" ShortCut="/天使" FileName=
 * "8CE6C6C0EEC8AA6540EE1BDCB8497F19.fce" GroupName="默认表情" /> <ce Name="恶魔"
 * ShortCut="/恶魔" FileName="3169C6289EFB56A5064532CCE78DB820.fce" GroupName=
 * "默认表情" /> </CustomEmotions>
 *
 * @author wuxiaoze
 *
 */
public class TestConverter {

	private static String DefultEmotionString = "<CustomEmotions version=\"0\"><Groups><Group Name=\"默认表情\" /></Groups><ce Name=\"微笑\" ShortCut=\"/微笑\" FileName=\"6EB4F02FA042B4C46D16AE64DFD4A4F4.fce\" GroupName=\"默认表情\" /><ce Name=\"大笑\" ShortCut=\"/大笑\" FileName=\"B0F1BC9F790792B79231E6F20523FF37.fce\" GroupName=\"默认表情\" /><ce Name=\"眨眼\" ShortCut=\"/眨眼\" FileName=\"D6305C62C149B8295BBA54E4695C9E1E.fce\" GroupName=\"默认表情\" /><ce Name=\"桃心\" ShortCut=\"/桃心\" FileName=\"66868D75F2F49282729B4AB5EEAC4724.fce\" GroupName=\"默认表情\" /><ce Name=\"害羞\" ShortCut=\"/害羞\" FileName=\"2BE2797778549403A43D782635532E03.fce\" GroupName=\"默认表情\" />"
			+ "<ce Name=\"惊讶\" ShortCut=\"/惊讶\" FileName=\"F80EE425C4817663FE3CAF5DA3316645.fce\" GroupName=\"默认表情\" /><ce Name=\"疑问\" ShortCut=\"/疑问\" FileName=\"0734845E6BD0760EA258109B28E11FF0.fce\" GroupName=\"默认表情\" /><ce Name=\"天真\" ShortCut=\"/天真\" FileName=\"346EB6BC9BC9D6435414704BE2971D25.fce\" GroupName=\"默认表情\" /><ce Name=\"鬼脸\" ShortCut=\"/鬼脸\" "
			+ "FileName=\"324519D40558F11FD84E156FCC872AAA.fce\" GroupName=\"默认表情\" /><ce Name=\"悲伤\" ShortCut=\"/悲伤\" FileName=\"54461531D7F27586684C730A01B4E9FC.fce\" GroupName=\"默认表情\" /><ce Name=\"白眼\" ShortCut=\"/白眼\" FileName=\"C4CD701183C2292161041633314CDEC8.fce\" GroupName=\"默认表情\" /><ce Name=\"坏笑\" ShortCut=\"/坏笑\" FileName=\"8EDD9D587A766476B0CE64BC85850005.fce\" GroupName=\"默认表情\" /><ce Name=\"流泪\" ShortCut=\"/流泪\" FileName=\"24961959463FCF6FE986AD3FA6EE64D6.fce\" GroupName=\"默认表情\" /><ce Name=\"尴尬\" ShortCut=\"/尴尬\" FileName=\"7D69DE5F23B12D759BEEFF429EDA33E4.fce\" GroupName=\"默认表情\" />"
			+ "<ce Name=\"鄙视\" ShortCut=\"/鄙视\" FileName=\"48316730CE76F87AEFCABA2ED2BE76B5.fce\" GroupName=\"默认表情\" /><ce Name=\"给力\" ShortCut=\"/给力\" FileName=\"62F98BCAE37E392D25C5B0C8E02D25D4.fce\" GroupName=\"默认表情\" /><ce Name=\"挖鼻孔\" ShortCut=\"/挖鼻孔\" FileName=\"B95A8223230121BA8A9E4D228FF90E82.fce\" GroupName=\"默认表情\" />"
			+ "<ce Name=\"晕\" ShortCut=\"/晕\" FileName=\"A317CC27F689EFBB5B4662746CCC8B7A.fce\" GroupName=\"默认表情\" /><ce Name=\"切\" ShortCut=\"/切\" FileName=\"417BCFB15D3885351F9C1924F717E0AF.fce\" GroupName=\"默认表情\" /><ce Name=\"睡觉\" ShortCut=\"/睡觉\" FileName=\"53B98B7F762D75CE6FBE7572BCB3233C.fce\" GroupName=\"默认表情\" /><ce Name=\"鼓掌\" ShortCut=\"/鼓掌\" FileName=\"D0BB1228F9EF675C72A123BCB296FDBA.fce\" GroupName=\"默认表情\" /><ce Name=\"嘘\" ShortCut=\"/嘘\" FileName=\"27B7533ADC66B0B1D6092866104C22EF.fce\" GroupName=\"默认表情\" /><ce Name=\"痛恨\" ShortCut=\"/痛恨\" FileName=\"4BA4059D8D34CBA2CF0072B688B8D38D.fce\" GroupName=\"默认表情\" />"
			+ "<ce Name=\"忐忑\" ShortCut=\"/忐忑\" FileName=\"825A177252F7E13B95BD53AA34DB35F2.fce\" GroupName=\"默认表情\" /><ce Name=\"失望\" ShortCut=\"/失望\" FileName=\"940386687A7C99949A5C65A05661E91E.fce\" GroupName=\"默认表情\" /><ce Name=\"困惑\" ShortCut=\"/困惑\" FileName=\"A1BD670BFF003C01FDF343440641B226.fce\" GroupName=\"默认表情\" /><ce Name=\"担心\" ShortCut=\"/担心\" "
			+ "FileName=\"C0A1976BEDC19BAE336D13F9B67E9FF9.fce\" GroupName=\"默认表情\" /><ce Name=\"纠结\" ShortCut=\"/纠结\" FileName=\"8A223EAA8A20CFDF4F6178481B66387E.fce\" GroupName=\"默认表情\" /><ce Name=\"思考\" ShortCut=\"/思考\" FileName=\"E07E869DA3F210F0F2B89179ADE230FE.fce\" GroupName=\"默认表情\" /><ce Name=\"窃喜\" ShortCut=\"/窃喜\" FileName=\"D4FC82D630B7B0BDEE1D3F59DDBBFDE2.fce\" GroupName=\"默认表情\" /><ce Name=\"得意\" ShortCut=\"/得意\" FileName=\"2267AB5A5E2BE0A80C8C15223F049C69.fce\" GroupName=\"默认表情\" /><ce Name=\"呆子\" ShortCut=\"/呆子\" FileName=\"6E351339FD769D923B13600E537F21D4.fce\" GroupName=\"默认表情\" /><ce Name=\"闭嘴\" "
			+ "ShortCut=\"/闭嘴\" FileName=\"BDF36EE1D1741E619F6A580741FE6517.fce\" GroupName=\"默认表情\" /><ce Name=\"汗\" ShortCut=\"/汗\" FileName=\"D672DE00BE80F7A68A6427C90F442017.fce\" GroupName=\"默认表情\" />"
			+ "<ce Name=\"吐\" ShortCut=\"/吐\" FileName=\"A1E6079BD8A6D350C5CC5DAFA9B7A0EA.fce\" GroupName=\"默认表情\" /><ce Name=\"惊恐\" ShortCut=\"/惊恐\" FileName=\"F5BEA5B82ECD481780BEA0BB1C268362.fce\" GroupName=\"默认表情\" /><ce Name=\"亲亲\" ShortCut=\"/亲亲\" FileName=\"D38A6F3A3E6A340F8168490F6DACDCD6.fce\" GroupName=\"默认表情\" /><ce Name=\"胜利\" ShortCut=\"/胜利\" FileName=\"97548776CA035306B6894F9736F0C0BB.fce\" GroupName=\"默认表情\" /><ce Name=\"痛扁\" ShortCut=\"/痛扁\" FileName=\"5F5B6A825A30C5E483F5BF4BFC4D81E7.fce\" GroupName=\"默认表情\" /><ce Name=\"吃饭\" ShortCut=\"/吃饭\" FileName=\"FE3999E7707212C325875395B8C88088.fce\" GroupName=\"默认表情\" /><ce Name=\"赞\" ShortCut=\"/赞\" FileName=\"C3CE05519D56B14B941C6068C8C8226F.fce\" GroupName=\"默认表情\" />"
			+ "<ce Name=\"喷血\" ShortCut=\"/喷血\" FileName=\"0E78048FECBC350E457093B75F8DC897.fce\" GroupName=\"默认表情\" /><ce Name=\"再见\" ShortCut=\"/再见\" FileName=\"4611EF384D63A0E996ECB372773DF05A.fce\" GroupName=\"默认表情\" /><ce Name=\"生病\" ShortCut=\"/生病\" "
			+ "FileName=\"9BFE77ADA27CF9EEBA4A324E877F66A5.fce\" GroupName=\"默认表情\" /><ce Name=\"拥抱\" ShortCut=\"/拥抱\" FileName=\"8161FAF17EB3E65D788B077EAF4CDB3E.fce\" GroupName=\"默认表情\" /><ce Name=\"无聊\" ShortCut=\"/无聊\" FileName=\"24F5085B0E6741F22E7853750B2673C8.fce\" GroupName=\"默认表情\" /><ce Name=\"灵感\" ShortCut=\"/灵感\" FileName=\"CFD815339BFA87665715B1595760A510.fce\" GroupName=\"默认表情\" /><ce Name=\"示爱\" ShortCut=\"/示爱\" FileName=\"B219E79E1DEFF2EF0FAABC86EE2FCA83.fce\" GroupName=\"默认表情\" /><ce Name=\"赞同\" ShortCut=\"赞同\" FileName=\"76E6E97860EAA102BD5FCBA1DFD3EFE5.fce\" GroupName=\"默认表情\" /><ce Name=\"拍砖\" ShortCut=\"/拍砖\" FileName=\"D34C16E358276BD2F7617A9EF0BA5182.fce\" GroupName=\"默认表情\" /><ce Name=\"囧\" "
			+ "ShortCut=\"/囧\" FileName=\"340FCC695F6EA2B835C001EED739BBB6.fce\" "
			+ "GroupName=\"默认表情\" /><ce Name=\"愤怒\" ShortCut=\"/愤怒\" FileName=\"36272005F6B496588308B0125C91785E.fce\" GroupName=\"默认表情\" /><ce Name=\"抓狂\" ShortCut=\"/抓狂\" FileName=\"42CE1E7C08CF0C06308B0ED5DCFB0556.fce\" GroupName=\"默认表情\" /><ce Name=\"谢谢\" ShortCut=\"/谢谢\" FileName=\"23BE49AE811162A14683071858F591B0.fce\" GroupName=\"默认表情\" /><ce Name=\"祈祷\" ShortCut=\"/祈祷\" FileName=\"DCC447A64172378284F3B89A1BE523FB.fce\" GroupName=\"默认表情\" /><ce Name=\"抱拳\" ShortCut=\"/抱拳\" FileName=\"C7068913FBB75DBA4B23A30190F7E3ED.fce\" GroupName=\"默认表情\" /><ce Name=\"OK\" ShortCut=\"/OK\" FileName=\"34FF00E926282E7C61F8360E1AC78BE0.fce\" GroupName=\"默认表情\" /><ce Name=\"强\" ShortCut=\"/强\" FileName=\"58EB4C0583734B6789C36240EBF8CD7C.fce\" GroupName=\"默认表情\" /><ce Name=\"弱\" ShortCut=\"/弱\" "
			+ "FileName=\"BFB2D8E19C0ABA7153A7A9835738DF7A.fce\" GroupName=\"默认表情\" />"
			+ "<ce Name=\"爱心\" ShortCut=\"/爱心\" FileName=\"B45703DF91A40513CFBEB8E73ACA3FA6.fce\" GroupName=\"默认表情\" /><ce Name=\"心碎\" ShortCut=\"/心碎\" FileName=\"5FCA74881FC473CEFC318E6C07D9D5C7.fce\" GroupName=\"默认表情\" /><ce Name=\"猪头\" ShortCut=\"/猪头\" "
			+ "FileName=\"3724D880A658C9FFEC090518118CB20F.fce\" GroupName=\"默认表情\" />"
			+ "<ce Name=\"菜刀\" ShortCut=\"/菜刀\" FileName=\"640DEFA21CD005568B404934490BE307.fce\" GroupName=\"默认表情\" /><ce Name=\"Hold\" "
			+ "ShortCut=\"/Hold\" FileName=\"50C21263D186AB6B9E269DF444067344.fce\" GroupName=\"默认表情\" /><ce Name=\"便便\" ShortCut=\"/便便\" FileName=\"B481AD3090D02C8869FE6C25A9BCBF0D.fce\" GroupName=\"默认表情\" /><ce Name=\"足球\" ShortCut=\"/足球\" FileName=\"71B977E10BBBCDC00359AE92ABA0223F.fce\" GroupName=\"默认表情\" /><ce Name=\"看电视\" ShortCut=\"/看电视\" FileName=\"0B3D6E7B307039D7FF3DE3B151362189.fce\" GroupName=\"默认表情\" /><ce Name=\"骷髅\" ShortCut=\"/骷髅\" FileName=\"9F6EA1D9588C6C950E96B4C4F64AB36C.fce\" GroupName=\"默认表情\" /><ce Name=\"棒棒糖\" ShortCut=\"/棒棒糖\" FileName=\"6D629FB69C432C85C0706C8E40EEC4C8.fce\" GroupName=\"默认表情\" /><ce Name=\"萌猫\" ShortCut=\"/萌猫\" FileName=\"E122D3A84FBE9FD078CD9F4A112A8CCE.fce\" GroupName=\"默认表情\" /><ce Name=\"玫瑰\" ShortCut=\"/玫瑰\" FileName=\"52F18E1989F9BA1CC647051E7B8317DF.fce\" GroupName=\"默认表情\" /><ce Name=\"凋谢\" ShortCut=\"/凋谢\" FileName=\"3A714DD6FE774098DB168107B612A9A3.fce\" GroupName=\"默认表情\" /><ce Name=\"咖啡\" ShortCut=\"/咖啡\" FileName=\"FB5CB1B9FF581B5D89DE55BEC92B8F25.fce\" GroupName=\"默认表情\" /><ce Name=\"蛋糕\" ShortCut=\"/蛋糕\" FileName"
			+ "=\"44920B7FEDB8A3D956E063DE66CE1E86.fce\" GroupName=\"默认表情\" /><ce Name=\"顶\" ShortCut=\"/顶\" FileName=\"3E5832776E4FD74BB5BBFD17478ACBA6.fce\" GroupName=\"默认表情\" /><ce Name=\"礼物\" ShortCut=\"/礼物\" FileName=\"2D82592B1F636ED8D5BA1DE711CB9BA0.fce\" GroupName=\"默认表情\" /><ce Name=\"钱\" ShortCut=\"/钱\" FileName=\"FF7E807F1E701133E470F630C81DF009.fce\" GroupName=\"默认表情\" /><ce Name=\"天使\" ShortCut=\"/天使\" FileName=\"8CE6C6C0EEC8AA6540EE1BDCB8497F19.fce\" GroupName=\"默认表情\" /><ce Name=\"恶魔\" ShortCut=\"/恶魔\" FileName=\"3169C6289EFB56A5064532CCE78DB820.fce\" GroupName=\"默认表情\" /></CustomEmotions>";

	private static String emojis = "<CustomEmotions>\n" +
			"\t<Groups>\n" +
			"\t\t<Group Name=\"emoji\" />\n" +
			"\t</Groups>\n" +
			"\t<ce Name=\"默认_1f601\" UnicodeChar=\"0x1f601\" ShortCut=\"\" FileName=\"FCA9D87FE773DA0D5C1C20487DE6AB09.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f602\" UnicodeChar=\"0x1f602\" ShortCut=\"\" FileName=\"DBE586E825C0E3F7247A1C6AA2717CE2.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f604\" UnicodeChar=\"0x1f604\" ShortCut=\"\" FileName=\"116DF9D836F3B044285164BD7CCDDF71.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f605\" UnicodeChar=\"0x1f605\" ShortCut=\"\" FileName=\"0B811896B31D22097C4DA8A6D9EA2E5C.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f607\" UnicodeChar=\"0x1f607\" ShortCut=\"\" FileName=\"BE355F4F195F75F38A2D4BB8CD92E468.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f608\" UnicodeChar=\"0x1f608\" ShortCut=\"\" FileName=\"FB03BB2FF5CB88671EE39786A4EDECA1.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f60a\" UnicodeChar=\"0x1f60a\" ShortCut=\"\" FileName=\"412269BD34718953AA770A3F7653E284.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f60b\" UnicodeChar=\"0x1f60b\" ShortCut=\"\" FileName=\"585A0220BBAE76E39018A819539F331D.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f60c\" UnicodeChar=\"0x1f60c\" ShortCut=\"\" FileName=\"5CF9321060A4A3135A1B271C063B086A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f60d\" UnicodeChar=\"0x1f60d\" ShortCut=\"\" FileName=\"3C0124D0DF9F3C1F1F323086BD381F20.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f60e\" UnicodeChar=\"0x1f60e\" ShortCut=\"\" FileName=\"1CFC7C31414B700DDBFAEA9426458789.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f613\" UnicodeChar=\"0x1f613\" ShortCut=\"\" FileName=\"D0E464DFCE58C5CDAA778C1D41BCD0CF.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f618\" UnicodeChar=\"0x1f618\" ShortCut=\"\" FileName=\"81DBE49B1761F0B0DF9BF809C63CE4DC.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f61a\" UnicodeChar=\"0x1f61a\" ShortCut=\"\" FileName=\"17FCA928D1D137774EC11E4F43E96D65.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f61c\" UnicodeChar=\"0x1f61c\" ShortCut=\"\" FileName=\"202B8E56C5A52485BF4447D8B6A3180A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f61d\" UnicodeChar=\"0x1f61d\" ShortCut=\"\" FileName=\"0A03DAFBD2E6B599808E57A7BE1B0426.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f61e\" UnicodeChar=\"0x1f61e\" ShortCut=\"\" FileName=\"2DC2640F10B07FA559137453EB7E9A18.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f620\" UnicodeChar=\"0x1f620\" ShortCut=\"\" FileName=\"0EB246CA00F67DF1179C774985901744.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f621\" UnicodeChar=\"0x1f621\" ShortCut=\"\" FileName=\"7918E0D22520050FBB3481C6A0913BC9.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f624\" UnicodeChar=\"0x1f624\" ShortCut=\"\" FileName=\"0828FAB671ACA4ED328E48C24A70D18B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f625\" UnicodeChar=\"0x1f625\" ShortCut=\"\" FileName=\"C07CDFD5BEFA57475DB2038974FE8582.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f627\" UnicodeChar=\"0x1f627\" ShortCut=\"\" FileName=\"A687E2F1FAE74FAFD335E4E624178E36.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f628\" UnicodeChar=\"0x1f628\" ShortCut=\"\" FileName=\"A326FBDDAE568F38832771CAF026C963.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f629\" UnicodeChar=\"0x1f629\" ShortCut=\"\" FileName=\"8B8EC06800E3C69E356D6359932ADA9B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f62a\" UnicodeChar=\"0x1f62a\" ShortCut=\"\" FileName=\"A4E0F8A4EEDBA41011C3B234292B2816.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f62d\" UnicodeChar=\"0x1f62d\" ShortCut=\"\" FileName=\"26F4F045C22ECA531472598AC77C9401.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f630\" UnicodeChar=\"0x1f630\" ShortCut=\"\" FileName=\"504DA5F05FB9CB305CA26A06BC922421.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f631\" UnicodeChar=\"0x1f631\" ShortCut=\"\" FileName=\"335439C9073B8E3EEA97A9A50AAEB09E.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f632\" UnicodeChar=\"0x1f632\" ShortCut=\"\" FileName=\"CB911F7E88910DC7E724DDB6D4DAED3C.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f633\" UnicodeChar=\"0x1f633\" ShortCut=\"\" FileName=\"D68EA5BE74555C20E753B191F90AC0C5.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f634\" UnicodeChar=\"0x1f634\" ShortCut=\"\" FileName=\"01B5B1B1542751968D94833BDE2259B4.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f637\" UnicodeChar=\"0x1f637\" ShortCut=\"\" FileName=\"56E00DC0ADCFC276908473B447B8A15A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f302\" UnicodeChar=\"0x1f302\" ShortCut=\"\" FileName=\"8A9D53003C363F784D118FB501233353.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f31f\" UnicodeChar=\"0x1f31f\" ShortCut=\"\" FileName=\"B6B6572FD2949DE79E238EA095579113.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f380\" UnicodeChar=\"0x1f380\" ShortCut=\"\" FileName=\"FFEF2FD74CB891BB0101A75237CB5FAD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f3a9\" UnicodeChar=\"0x1f3a9\" ShortCut=\"\" FileName=\"5AC14768F05D324E2A5C50564D032393.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f3bd\" UnicodeChar=\"0x1f3bd\" ShortCut=\"\" FileName=\"FAB66DDF79BAAB38D9773487C224A507.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f3c3\" UnicodeChar=\"0x1f3c3\" ShortCut=\"\" FileName=\"65D5CDE29F930FB18EBA12C8C16045DF.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f40d\" UnicodeChar=\"0x1f40d\" ShortCut=\"\" FileName=\"F2DF4B3370CDE4B6CF33388B7DF70AD1.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f40e\" UnicodeChar=\"0x1f40e\" ShortCut=\"\" FileName=\"5722B20010E17D88AAE05E801B0CDFB1.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f411\" UnicodeChar=\"0x1f411\" ShortCut=\"\" FileName=\"C6B6E7F3E21A8DC32EACFBD51522DF60.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f412\" UnicodeChar=\"0x1f412\" ShortCut=\"\" FileName=\"2A9C68F18C9B3CA35283906AECB5FEB8.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f414\" UnicodeChar=\"0x1f414\" ShortCut=\"\" FileName=\"5D5D2202BF9C1B2648B9586B3249249B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f417\" UnicodeChar=\"0x1f417\" ShortCut=\"\" FileName=\"5DB14AE6CE4E15AB6F17193D980F5E4F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f418\" UnicodeChar=\"0x1f418\" ShortCut=\"\" FileName=\"80D49DD8ADF706A0AAAA95D777E0C224.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f419\" UnicodeChar=\"0x1f419\" ShortCut=\"\" FileName=\"EB818215BFC7B48F7BC923D32602BB17.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f41a\" UnicodeChar=\"0x1f41a\" ShortCut=\"\" FileName=\"6DE3610AA2A75514F44597E5EB7E73C9.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f41b\" UnicodeChar=\"0x1f41b\" ShortCut=\"\" FileName=\"949E6E9EAD37547CEDBDE235E4970F5E.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f420\" UnicodeChar=\"0x1f420\" ShortCut=\"\" FileName=\"9E921FC79CC22DB5F207C4DC0832FAA6.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f423\" UnicodeChar=\"0x1f423\" ShortCut=\"\" FileName=\"986D65B83A5C5D21B0A7C0ED64AA0C1A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f426\" UnicodeChar=\"0x1f426\" ShortCut=\"\" FileName=\"7F33ADACD60106E601433BB1C4FD30EF.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f427\" UnicodeChar=\"0x1f427\" ShortCut=\"\" FileName=\"C061898CDADAD82ABE0943726B41D6FB.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f428\" UnicodeChar=\"0x1f428\" ShortCut=\"\" FileName=\"E04F160B80362A1AFBFC2F4A5B90837E.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f429\" UnicodeChar=\"0x1f429\" ShortCut=\"\" FileName=\"010CAEFECB96C705F429D9C3BF716117.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f42b\" UnicodeChar=\"0x1f42b\" ShortCut=\"\" FileName=\"D741EB1DF528C12416D00C74C020CFE2.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f42c\" UnicodeChar=\"0x1f42c\" ShortCut=\"\" FileName=\"19E4E891D03A2F126D7C3900626B64B6.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f42d\" UnicodeChar=\"0x1f42d\" ShortCut=\"\" FileName=\"B5D2A4F01B353BC0205721E76A383E69.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f42e\" UnicodeChar=\"0x1f42e\" ShortCut=\"\" FileName=\"9AA1E4BE644454E60D0A1446D830EF1F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f42f\" UnicodeChar=\"0x1f42f\" ShortCut=\"\" FileName=\"D157FFA229E8AA56F207C5ADE2734893.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f430\" UnicodeChar=\"0x1f430\" ShortCut=\"\" FileName=\"EA469C8293B78D55EF3CB1E7A3F9B675.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f431\" UnicodeChar=\"0x1f431\" ShortCut=\"\" FileName=\"8752686396081ECCD5ACCAFA02857FFE.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f433\" UnicodeChar=\"0x1f433\" ShortCut=\"\" FileName=\"C80E753A21C6EF3A13138B28F86148F2.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f434\" UnicodeChar=\"0x1f434\" ShortCut=\"\" FileName=\"7C736C2B8F0D8A5AA725745D5813BCEC.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f435\" UnicodeChar=\"0x1f435\" ShortCut=\"\" FileName=\"6FF713541446AE84F06A0958CD3DB7CB.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f437\" UnicodeChar=\"0x1f437\" ShortCut=\"\" FileName=\"F4BA03C1161C8EF8CE1A4AF74650EDF7.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f438\" UnicodeChar=\"0x1f438\" ShortCut=\"\" FileName=\"D0C02D3BB0F4C0A6C216D26D077E453F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f439\" UnicodeChar=\"0x1f439\" ShortCut=\"\" FileName=\"049A44D3F38677C05B1E7A4F9071FFB5.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f43a\" UnicodeChar=\"0x1f43a\" ShortCut=\"\" FileName=\"945AAC5F11ADD28882CB414979F1D423.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f43b\" UnicodeChar=\"0x1f43b\" ShortCut=\"\" FileName=\"D2A0F8FC8682C4FD319DCEAE359DED9C.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f43e\" UnicodeChar=\"0x1f43e\" ShortCut=\"\" FileName=\"DA0F6F1297B27BA0BDBEE9B6BA8AF1B7.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f440\" UnicodeChar=\"0x1f440\" ShortCut=\"\" FileName=\"95B5AECF4D506EBC03D9023BAF6EA456.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f442\" UnicodeChar=\"0x1f442\" ShortCut=\"\" FileName=\"4CA47EC2E1344E9B96D220BF145BFC65.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f443\" UnicodeChar=\"0x1f443\" ShortCut=\"\" FileName=\"10CA709FCA5A404335AA204D8712595F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f444\" UnicodeChar=\"0x1f444\" ShortCut=\"\" FileName=\"1E0491C78162A2686ACC8A77F4CB15E5.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f445\" UnicodeChar=\"0x1f445\" ShortCut=\"\" FileName=\"B43FB7CBDBB321025250F3950F140E2D.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f446\" UnicodeChar=\"0x1f446\" ShortCut=\"\" FileName=\"5E899BAD0765B64BF041AEDBB23A077F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f447\" UnicodeChar=\"0x1f447\" ShortCut=\"\" FileName=\"A7FAB4A0D1838DB37638ED7CB1FB0528.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f448\" UnicodeChar=\"0x1f448\" ShortCut=\"\" FileName=\"FC5284F105E07A8E4A43A156B900EF64.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f449\" UnicodeChar=\"0x1f449\" ShortCut=\"\" FileName=\"33918ABEADA3D45780C45B3BC1069693.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f44a\" UnicodeChar=\"0x1f44a\" ShortCut=\"\" FileName=\"EC76FA0B973952F9EE55DAF38A0E1340.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f44b\" UnicodeChar=\"0x1f44b\" ShortCut=\"\" FileName=\"BC2714EE198063E0BC732C44EA66B6F4.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f44c\" UnicodeChar=\"0x1f44c\" ShortCut=\"\" FileName=\"4F9B065BA3D6DF563F22841F31ECFCB0.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f44d\" UnicodeChar=\"0x1f44d\" ShortCut=\"\" FileName=\"AEFCC856419779CC28E7DAAC70066506.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f44e\" UnicodeChar=\"0x1f44e\" ShortCut=\"\" FileName=\"FD006EF642A21F74D98BADD267B74E72.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f44f\" UnicodeChar=\"0x1f44f\" ShortCut=\"\" FileName=\"9627982C86758B95B942AFE4467D7AD8.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f450\" UnicodeChar=\"0x1f450\" ShortCut=\"\" FileName=\"BDEDB8184E810F4A0E3BFD4B25BE9112.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f451\" UnicodeChar=\"0x1f451\" ShortCut=\"\" FileName=\"1C866F6D23D949D7E708F997E5F0FBE9.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f452\" UnicodeChar=\"0x1f452\" ShortCut=\"\" FileName=\"04ECCF9B0A254825C6ADA5644FEAD586.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f453\" UnicodeChar=\"0x1f453\" ShortCut=\"\" FileName=\"27E89390F2BB038D56FBC04F692A86D8.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f454\" UnicodeChar=\"0x1f454\" ShortCut=\"\" FileName=\"C5F420EB47FFD6325F96210A71959F36.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f455\" UnicodeChar=\"0x1f455\" ShortCut=\"\" FileName=\"C228109E87728FDDBAFCADC7F08AE107.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f456\" UnicodeChar=\"0x1f456\" ShortCut=\"\" FileName=\"2DA225C0AB377149F60B6D933D6A12CB.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f457\" UnicodeChar=\"0x1f457\" ShortCut=\"\" FileName=\"1BFCAE0963D9C4C3EF5A3A7B8A031587.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f458\" UnicodeChar=\"0x1f458\" ShortCut=\"\" FileName=\"B51DBB56ADA9C052F545DFE93903EC16.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f459\" UnicodeChar=\"0x1f459\" ShortCut=\"\" FileName=\"CE9631F810E71CEE51A78CDBFC8C466B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f45a\" UnicodeChar=\"0x1f45a\" ShortCut=\"\" FileName=\"CC8BA6DE5520ECB5FD4BA9F221C5AF8E.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f45b\" UnicodeChar=\"0x1f45b\" ShortCut=\"\" FileName=\"4EFD32F5B86B01F69693E1A1290C16AC.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f45c\" UnicodeChar=\"0x1f45c\" ShortCut=\"\" FileName=\"FB6CA63D935F14ECD08171150D887C3A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f45d\" UnicodeChar=\"0x1f45d\" ShortCut=\"\" FileName=\"BBA94C7110A2588A36B668F9E273AAC1.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f45e\" UnicodeChar=\"0x1f45e\" ShortCut=\"\" FileName=\"F2DBB3E3F7926213C93C7B980D2AFBB2.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f45f\" UnicodeChar=\"0x1f45f\" ShortCut=\"\" FileName=\"5841146E07CFB575D808C3384D56ACC5.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f460\" UnicodeChar=\"0x1f460\" ShortCut=\"\" FileName=\"2CE5139F343709583EEBDBA154A61BA0.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f461\" UnicodeChar=\"0x1f461\" ShortCut=\"\" FileName=\"A3F41D6BECD3238E387C42A8AEF0CBD4.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f462\" UnicodeChar=\"0x1f462\" ShortCut=\"\" FileName=\"5DA121ABDAAC0D388E01081AED61E1D5.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f463\" UnicodeChar=\"0x1f463\" ShortCut=\"\" FileName=\"2438EE9F7DC0C401712AFD80A7E5D7ED.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f464\" UnicodeChar=\"0x1f464\" ShortCut=\"\" FileName=\"E1A21B2F2193EBB1C060CA17D72E913C.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f465\" UnicodeChar=\"0x1f465\" ShortCut=\"\" FileName=\"0CA211F93868157427ECEDF3C446FA5A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f466\" UnicodeChar=\"0x1f466\" ShortCut=\"\" FileName=\"C5D9AC500EB05E5632CA9E63943836AD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f467\" UnicodeChar=\"0x1f467\" ShortCut=\"\" FileName=\"35619A846E676BD817E3E1C0608771DE.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f468\" UnicodeChar=\"0x1f468\" ShortCut=\"\" FileName=\"18A02317158FFD0C46EA29AA341AF7A8.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f469\" UnicodeChar=\"0x1f469\" ShortCut=\"\" FileName=\"FF56BF99E8D99A6990E2328DBCB096E7.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f46a\" UnicodeChar=\"0x1f46a\" ShortCut=\"\" FileName=\"7969C46D30AD57F3EE4A3CB6C3C39B1B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f46b\" UnicodeChar=\"0x1f46b\" ShortCut=\"\" FileName=\"4A4A3E6DF060EA30902C6ACE300BBFC6.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f46c\" UnicodeChar=\"0x1f46c\" ShortCut=\"\" FileName=\"BDA4BCEC27EEDE4F4A7DB231FA6B0B67.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f46d\" UnicodeChar=\"0x1f46d\" ShortCut=\"\" FileName=\"DEDBDACF51EC0B6565EBD2721DD7E709.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f46e\" UnicodeChar=\"0x1f46e\" ShortCut=\"\" FileName=\"E8865BF8781DCDDE48E3CC61BDB526F9.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f46f\" UnicodeChar=\"0x1f46f\" ShortCut=\"\" FileName=\"7F1AB2BB44338574EE72E0A98048BB65.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f470\" UnicodeChar=\"0x1f470\" ShortCut=\"\" FileName=\"D8F777E121AFD0F32AEB1DF38AF1A3AE.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f471\" UnicodeChar=\"0x1f471\" ShortCut=\"\" FileName=\"09A16AC4026CB44A52BB117D939F756E.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f472\" UnicodeChar=\"0x1f472\" ShortCut=\"\" FileName=\"2CDAB2B156D09AACCB1B946764AC4F37.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f473\" UnicodeChar=\"0x1f473\" ShortCut=\"\" FileName=\"E6DF78120BDC9D72D22DA9CFD609D341.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f474\" UnicodeChar=\"0x1f474\" ShortCut=\"\" FileName=\"876AB43A97F7168CB5C3D7A8D86B0D01.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f475\" UnicodeChar=\"0x1f475\" ShortCut=\"\" FileName=\"7E7C0F210711A1A430E4BF01AE114503.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f476\" UnicodeChar=\"0x1f476\" ShortCut=\"\" FileName=\"B8F47F4294D7C0CA8F1F60840F4187BD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f477\" UnicodeChar=\"0x1f477\" ShortCut=\"\" FileName=\"C4D39ED84FF0098FD61664490731390B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f478\" UnicodeChar=\"0x1f478\" ShortCut=\"\" FileName=\"32800952D4121C6580E3FFAD8F3923B3.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f479\" UnicodeChar=\"0x1f479\" ShortCut=\"\" FileName=\"6A4EA5B2F56A04872F15B797B46F6D84.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f47a\" UnicodeChar=\"0x1f47a\" ShortCut=\"\" FileName=\"4106A0716D9239681B5CD7EC1B34F985.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f47b\" UnicodeChar=\"0x1f47b\" ShortCut=\"\" FileName=\"8A8422EA07C8E8B4DEF3EA81C39B32B3.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f47c\" UnicodeChar=\"0x1f47c\" ShortCut=\"\" FileName=\"50B6FBA7D88690A1A0E6C86A9ACF6596.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f47d\" UnicodeChar=\"0x1f47d\" ShortCut=\"\" FileName=\"57DE1273883223971FFDDB901D6E15B7.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f47e\" UnicodeChar=\"0x1f47e\" ShortCut=\"\" FileName=\"E0CE1912C21C84169AA8260379588B53.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f480\" UnicodeChar=\"0x1f480\" ShortCut=\"\" FileName=\"828899BD4F3611C1543E236CA060846F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f481\" UnicodeChar=\"0x1f481\" ShortCut=\"\" FileName=\"C8A5E03A6776A8E0172F50A2C8728349.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f482\" UnicodeChar=\"0x1f482\" ShortCut=\"\" FileName=\"B2717506F89137D7D90D8BBA327DDDDD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f483\" UnicodeChar=\"0x1f483\" ShortCut=\"\" FileName=\"4BFAFCF1FAEACAB7586AEC64687C6C96.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f484\" UnicodeChar=\"0x1f484\" ShortCut=\"\" FileName=\"6040A67E044DDF0CE9D8BD121A418214.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f485\" UnicodeChar=\"0x1f485\" ShortCut=\"\" FileName=\"519874E62141016C71F9B52723B64234.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f486\" UnicodeChar=\"0x1f486\" ShortCut=\"\" FileName=\"6B2D553B7F730FECB9934260F3F89089.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f487\" UnicodeChar=\"0x1f487\" ShortCut=\"\" FileName=\"6EF502269960958288B8333A53A21D37.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f488\" UnicodeChar=\"0x1f488\" ShortCut=\"\" FileName=\"0074A44DCCC6BC7157406B190BED4060.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f489\" UnicodeChar=\"0x1f489\" ShortCut=\"\" FileName=\"70FBBC2F0E4F971919F52A285D86FE59.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f48a\" UnicodeChar=\"0x1f48a\" ShortCut=\"\" FileName=\"B708953886B739D8407E0880CA09C80D.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f48b\" UnicodeChar=\"0x1f48b\" ShortCut=\"\" FileName=\"F05CF487492D3705355EC99024A00DAD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f48c\" UnicodeChar=\"0x1f48c\" ShortCut=\"\" FileName=\"4C6BC257BC5925B1B2316D8DD4DD0918.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f48d\" UnicodeChar=\"0x1f48d\" ShortCut=\"\" FileName=\"364CFC0410926EFE352B4C84CEF00984.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f48e\" UnicodeChar=\"0x1f48e\" ShortCut=\"\" FileName=\"FA072ABC6B6A6224957D0A63A353F215.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f48f\" UnicodeChar=\"0x1f48f\" ShortCut=\"\" FileName=\"5B0098AF72085248ED153AC9B3251EB4.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f490\" UnicodeChar=\"0x1f490\" ShortCut=\"\" FileName=\"6FDF8D4772EC404AD5533EC316E9530B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f491\" UnicodeChar=\"0x1f491\" ShortCut=\"\" FileName=\"2CEEBEAFFF5FCF53E037BF7A23EE671A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f492\" UnicodeChar=\"0x1f492\" ShortCut=\"\" FileName=\"08927A3830AE3E54CC07801AA93D23A4.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f493\" UnicodeChar=\"0x1f493\" ShortCut=\"\" FileName=\"2A609338428D05690E6F1D96602D28F2.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f494\" UnicodeChar=\"0x1f494\" ShortCut=\"\" FileName=\"20AAC6AC2037E6ADAF6E70740BC756DC.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f495\" UnicodeChar=\"0x1f495\" ShortCut=\"\" FileName=\"951404C7DF8E43E54D9851D7457E3722.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f496\" UnicodeChar=\"0x1f496\" ShortCut=\"\" FileName=\"E760B428D64285B422A8ACD3CCC279BD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f497\" UnicodeChar=\"0x1f497\" ShortCut=\"\" FileName=\"C39F0060294558B1F3F1219FDB784EF7.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f498\" UnicodeChar=\"0x1f498\" ShortCut=\"\" FileName=\"2471679570573C28630E8D9A7B7A80C6.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f499\" UnicodeChar=\"0x1f499\" ShortCut=\"\" FileName=\"46C8EF8A618DCAE68562E1B461F6D801.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f49a\" UnicodeChar=\"0x1f49a\" ShortCut=\"\" FileName=\"EDBE8023BDBEFE6D1DDC5400C2178A41.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f49b\" UnicodeChar=\"0x1f49b\" ShortCut=\"\" FileName=\"12A0CA0E6CADB0EC013139F9FFCC359A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f49c\" UnicodeChar=\"0x1f49c\" ShortCut=\"\" FileName=\"41B6BD1555D49508033AD05A6330C8FF.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f49d\" UnicodeChar=\"0x1f49d\" ShortCut=\"\" FileName=\"0330B8BBA3336A1EB0FFA270C0DD1454.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f49e\" UnicodeChar=\"0x1f49e\" ShortCut=\"\" FileName=\"7C70250168A60B85F148606F6198A125.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f49f\" UnicodeChar=\"0x1f49f\" ShortCut=\"\" FileName=\"EFD96BE903633AC968064EBD02A311ED.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a1\" UnicodeChar=\"0x1f4a1\" ShortCut=\"\" FileName=\"DA959D369BFBB79EAA64B6DDEA3A9014.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a2\" UnicodeChar=\"0x1f4a2\" ShortCut=\"\" FileName=\"5598997D17344E02AEB42AD893353D5F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a3\" UnicodeChar=\"0x1f4a3\" ShortCut=\"\" FileName=\"FF588B11B344E27057EF9124300D372C.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a4\" UnicodeChar=\"0x1f4a4\" ShortCut=\"\" FileName=\"614ECEC252AA92E1BBFBD18CAE050D8F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a5\" UnicodeChar=\"0x1f4a5\" ShortCut=\"\" FileName=\"DE817F46F6D16E5188934E9F7837FD83.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a6\" UnicodeChar=\"0x1f4a6\" ShortCut=\"\" FileName=\"1AE78AC678F926B2F52B0F70F21AED70.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a7\" UnicodeChar=\"0x1f4a7\" ShortCut=\"\" FileName=\"355F2EFDF5602AE74A58847DAA90BA76.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a8\" UnicodeChar=\"0x1f4a8\" ShortCut=\"\" FileName=\"F4715314C2FD2D874A34B8C21D5E5C1D.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a9\" UnicodeChar=\"0x1f4a9\" ShortCut=\"\" FileName=\"5F79875975B61BD9613DBD3513EAB2A4.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4aa\" UnicodeChar=\"0x1f4aa\" ShortCut=\"\" FileName=\"8C7E46E3AE08DE7C17FB0065D17BB108.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4ab\" UnicodeChar=\"0x1f4ab\" ShortCut=\"\" FileName=\"B7D0B4C8341F35408B8CE87489EF5CD2.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4ac\" UnicodeChar=\"0x1f4ac\" ShortCut=\"\" FileName=\"EC82B7FF0138BB91CFD23A2B00A070E8.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4ad\" UnicodeChar=\"0x1f4ad\" ShortCut=\"\" FileName=\"2F503F288729258827B66F2F03F38586.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4b0\" UnicodeChar=\"0x1f4b0\" ShortCut=\"\" FileName=\"918BC6BAE083AAD3656F6F9B12CAC4A1.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4b1\" UnicodeChar=\"0x1f4b1\" ShortCut=\"\" FileName=\"2D249142EA31B09EDE0FC5F6050835F8.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4b9\" UnicodeChar=\"0x1f4b9\" ShortCut=\"\" FileName=\"25EBC4E6AEE36A162D78AB098F3EAB47.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4ba\" UnicodeChar=\"0x1f4ba\" ShortCut=\"\" FileName=\"D0D4E1E751ACE6B194544BC79DD5DFB8.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4bb\" UnicodeChar=\"0x1f4bb\" ShortCut=\"\" FileName=\"70A6E53DED46859B67D516EFB85D2541.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4bc\" UnicodeChar=\"0x1f4bc\" ShortCut=\"\" FileName=\"81FB1504373AAEB350EAF3E65D0F98C4.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4bd\" UnicodeChar=\"0x1f4bd\" ShortCut=\"\" FileName=\"ED6738807EC42FE8CA894DC745E6A086.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4bf\" UnicodeChar=\"0x1f4bf\" ShortCut=\"\" FileName=\"2D5FBF3798004B0C360D1F84497E056D.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4c0\" UnicodeChar=\"0x1f4c0\" ShortCut=\"\" FileName=\"83AE76F591E0CDEBB76B7588D4656C74.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4c3\" UnicodeChar=\"0x1f4c3\" ShortCut=\"\" FileName=\"201EFCFEFFB930F338F1FE7118CBD587.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4c7\" UnicodeChar=\"0x1f4c7\" ShortCut=\"\" FileName=\"434B48FA18C632F73D77EDE06B4C4FFD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4de\" UnicodeChar=\"0x1f4de\" ShortCut=\"\" FileName=\"D2C40B6CA668A040F28DC5E2A7B1C6AA.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4e0\" UnicodeChar=\"0x1f4e0\" ShortCut=\"\" FileName=\"7AF78F32E36397FA0B0D13032CB885B0.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4e1\" UnicodeChar=\"0x1f4e1\" ShortCut=\"\" FileName=\"9AD29315A7F744A0CC79A2D1FF2B6839.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4e2\" UnicodeChar=\"0x1f4e2\" ShortCut=\"\" FileName=\"DB339B2849C0991B7CCA2F26741DEE40.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4e3\" UnicodeChar=\"0x1f4e3\" ShortCut=\"\" FileName=\"81F1FB8935B1C542B0CAAE3F3358CE19.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4e7\" UnicodeChar=\"0x1f4e7\" ShortCut=\"\" FileName=\"8F49953C867856FFE04733874131F223.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4ea\" UnicodeChar=\"0x1f4ea\" ShortCut=\"\" FileName=\"037BCBA211BB33543E98FFB198B7016C.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4ee\" UnicodeChar=\"0x1f4ee\" ShortCut=\"\" FileName=\"68AC310E0F9199C9F26A366C9CA82C2B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4f1\" UnicodeChar=\"0x1f4f1\" ShortCut=\"\" FileName=\"EB0146FA119F0C91F8F0E7C4BA89BC9E.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4f2\" UnicodeChar=\"0x1f4f2\" ShortCut=\"\" FileName=\"B4E32B43209F3CC89CEF3323E5D9ABF0.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4f3\" UnicodeChar=\"0x1f4f3\" ShortCut=\"\" FileName=\"254C34F2C188256537506A105F0E8AF7.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4f4\" UnicodeChar=\"0x1f4f4\" ShortCut=\"\" FileName=\"9FD9FA647952E772BBCB77E20ADC490A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4f6\" UnicodeChar=\"0x1f4f6\" ShortCut=\"\" FileName=\"794120D230C5F653DEEF9DBEA423F4BF.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4f7\" UnicodeChar=\"0x1f4f7\" ShortCut=\"\" FileName=\"6D297D27D4F0AD74B3AEAFE3C5B444B2.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4fa\" UnicodeChar=\"0x1f4fa\" ShortCut=\"\" FileName=\"2725D6CE6AB986298A8236D339FFC075.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4fb\" UnicodeChar=\"0x1f4fb\" ShortCut=\"\" FileName=\"73FB3DE5BBF837D877C87EA009ED4179.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4fc\" UnicodeChar=\"0x1f4fc\" ShortCut=\"\" FileName=\"994980EA80CB17A35482969567FEF27E.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f525\" UnicodeChar=\"0x1f525\" ShortCut=\"\" FileName=\"A6B1E1095C8C8D62CC4ACADE9254F8C1.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f639\" UnicodeChar=\"0x1f639\" ShortCut=\"\" FileName=\"D77CA0F705B177481BEF67878DD2B842.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f63a\" UnicodeChar=\"0x1f63a\" ShortCut=\"\" FileName=\"AA30DD1BFFC7C4E09DF46554F2645AA3.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f63b\" UnicodeChar=\"0x1f63b\" ShortCut=\"\" FileName=\"E26DC00886102A7ACA6A48B4F4A9F561.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f63d\" UnicodeChar=\"0x1f63d\" ShortCut=\"\" FileName=\"7BCBD281D851B9C1C2CE734BBAAC1289.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f63e\" UnicodeChar=\"0x1f63e\" ShortCut=\"\" FileName=\"27EE003C782F9F68175FFFB7DFCCF061.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f63f\" UnicodeChar=\"0x1f63f\" ShortCut=\"\" FileName=\"3B82186397504365840A862510755F5A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f640\" UnicodeChar=\"0x1f640\" ShortCut=\"\" FileName=\"602E02DD47ECCC6DB0CBFDF4F775EAAD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f645\" UnicodeChar=\"0x1f645\" ShortCut=\"\" FileName=\"E6208459696073619671BF505469A4B3.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f646\" UnicodeChar=\"0x1f646\" ShortCut=\"\" FileName=\"F2CBEE0873674EB99116A9782D1D6850.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f647\" UnicodeChar=\"0x1f647\" ShortCut=\"\" FileName=\"6F6EA86FAAD092A115464CEE5BED5B21.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f648\" UnicodeChar=\"0x1f648\" ShortCut=\"\" FileName=\"F0DF9FB37989096E45D91055ACC43623.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f649\" UnicodeChar=\"0x1f649\" ShortCut=\"\" FileName=\"44D8B44D6CA8E91FDD212961D47C3B6B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f64a\" UnicodeChar=\"0x1f64a\" ShortCut=\"\" FileName=\"54ED7F47B2FDDFF56610BC70661EE594.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f64b\" UnicodeChar=\"0x1f64b\" ShortCut=\"\" FileName=\"828B6242670AF667F2FB2ED836325E6F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f64c\" UnicodeChar=\"0x1f64c\" ShortCut=\"\" FileName=\"BECD4D6474DCDB966F0433A79715E0D2.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f64e\" UnicodeChar=\"0x1f64e\" ShortCut=\"\" FileName=\"778D4A6075008694C3A263A296C9D89B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f64f\" UnicodeChar=\"0x1f64f\" ShortCut=\"\" FileName=\"8485A083E990551036F0460EEB19A44F.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f680\" UnicodeChar=\"0x1f680\" ShortCut=\"\" FileName=\"A28AA33D31BB4333023010A965D16CCE.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f683\" UnicodeChar=\"0x1f683\" ShortCut=\"\" FileName=\"42EB4447591AC53F206076AF37E55BDA.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f684\" UnicodeChar=\"0x1f684\" ShortCut=\"\" FileName=\"BADCC902F1DC04EA65844DF57403976B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f685\" UnicodeChar=\"0x1f685\" ShortCut=\"\" FileName=\"DE8054989EBC400A46B6C9EE52320C7A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f687\" UnicodeChar=\"0x1f687\" ShortCut=\"\" FileName=\"1DB8FB0C44FA98369B66490EE6A1F4CB.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f689\" UnicodeChar=\"0x1f689\" ShortCut=\"\" FileName=\"FE2BE79D44D4D34B3E2C6F1909D32C46.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f68c\" UnicodeChar=\"0x1f68c\" ShortCut=\"\" FileName=\"D41618B95BCC27EAD31806EB051BE766.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f68f\" UnicodeChar=\"0x1f68f\" ShortCut=\"\" FileName=\"0648167FEB076EBB85F69CF338368BD5.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f691\" UnicodeChar=\"0x1f691\" ShortCut=\"\" FileName=\"670B24016C8EA93E024F9C826C9E8274.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f692\" UnicodeChar=\"0x1f692\" ShortCut=\"\" FileName=\"60E183124724D4148B2EFD4606A35D87.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f693\" UnicodeChar=\"0x1f693\" ShortCut=\"\" FileName=\"03110E357D276B43270934BD279B9ED5.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f695\" UnicodeChar=\"0x1f695\" ShortCut=\"\" FileName=\"CB1D68C6B68E5823AE86DF23BD905498.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f697\" UnicodeChar=\"0x1f697\" ShortCut=\"\" FileName=\"1BF95C5E061C8822CD339B70EBB73DAA.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f699\" UnicodeChar=\"0x1f699\" ShortCut=\"\" FileName=\"2C7C71328434AB887EF7BB3055F65B49.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f69a\" UnicodeChar=\"0x1f69a\" ShortCut=\"\" FileName=\"D0CFC39D58837FF484D91ADB8C78E308.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6a2\" UnicodeChar=\"0x1f6a2\" ShortCut=\"\" FileName=\"367D6555031A55F4C949FE56759347AD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6a4\" UnicodeChar=\"0x1f6a4\" ShortCut=\"\" FileName=\"8FDE8890D39CABCDA24E61615944840C.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6a5\" UnicodeChar=\"0x1f6a5\" ShortCut=\"\" FileName=\"5414AC610621225F32B85F0373A31A7E.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6a7\" UnicodeChar=\"0x1f6a7\" ShortCut=\"\" FileName=\"76F96EBA4CBC48882A3C3E104046A83B.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6ac\" UnicodeChar=\"0x1f6ac\" ShortCut=\"\" FileName=\"870B5432612DD77C2F297046B266EBFD.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6ad\" UnicodeChar=\"0x1f6ad\" ShortCut=\"\" FileName=\"A9732E2EBF9BBB8BE123BB8FE92D27C0.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6b2\" UnicodeChar=\"0x1f6b2\" ShortCut=\"\" FileName=\"2347DF662A4CE657C14B23DE7F74C086.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6b6\" UnicodeChar=\"0x1f6b6\" ShortCut=\"\" FileName=\"969F3DCBBE31330E8313136321830448.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6b9\" UnicodeChar=\"0x1f6b9\" ShortCut=\"\" FileName=\"F3DB0B1E6D38E167853A27C9194825EC.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6ba\" UnicodeChar=\"0x1f6ba\" ShortCut=\"\" FileName=\"19584E64DCA4D854FE49748DD58253AE.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6bb\" UnicodeChar=\"0x1f6bb\" ShortCut=\"\" FileName=\"52C801061B120F1B3FD712234A9788C7.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6bc\" UnicodeChar=\"0x1f6bc\" ShortCut=\"\" FileName=\"34DABD59907605EECC09761E94B2B1CA.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6bd\" UnicodeChar=\"0x1f6bd\" ShortCut=\"\" FileName=\"184151C6130549EC120CBBB008C37419.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6be\" UnicodeChar=\"0x1f6be\" ShortCut=\"\" FileName=\"92BF8665B65475581A659575727140F5.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f6c0\" UnicodeChar=\"0x1f6c0\" ShortCut=\"\" FileName=\"DD246904983D5BD7A2853261A9B2CE2A.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_261d\" UnicodeChar=\"0x261d\" ShortCut=\"\" FileName=\"DF36CEE43D0DE0141A71939503E533C7.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_270a\" UnicodeChar=\"0x270a\" ShortCut=\"\" FileName=\"8885D3B9550D8165E68569A0AEA27560.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_270b\" UnicodeChar=\"0x270b\" ShortCut=\"\" FileName=\"9764B5186A6FD242295A84DF932C0E29.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_270c\" UnicodeChar=\"0x270c\" ShortCut=\"\" FileName=\"816E8FCA3545C9BB54A3DA20AEF9C263.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_2728\" UnicodeChar=\"0x2728\" ShortCut=\"\" FileName=\"42FD152F2AD67D6D8C47BFF628C42B97.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_2764\" UnicodeChar=\"0x2764\" ShortCut=\"\" FileName=\"2F58830171774A58B52A1296EB8B44F6.png\" isshow=\"1\" GroupName=\"emoji\" />\n" +
			"\t<ce Name=\"默认_1f4a0\" UnicodeChar=\"0x1f4a0\" ShortCut=\"\" FileName=\"2435A43A2056C27184525006C5047CAD.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f4ae\" UnicodeChar=\"0x1f4ae\" ShortCut=\"\" FileName=\"85C0F7D0E4560BB6030A550B45E38F18.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f4af\" UnicodeChar=\"0x1f4af\" ShortCut=\"\" FileName=\"E5160F2752BA1CD3A2E890294C634757.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f4be\" UnicodeChar=\"0x1f4be\" ShortCut=\"\" FileName=\"BCFFFF10830E5809DD726DF251ED6DC9.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5a5\" UnicodeChar=\"0x1f5a5\" ShortCut=\"\" FileName=\"0B7F141D112453640400DC44BCE6DCAC.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5a8\" UnicodeChar=\"0x1f5a8\" ShortCut=\"\" FileName=\"C48BA0EDBB11FAE4F37A7E75998CF1DB.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5b1\" UnicodeChar=\"0x1f5b1\" ShortCut=\"\" FileName=\"8D75AE75296B04D2652CA778673FA282.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5b2\" UnicodeChar=\"0x1f5b2\" ShortCut=\"\" FileName=\"FB452F98E86844862F789EEFA4106239.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5bc\" UnicodeChar=\"0x1f5bc\" ShortCut=\"\" FileName=\"BC2DA4AC4A89BD1BDBB18A2CC16A8712.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5c2\" UnicodeChar=\"0x1f5c2\" ShortCut=\"\" FileName=\"3D1E589AB317E06B28773C663B178288.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5c3\" UnicodeChar=\"0x1f5c3\" ShortCut=\"\" FileName=\"B91F4FA197CCA59367086A7B9A1624ED.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5c4\" UnicodeChar=\"0x1f5c4\" ShortCut=\"\" FileName=\"844A5FD94F9B09E674D64BAEBDC88B64.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5d1\" UnicodeChar=\"0x1f5d1\" ShortCut=\"\" FileName=\"D5C1FD523270E0DFB25D8030D1FF9C05.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5d2\" UnicodeChar=\"0x1f5d2\" ShortCut=\"\" FileName=\"0AAC35811E8D30864F59ACE3DD7CBC08.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5d3\" UnicodeChar=\"0x1f5d3\" ShortCut=\"\" FileName=\"3F804B1908EE382B26C0E9B71E0178CC.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5fb\" UnicodeChar=\"0x1f5fb\" ShortCut=\"\" FileName=\"A0481785EA0ED8F97795376487C7D90E.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5fc\" UnicodeChar=\"0x1f5fc\" ShortCut=\"\" FileName=\"597F877776BDA199B7BF0373192CFABC.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f5fd\" UnicodeChar=\"0x1f5fd\" ShortCut=\"\" FileName=\"05679E5041E587479534D64BE318B94F.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f6b4\" UnicodeChar=\"0x1f6b4\" ShortCut=\"\" FileName=\"8D6B6CDEE5F1728F9E70723F53CA5A64.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f6b5\" UnicodeChar=\"0x1f6b5\" ShortCut=\"\" FileName=\"57841C2E267C424EBF71CA6351184781.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f30a\" UnicodeChar=\"0x1f30a\" ShortCut=\"\" FileName=\"042E390F5975378A9989D853104EA130.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f30b\" UnicodeChar=\"0x1f30b\" ShortCut=\"\" FileName=\"F521D36D000171CEAB4F599D93CC749E.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f30c\" UnicodeChar=\"0x1f30c\" ShortCut=\"\" FileName=\"F2204A9C35C6DABBF0611A266A322DAB.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f30d\" UnicodeChar=\"0x1f30d\" ShortCut=\"\" FileName=\"4B34E181BC649E7C468C22A3FEFF09A6.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f30e\" UnicodeChar=\"0x1f30e\" ShortCut=\"\" FileName=\"A68B8C4A83A74662C8894B4A5315D671.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f30f\" UnicodeChar=\"0x1f30f\" ShortCut=\"\" FileName=\"D577D15ED3977BF2321E227F0A43BD10.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f31a\" UnicodeChar=\"0x1f31a\" ShortCut=\"\" FileName=\"53CD8143962860F9F2C1305844605C06.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f31b\" UnicodeChar=\"0x1f31b\" ShortCut=\"\" FileName=\"C3DFD4452086BE5A8AD7EFDED3228670.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f31c\" UnicodeChar=\"0x1f31c\" ShortCut=\"\" FileName=\"527FFBA82A4A7626DB8EA5545A6CCA31.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f31d\" UnicodeChar=\"0x1f31d\" ShortCut=\"\" FileName=\"5949B437F0D8AF0F283876EB19200E80.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f31e\" UnicodeChar=\"0x1f31e\" ShortCut=\"\" FileName=\"DD9D84018FDDDBFFC91CD2DAF4B38437.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f300\" UnicodeChar=\"0x1f300\" ShortCut=\"\" FileName=\"F614E878875E418505F98F816BFB7E65.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f301\" UnicodeChar=\"0x1f301\" ShortCut=\"\" FileName=\"BBF509FCE9026146407DDB8BFC8787BB.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f303\" UnicodeChar=\"0x1f303\" ShortCut=\"\" FileName=\"959A05A1FD940A66F53AF86F4DBF7002.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f304\" UnicodeChar=\"0x1f304\" ShortCut=\"\" FileName=\"1F019923AA95ADBFB76C6C6A6C3FF856.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f305\" UnicodeChar=\"0x1f305\" ShortCut=\"\" FileName=\"8164F9CCD9228C8C2E7374066CDAF6A8.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f306\" UnicodeChar=\"0x1f306\" ShortCut=\"\" FileName=\"0FF827601E17C00242BB21E61F7D3369.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f307\" UnicodeChar=\"0x1f307\" ShortCut=\"\" FileName=\"DCD8B898F79E19D2FB3073E92345709F.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f308\" UnicodeChar=\"0x1f308\" ShortCut=\"\" FileName=\"DE6834641F4FB15E385D99F98B3EEF06.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f309\" UnicodeChar=\"0x1f309\" ShortCut=\"\" FileName=\"0DB2770F170758BCE51D51182AD2CD7F.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f310\" UnicodeChar=\"0x1f310\" ShortCut=\"\" FileName=\"53BB0EAFD675C4CC3E387FC3D4E3158D.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f311\" UnicodeChar=\"0x1f311\" ShortCut=\"\" FileName=\"377073F3EF799B1C432E1404E6CA757F.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f312\" UnicodeChar=\"0x1f312\" ShortCut=\"\" FileName=\"4F5BDF6F11136FC212718866A76FC136.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f313\" UnicodeChar=\"0x1f313\" ShortCut=\"\" FileName=\"17E69D8FE4C92731D23530BC697DAB4A.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f314\" UnicodeChar=\"0x1f314\" ShortCut=\"\" FileName=\"7F9CCC67FEA4481938B5CAA97C6476EF.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f315\" UnicodeChar=\"0x1f315\" ShortCut=\"\" FileName=\"BF3A375B42FC49FD7E742DDABAA7E428.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f316\" UnicodeChar=\"0x1f316\" ShortCut=\"\" FileName=\"38D489C2676B374831E627C9853BB2D4.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f317\" UnicodeChar=\"0x1f317\" ShortCut=\"\" FileName=\"8EE0BFDF1BDC57342EC913527B0E6804.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f318\" UnicodeChar=\"0x1f318\" ShortCut=\"\" FileName=\"A9EC547A581E8630FBD3638B36BF29C1.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f319\" UnicodeChar=\"0x1f319\" ShortCut=\"\" FileName=\"15999CADE57832DA6687C381234DC28D.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f320\" UnicodeChar=\"0x1f320\" ShortCut=\"\" FileName=\"1D90BB90486F651B5D4331DC10B22BBD.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f500\" UnicodeChar=\"0x1f500\" ShortCut=\"\" FileName=\"9592275EEF4692183AC54F2A677427AC.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f501\" UnicodeChar=\"0x1f501\" ShortCut=\"\" FileName=\"B3DC1B3A98F096D2051E7CBC984C17A1.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f502\" UnicodeChar=\"0x1f502\" ShortCut=\"\" FileName=\"E136067D7E7D4E51FE52C31973255F6F.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f503\" UnicodeChar=\"0x1f503\" ShortCut=\"\" FileName=\"5CCCFF616DD6175CA1CB011D5F97D7C4.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f504\" UnicodeChar=\"0x1f504\" ShortCut=\"\" FileName=\"F31352E8DCCA4E285E404725F30B4C82.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f505\" UnicodeChar=\"0x1f505\" ShortCut=\"\" FileName=\"D3E39FE463BA3EC1C8BD40A11FFC4615.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f506\" UnicodeChar=\"0x1f506\" ShortCut=\"\" FileName=\"B6DB06296483AF0922547E1E8A1A46F5.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f507\" UnicodeChar=\"0x1f507\" ShortCut=\"\" FileName=\"1418F7B6197385224E0A5471ACB6CFBF.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f508\" UnicodeChar=\"0x1f508\" ShortCut=\"\" FileName=\"2106DFE6C67AA468FD2C875CE5A6AD56.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f509\" UnicodeChar=\"0x1f509\" ShortCut=\"\" FileName=\"A9D0C2EDA919DF85EF4658FDE69FB9E7.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f510\" UnicodeChar=\"0x1f510\" ShortCut=\"\" FileName=\"3D1E5831D055EA51AABD730C11145FDF.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f511\" UnicodeChar=\"0x1f511\" ShortCut=\"\" FileName=\"E5E8C28F7133EDB2357F5FA38554F138.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f512\" UnicodeChar=\"0x1f512\" ShortCut=\"\" FileName=\"80C9843249E3756478825900DFA2AC6D.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f513\" UnicodeChar=\"0x1f513\" ShortCut=\"\" FileName=\"1DB45731391CDCD238FE2E6431733732.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f514\" UnicodeChar=\"0x1f514\" ShortCut=\"\" FileName=\"31E2C5677058043DC148A3E69595DA57.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f515\" UnicodeChar=\"0x1f515\" ShortCut=\"\" FileName=\"74B083BA1A3C31B04FCB9AAB3E4CA91D.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f526\" UnicodeChar=\"0x1f526\" ShortCut=\"\" FileName=\"A38F66BBAD99261C8D516989C3B3A70B.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f527\" UnicodeChar=\"0x1f527\" ShortCut=\"\" FileName=\"36740CD6E9A8E30C33CB9CC2C179B43F.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f528\" UnicodeChar=\"0x1f528\" ShortCut=\"\" FileName=\"1AE07CBDA92F22CFFC56C30B327C62B7.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f570\" UnicodeChar=\"0x1f570\" ShortCut=\"\" FileName=\"357628004E1FB5763329B8493F543007.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f574\" UnicodeChar=\"0x1f574\" ShortCut=\"\" FileName=\"867BC9A6B69AA3C5FD1A15EB25D3A480.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f575\" UnicodeChar=\"0x1f575\" ShortCut=\"\" FileName=\"2B3200FADF6F49793163B55D8D685EE4.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f576\" UnicodeChar=\"0x1f576\" ShortCut=\"\" FileName=\"03D5F4480E07830C9C0B128FE856BF87.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f577\" UnicodeChar=\"0x1f577\" ShortCut=\"\" FileName=\"70A7157CCBA1EABD565C562173EFCB44.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f590\" UnicodeChar=\"0x1f590\" ShortCut=\"\" FileName=\"37BB58AF1B7150CA10AAF663BA10FD29.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f595\" UnicodeChar=\"0x1f595\" ShortCut=\"\" FileName=\"F240D6F752DA885172C32D2E2A050D9D.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f596\" UnicodeChar=\"0x1f596\" ShortCut=\"\" FileName=\"33445C4D120D5C62ED7241527D11403A.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"    <ce Name=\"默认_1f644\" UnicodeChar=\"0x1f644\" ShortCut=\"\" FileName=\"E3B385B993053C2979A2B514F7DC0E4A.png\" isshow=\"0\" GroupName=\"emoji\" />\n" +
			"</CustomEmotions>";



	private static HashMap<String, String> defultEmotionMap = new HashMap<>();;

	static {
		String[] strings = TestConverter.DefultEmotionString.split("<ce ");
		for (String string : strings) {
			if (string.startsWith("Name=")) {
				String name = string.substring(string.indexOf("ShortCut=") + 10, string.indexOf("FileName=") - 2);
				String id = string.substring(string.indexOf("FileName=") + 10, string.indexOf("GroupName=") - 2);

				System.out.println("addItem(\"" + name.substring(1) + "\",\"" + id.substring(0, 32) + "\");");
				defultEmotionMap.put(name, id);
			}
		}
	}

	public static void main(String[] args) {
	}

//	/**
//	 * 文本转CE 方法 支持传入和返回 为 content 对象
//	 * <OBJECT TYPE=\"CE\" NAME=\"大笑\" ID=\"B0F1BC9F790792B79231E6F20523FF37\">/大笑</OBJECT>
//	 * @param text
//	 * @return TextCe
//	 */
//	public static Vemoticon text2Vemoticon(Text text) {
//
//		String textString = text.getText();
//		textString = textString.replaceAll("<[^>]*>", "");
//		if (textString.contains("[/") && textString.contains("]")) {
//
//			textString = textString.replace("[/", "/");
//			textString = textString.replace("]", "");
//		}
//		String id = defultEmotionMap.get(textString);
//		// String oo="<font faceex=\"\"><OBJECT TYPE=\"CE\" NAME=\"大笑\"
//		// ID=\"B0F1BC9F790792B79231E6F20523FF37\">/大笑</OBJECT>ss</OBJECT></font>";
//		Vemoticon vemoticon = new Vemoticon();
//		vemoticon.setType("CE");
//		vemoticon.setMd5(textString.replace("/", ""));
//		vemoticon.setText(textString);
//		vemoticon.setEid(id);
//		return vemoticon;
//	}
//
//	/**
//	 * 支持 直接转成String ,若无[/表情] 或者不匹配默认库 则不变
//	 *
//	 *
//	 * @param o
//	 * @return string[0]="true" 匹配成功 ; string[1] bodyString
//	 */
//	public static boolean text2Ce(Outter<String> o) {
//		String text = o.value();
//		String textString = text.replaceAll("<[^>]*>", "");
//
//		if (textString.contains("[/") && textString.contains("]")) {
//			textString = textString.replace("[/", "/");
//			textString = textString.replace("]", "");
//
//			String id = defultEmotionMap.get(textString);
//			if (!StringUtils.isNullOrEmpty(id)) {
//				o.setValue(String.format(FxContentFormat.TEXT_CE, "CE", "", textString.replace("/", ""), "", id, "", "", textString));
//				return true;
//			} else
//				o.setValue(text);
//			return false;
//		} else {
//			o.setValue(text);
//			return false;
//		}
//	}
//
//	/**
//	 * <OBJECT CE> 转成[/表情]
//	 *
//	 * @param ce
//	 * @return
//	 */
//	public static String Ce2Text(String ce) {
//		ce = ce.replaceAll("<[^>]*>", "");
//		ce = "[" + ce + "]";
//		return ce;
//	}
//
//
//	/**
//	 * 转换[/大笑]..
//	 * @param text
//	 * @return
//	 */
//	public static List<FxContentObject> convertV6ToFx(String text) {
//		for ()
//
//	}
//
//	/**
//	 * 默认只含有TEXT, TEXT_CE
//	 * @param objects
//	 * @return
//	 */
//	public static String convertFxToV6(List<FxContentObject> objects) {
//
//	}
}
