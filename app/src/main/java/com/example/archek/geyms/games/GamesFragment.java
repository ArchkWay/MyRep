package com.example.archek.geyms.games;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.archek.geyms.R;
import com.example.archek.geyms.network.GbObjectResponse;
import com.example.archek.geyms.network.GbObjectsListResponse;
import com.example.archek.geyms.network.GiantBombService;
import com.example.archek.geyms.network.RestApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GamesFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    public static final String TAG = "33__";
    private static final int TOTAL_GAMES_COUNT = 64131;

    private GiantBombService service = RestApi.creteService( GiantBombService.class );
    private Random random = new Random(  );
    private GamesAdapter adapter = new GamesAdapter();
    private RecyclerView rvGames;
    private ProgressBar progressBar;
    @Nullable private Call<GbObjectsListResponse> call;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupToolbar(view);
        setupRecyclerView(view);
        progressBar = view.findViewById( R.id.progressBar );
        loadRandomGames();
       }
    private void loadRandomGames(){
        if(call != null && call.isExecuted()){
            return;
        }
        showLoading();
        int limit = 10;
        int offset = random.nextInt(TOTAL_GAMES_COUNT-limit+1);
        call =service.getGames( limit, offset );
        Log.d(TAG, toString());
        //noinspection ConstantConditions
        call.enqueue( new Callback <GbObjectsListResponse>() {
            @Override
            public void onResponse(Call<GbObjectsListResponse> call, Response <GbObjectsListResponse> response) {
                Log.d(TAG, toString());
                showContent();
                GamesFragment.this.call = call.clone();
                GbObjectsListResponse gbObjectsListResponse = response.body();
                if(gbObjectsListResponse != null){
                    adapter.replaceAll( gbObjectsListResponse.getResults() );
                }
            }

            @Override
            public void onFailure(Call <GbObjectsListResponse> call, Throwable t) {
                showContent();
                if(call.isCanceled()){
                    Toast.makeText( getContext(), R.string.error, Toast.LENGTH_SHORT ).show();

                }
            }
        } );
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if (call != null) {
            call.cancel();
        }
    }

    private void showLoading(){
    rvGames.setVisibility( View.GONE );
    progressBar.setVisibility( View.VISIBLE );
    }

    private void showContent(){
        progressBar.setVisibility( View.GONE );
        rvGames.setVisibility(View.VISIBLE);

    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.games);
        toolbar.inflateMenu(R.menu.menu_games);
        toolbar.setOnMenuItemClickListener(this);

    }

    private void setupRecyclerView(View view) {
        rvGames = view.findViewById(R.id.rvGames);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvGames.setLayoutManager(layoutManager);
        rvGames.setAdapter(adapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            loadRandomGames();
            return true;
        }
        return false;
    }
}