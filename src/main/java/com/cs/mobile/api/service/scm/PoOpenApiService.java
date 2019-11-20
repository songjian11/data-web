package com.cs.mobile.api.service.scm;

import com.cs.mobile.api.model.scm.ItemTaxRate;
import com.cs.mobile.api.model.scm.openapi.CheckResult;
import com.cs.mobile.api.model.scm.openapi.PoCountResp;
import com.cs.mobile.api.model.scm.openapi.PoReq;
import com.cs.mobile.api.model.scm.request.PoDetailReq;
import com.cs.mobile.api.model.scm.request.PoHeadReq;

public interface PoOpenApiService {
    /**
     * @param supplier
     * @return com.cs.mobile.api.model.scm.openapi.PoCountResp
     * @author wells.wong
     * @date 2019/9/10
     */
    PoCountResp getPoCountBySupplier(String supplier);

    /**
     * @param poReq
     * @return void
     * @author wells.wong
     * @date 2019/9/10
     */
    void creatPo(PoReq poReq, CheckResult checkResult) throws Exception;

    CheckResult checkData(PoReq poReq) throws Exception;
}
