package com.cs.mobile.api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.cs.mobile.api.model.partner.battle.Accounting;
import com.cs.mobile.api.model.partner.progress.ProgressReport;
import com.cs.mobile.api.model.partner.progress.ShareDetail;

public interface IndexService {

	public ProgressReport getIndexSale(Map<String,Object> paramMap) throws Exception;

	public ProgressReport getIndexFontGp(Map<String,Object> paramMap) throws Exception;

	public ProgressReport getIndexAfterGp(Map<String,Object> paramMap) throws Exception;

	public ProgressReport getIndexCost(Map<String,Object> paramMap) throws Exception;

	public BigDecimal getIndexShare(Map<String,Object> paramMap) throws Exception;

	public ShareDetail getShareDetail(Map<String,Object> paramMap) throws Exception;

	public Map<String,Object> getAccountingDesc(Map<String,Object> paramMap) throws Exception;

	public List<Accounting> getAccountingList(Map<String,Object> paramMap) throws Exception;
}
