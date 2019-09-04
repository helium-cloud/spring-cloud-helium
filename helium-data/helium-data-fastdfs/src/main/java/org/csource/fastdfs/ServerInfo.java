/**
* Copyright (C) 2008 Happy Fish / YuQing
*
* FastDFS Java Client may be copied only under the terms of the GNU Lesser
* General Public License (LGPL).
* Please visit the FastDFS Home Page http://www.csource.org/ for more detail.
*/

package org.csource.fastdfs;


/**
* Server Info
* @author Happy Fish / YuQing
* @version Version 1.7
*/
public class ServerInfo
{
	protected String ip_addr;
	protected int port;
	protected byte storyPath;
	
	
	
	
/**
* Constructor
* @param ip_addr address of the server
* @param port the port of the server
*/
	public ServerInfo(String ip_addr, int port)
	{
		this.ip_addr = ip_addr;
		this.port = port;
	}
	
	public ServerInfo(String ip_addr, int port, byte storyPath) {
		this.ip_addr = ip_addr;
		this.port = port;
		this.storyPath = storyPath;
	}
	
/**
* return the ip address
* @return the ip address
*/
	public String getIpAddr()
	{
		return this.ip_addr;
	}
	
/**
* return the port of the server
* @return the port of the server
*/
	public int getPort()
	{
		return this.port;
	}
	
	
	
	public byte getStoryPath() {
		return storyPath;
	}

	public void setStoryPath(byte storyPath) {
		this.storyPath = storyPath;
	}

	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append(String.format("ip_addr:%s|", ip_addr));
		sb.append(String.format("port:%s|", port));
		sb.append(String.format("storyPath:%s|", storyPath));
		return sb.toString();
	}
	
	

}
