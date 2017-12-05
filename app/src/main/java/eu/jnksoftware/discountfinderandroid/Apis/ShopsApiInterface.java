package eu.jnksoftware.discountfinderandroid.Apis;

import java.util.List;

import eu.jnksoftware.discountfinderandroid.models.Shop;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShopsApiInterface {

    @GET("shop")
    Call<List<Shop>> getShopsList();

    @GET ("shop/")
    Call<List<Shop>> getShopWithId(@Query("id") int id);

    @GET ("user/shop")
    Call<List<Shop>> getUserShops();

    @GET ("user/shop/")
    Call<List<Shop>> getUserShopWithId(@Query("id") int id);

    @Headers({("Content-Type:application/json"),("Accept:application/json")})
    @POST("shop")
    Call<Void> addShop(@Body PostShop postShop, @Header("Authorization") String auth);

    @Headers({("Content-Type:application/json"),("Accept:application/json")})
    @DELETE("shop/{id}")
    Call<Void> deleteShop(@Path("id") int id, @Header("Authorization") String auth);
}
