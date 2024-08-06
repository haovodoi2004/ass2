package fpoly.pro1121.asm2client.Service;

import java.util.ArrayList;

import fpoly.pro1121.asm2client.Model.Category;
import fpoly.pro1121.asm2client.Model.Order;
import fpoly.pro1121.asm2client.Model.OrderResponse;
import fpoly.pro1121.asm2client.Model.Product;
import fpoly.pro1121.asm2client.Model.Response;
import fpoly.pro1121.asm2client.Model.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    public static String BASE_URL = "http://192.168.2.3:5000";

    @POST("/api/user/login")
    Call<Response<User>> login(@Body User user);

    @Multipart
    @POST("/api/user/register")
    Call<User> register(
            @Part("name") RequestBody username,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part avatar,
            @Part("address") RequestBody address
    );

    @Multipart
    @PUT("/api/user/update/{id}")
    Call<User> updateUser(
            @Path("id") String id,
            @Part("name") RequestBody username,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part avatar,
            @Part("address") RequestBody address
    );

    @GET("/api/product/getAll")
    Call<Response<ArrayList<Product>>> getAllProduct();

    @GET("/api/product/search/")
    Call<Response<ArrayList<Product>>> getProductByName(@Query("name") String name);

    @GET("/api/category")
    Call<Response<ArrayList<Category>>> getAllCategory();

    @GET("/api/product/findByCate/{id}")
    Call<Response<ArrayList<Product>>> getProductByCategory(@Path("id") String id);

    @GET("/api/product/find/{id}")
    Call<Response<Product>> getProductByID(@Path("id") String id);

    @POST("/api/order")
    Call<Order> createOrder(@Body Order order);

    @GET("/api/orders/{userId}")
    Call<OrderResponse> getOrders(@Path("userId") String userId);

    @GET("/api/user/get/{id}")
    Call<Response<User>> getUserById(@Path("id") String id);

    @DELETE("api/product/delete/{id}")
    Call<Void> deleteProduct(@Path("id") String id);

    @Multipart
    @POST("/api/product/add")
    Call<Product> AddProd(
            @Part("name") RequestBody name,
            @Part("category") RequestBody category,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part MultipartBody.Part imageUrl,
            @Part("stock") RequestBody stock
    );

    @Multipart
    @PUT("api/product/update/{id}")
    Call<Void> updateProduct(
            @Path("id") String id,
            @Part("name") RequestBody name,
            @Part("category") RequestBody category,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part MultipartBody.Part imageUrl,
            @Part("stock") RequestBody stock
    );

    @POST("/api/category/add")
    Call<Void> AddCategory(@Body Category category);

    @DELETE("/api/category/delete/{id}")
    Call<Void> DeleteCategory(@Path("id") String id);
}

