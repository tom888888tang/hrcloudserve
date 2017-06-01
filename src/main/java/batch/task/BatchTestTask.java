package batch.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BatchTestTask {
	private static Logger log = LoggerFactory.getLogger(BatchTestTask.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
	
	public void printLog() {
		
		log.info("=========跑批开始{}============",sdf.format(Calendar.getInstance().getTime()));
	}
}
