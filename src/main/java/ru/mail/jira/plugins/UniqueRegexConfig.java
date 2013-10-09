package ru.mail.jira.plugins;

import java.util.ArrayList;
import java.util.List;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;

public class UniqueRegexConfig extends JiraWebActionSupport {
    private static final long serialVersionUID = 1381842671050861762L;

    private final ApplicationProperties applicationProperties;
    private final UniqueRegexMgr urMgr;
    private final CustomFieldManager cfMgr;

    private List<CFData> datas;
    private String customFieldId;
    private String cfKey;
    private String regexclause;
    private String regexerror;
    private String jqlclause;
    private String targetcf;

    public UniqueRegexConfig(UniqueRegexMgr urMgr, ApplicationProperties applicationProperties, CustomFieldManager cfMgr) {
        this.urMgr = urMgr;
        this.applicationProperties = applicationProperties;
        this.cfMgr = cfMgr;
    }

    public String doConfigure() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        if (customFieldId == null || customFieldId.length() == 0) {
            return INPUT;
        } else {
            
            return "configure";
        }
    }

    @Override
    public String doDefault() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        datas = new ArrayList<CFData>();
        List<CustomField> cgList = cfMgr.getCustomFieldObjects();
        for (CustomField cf : cgList) {
            if (cf.getCustomFieldType().getClass().equals(UniqueRegexCF.class)) {
                CFData cFData = urMgr.getCFData(cf.getId());
                cFData.setCfKey(cf.getId());
                cFData.setCfName(cf.getName());
                datas.add(cFData);
            }
        }

        return INPUT;
    }

    public List<CustomField> getCustomFields() {
        return cfMgr.getCustomFieldObjects();
    }

    @Override
    @com.atlassian.jira.security.xsrf.RequiresXsrfCheck
    protected String doExecute() throws Exception {
        urMgr.setCfJql(customFieldId, jqlclause);
        urMgr.setCfRegex(customFieldId, regexclause);
        urMgr.setCfRegexError(customFieldId, regexerror);
        urMgr.setCfTarget(customFieldId, targetcf);
        return getRedirect("UniqueRegexConfig!default.jspa?saved=true");
    }

    @Override
    protected void doValidation() {
        if (!UrUtils.checkJQL(jqlclause)) {
            addError("jqlclause", "dedede");
        }

        if (!UrUtils.checkRegex(regexclause)) {
            addError("regexclause", "dedede");
        }

        super.doValidation();
    }

    public String getBaseUrl() {
        return applicationProperties.getBaseUrl();
    }

    public String getCfKey() {
        return cfKey;
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public List<CFData> getDatas() {
        return datas;
    }

    public String getJqlclause() {
        return jqlclause;
    }

    public String getRegexclause() {
        return regexclause;
    }

    public String getRegexerror() {
        return regexerror;
    }

    public String getTargetcf() {
        return targetcf;
    }

    public boolean hasAdminPermission() {
        User user = getLoggedInUser();
        if (user == null) {
            return false;
        }

        if (getPermissionManager().hasPermission(Permissions.ADMINISTER, getLoggedInUser())) {
            return true;
        }

        return false;
    }

    public void setCfKey(String cfKey) {
        this.cfKey = cfKey;
    }

    public void setCustomFieldId(String customFieldId) {
        this.customFieldId = customFieldId;
    }

    public void setJqlclause(String jqlclause) {
        this.jqlclause = jqlclause;
    }

    public void setRegexclause(String regexclause) {
        this.regexclause = regexclause;
    }

    public void setRegexerror(String regexerror) {
        this.regexerror = regexerror;
    }

    public void setTargetcf(String targetcf) {
        this.targetcf = targetcf;
    }
}
