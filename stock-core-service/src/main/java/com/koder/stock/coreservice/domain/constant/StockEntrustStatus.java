package com.koder.stock.coreservice.domain.constant;

import com.google.common.collect.Lists;

import java.util.List;

public class StockEntrustStatus {
    /**
     * 0	No Register(未报)
     * 1	Wait to Register(待报)
     * 2	Host Registered(已报)
     * 3	Wait for Cancel(已报待撤)
     * 4	Wait for Cancel(Partially Matched)(部成待撤)
     * 5	Partially Cancelled(部撤)
     * 6	Cancelled(已撤)
     * 7	Partially Filled(部成)
     * 8	Filled(已成)
     * 9	Host Reject(废单)
     * A	Wait for Modify (Registed)（已报待改）
     * B	Unregistered（无用）
     * C	Registering（无用）
     * D	Revoke Cancel（无用）
     * W	Wait for Confirming（待确认）
     * X	Pre Filled（无用）
     * E	Wait for Modify (Partially Matched)（部成待改）
     * F	Reject（预埋单检查废单）
     * G	Cancelled(Pre-Order)（预埋单已撤）
     * H	Wait for Review（待审核）
     * J	Review Fail（审核失败）
     * #
     */
    public static final String INIT_ED = "INIT_ED";
    public static final String EN_TRUSTING = "EN_TRUSTING";
    public static final String EN_TRUSTED = "EN_TRUSTED";
    public static final String EN_TRUSTED_FAILED = "EN_TRUSTED_FAILED";
    public static final String SUCCESS = "8";
    public static final String EN_REPORTED = "2";
    public static final String EN_WAIT_REPORTED = "1";
    public static final String EN_UN_REPORTED = "0";
    public static final String CANCELLED = "6";
    public static final String EN_TRUSTED_INVALID_ORDER = "9";

    public static List<String> getUnfinishedStatus() {
        List<String> ret = Lists.newArrayList(INIT_ED, EN_TRUSTING, EN_TRUSTED, EN_REPORTED, EN_WAIT_REPORTED, EN_UN_REPORTED);
        return ret;
    }


}
