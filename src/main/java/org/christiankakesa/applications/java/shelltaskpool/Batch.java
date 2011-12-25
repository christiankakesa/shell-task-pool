package org.christiankakesa.applications.java.shelltaskpool;

//import org.apache.commons.logging.LogFactory;
import java.util.Date;

/**
 * Store all informations about Batch and Jobs
 */
public final class Batch {
	//private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(Batch.class.getName());
	public static final String DEFAULT_BATCH_NAME = "NO NAME";
	
	private static final Batch INSTANCE = new Batch();
	private volatile String name;
	private volatile String id;
	private volatile Date startDate;
	private volatile Date endDate;
	private volatile BatchStatus status = new BatchStatus();
	//TODO: move all to JobExecution static class field ?
	
	private Batch() {
	}

	public static Batch getInstance() {
		return INSTANCE;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		if (name != null && !name.isEmpty()) { //Test in name is not null and not empty
			this.name = name;
		} else {
			this.name = Batch.DEFAULT_BATCH_NAME;
		}
		this.setId(Util.buildUUID()); //Set the Batch Id when name is set
	}

	public String getId() {
		return id;
	}
	
	private void setId(final String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public BatchStatus getStatus() {
		return status;
	}
}
