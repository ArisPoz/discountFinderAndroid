package eu.jnksoftware.discountfinderandroid.ui.customer.userPreferences;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import eu.jnksoftware.discountfinderandroid.Apis.ApiUtils;
import eu.jnksoftware.discountfinderandroid.R;
import eu.jnksoftware.discountfinderandroid.Utilities.ManageSharePrefs;
import eu.jnksoftware.discountfinderandroid.models.discountPreferences.DiscountPreferencesResponse;
import eu.jnksoftware.discountfinderandroid.models.token.User;
import eu.jnksoftware.discountfinderandroid.services.IuserService;
import eu.jnksoftware.discountfinderandroid.ui.customer.adapters.DiscountPreferenceAdapter;
import eu.jnksoftware.discountfinderandroid.ui.customer.adapters.DiscountPreferenceItemTouchHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPreferenceList extends Fragment implements DiscountPreferenceItemTouchHelper.RecyclerItemTouchHelperListener{
    private User user;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DiscountPreferenceAdapter adapter;
    private IuserService iuserService;
    private List<DiscountPreferencesResponse> discountPreferencesResponses ;
    private String auth;
    private Button addPreference;
   private Button updatePreference;
    private ConstraintLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_user_preference_list,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //layout=view.findViewById(R.id.userpreferencesConst);
        user = ManageSharePrefs.readUser(null);
        iuserService= ApiUtils.getUserService();
        layout = view.findViewById(R.id.userpreferencesConst);
        recyclerView= view.findViewById(R.id.userPreferenceRecycler);
        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        updatePreference=view.findViewById(R.id.updateprefBtn);
        updatePreference.setOnClickListener(uppref);
        addPreference = view.findViewById(R.id.userPreferencesBtn);
        addPreference.setOnClickListener(addPreferenceClick);
        auth = getArguments().getString("auth");
        fetchUserPreferences();

        ItemTouchHelper.SimpleCallback itemTouchHelper= new DiscountPreferenceItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserPreferences();
    }



   private View.OnClickListener uppref=new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           Intent userUpdatePreference=new Intent(getActivity(),UserUpdatePreferences.class);
           getActivity().startActivity(userUpdatePreference);
       }
   };

    private View.OnClickListener addPreferenceClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent userPreferences = new Intent(getActivity(), UserPreferences.class);
            getActivity().startActivity(userPreferences);

        }
    };

    private void fetchUserPreferences() {

        Call<List<DiscountPreferencesResponse>> disc=iuserService.getDiscountsPreference(auth);
        disc.enqueue(new Callback<List<DiscountPreferencesResponse>>() {
            @Override
            public void onResponse(Call<List<DiscountPreferencesResponse>> call, Response<List<DiscountPreferencesResponse>> response) {

                discountPreferencesResponses=response.body();
                adapter=new DiscountPreferenceAdapter(discountPreferencesResponses, getContext(),auth);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<DiscountPreferencesResponse>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DiscountPreferenceAdapter.MyViewHolder){
            String catTitle = discountPreferencesResponses.get(viewHolder.getAdapterPosition()).getCategoryTitle();
            final DiscountPreferencesResponse deletedDiscount=discountPreferencesResponses.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            int pos = viewHolder.getAdapterPosition();
            deletePref(adapter.getDiscountId(pos));
            adapter.removeDiscount(pos);
            adapter.notifyItemRemoved(pos);

            adapter.notifyDataSetChanged();
            Snackbar snackbar = Snackbar
                    .make(layout, catTitle + " removed from your shop!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    adapter.restoreDiscount(deletedDiscount, deletedIndex);
                    adapter.notifyItemInserted(deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void deletePref( int id)
    {
        iuserService= ApiUtils.getUserService();
        Call<Void> delete=iuserService.deleteDiscountPreference(id,auth);
        delete.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}