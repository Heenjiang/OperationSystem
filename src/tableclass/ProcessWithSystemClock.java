package tableclass;

import java.util.Calendar;

public class ProcessWithSystemClock extends TProcess {
	private Calendar createTime;
	public ProcessWithSystemClock(String proName, String proPriority, String proRuntime, String proStoreSize,String proStoreStart, String proNextName, Calendar createTime) {
		super(proName, proPriority, proRuntime, proStoreSize, proStoreStart, proNextName);
		this.createTime = createTime;
	}
	
	public void changePriority(Calendar systemDate) {
		long to = systemDate.getTimeInMillis();
		long from  = createTime.getTimeInMillis();
		double difference = ((double)from - (double)to) / 60.0;
//		this.prosPriority = 
	}
}
