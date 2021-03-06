package eu.jnksoftware.discountfinderandroid.ui.customer.shops;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.jnksoftware.discountfinderandroid.Apis.ApiUtils;
import eu.jnksoftware.discountfinderandroid.R;
import eu.jnksoftware.discountfinderandroid.Utilities.ManageSharePrefs;
import eu.jnksoftware.discountfinderandroid.models.Location;
import eu.jnksoftware.discountfinderandroid.models.Shop;
import eu.jnksoftware.discountfinderandroid.services.IuserService;
import eu.jnksoftware.discountfinderandroid.ui.customer.adapters.RecyclerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerShops extends Fragment {

    public IuserService getApiService() {
        return apiService;
    }

    public void setApiService(IuserService apiService) {
        this.apiService = apiService;
    }

    public List<Shop> getShops() {
        return shops;
    }

    private RecyclerView shopsRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Shop> shops = new ArrayList<>();
    private IuserService apiService;
    private String auth;

    private static final int requestCode = 1;
    private Location userLocation = new Location();

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_seller_shops,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopsRecyclerView = view.findViewById(R.id.shopsRecyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        shopsRecyclerView.setLayoutManager(layoutManager);
        shopsRecyclerView.setHasFixedSize(true);
        auth= getArguments().getString("auth");
        apiService = ApiUtils.getUserService();
        getUserShops();
        Button addStore = view.findViewById(R.id.addStoreButton);
        addStore.setOnClickListener(addStoreButtonClick);
        Button refreshButton = view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(refreshButtonClick);
        userLocation = ManageSharePrefs.readLocation(" ");
        }

        private final View.OnClickListener refreshButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserShops();
            }
        };

        public void getUserShops(){
        Call<List<Shop>> call = apiService.getUserShops(auth);
        call.enqueue(new Callback<List<Shop>>() {
            @Override
            public void onResponse(Call<List<Shop>> call, Response<List<Shop>> response) {
                if(response.body()!=null){
                    shops = response.body();
                    adapter = new RecyclerAdapter(shops, getContext(), auth);
                    shopsRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Shop>> call, Throwable t) {
                Toast.makeText(getContext(), "error occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final View.OnClickListener addStoreButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(),SellerAddShop.class);
            intent.putExtra("auth",auth);
            intent.putExtra("lat",userLocation.getLatPos());
            intent.putExtra("lon",userLocation.getLogPos());
            startActivityForResult(intent,requestCode);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getUserShops();
    }
}
