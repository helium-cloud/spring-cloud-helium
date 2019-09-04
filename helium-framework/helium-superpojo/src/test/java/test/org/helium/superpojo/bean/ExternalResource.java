package test.org.helium.superpojo.bean;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;

/**
 * Created by lvmingwei on 7/20/15.
 */
@Entity(name = "Resource")
public class ExternalResource extends SuperPojo {
    @Field(id = 1, name = "ResourceId")
    private String resourceID;
    @Field(id = 2, name = "Expired")
    private Long expired;
    @Field(id = 3, name = "Type")
    private Integer type;
    @Field(id = 4, name = "SDP", isCDATA = true)
    private String sdp;

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public Long getExpired() {
        return expired;
    }

    public void setExpired(Long expired) {
        this.expired = expired;
    }

    public Integer getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public static ExternalResource create(String resourceID, long expired, int type, String sdp) {
        ExternalResource er = new ExternalResource();
        er.setResourceID(resourceID);
        er.setExpired(expired);
        if (type >= 0) {
            er.setType(type);
        }
        if (sdp != null && sdp.length() > 0) {
            er.setSdp(sdp);
        }
        return er;

    }

    public static void main(String args[]) {
        StringBuilder sb = new StringBuilder();
        sb.append("v=0\r\n");
        sb.append("o=- 710764915 0 IN IP4 10.10.50.165\r\n");
        sb.append("s=-\r\n");
        sb.append("c=IN IP4 10.10.50.165\r\n");
        sb.append("t=0 0\r\n");
        sb.append("m=audio 40212 RTP/AVP 103 105\r\n");
        sb.append("a=rtpmap:103 AMR/8000\r\n");
        sb.append("a=fmtp:103 mode-set=0,1,2,3,4,5;octet-align=1\r\n");
        sb.append("a=rtpmap:105 iLBC/8000\r\n");
        sb.append("a=fmtp:105 mode=30\r\n");
        sb.append("a=ptime:60\r\n");
        sb.append("a=maxptime:400\r\n");
        sb.append("a=sendrecv\r\n");
        sb.append("m=video 40214 RTP/AVP 122 121\r\n");
        sb.append("a=rtpmap:122 VP8/90000\r\n");
        sb.append("a=framesize:122 640-480\r\n");
        sb.append("a=imageattr:122 send * recv [x=[128:8:640],y=[96:8:480],par=[1.3333-1.3334]] [br=[30-150000]] [fr=[1-20]]\r\n");
        sb.append("a=rtpmap:121 H264/90000\r\n");
        sb.append("a=fmtp:121 profile-level-id=42801E; packetization-mode=1\r\n");
        sb.append("a=framesize:121 640-480\r\n");
        sb.append("a=imageattr:121 send * recv [x=[128:8:640],y=[96:8:480],par=[1.3333-1.3334]] [br=[30-150000]] [fr=[1-20]]\r\n");
        sb.append("a=sendrecv\r\n");
        sb.append("a=extmap:4 urn:3gpp:video-orientation\r\n");
        sb.append("a=rtcp-fb:* nack\r\n");
        sb.append("a=rtcp-fb:* ack rpsi\r\n");
        sb.append("a=rtcp-fb:* ccm tmmbr");

        ExternalResource er = new ExternalResource();

        er.setSdp(sb.toString());

        String s = new String(er.toXmlByteArray());
        ExternalResource er2 = new ExternalResource();
        er2.parseXmlFrom(s);
        System.out.println();

    }
}