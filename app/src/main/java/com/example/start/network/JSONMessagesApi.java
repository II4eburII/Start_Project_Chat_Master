package com.example.start.network;

import com.example.start.data.Message;
import com.example.start.data.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JSONMessagesApi {
    @POST("/api/message/")
    public Call<Post> postMessage(@Body Message data);

    @GET("/api/message/{id}")
    public Call<Message> getMessage(@Path("id") String id);

    @GET("/api/message")
    public Call<List<Message>> getMessages();
}
