package com.newblack.coffit;

import android.app.DownloadManager;

import com.newblack.coffit.Data.Noti;
import com.newblack.coffit.Data.PT;
import com.newblack.coffit.Data.Schedule;
import com.newblack.coffit.Data.Trainer;
import com.newblack.coffit.Data.TrainerSchedule;
import com.newblack.coffit.Response.HomeResponse;
import com.newblack.coffit.Response.NotiResponse;
import com.newblack.coffit.Response.TrainerResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("home")
    Call<TrainerResponse> getMain();

    @GET("trainers")
    Call<List<Trainer>> getTrainerList();

    @GET("trainers/{id}")
    Call<Trainer> getTrainer(@Path("id") int trainerId);

    @FormUrlEncoded
    @POST("PTs")
    Call<PT> postPT(@FieldMap HashMap<String,Object> param);

    @FormUrlEncoded
    @POST("schedules")
    Call<Schedule> postSchedule(@FieldMap HashMap<String,Object> param);

    @GET("schedules/students/{studentId}")
    Call<Schedule> getSchedule(@Path("studentId") int studentId);

    @GET("trainerSchedules/{trainerId}")
    Call<List<TrainerSchedule>> getTrainerSchedule(@Path("trainerId") int trainerId);

    @GET("PTs/students/{id}")
    Call<List<HomeResponse>> getPT(@Path("id") int studentId);

    @GET("schedules/trainer/{id}")
    Call<List<Date>> getAvailableList();

    @GET("notifications/students/{id}")
    Call<List<NotiResponse>> getNotiList(@Path("id") int studentId);

    @PUT("schedules/{scheduleId}")
    Call<Schedule> putSchedule(@Body Schedule schedule, @Path("scheduleId") int scheduleId, @Query("iAm") String iAm);

    @DELETE("schedules/{scheduleId}")
    Call<Schedule> deleteSchedule(@Path("scheduleId") int scheduleId);
}
