package com.strs.rkot;

import java.util.List;

public interface JobSubmitService {
	
	List<JobSubmitResult> submitJobs(List<Job> jobs, String serviceURI, String endpoint);

}
