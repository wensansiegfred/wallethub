package wallethub;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.ef.model.LogIp;
import com.ef.service.IpService;

public class SampleTest {

	@Test
	public void testSampleInsert() {
		List<LogIp> logIps = new ArrayList<LogIp>();
		String ip = "102.168.1.1";
		logIps.add(new LogIp(ip, new Date()));
		String sql = "INSERT INTO server_log (ip_address, log_time) "
				+ "SELECT * FROM (SELECT ?, ?) AS tmp "
				+ "WHERE NOT EXISTS (SELECT ip_address, log_time FROM server_log WHERE ip_address = ? AND log_time = ?) LIMIT 1";
		IpService.getInstance().insertIpLogs(sql, logIps);

		String sqlSel = "SELECT * FROM server_log WHERE ip_address = ?";
		List<LogIp> logIpsTest = IpService.getInstance().getLogById(sqlSel, ip);
		assertFalse(logIpsTest.isEmpty());
	}
}
