package com.imagination.cbs.constant;

/**
 * @author pappu.rout
 *
 */

public enum EmailConstants {

	
	DISCIPLINE("discipline"),
	ROLE("role"),
	DISCIPLINE_ID("disciplineId"),
	ROLE_ID("roleId"),
	CONTRCTOR_EMPLOYEE("contractorEmployee"),
	CONTRCTOR ("contractor"),
	SUPPLIER_TYPE("supplierType"),
	START_DATE("startDate"),
	END_DATE("endDate"),
	WORK_LOCATIONS("workLocations"),
	REASON_FOR_RECRUITING("reasonForRecruiting"),
	TASK("task"),
	DELIVERY_DATE("deliveryDate"),
	DAY_RATE("dayRate"),
	TOTAL_DAYS("totalDays"),
	TOTAL("total"),
	TOTAL_COST("totalCost"),
	REQUESTED_BY("requestedBy"),
	DOMAIN("@imagination.com"),
	EMAIL_ADDRESS("mailAddress"),
	JOB_NUMBER("jobNumber"),
	JOB_NAME("jobName"),

	CONTRACTOR_PDF_LINK("contractorPdf"),
	SCOPE_OF_WORK_LINK("scopeOfWorkPdf"),

	BOOKING_REQUEST_TEMPLATE("Request"),
	BOOKING_REQUEST_FOR_APPROVAL_TEMPLATE("RequestForApproval"),
	CONTRACT_TO_CONTRACTOR_TEMPLATE("ContractToContractor"),
	CONTRACT_RECEIPT_TEMPLATE("ContractReceipt"),

	FROM_EMAIL("CBS@imagination.com"),
	TO_EMAIL("internal_sourcing.email_to"),
	INTERNAL_NOTIFICATION_SUBJECT_LINE("Internal Email Notification : Contractor Booking request #  ");
	
	
	private String emailConstantsString;
	
	private EmailConstants(String emailConstantsString) {
		this.emailConstantsString = emailConstantsString;
	}

	public String getEmailConstantsString() {
		return emailConstantsString;
	}
	
	
	
}