package test.org.helium.superpojo.bean;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.util.SuperPojoUtils;

@Entity(name = "cr:ruleset")
public class IndexEntity extends SuperPojo {

	@Field(id = 1, name = "xmlns:cr", type = NodeType.ATTR)
	private String xmlns_cr;

	@Field(id = 2, name = "xmlns:", type = NodeType.ATTR)
	private String xmlns;

	@Field(id = 3, name = "xmlns:xsi", type = NodeType.ATTR)
	private String xmlns_xsi;

	@Field(id = 4, name = "xsi:schemaLocation", type = NodeType.ATTR)
	private String xsi_schemaLocation;

	@Field(id = 5, name = "cr:rule")
	private List<Crrule> cr_rule;

	public String getXmlns_cr() {
		return xmlns_cr;
	}

	public void setXmlns_cr(String xmlns_cr) {
		this.xmlns_cr = xmlns_cr;
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getXmlns_xsi() {
		return xmlns_xsi;
	}

	public void setXmlns_xsi(String xmlns_xsi) {
		this.xmlns_xsi = xmlns_xsi;
	}

	public String getXsi_schemaLocation() {
		return xsi_schemaLocation;
	}

	public void setXsi_schemaLocation(String xsi_schemaLocation) {
		this.xsi_schemaLocation = xsi_schemaLocation;
	}

	public List<Crrule> getCr_rule() {
		return cr_rule;
	}

	public void setCr_rule(List<Crrule> cr_rule) {
		this.cr_rule = cr_rule;
	}

	public static class Crrule extends SuperPojo {
		@Field(id = 1, name = "id", type = NodeType.ATTR)
		private String id;
		@Field(id = 2, name = "cr:conditions")
		private Crconditions cr_conditions;
		@Field(id = 3, name = "cr:actions")
		private Cractions cr_actions;
		@Field(id = 4, name = "cr:transformations")
		private Crtransformations cr_transformations;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Crconditions getCr_conditions() {
			return cr_conditions;
		}

		public void setCr_conditions(Crconditions cr_conditions) {
			this.cr_conditions = cr_conditions;
		}

		public Cractions getCr_actions() {
			return cr_actions;
		}

		public void setCr_actions(Cractions cr_actions) {
			this.cr_actions = cr_actions;
		}

		public Crtransformations getCr_transformations() {
			return cr_transformations;
		}

		public void setCr_transformations(Crtransformations cr_transformations) {
			this.cr_transformations = cr_transformations;
		}

	}

	public static class Crconditions extends SuperPojo {
		@Field(id = 1, name = "cr:identity")
		private Cridentity cr_identity;

		public Cridentity getCr_identity() {
			return cr_identity;
		}

		public void setCr_identity(Cridentity cr_identity) {
			this.cr_identity = cr_identity;
		}
	}

	public static class Cridentity extends SuperPojo {
		@Field(id = 1, name = "cr:id")
		private List<Crid> cr_id;

		public List<Crid> getCr_id() {
			return cr_id;
		}

		public void setCr_id(List<Crid> cr_id) {
			this.cr_id = cr_id;
		}
	}

	public static class Crid extends SuperPojo {
		@Field(id = 1, name = "entity", type = NodeType.ATTR)
		private String entity;

		public String getEntity() {
			return entity;
		}

		public void setEntity(String entity) {
			this.entity = entity;
		}
	}

	public static class Cractions extends SuperPojo {
		@Field(id = 1, name = "sub-handling")
		private List<Subhandling> sub_handling;

		public List<Subhandling> getSub_handling() {
			return sub_handling;
		}

		public void setSub_handling(List<Subhandling> sub_handling) {
			this.sub_handling = sub_handling;
		}

	}
	public static class Subhandling extends SuperPojo {
		@Field(id = 1, name = "xmlns:", type = NodeType.ATTR)
		private String xmlns;
		public String getStringAnyNode() {
			return SuperPojoUtils.getStringAnyNode(this);
		}
		public String getXmlns() {
			return xmlns;
		}
		public void setXmlns(String xmlns) {
			this.xmlns = xmlns;
		}
	}
	
	public static class Crtransformations extends SuperPojo {
		@Field(id = 1, name = "provide-services")
		private List<Provideservices> provide_services;
		
		@Field(id = 2, name = "provide-all-attributes")
		private List<Provideallattributes> provide_all_attributes;

		public List<Provideservices> getProvide_services() {
			return provide_services;
		}

		public void setProvide_services(List<Provideservices> provide_services) {
			this.provide_services = provide_services;
		}

		public List<Provideallattributes> getProvide_all_attributes() {
			return provide_all_attributes;
		}

		public void setProvide_all_attributes(List<Provideallattributes> provide_all_attributes) {
			this.provide_all_attributes = provide_all_attributes;
		}
		
		
	}
	
	public static class Provideservices extends SuperPojo {
		@Field(id = 1, name = "xmlns:", type = NodeType.ATTR)
		private String xmlns;
		public String getXmlns() {
			return xmlns;
		}
		public void setXmlns(String xmlns) {
			this.xmlns = xmlns;
		}
	}
	
	public static class Provideallattributes extends SuperPojo {
		@Field(id = 1, name = "xmlns", type = NodeType.ATTR)
		private String xmlns;
		public String getXmlns() {
			return xmlns;
		}
		public void setXmlns(String xmlns) {
			this.xmlns = xmlns;
		}
	}
	
	public static void main(String[] args) throws Exception {
		int time =10000;
		InputStream in = IndexEntity.class.getResourceAsStream("index.xml");
		
		IndexEntity index = new IndexEntity();
		index.parseXmlFrom(in);
		System.out.println(new String(index.toXmlByteArray()));
	}
	
	//*******************************************************************************************
	
	
	
	
	
	
	
	
	public static Object matchAttribute(String arrName,String arrValue,List<?> list) throws Exception{
		for(Object node : list){
			Class<? extends Object> nodeClazz = node.getClass();
			java.lang.reflect.Field[] declaredFields = nodeClazz.getDeclaredFields();
			for(java.lang.reflect.Field  declaredField: declaredFields){
				Field annotation = declaredField.getAnnotation(Field.class);
				String name = annotation.name();
				if(name!=null&&name.equals(arrName)){
					String fieldName = declaredField.getName();
					Class<?> fieldType = declaredField.getType();
					Method method = nodeClazz.getMethod("get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1));
					Object obj = method.invoke(node);
					if(fieldType == String.class){
						String strr = (String)obj;
						if(strr!=null&&strr.equals(arrValue)){
//							System.out.println(strr);
							return node;
						}
					}else{
						throw new Exception("");
					}
				}
			}
		}
		return null;
	}
	
	public static Object match(Object object,List<String> url_arrs) throws Exception{
		if(url_arrs.size()==0){
			return object;
		}
		String url_arr = url_arrs.remove(0);
		if(url_arr.startsWith("@")){
			
		}else if(url_arr.indexOf("'")!=-1){
			String nodeName = url_arr.substring(0,url_arr.indexOf("["));
			String temp = url_arr.substring(url_arr.indexOf("[")+1);
			temp = temp.substring(1, temp.length()-1);
			String[] split = temp.split("=");
			String arrName = split[0];
			String arrValue = split[1].substring(1,split[1].length()-1);
			java.lang.reflect.Field[] declaredFields = object.getClass().getDeclaredFields();
			for(java.lang.reflect.Field declaredField : declaredFields){
				Field annotation = declaredField.getAnnotation(Field.class);
				String name = annotation.name();
				if(name!=null&&name.equals(nodeName)){
					String fieldName = declaredField.getName();
					Class<?> fieldType = declaredField.getType();
					Method method = object.getClass().getMethod("get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1));
					Object obj = method.invoke(object);
					if(fieldType == List.class){
						List<?> list = (List<?>)obj;
						Object matchAttribute = matchAttribute(arrName,arrValue,list);
						return match(matchAttribute,url_arrs);
					}else if(fieldType == String.class){
//						System.out.println("String");
					}
				}
			}
		}else{
			java.lang.reflect.Field[] declaredFields = object.getClass().getDeclaredFields();
			for(java.lang.reflect.Field declaredField : declaredFields){
				Field annotation = declaredField.getAnnotation(Field.class);
				String name = annotation.name();
				if(name!=null&&name.equals(url_arr)){
					//
					String fieldName = declaredField.getName();
					Class<?> fieldType = declaredField.getType();
					Method method =  object.getClass().getMethod("get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1));
					Object obj = method.invoke(object);
					if(fieldType == List.class){
//						System.out.println("list");
						return match(obj,url_arrs);
					}else if(fieldType == String.class){
//						System.out.println("String");
						return match(obj,url_arrs);
					}else{
						return match(obj,url_arrs);
					}
					
				}
			}
		}
		return null;
	}
	
	public static void test(IndexEntity index) throws Exception{
		
		String[] url_arrs = path.split("/");
		LinkedList<String> list = new LinkedList<String>();
		for(int i=0;i<url_arrs.length;i++){
			String url_arr= url_arrs[i];
			if(i==0){
				if(v(url_arr,index)){
					continue;
				}
			}
			list.add(url_arr);
		}
		Cridentity match = (Cridentity)match(index,list);
		match.setCr_id(null);
		//System.out.println(new String(match.toXmlByteArray()));
		System.out.println(new String(index.toXmlByteArray()));
	}
	public static boolean v(String url_arr,Object object){
		Entity annotation = object.getClass().getAnnotation(Entity.class);
		if(annotation==null){
			return true;
		}
		String name = annotation.name();
		if(name!=null&&name.equals(name)){
			return true;
		}else{
			return false;
		}
	}
	
	private static String path="cr:ruleset/cr:rule[@id='099d8b7b5ca74416b']/cr:conditions/cr:identity";
	
//	public static void main(String[] args) throws Exception {
//		int time =10000;
//		File file = new File("C:\\index.xml");
//		FileInputStream in = new FileInputStream(file);
//		
//		IndexEntity index = new IndexEntity();
//		index.parseXmlFrom(in);
//		System.out.println("start");
//		long start = System.currentTimeMillis();
////		while(time-->0){
//			test(index);
////		}
//		long end = System.currentTimeMillis();
//		System.out.println((end-start));
//	}
	
	public static class NodeMatch{
		public String url_arr;
		public Object node;
		
	}
	
}
