package batch.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import core.DSUtill;

@Component
public class ClearDBMapTask {
	private static Logger log = LoggerFactory.getLogger(ClearDBMapTask.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
	
	public void clearMap() {
		DSUtill.dbMap.clear();
		
		log.info("=========删除缓存完成{}============",sdf.format(Calendar.getInstance().getTime()));
	}
}
