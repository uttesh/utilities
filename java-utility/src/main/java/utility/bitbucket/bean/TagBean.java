package utility.bitbucket.bean;

import java.util.List;

public class TagBean {

	String name;
	List<Object> links;
    Object tagger;
    String date;
    String message;
    String type;
    Object target;
    int pagelen;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Object> getLinks() {
		return links;
	}
	public void setLinks(List<Object> links) {
		this.links = links;
	}
	public Object getTagger() {
		return tagger;
	}
	public void setTagger(Object tagger) {
		this.tagger = tagger;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getTarget() {
		return target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}
	public int getPagelen() {
		return pagelen;
	}
	public void setPagelen(int pagelen) {
		this.pagelen = pagelen;
	}
    
}
