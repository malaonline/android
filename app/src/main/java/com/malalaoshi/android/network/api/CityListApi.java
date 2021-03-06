package com.malalaoshi.android.network.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.network.result.CityListResult;

/**
 * 城市列表
 * Created by zhoukang
 */
public class CityListApi extends BaseApi {

    @Override
    protected String getPath() {
        return "/api/v1/regions";
    }

    @Override
    protected boolean addAuthHeader() {
        return true;
    }

    public CityListResult getCityList()
            throws Exception {
        return httpGet(getPath() , CityListResult.class);
    }
}
