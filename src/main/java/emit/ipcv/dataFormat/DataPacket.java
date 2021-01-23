/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.dataFormat;

import java.io.Serializable;

/**
 *
 * @author rinelfi
 */
public class DataPacket implements Serializable{
	private String header;
	private Object data;
	
	public String getHeader() {
		return header;
	}
	
	public DataPacket setHeader(String header) {
		this.header = header;
		return this;
	}
	
	public Object getData() {
		return data;
	}
	
	public DataPacket setData(Object data) {
		this.data = data;
		return this;
	}
	
}
