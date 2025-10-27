package cern.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Details of a transfer job
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobInfoExtended extends JobInfo {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String job_state;    // https://fts3-docs.web.cern.ch/fts3-docs/docs/state_machine.html

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String job_type;     // "N" or "R"

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Map<String, String> job_metadata;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String source_se;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String source_space_token;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String dest_se;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String space_token;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String verify_checksum; // "b" or "n"

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String overwrite_flag;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Integer priority;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Integer retry;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Integer retry_delay;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Integer max_time_in_queue;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Boolean cancel_job;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Date job_finished;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Date submit_time;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String submit_host;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String reason;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String vo_name;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String user_dn;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String cred_id;

    public List<JobFileInfo> file_info;

    /**
     * Constructor
     */
    public JobInfoExtended() {

    }
}
