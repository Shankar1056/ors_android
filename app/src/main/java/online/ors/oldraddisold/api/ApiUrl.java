package online.ors.oldraddisold.api;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 2017-04-11 at 4:15 PM
 */

public final class ApiUrl {
	private static final String URL_BASE = "http://www.ors.online/manage/api_v1/client/";
	
	public static final String GENERATE_OTP = URL_BASE + "sendOTP";
	public static final String SUBMIT_OTP = URL_BASE + "validateOTP";
	public static final String REGISTRATION = URL_BASE + "registerUser";
	public static final String GCM_TOKEN_UPDATE = URL_BASE + "setPushToken";
	
	public static final String RATE_CARD = URL_BASE + "getRateCard";
	public static final String SCHEDULE = URL_BASE + "schedulePickup";
	public static final String TIME_SLOTS = URL_BASE + "getTimeSlots";
	public static final String PICKUP_LIST = URL_BASE + "listSchedules";
	public static final String SCHEDULE_DETAILS = URL_BASE + "getSchedule";
	public static final String SCHEDULE_CANCEL = URL_BASE + "cancelSchedule";
	public static final String WALLET_BALANCE = URL_BASE + "getWalletBalance";
	public static final String DONATE = URL_BASE + "donateCredits";
	public static final String REDEEM = URL_BASE + "redeemCredits";
	public static final String REDEEM_HISTORY = URL_BASE + "getWalletTransactions";
	public static final String FEEDS = URL_BASE + "getFeed";
	public static final String FEED_BY_ID = URL_BASE + "getFullArticle";
	public static final String COMMENTS = URL_BASE + "getComments";
	public static final String POST_COMMENT = URL_BASE + "postComment";
	public static final String ILLEGAL_DUMP = URL_BASE + "reportIllegalDumping";
	public static final String UPDATE_USER_PROFILE = URL_BASE + "updateUserProfile";
	public static final String GET_USER_BANK_DETAILS = URL_BASE + "getUserBankDetails";
	public static final String FAQ = URL_BASE + "getFAQs";
	public static final String MODIFY_MOBILE = URL_BASE + "updateUserPhone";
	public static final String CHECK_FOR_UPDATES = URL_BASE + "checkForUpdates";
}
