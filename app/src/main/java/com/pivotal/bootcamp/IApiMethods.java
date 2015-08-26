package com.pivotal.bootcamp;

import retrofit.http.GET;
import retrofit.http.Query;

public interface IApiMethods {

    @GET("/get/curators.json")
    SomePOJO getCurators(
            @Query("api_key") String key
    );
}