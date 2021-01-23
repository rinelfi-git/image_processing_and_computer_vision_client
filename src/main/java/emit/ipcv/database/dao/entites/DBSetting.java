package emit.ipcv.database.dao.entites;

public class DBSetting {
	private int language, remotePortAddress;
	private String remoteIpAddress;
	private boolean useLocalHardware;
	
	public DBSetting() {}
	
	public DBSetting(int language, int remotePortAddress, String remoteIpAddress, boolean useLocalHardware) {
		this.language = language;
		this.remotePortAddress = remotePortAddress;
		this.remoteIpAddress = remoteIpAddress;
		this.useLocalHardware = useLocalHardware;
	}
	
	public int getLanguage() {
		return language;
	}
	
	public int getRemotePortAddress() {
		return remotePortAddress;
	}
	
	public String getRemoteIpAddress() {
		return remoteIpAddress;
	}
	
	public boolean isUseLocalHardware() {
		return useLocalHardware;
	}
}
